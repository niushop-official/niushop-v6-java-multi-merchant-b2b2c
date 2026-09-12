package com.niu.mall.service.admin.goods.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 编辑商品规格列表付费会员价参数
 */
@Data
public class EditGoodsListVipCardPriceParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "goods_id参数缺失")
    private Integer goodsId;

    @NotNull(message = "vip_card_discount参数缺失")
    private String vipCardDiscount;

    @NotNull(message = "sku_list参数缺失")
    private List<Sku> skuList;

    @Data
    public static class Sku {
        @NotNull(message = "sku_id参数缺失")
        private Integer skuId;

        private JSONObject svipPrice;

        private JSONObject svipDiscount;
    }
}
