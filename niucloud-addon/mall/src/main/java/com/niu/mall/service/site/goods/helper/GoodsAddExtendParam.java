package com.niu.mall.service.site.goods.helper;

import com.niu.mall.service.site.goods.param.MallGoodsParam;
import com.niu.mall.service.site.goods.param.MallVirtualGoodsParam;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsAddExtendParam implements Serializable {

    private Integer goodsStatus;
    private boolean verifyOpen;

    private Integer supplierId;
    private Integer timerOnStatus;
    private Long timerOnTime;
    private Integer timerOffStatus;
    private Long timerOffTime;
    private List<String> supportPayType;
    private Integer isEnableRefund;
    private String pointDeductType;
    private BigDecimal pointDeductRate;
    private Integer[] recommendGoodsIds;

    private Integer sourceType;
    private String sourceGoodsId;
    private String sourceSkuId;
    private String sourceFlatform;
    private String sourceParty;

    public static GoodsAddExtendParam from(MallGoodsParam param, boolean verifyOpen) {
        GoodsAddExtendParam extend = new GoodsAddExtendParam();
        extend.setGoodsStatus(param.getStatus());
        extend.setVerifyOpen(verifyOpen);
        extend.setSupplierId(param.getSupplierId());
        extend.setTimerOnStatus(param.getTimerOnStatus());
        extend.setTimerOnTime(param.getTimerOnTime());
        extend.setTimerOffStatus(param.getTimerOffStatus());
        extend.setTimerOffTime(param.getTimerOffTime());
        extend.setSupportPayType(param.getSupportPayType());
        extend.setIsEnableRefund(param.getIsEnableRefund());
        extend.setPointDeductType(param.getPointDeductType());
        extend.setPointDeductRate(param.getPointDeductRate());
        extend.setRecommendGoodsIds(param.getRecommendGoodsIds());
        extend.setSourceType(param.getSourceType());
        extend.setSourceGoodsId(param.getSourceGoodsId());
        extend.setSourceSkuId(param.getSourceSkuId());
        extend.setSourceFlatform(param.getSourceFlatform());
        extend.setSourceParty(param.getSourceParty());
        return extend;
    }

    public static GoodsAddExtendParam from(MallVirtualGoodsParam param, boolean verifyOpen) {
        GoodsAddExtendParam extend = new GoodsAddExtendParam();
        extend.setGoodsStatus(param.getStatus());
        extend.setVerifyOpen(verifyOpen);
        extend.setSupplierId(param.getSupplierId());
        extend.setTimerOnStatus(param.getTimerOnStatus());
        extend.setTimerOnTime(param.getTimerOnTime());
        extend.setTimerOffStatus(param.getTimerOffStatus());
        extend.setTimerOffTime(param.getTimerOffTime());
        extend.setSupportPayType(param.getSupportPayType());
        extend.setIsEnableRefund(param.getIsEnableRefund());
        extend.setPointDeductType(param.getPointDeductType());
        extend.setPointDeductRate(param.getPointDeductRate());
        extend.setRecommendGoodsIds(param.getRecommendGoodsIds());
        extend.setSourceType(param.getSourceType());
        extend.setSourceGoodsId(param.getSourceGoodsId());
        extend.setSourceSkuId(param.getSourceSkuId());
        extend.setSourceFlatform(param.getSourceFlatform());
        extend.setSourceParty(param.getSourceParty());
        return extend;
    }

    public GoodsAddSourceParam toGoodsSourceParam() {
        return GoodsAddSourceParam.of(sourceType, sourceGoodsId, sourceSkuId, sourceFlatform, sourceParty);
    }
}
