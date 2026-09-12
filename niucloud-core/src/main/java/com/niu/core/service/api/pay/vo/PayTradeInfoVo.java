package com.niu.core.service.api.pay.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 帮付/支付交易商品信息（对齐 PHP PayTradeInfo 事件返回结构）
 */
@Data
public class PayTradeInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单项列表 */
    private List<Item> itemList = new ArrayList<>();

    /** 订单项总计文案，如：共3种商品 */
    private String itemTotal;

    /** 订单列表跳转路径 */
    private String detailUrl;

    @Data
    public static class Item implements Serializable {
        private static final long serialVersionUID = 1L;

        private List<ChildItem> childList = new ArrayList<>();
        private Site site;
    }

    @Data
    public static class ChildItem implements Serializable {
        private static final long serialVersionUID = 1L;

        private String itemImage;
        private String itemName;
        private String itemSubName;
        private BigDecimal itemPrice;
        private Integer itemNum;
    }

    @Data
    public static class Site implements Serializable {
        private static final long serialVersionUID = 1L;

        private Integer siteId;
        private String siteName;
    }
}
