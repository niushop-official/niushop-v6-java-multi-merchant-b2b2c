package com.niu.mall.enums.goods;

import lombok.Getter;

@Getter
public enum GoodsFieldEnum {
    DISABLE_EDIT_FIELD(new String[]{
            "goods_type",
            "goods_mall_category",
            "brand_id",
            "unit",
            "spec_type",
            "cost_price",
            "min_price",
            "max_price",
            "stock",
            "weight",
            "volume",
            "sku_image",
            "sku_no",
            "is_default",
            "delivery_type",
            "is_free_shipping",
            "fee_type",
            "delivery_money",
            "delivery_template_id",
            "is_limit",
            "limit_type",
            "max_buy",
            "min_buy",
            "attr_ids",
            "shop_attr_ids",
            "virtual_auto_delivery",
            "virtual_receive_type",
            "virtual_verify_type",
            "virtual_indate",
            "virtual_verify_mobile",
            "form_id"
    }),
    DISABLE_EDIT_AREA(new String[]{
            "goods_spec_format" //商品规格项
    }),
    /** 供货商开启同步时可覆盖的字段 */
    ENABLE_SYNC_FIELD(new String[]{
            "goods_name",
            "sub_title",
            "goods_cover",
            "goods_image",
            "goods_video",
            "goods_desc"
    }),
    NEED_AUDIT_FIELD(new String[]{
            "goods_name",
            "sub_title",
            "goods_type",
            "goods_cover",
            "goods_image",
            "goods_video",
            "brand_id",
            "goods_category",
            "goods_mall_category",
            "attr_ids",
            "shop_attr_ids",
            "attr_format",
            "goods_desc",

            "sku_name",
            "sku_image",
            "sku_no",
            "sku_spec_format",

            "spec_type",
            "spec_name",
            "spec_values"
    });

    private final String[] value;

    GoodsFieldEnum(String[] value) {
        this.value = value;
    }
}
