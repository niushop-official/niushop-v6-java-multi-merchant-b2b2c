package com.niu.core.service.core.finance.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.trade.SettlementEnum;
import com.niu.core.service.core.finance.ICoreFinanceCalculateService;
import com.niu.core.service.core.finance.ICoreFinanceService;
import com.niu.core.service.core.finance.ICorePlatService;
import com.niu.core.service.core.finance.ICoreSitePlatService;
import com.niu.core.service.core.finance.param.*;
import com.niu.core.service.core.finance.vo.CoreFinanceCalculateVo;
import com.niu.core.service.core.finance.vo.OrderRefundCalcVo;
import com.niu.core.service.core.sys.ICoreSysStatService;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CoreFinanceServiceImpl implements ICoreFinanceService {

    @Resource
    ICoreFinanceCalculateService coreFinanceCalculateService;

    @Resource
    ICorePlatService corePlatService;

    @Resource
    ICoreSitePlatService coreSitePlatService;

    @Resource
    ICoreSysStatService coreStatService;

    @Override
    public void orderPay(OrderFinanceParam orderParam) {

        CoreFinanceCalculateVo calculateVo = coreFinanceCalculateService.orderPayCalc(orderParam);

        OrderPlatParam orderPlatParam = new OrderPlatParam();
        BeanUtil.copyProperties(orderParam, orderPlatParam);
        orderPlatParam.setSiteId(RequestUtils.defaultSiteId());
        orderPlatParam.setMallSiteId(orderParam.getSiteId());
        orderPlatParam.setMallMoney(calculateVo.getMallMoney());
        orderPlatParam.setMallCouponMoney(orderParam.getMallCouponMoney());
        corePlatService.platIncomeOrderPay(orderPlatParam);
        corePlatService.platIncomePlatCoupon(orderPlatParam);

        SiteOrderPlatParam siteOrderPlatParam = new SiteOrderPlatParam();
        BeanUtil.copyProperties(orderParam, siteOrderPlatParam);
        siteOrderPlatParam.setSiteId(orderParam.getSiteId());
        siteOrderPlatParam.setPlatSiteId(RequestUtils.defaultSiteId());
        siteOrderPlatParam.setMallMoney(calculateVo.getMallMoney());
        siteOrderPlatParam.setMallCouponMoney(orderParam.getMallCouponMoney());
        //平台优惠券补贴
        coreSitePlatService.siteIncomePlatCoupon(siteOrderPlatParam);

        // 平台统计
        SysDataAddParam sysDataAddParam = new SysDataAddParam();
        sysDataAddParam.setSiteId(RequestUtils.defaultSiteId());
        sysDataAddParam.setOrderReceivableMoney(calculateVo.getOrderReceivableMoney());
        sysDataAddParam.setOrderActualMoney(orderParam.getOrderMoney());
        sysDataAddParam.setMallCouponMoney(orderParam.getMallCouponMoney());
        sysDataAddParam.setDeliveryMoney(orderParam.getDeliveryMoney());
        coreStatService.addStat(sysDataAddParam);

        // 店铺统计
        sysDataAddParam = new SysDataAddParam();
        sysDataAddParam.setSiteId(orderParam.getSiteId());
        sysDataAddParam.setOrderReceivableMoney(calculateVo.getOrderReceivableMoney());
        sysDataAddParam.setOrderActualMoney(orderParam.getOrderMoney());
        sysDataAddParam.setMallCouponMoney(orderParam.getMallCouponMoney());
        coreStatService.addStat(sysDataAddParam);
    }

    @Override
    public void orderFinish(OrderFinanceParam orderFinanceParam) {
        // 财务计算
        CoreFinanceCalculateVo calculateVo = coreFinanceCalculateService.orderPayCalc(orderFinanceParam);

        // 平台数据处理
        OrderPlatParam platParam = new OrderPlatParam();
        BeanUtil.copyProperties(orderFinanceParam, platParam);
        platParam.setSiteId(RequestUtils.defaultSiteId());
        platParam.setMallSiteId(orderFinanceParam.getSiteId());
        platParam.setShopSeparateMoney(calculateVo.getShopSeparateMoney());
        platParam.setMallMoney(calculateVo.getMallMoney());
        // 平台支出（支付佣金）
        corePlatService.platIncomeMallService(platParam);
        // 平台支出（支付佣金）
        corePlatService.platOutlayCommission(platParam);
        // 支付分账
        corePlatService.platOutlaySeparate(platParam);

        // 商户数据处理
        SiteOrderPlatParam siteParam = new SiteOrderPlatParam();
        BeanUtil.copyProperties(orderFinanceParam, siteParam);
        siteParam.setSiteId(orderFinanceParam.getSiteId());
        if (!(ObjectUtil.isNotNull(orderFinanceParam.getSupplyId()) && orderFinanceParam.getSupplyId() > 0) ){
            // 商户支出（支付佣金）
            coreSitePlatService.siteOutlayCommission(siteParam);
        }


        // 平台统计
        SysDataAddParam sysStatParam = new SysDataAddParam();
        sysStatParam.setSiteId(RequestUtils.defaultSiteId());
        sysStatParam.setShopSeparateMoney(calculateVo.getShopSeparateMoney());
        sysStatParam.setShopCommission(orderFinanceParam.getShopCommission());
        coreStatService.addStat(sysStatParam);

        // 店铺统计
        SysDataAddParam shopStatParam = new SysDataAddParam();
        shopStatParam.setSiteId(orderFinanceParam.getSiteId());
        shopStatParam.setShopCommission(orderFinanceParam.getShopCommission());
        shopStatParam.setMallServiceMoney(calculateVo.getMallMoney());
        coreStatService.addStat(shopStatParam);
    }

    @Override
    public void orderRefund(OrderRefundParam orderRefundParam) {
        // 财务计算
        OrderRefundCalcVo calculateVo = coreFinanceCalculateService.orderRefundCalc(orderRefundParam);

        // 平台数据处理
        OrderRefundParam platParam = new OrderRefundParam();
        BeanUtil.copyProperties(orderRefundParam, platParam);
        platParam.setSiteId(RequestUtils.defaultSiteId());
        platParam.setMoney(orderRefundParam.getMoney());
        platParam.setMallRefundMoney(calculateVo.getMallRefundMoney());
        platParam.setMallRefundCouponMoney(calculateVo.getMallRefundCouponMoney());
        platParam.setShopRefundSeparateMoney(calculateVo.getShopRefundSeparateMoney());
        platParam.setRefundCommission(calculateVo.getRefundCommission());
        platParam.setSupplyRefundMoney(orderRefundParam.getSupplyRefundMoney());

        // 退还平台优惠券补贴
        corePlatService.platIncomePlatRefundCoupon(platParam);
        if(SettlementEnum.SETTLED.getCode().equals(orderRefundParam.getSettlementStatus())){
            // 订单应退
            corePlatService.platIncomeOrderRefund(platParam);
            // 退还佣金
            corePlatService.platOutlayRefundCommission(platParam);
        }else{
            corePlatService.platIncomeOrderRefundUnsettleMoney(platParam);
        }

        // 商户数据处理
        SiteOrderRefundParam siteParam = new SiteOrderRefundParam();
        BeanUtil.copyProperties(orderRefundParam, siteParam);
        siteParam.setSiteId(orderRefundParam.getSiteId());
        siteParam.setMoney(orderRefundParam.getMoney());
        siteParam.setShopRefundMoney(calculateVo.getShopRefundMoney());
        siteParam.setMallRefundCouponMoney(calculateVo.getMallRefundCouponMoney());
        siteParam.setMallRefundMoney(calculateVo.getMallRefundMoney());
        siteParam.setRefundCommission(calculateVo.getRefundCommission());
        siteParam.setSupplyRefundMoney(orderRefundParam.getSupplyRefundMoney());
        siteParam.setRefundDeliveryMoney(calculateVo.getRefundDeliveryMoney());
        if(SettlementEnum.SETTLED.getCode().equals(orderRefundParam.getSettlementStatus())){
            // 订单应退
            coreSitePlatService.siteIncomeOrderRefund(siteParam);
        }
        // 退还平台优惠券补贴
        coreSitePlatService.siteIncomePlatRefundCoupon(siteParam);

        // 平台统计
        SysDataAddParam sysStatParam = new SysDataAddParam();
        sysStatParam.setSiteId(RequestUtils.defaultSiteId());
        sysStatParam.setMallRefundCouponMoney(calculateVo.getMallRefundCouponMoney());
        sysStatParam.setOrderRefundMoney(calculateVo.getOrderNeedRefundMoney());
        sysStatParam.setOrderActualRefundMoney(orderRefundParam.getMoney());
        sysStatParam.setShopRefundSeparateMoney(calculateVo.getShopRefundSeparateMoney());
        sysStatParam.setShopRefundCommission(calculateVo.getRefundCommission());
        sysStatParam.setRefundDeliveryMoney(orderRefundParam.getRefundDeliveryMoney());
        sysStatParam.setSupplyRefundMoney(orderRefundParam.getSupplyRefundMoney());
        coreStatService.addStat(sysStatParam);

        // 店铺统计
        SysDataAddParam shopStatParam = new SysDataAddParam();
        shopStatParam.setSiteId(orderRefundParam.getSiteId());
        shopStatParam.setMallRefundCouponMoney(calculateVo.getMallRefundCouponMoney());
        shopStatParam.setOrderRefundMoney(calculateVo.getOrderNeedRefundMoney());
        shopStatParam.setOrderActualRefundMoney(orderRefundParam.getMoney());
        shopStatParam.setShopRefundCommission(calculateVo.getRefundCommission());
        shopStatParam.setMallRefundServiceMoney(calculateVo.getMallRefundMoney());
        shopStatParam.setSupplyRefundMoney(orderRefundParam.getSupplyRefundMoney());
        coreStatService.addStat(shopStatParam);
    }
}
