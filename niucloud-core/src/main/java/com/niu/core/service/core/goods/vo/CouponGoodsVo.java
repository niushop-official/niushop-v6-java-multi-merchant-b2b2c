package com.niu.core.service.core.goods.vo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.niu.core.service.api.marketing.vo.MallCouponListVo;
import lombok.Data;

import java.io.Serializable;

/**
 * GoodsConfigVo视图
 */
@Data
public class CouponGoodsVo{

    private Integer siteId;  // 站点id
    private Integer couponId;  // 优惠券id
    private Integer goodsId;
    private String goodsCover;
    private Integer shopSiteId;
    @JsonProperty("goodsSku")
    private goodsSkuInfo goodsSku;
    private ShopInfo shop;

    @Data
    public static class goodsSkuInfo {
        private Integer goodsId;
    }

    @Data
    public static class ShopInfo {
        private Integer siteId;
    }
}
