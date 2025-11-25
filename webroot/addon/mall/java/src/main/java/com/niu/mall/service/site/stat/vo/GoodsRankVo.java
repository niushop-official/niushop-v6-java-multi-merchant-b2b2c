package com.niu.mall.service.site.stat.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class GoodsRankVo {

    @JsonProperty("goods")
    private goods goods = new goods();

    @Data
    public static class goods{
        private String goodsCover;//商品图片

        private Integer goodsId;//商品ID

        private String goodsName;//商品名称
    }

    private String goodsCover;//商品图片

    private Integer goodsId;//商品ID

    private String goodsName;//商品名称

    private BigDecimal payMoney;//销售额

    private  Integer saleNum;//销售量

}
