package com.niu.core.service.admin.stat.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class StatInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**今日订单数**/
    private OrderStat orderStat;
    /**今日销售额**/
    private SaleStat saleStat;
    /**今日退款额**/
    private RefundStat refundStat;
    /**今日新增会员数**/
    private MemberStat memberStat;
    /**今日申请店铺数**/
    private SiteStat siteStat;
    /**商城订单**/
    private MyOrderStat myOrderStat;
    /**提现**/
    private CashOutStat cashOutStat;
    /**商品**/
    private GoodsStat goodsStat;

    @Data
    public static class GoodsStat{
        private Long saleGoodsNum;
        private Long auditGoodsNum;
    }
    @Data
    public static class CashOutStat{
        private Long memberCount;
        private Long siteCount;
    }
    @Data
    public static class MyOrderStat{
        private Long waitPayOrder;
        private Long waitDeliveryOrder;
        private Long waitTakeOrder;
        private Long refundOrder;
    }
    @Data
    public static class SiteStat{
        private Long todaySiteCount;
        private Long yesterdaySiteCount;
        private Long totalSiteCount;

    }
    @Data
    public static class MemberStat{
        private Long todayMemberCount;

        private Long yesterdayMemberCount;

        private Long totalMemberCount;

    }
    @Data
    public static class RefundStat{
        private String todayRefundMoney;

        private String yesterdayRefundMoney;

        private String totalRefundMoney ;

    }
    @Data
    public static class SaleStat{
        private String todaySaleMoney;

        private String yesterdaySaleMoney;

        private String totalSaleMoney;

    }
    @Data
    public static class OrderStat{
        private Long todayOrderCount;

        private Long totalOrderCount;

        private Long yesterdayOrderCount;

    }

}
