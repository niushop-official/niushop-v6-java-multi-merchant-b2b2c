package com.niu.core.service.core.finance;


import com.niu.core.service.core.finance.param.OrderPlatParam;
import com.niu.core.service.core.finance.param.OrderRefundParam;
import com.niu.core.service.core.finance.param.SiteOrderPlatParam;
import com.niu.core.service.core.finance.param.SiteOrderRefundParam;

/**
 * 站点订单财务计算
 */
public interface ICoreSitePlatService {

    /**
     * 商户收入（订单应收）
     * @param orderPlatParam
     */
    void siteIncomeOrderPay(SiteOrderPlatParam orderPlatParam);

    /**
     * 商户收入（订单应退）
     * @param orderRefundParam
     */
    void siteIncomeOrderRefund(SiteOrderRefundParam orderRefundParam);

    /**
     * 商户收入（平台优惠券补贴）
     * @param orderPlatParam
     */
    void siteIncomePlatCoupon(SiteOrderPlatParam orderPlatParam);

    /**
     * 商户收入（退还平台优惠券补贴）
     * @param orderRefundParam
     */
    void siteIncomePlatRefundCoupon(SiteOrderRefundParam orderRefundParam);

    /**
     * 商户支出（支付手续费）
     * @param orderPlatParam
     */
    void siteOutlayService(SiteOrderPlatParam orderPlatParam);

    /**
     * 商户支出（退还手续费）
     * @param orderRefundParam
     */
    void siteOutlayRefundService(SiteOrderRefundParam orderRefundParam);

    /**
     * 商户支出（支付佣金）
     * @param orderPlatParam
     */
    void siteOutlayCommission(SiteOrderPlatParam orderPlatParam);

    /**
     * 商户支出（退还佣金）
     * @param orderRefundParam
     */
    void siteOutlayRefundCommission(SiteOrderRefundParam orderRefundParam);


    /**
     * 商户支出（支付供货商货款）
     * @param orderPlatParam
     */
    void siteOutlaySupplyMoney(SiteOrderPlatParam orderPlatParam);

    /**
     * 商户支出（退还供货商货款）
     * @param orderRefundParam
     */
    void siteOutlayRefundSupplyMoney(SiteOrderRefundParam orderRefundParam);


}
