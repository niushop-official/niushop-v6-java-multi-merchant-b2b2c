package com.niu.core.service.core.recharge.order.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.entity.separ_account.SeparAccount;
import com.niu.core.entity.separ_account.SeparAccountDetail;
import com.niu.core.entity.site.Site;
import com.niu.core.enums.account.SeparMainTypeEnum;
import com.niu.core.enums.account.SeparValueTypeEnum;
import com.niu.core.enums.recharge.RechargeOrderLogEnum;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.enums.site.SiteFinanceLogEnum;
import com.niu.core.enums.trade.SettlementEnum;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.recharge.order.ICoreOrderCloseService;
import com.niu.core.service.core.recharge.order.ICoreOrderEventService;
import com.niu.core.service.core.recharge.order.ICoreOrderSettlementService;
import com.niu.core.service.core.recharge.order.param.AfterOrderCloseParam;
import com.niu.core.service.core.separ_account.ICoreSeparAccountService;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充值订单结算服务层
 */
@Slf4j
@Service
public class CoreOrderSettlementServiceImpl implements ICoreOrderSettlementService {

    @Resource
    private RechargeOrderMapper rechargeOrderMapper;
    @Resource
    private ICorePayService corePayService;
    @Resource
    private SiteMapper siteMapper;
    @Resource
    private ICoreSeparAccountService coreSeparAccountService;
    /**
     * 充值订单结算
     * @param orderId 创建参数
     */
    @Override
    @Transactional
    public void settlement(Integer orderId) {
        RechargeOrder order = rechargeOrderMapper.selectById(orderId);
        if (ObjectUtil.isEmpty(order)){
            throw new CommonException("订单不存在");
        }
        dealing(order);

        //支付分账
        Pay pay = corePayService.findPayInfoByOutTradeNo(order.getOutTradeNo());
        if(ObjectUtil.isNotEmpty(pay)){
            if(!"offlinepay".equals(pay.getType())){
                Site site = siteMapper.selectById(order.getSiteId());
                String siteName =  ObjectUtil.isNotEmpty( site) && StringUtils.isNotEmpty(site.getSiteName()) ? site.getSiteName() : "";
                String valueType = pay.getType().equals("balancepay") ? SeparValueTypeEnum.BALANCE.getType() : SeparValueTypeEnum.MONEY.getType();

                BigDecimal separMoney = order.getOrderMoney();

                // 分账数据
                List<Map<String, Object>> separData = new ArrayList<>();
                Map<String, Object> orderMoneyData = new HashMap<>();
                orderMoneyData.put("money_type", "order_money");
                orderMoneyData.put("money_type_name", "订单总额");
                orderMoneyData.put("value", order.getOrderMoney());
                orderMoneyData.put("value_type", valueType);
                separData.add(orderMoneyData);

                // 退款金额处理
                BigDecimal refundMoney = BigDecimal.ZERO;
                if (refundMoney.compareTo(BigDecimal.ZERO) > 0) {
                    Map<String, Object> refundData = new HashMap<>();
                    refundData.put("money_type", "order_refund_money");
                    refundData.put("money_type_name", "退款金额");
                    refundData.put("value", refundMoney);
                    refundData.put("value_type", valueType);
                    separData.add(refundData);
                }

                //判断是否存在支付手续费
                BigDecimal feeAmt = ObjectUtil.defaultIfNull(pay.getFeeAmt(), BigDecimal.ZERO);
                if(feeAmt.compareTo(BigDecimal.ZERO) > 0){
                    Map<String, Object> feeData = new HashMap<>();
                    feeData.put("money_type", "pay_fee_amt");
                    feeData.put("money_type_name", "付款手续费");
                    feeData.put("value", feeAmt);
                    feeData.put("value_type", valueType);
                    separData.add(feeData);

                    separMoney = separMoney.add(feeAmt).setScale(2, RoundingMode.HALF_UP);
                }
                // 商家分账
                Map<String, Object> siteSeparData = new HashMap<>();
                siteSeparData.put("money_type", SiteFinanceLogEnum.SITE_SEPARATE.getSource());
                siteSeparData.put("money_type_name", SiteFinanceLogEnum.SITE_SEPARATE.getName());
                siteSeparData.put("value", separMoney);
                siteSeparData.put("value_type", valueType);
                separData.add(siteSeparData);

                // 构建分账主记录
                SeparAccount separAccount = new SeparAccount();
                separAccount.setSiteId(order.getSiteId());
                separAccount.setSiteName(siteName);
                separAccount.setTradeType("recharge_order_trade");
                separAccount.setTradeId(order.getOrderId());
                separAccount.setTradeNo(order.getOrderNo());
                separAccount.setTradeMoney(order.getOrderMoney());
                separAccount.setOutTradeNo(order.getOutTradeNo());
                separAccount.setPayType(pay.getType());
                separAccount.setSeparData(JSONUtil.toJsonStr(separData));
                separAccount.setTradeScene(order.getOrderFrom() != null ? order.getOrderFrom() : "");

                // 构建分账详情记录
                List<SeparAccountDetail> separDetailList = new ArrayList<>();
                SeparAccountDetail detail = new SeparAccountDetail();
                detail.setMainType(SeparMainTypeEnum.PLAT.getType());
                detail.setMainName(siteName);
                detail.setMainId(RequestUtils.defaultSiteId());
                detail.setValue(separMoney.max(BigDecimal.ZERO));
                detail.setValueType(valueType);
                detail.setValueSource(SiteFinanceLogEnum.SITE_SEPARATE.getSource());
                detail.setMemo("充值分账");
                separDetailList.add(detail);

                coreSeparAccountService.add(separAccount, separDetailList);
            }
        }
    }

    public void dealing(RechargeOrder order) {
        RechargeOrder updateOrder = new RechargeOrder();
        updateOrder.setOrderId(order.getOrderId());
        updateOrder.setSettlementStatus(SettlementEnum.DEALING.getCode());
        rechargeOrderMapper.updateById(updateOrder);
    }

    @Override
    public void success(RechargeOrder order) {
        RechargeOrder updateOrder = new RechargeOrder();
        updateOrder.setOrderId(order.getOrderId());
        updateOrder.setSettlementStatus(SettlementEnum.SETTLED.getCode());
        rechargeOrderMapper.updateById(updateOrder);
    }

}
