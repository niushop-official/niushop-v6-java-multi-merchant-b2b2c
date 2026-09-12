package com.niu.core.service.core.vip_card.order.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.separ_account.SeparAccount;
import com.niu.core.entity.separ_account.SeparAccountDetail;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.enums.account.SeparMainTypeEnum;
import com.niu.core.enums.account.SeparValueTypeEnum;
import com.niu.core.enums.o2o.PayTypeEnum;
import com.niu.core.enums.shop.ShopAccountEnum;
import com.niu.core.enums.trade.SettlementEnum;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.separ_account.ICoreSeparAccountService;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderSettlementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoreVipCardOrderSettlementServiceImpl implements ICoreVipCardOrderSettlementService {

    @Autowired
    private VipCardOrderMapper orderMapper;

    @Autowired
    private ICorePayService corePayService;

    @Autowired
    private ICoreSiteService coreSiteService;

    @Autowired
    private ICoreSeparAccountService coreSeparAccountService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void settlement(Map<String, Object> data) {
        // 1. 更新结算状态为处理中
        dealing(data);

        Integer orderId = (Integer) data.get("order_id");
        BigDecimal orderMoney = (BigDecimal) data.get("order_money");
        Integer siteId = (Integer) data.get("site_id");
        String outTradeNo = (String) data.get("out_trade_no");
        BigDecimal refundMoney = (BigDecimal) data.getOrDefault("refund_money", BigDecimal.ZERO);
        String orderFrom = (String) data.getOrDefault("order_from", "");

        // 2. 查询支付信息
        Pay pay = corePayService.findPayInfoByOutTradeNo(outTradeNo);
        if (pay == null) {
            // 若无支付记录，可能为线下支付，跳过
            return;
        }

        // 3. 判断是否非线下支付（需要进行分账）
        if (!PayTypeEnum.OFFLINEPAY.getKey().equals(pay.getType())) {
            // 获取平台名称
            Integer defaultSiteId = 0; // 平台站点ID
            SiteInfoVo siteCache = coreSiteService.getSiteCache(defaultSiteId);
            String platName = siteCache != null ? siteCache.getSiteName() : "平台";

            // 确定价值类型（余额支付用 balance，其他用 money）
            String valueType = PayTypeEnum.BALANCEPAY.getKey().equals(pay.getType()) ? SeparValueTypeEnum.BALANCE.getType() : SeparValueTypeEnum.MONEY.getType();

            BigDecimal separMoney = orderMoney;

            // 构建分账数据列表
            List<Map<String, Object>> separData = new ArrayList<>();

            // 订单金额项
            Map<String, Object> orderMoneyItem = new LinkedHashMap<>();
            orderMoneyItem.put("money_type", "order_money");
            orderMoneyItem.put("money_type_name", "订单总额");
            orderMoneyItem.put("value", orderMoney);
            orderMoneyItem.put("value_type", valueType);
            separData.add(orderMoneyItem);

            // 退款金额项
            if (refundMoney != null && refundMoney.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> refundItem = new LinkedHashMap<>();
                refundItem.put("money_type", "order_refund_money");
                refundItem.put("money_type_name", "退款金额");
                refundItem.put("value", refundMoney);
                refundItem.put("value_type", valueType);
                separData.add(refundItem);
                separMoney = separMoney.subtract(refundMoney).setScale(2, RoundingMode.HALF_UP);
            }

            // 支付手续费
            BigDecimal feeAmt = pay.getFeeAmt() != null ? pay.getFeeAmt() : BigDecimal.ZERO;
            if (feeAmt.compareTo(BigDecimal.ZERO) > 0) {
                Map<String, Object> feeItem = new LinkedHashMap<>();
                feeItem.put("money_type", "pay_fee_amt");
                feeItem.put("money_type_name", "付款手续费");
                feeItem.put("value", feeAmt);
                feeItem.put("value_type", valueType);
                separData.add(feeItem);
                separMoney = separMoney.subtract(feeAmt).setScale(2, RoundingMode.HALF_UP);
            }

            // 商户分账项（收款方）
            Map<String, Object> siteSeparateItem = new LinkedHashMap<>();
            siteSeparateItem.put("money_type", ShopAccountEnum.SITE_SEPARATE.getType());
            siteSeparateItem.put("money_type_name", "商户分账");
            siteSeparateItem.put("value", separMoney.max(BigDecimal.ZERO));
            siteSeparateItem.put("value_type", valueType);
            separData.add(siteSeparateItem);

            // 构建分账主记录
            SeparAccount account = new SeparAccount();
            account.setSiteId(siteId);
            account.setSiteName(platName);
            account.setTradeType("vip_card_order_trade");
            account.setTradeId(orderId);
            account.setTradeNo((String) data.get("order_no"));
            account.setTradeMoney(orderMoney);
            account.setOutTradeNo(outTradeNo);
            account.setPayType(pay.getType());
            account.setSeparData(JSONUtil.toJsonStr(separData));
            account.setTradeScene(orderFrom);

            // 构建分账明细（只有一条商户分账）
            List<SeparAccountDetail> details = new ArrayList<>();
            SeparAccountDetail detail = new SeparAccountDetail();
            detail.setMainType(SeparMainTypeEnum.PLAT.getType());
            detail.setMainName(platName);
            detail.setMainId(siteId);
            detail.setValue(separMoney.max(BigDecimal.ZERO));
            detail.setValueType(valueType);
            detail.setValueSource(ShopAccountEnum.SITE_SEPARATE.getType());
            detail.setMemo("付费会员卡订单分账");
            details.add(detail);

            // 调用分账服务
            coreSeparAccountService.add(account, details);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dealing(Map<String, Object> data) {
        Integer orderId = (Integer) data.get("order_id");
        if (orderId == null) {
            throw new CommonException("订单ID不能为空");
        }
        LambdaUpdateWrapper<MemberVipCardOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberVipCardOrder::getOrderId, orderId)
                .set(MemberVipCardOrder::getSettlementStatus, SettlementEnum.DEALING.getCode());
        orderMapper.update(null, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void success(Map<String, Object> data) {
        Integer orderId = (Integer) data.get("order_id");
        if (orderId == null) {
            throw new CommonException("订单ID不能为空");
        }
        long now = System.currentTimeMillis() / 1000;
        LambdaUpdateWrapper<MemberVipCardOrder> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberVipCardOrder::getOrderId, orderId)
                .set(MemberVipCardOrder::getSettlementStatus, SettlementEnum.SETTLED.getCode())
//                .set(VipCardOrder::getSettlementTime, now)
        ;
        orderMapper.update(null, wrapper);
        // 触发结算完成事件（若需要）
        // CoreOrderEventService.orderSettlementFinish(data);
    }
}