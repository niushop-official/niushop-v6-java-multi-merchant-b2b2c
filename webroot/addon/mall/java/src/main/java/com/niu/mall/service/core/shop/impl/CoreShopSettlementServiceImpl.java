package com.niu.mall.service.core.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.separ_account.SeparAccount;
import com.niu.core.entity.separ_account.SeparAccountDetail;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.enums.account.SeparMainTypeEnum;
import com.niu.core.enums.account.SeparValueSourceEnum;
import com.niu.core.enums.account.SeparValueTypeEnum;
import com.niu.core.enums.shop.ShopAccountEnum;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.core.separ_account.ICoreSeparAccountService;
import com.niu.core.service.core.shop.ICoreShopAccountService;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.shop.ICoreShopSettlementService;
import com.niu.mall.service.core.shop.param.OrderRefundParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 店铺结算服务实现类
 */
@Service
public class CoreShopSettlementServiceImpl implements ICoreShopSettlementService {

    @Resource
    private ICoreShopAccountService coreShopAccountService;
    
    @Resource
    private ICoreSeparAccountService coreSeparAccountService;
    
    @Resource
    private MallOrderMapper mallOrderMapper;

    @Resource
    private PayMapper payMapper;

    @Resource
    private SiteShopMapper siteShopMapper;

    @Resource
    private ICoreSiteService coreSiteService;

    /**
     * 订单支付结算处理
     * @param order 订单信息
     */
    @Override
    @Transactional
    public void orderPay(MallOrder order) {

        SiteShop siteShop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id", order.getSiteId()));
        if(ObjectUtils.isNotEmpty(siteShop))
        {
            BigDecimal commissionRate = siteShop.getCommissionRate();
            if(commissionRate.compareTo(BigDecimal.ZERO) > 0)
            {
                BigDecimal siteOrderMoney = order.getOrderMoney().subtract(order.getSupplyMoney());
                BigDecimal mallMoney = siteOrderMoney.multiply(commissionRate).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                BigDecimal shopMoney = siteOrderMoney.subtract(mallMoney);
                MallOrder mallOrder = new MallOrder();
                mallOrder.setOrderId(order.getOrderId());
                mallOrder.setMallMoney(mallMoney);
                mallOrder.setShopMoney(shopMoney);
                mallOrder.setMallRate(commissionRate);
                mallOrderMapper.updateById(mallOrder);
            }

        }

    }

    /**
     * 订单退款结算处理
     * @param orderRefundParam 退款参数
     */
    @Override
    @Transactional
    public void orderRefund(OrderRefundParam orderRefundParam) {
        // 获取订单信息
        MallOrder order = mallOrderMapper.selectById(orderRefundParam.getOrderId());
        if (order == null) {
            return;
        }
        
        BigDecimal refundMoney = orderRefundParam.getRefundMoney();
        BigDecimal mallRate = order.getMallRate();
        BigDecimal orderMoney = order.getOrderMoney();
        BigDecimal refundDeliveryMoney = order.getRefundDeliveryMoney();
        
        // 只有当订单金额大于0时才进行计算
        if (orderMoney.compareTo(BigDecimal.ZERO) > 0) {
            // 计算退款比例
            BigDecimal refundRate = refundMoney.divide(orderMoney, 2, BigDecimal.ROUND_HALF_UP);
            
            // 计算平台优惠券退款金额
            BigDecimal mallCouponMoney = order.getMallCouponMoney();
            BigDecimal mallRefundCouponMoney = mallCouponMoney.multiply(refundRate).setScale(2, BigDecimal.ROUND_HALF_UP);
            // 确保退款优惠券金额不超过原优惠券金额
            if (mallRefundCouponMoney.compareTo(mallCouponMoney) > 0) {
                mallRefundCouponMoney = mallCouponMoney;
            }
            
            // 获取供货商退款金额
            BigDecimal supplyRefundMoney = order.getSupplyRefundMoney();
            
            // 计算商品退款金额（扣除运费退款）
            BigDecimal refundGoodsMoney = refundMoney.subtract(refundDeliveryMoney).setScale(2, BigDecimal.ROUND_HALF_UP);
            // 计算商品退款金额与供货商退款金额的差值
            BigDecimal diffRefundGoodsMoney = refundGoodsMoney.subtract(supplyRefundMoney).setScale(2, BigDecimal.ROUND_HALF_UP);
            
            // 计算平台退款金额（根据佣金比例）
            BigDecimal mallRefundMoney = diffRefundGoodsMoney.multiply(mallRate.divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            
            // 计算店铺退款金额（差值扣除平台退款金额，再加上运费退款）
            BigDecimal shopRefundMoney = diffRefundGoodsMoney.subtract(mallRefundMoney)
                    .add(refundDeliveryMoney)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            
            // 更新订单退款相关金额
            MallOrder updateOrder = new MallOrder();
            updateOrder.setOrderId(order.getOrderId());
            updateOrder.setMallRefundMoney(mallRefundMoney);
            updateOrder.setShopRefundMoney(shopRefundMoney);
            updateOrder.setMallRefundCouponMoney(mallRefundCouponMoney);
            mallOrderMapper.updateById(updateOrder);
        }
    }

    /**
     * 店铺结算处理
     * @param order 订单信息
     */
    @Override
    @Transactional
    public void settlement(MallOrder order) {
        Integer siteId = order.getSiteId();
        String orderNo = order.getOrderNo();

        // 设置结算状态为已结算
        order.setSettlementStatus("settled");
        // 记录结算时间
        order.setSettlementTime(System.currentTimeMillis() / 1000);
        // 保存订单更新
        mallOrderMapper.updateById(order);
        Map<String, BigDecimal> commissionMap = getSettlement(order);
        BigDecimal siteMoney = commissionMap.get("site_money");
        BigDecimal platMoney = commissionMap.get("plat_money");
        BigDecimal commission = commissionMap.get("commission");
        BigDecimal supplyMoney = commissionMap.get("supply_money");
        // 构建分账数据
        Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", order.getOutTradeNo()));
        if(ObjectUtils.isEmpty(pay)) return;
        if(pay.getType().equals("offline_pay")) return;

        String siteName = coreSiteService.getSiteCache(siteId).getSiteName();
        String platName = coreSiteService.getSiteCache(0).getSiteName();

        String valueType = pay.getType().equals("balancepay") ? "balance" : "money";
        //构建分账数据
        JSONArray separData = new JSONArray();
        separData.add(JSONUtil.parseObj("{\"money_type\":" + "order_money" + ",\"money_type_name\":" + "订单总额" + ",\"value\":" + order.getOrderMoney() + ",\"value_type\":" + valueType  + "}"));

        if(order.getRefundMoney().compareTo(BigDecimal.ZERO) > 0){
            separData.add(JSONUtil.parseObj("{\"money_type\":" + "order_refund_money" + ",\"money_type_name\":" + "退款金额" + ",\"value\":" + order.getRefundMoney() + ",\"value_type\":" + valueType  + "}"));

        }

        if(commission.compareTo(BigDecimal.ZERO) > 0){
            separData.add(JSONUtil.parseObj("{\"money_type\":" + "fenxiao_commission_money" + ",\"money_type_name\":" + "佣金支出(平台转账)" + ",\"value\":" + commission + ",\"value_type\":" + valueType  + "}"));
        }
        if(supplyMoney.compareTo(BigDecimal.ZERO) > 0){
            separData.add(JSONUtil.parseObj("{\"money_type\":" + "supply_money" + ",\"money_type_name\":" + "供货商货款" + ",\"value\":" + supplyMoney + ",\"value_type\":" + valueType  + "}"));
        }

        if(ObjectUtil.isNotEmpty(pay.getFeeAmt()) &&  pay.getFeeAmt().compareTo(BigDecimal.ZERO) > 0){
            separData.add(JSONUtil.parseObj("{\"money_type\":" + "pay_fee_amt" + ",\"money_type_name\":" + "支付手续费" + ",\"value\":" + pay.getFeeAmt() + ",\"value_type\":" + valueType  + "}"));
        }
        separData.add(JSONUtil.parseObj("{\"money_type\":" + "plat_service" + ",\"money_type_name\":" + "平台手续费" + ",\"value\":" + platMoney + ",\"value_type\":" + valueType  + "}"));
        separData.add(JSONUtil.parseObj("{\"money_type\":" + "site_money" + ",\"money_type_name\":" + "店铺结算" + ",\"value\":" + siteMoney + ",\"value_type\":" + valueType  + "}"));

        // 创建分账记录
        SeparAccount separAccount = new SeparAccount();
        separAccount.setSiteId(siteId);
        separAccount.setSiteName(order.getSiteName());
        separAccount.setTradeType("order_trade");
        separAccount.setTradeId(order.getOrderId());
        separAccount.setTradeNo(orderNo);
        separAccount.setTradeMoney(order.getOrderMoney());
        separAccount.setSeparData(separData.toString());
        separAccount.setStatus("wait");
        separAccount.setOutTradeNo(order.getOutTradeNo());
        separAccount.setPayType(pay.getType());
        separAccount.setCreateTime(System.currentTimeMillis() / 1000);
        
        // 创建分账详情
        List<SeparAccountDetail> separDetails = new ArrayList<>();

        BigDecimal siteSeparMoney = siteMoney;
        BigDecimal platSeparMoney = platMoney.subtract(commission);
        BigDecimal surplusFeeAmt = ObjectUtil.isNotEmpty(pay.getFeeAmt())  ?  pay.getFeeAmt() : BigDecimal.ZERO;
        if(surplusFeeAmt.compareTo(platSeparMoney) > 0){
            platSeparMoney = BigDecimal.ZERO;
            surplusFeeAmt = surplusFeeAmt.subtract(platSeparMoney);
            if(siteSeparMoney.compareTo(surplusFeeAmt) < 0){
                siteSeparMoney = BigDecimal.ZERO;
            }else{
                siteSeparMoney = siteSeparMoney.subtract(surplusFeeAmt);
            }
        }else{
            platSeparMoney = platSeparMoney.subtract(surplusFeeAmt);
        }
        // 商户分账详情
        SeparAccountDetail siteDetail = new SeparAccountDetail();
        siteDetail.setMainType(SeparMainTypeEnum.SITE.getType());
        siteDetail.setMainName(siteName);
        siteDetail.setMainId(siteId);
        siteDetail.setValue(siteSeparMoney.max(BigDecimal.ZERO));
        siteDetail.setValueType(valueType);
        siteDetail.setValueSource(SeparValueSourceEnum.SITE_SEPARATE.getSource());
        siteDetail.setMemo("商户分账");
        separDetails.add(siteDetail);
        
        // 平台手续费详情
        SeparAccountDetail platDetail = new SeparAccountDetail();
        platDetail.setMainType(SeparMainTypeEnum.PLAT.getType());
        platDetail.setMainName(platName);
        platDetail.setMainId(RequestUtils.defaultSiteId());
        platDetail.setValue(platSeparMoney.max(BigDecimal.ZERO));
        platDetail.setValueType(valueType);
        platDetail.setValueSource(SeparValueSourceEnum.PLAT_SERVICE.getSource());
        platDetail.setMemo("平台手续费");
        separDetails.add(platDetail);


        
        // 添加分账记录
            coreSeparAccountService.add(separAccount, separDetails);
    }

    private Map<String, BigDecimal> getSettlement(MallOrder data) {
        // 订单应收
        BigDecimal orderMoneyReceive = data.getOrderMoney();

        // 订单应退
        BigDecimal orderMoneyRefund = data.getRefundMoney().negate();

        // 实际收入 = 订单应收 + 订单应退
        BigDecimal receiveReal = orderMoneyReceive.add(orderMoneyRefund);

        // 平台手续费 = 成交订单平台手续费 - 退款订单平台手续费
        BigDecimal platServiceMoney = data.getMallMoney().subtract(data.getMallRefundMoney());

        // 供货商货款 = 供货商货款 - 供货商退款金额
        BigDecimal supplyMoney = data.getSupplyMoney().subtract(data.getSupplyRefundMoney());

        // 佣金 = 店铺佣金 - 店铺退款佣金
        BigDecimal commission = data.getShopCommission().subtract(data.getShopRefundCommission());

        // 实际支出 = 平台手续费 + 供货商货款 + 佣金
        BigDecimal outReal = platServiceMoney.add(supplyMoney).add(commission);

        // 当日结余 = 实际收入 - 实际支出，且不小于0
        BigDecimal siteMoney = receiveReal.subtract(outReal);
        siteMoney = siteMoney.max(BigDecimal.ZERO);

        // 返回结算结果
        Map<String, BigDecimal> result = new HashMap<>();
        result.put("site_money", siteMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
        result.put("supply_money", supplyMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
        result.put("plat_money", platServiceMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
        result.put("commission", commission.setScale(2, BigDecimal.ROUND_HALF_UP));

        return result;
    }
}
