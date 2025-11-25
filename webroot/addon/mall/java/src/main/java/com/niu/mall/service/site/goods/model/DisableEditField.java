package com.niu.mall.service.site.goods.model;

import java.util.Arrays;
import java.util.List;

public class DisableEditField {

    public static final List<String> DISABLE_EDIT_FIELD = Arrays.asList(
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
    );

    /**
     * 允许同步的字段
     */
    public static final List<String> ENABLE_SYNC_FIELD = Arrays.asList(
            "goods_name",
            "sub_title",
            "goods_cover",
            "goods_image",
            "goods_video",
            "goods_desc"
    );
}
