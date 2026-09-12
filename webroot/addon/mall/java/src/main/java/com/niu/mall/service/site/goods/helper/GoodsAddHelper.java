package com.niu.mall.service.site.goods.helper;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.core.enums.goods.GoodsAuditStatusEnum;
import com.niu.mall.enums.goods.GoodsDictEnum;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品新增共用字段写入（对齐 PHP GoodsService / VirtualGoodsService add）
 */
public final class GoodsAddHelper {

    private GoodsAddHelper() {
    }

    public static Integer resolveStatusForTimer(GoodsAddExtendParam extend) {
        if (extend.isVerifyOpen()) {
            return GoodsAuditStatusEnum.WAIT_AUDIT.getStatus();
        }
        return ObjectUtil.defaultIfNull(extend.getGoodsStatus(), 0);
    }

    public static void applyAddExtendFields(MallGoods model, GoodsAddExtendParam extend) {
        Integer statusForTimer = resolveStatusForTimer(extend);
        applyTimerFields(model, extend, statusForTimer);

        model.setSupplierId(ObjectUtil.defaultIfNull(extend.getSupplierId(), 0));
        model.setSupportPayType(toJsonStringArray(extend.getSupportPayType()));
        model.setRecommendGoodsIds(toRecommendGoodsIds(extend.getRecommendGoodsIds()));
        model.setIsEnableRefund(ObjectUtil.defaultIfNull(extend.getIsEnableRefund(), 1));
        model.setPointDeductType(ObjectUtil.defaultIfEmpty(extend.getPointDeductType(), GoodsDictEnum.SITE.getValue()));
        model.setPointDeductRate(ObjectUtil.defaultIfNull(extend.getPointDeductRate(), BigDecimal.ZERO));
        model.setSourceType(ObjectUtil.defaultIfNull(extend.getSourceType(), 0));
        model.setSourceGoodsId(ObjectUtil.defaultIfEmpty(extend.getSourceGoodsId(), ""));
        model.setSourceFlatform(ObjectUtil.defaultIfEmpty(extend.getSourceFlatform(), ""));
        model.setSourceParty(ObjectUtil.defaultIfEmpty(extend.getSourceParty(), ""));
        model.setApplyTime(extend.isVerifyOpen() ? System.currentTimeMillis() / 1000 : 0L);
    }

    public static void applyTimerFields(MallGoods model, GoodsAddExtendParam extend, Integer statusForTimer) {
        model.setTimerOnStatus(0);
        model.setTimerOnTime(0);
        model.setTimerOffStatus(0);
        model.setTimerOffTime(0);

        if (Integer.valueOf(0).equals(statusForTimer)
                && Integer.valueOf(1).equals(extend.getTimerOnStatus())
                && ObjectUtil.isNotEmpty(extend.getTimerOnTime())) {
            model.setTimerOnStatus(extend.getTimerOnStatus());
            model.setTimerOnTime(toSecondTimestamp(extend.getTimerOnTime()));
        }
        if (Integer.valueOf(1).equals(statusForTimer)
                && Integer.valueOf(1).equals(extend.getTimerOffStatus())
                && ObjectUtil.isNotEmpty(extend.getTimerOffTime())) {
            model.setTimerOffStatus(extend.getTimerOffStatus());
            model.setTimerOffTime(toSecondTimestamp(extend.getTimerOffTime()));
        }
    }

    public static void applySkuSourceFields(MallGoodsSku sku, GoodsAddSourceParam goodsSource, GoodsAddSourceParam skuSource) {
        sku.setSourceType(ObjectUtil.defaultIfNull(goodsSource.getSourceType(), 0));
        if (skuSource != null) {
            sku.setSourceGoodsId(ObjectUtil.defaultIfEmpty(skuSource.getSourceGoodsId(), ""));
            sku.setSourceSkuId(ObjectUtil.defaultIfEmpty(skuSource.getSourceSkuId(), ""));
            sku.setSourceFlatform(ObjectUtil.defaultIfEmpty(skuSource.getSourceFlatform(), ""));
            sku.setSourceParty(ObjectUtil.defaultIfEmpty(skuSource.getSourceParty(), ""));
            return;
        }
        sku.setSourceGoodsId(ObjectUtil.defaultIfEmpty(goodsSource.getSourceGoodsId(), ""));
        sku.setSourceSkuId(ObjectUtil.defaultIfEmpty(goodsSource.getSourceSkuId(), ""));
        sku.setSourceFlatform(ObjectUtil.defaultIfEmpty(goodsSource.getSourceFlatform(), ""));
        sku.setSourceParty(ObjectUtil.defaultIfEmpty(goodsSource.getSourceParty(), ""));
    }

    private static Integer toSecondTimestamp(Long timestamp) {
        if (timestamp == null) {
            return 0;
        }
        return timestamp > 1_000_000_000_000L ? (int) (timestamp / 1000) : timestamp.intValue();
    }

    private static String toJsonStringArray(List<String> values) {
        if (ObjectUtil.isEmpty(values)) {
            return "[]";
        }
        return JSONUtil.parseArray(values.stream().map(String::valueOf).collect(Collectors.toList())).toString();
    }

    private static String toRecommendGoodsIds(Integer[] recommendGoodsIds) {
        if (ObjectUtil.isEmpty(recommendGoodsIds)) {
            return "[]";
        }
        String[] strArr = Arrays.stream(recommendGoodsIds).map(String::valueOf).toArray(String[]::new);
        return JSONUtil.parseArray(strArr).toString();
    }

    /**
     * 编码 SKU 会员价（member_discount=fixed_price 时写入 JSON，否则清空）
     */
    public static String encodeMemberPrice(String memberDiscount, JSONObject memberPrice) {
        if (!"fixed_price".equals(memberDiscount) || memberPrice == null || memberPrice.isEmpty()) {
            return "";
        }
        return JSONUtil.toJsonStr(memberPrice);
    }
}
