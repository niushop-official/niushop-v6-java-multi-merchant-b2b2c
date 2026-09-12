package com.niu.mall.service.site.goods.param;

import cn.hutool.json.JSONObject;
import lombok.Data;
import jakarta.validation.constraints.NotNull;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 编辑商品规格列表付费会员价参数
 */
@Data
public class EditGoodsListVipCardPriceParam {
    @NotNull(message = "goods_id不能为空")
    private Integer goodsId;

    @NotNull(message = "vip_card_discount参数缺失")
    private String vipCardDiscount;

    @NotNull(message = "sku_list参数缺失")
    private List<Sku> skuList;

    @Data
    public static class Sku {
        @NotNull(message = "sku_id参数缺失")
        private Integer skuId;

        // 指定会员价，格式如 {"level_1":"10.00","level_2":"8.00"}
        private Map<String, String> svipPrice;

        // 指定会员折扣，格式如 {"card_1":"8","card_2":"5"}
        private Map<String, String> svipDiscount;
    }
}