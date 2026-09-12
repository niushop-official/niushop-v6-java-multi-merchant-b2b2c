package com.niu.mall.service.site.goods.helper;

import lombok.Data;

import java.io.Serializable;

@Data
public class GoodsAddSourceParam implements Serializable {

    private Integer sourceType;
    private String sourceGoodsId;
    private String sourceSkuId;
    private String sourceFlatform;
    private String sourceParty;

    public static GoodsAddSourceParam of(Integer sourceType, String sourceGoodsId, String sourceSkuId,
                                         String sourceFlatform, String sourceParty) {
        GoodsAddSourceParam param = new GoodsAddSourceParam();
        param.setSourceType(sourceType);
        param.setSourceGoodsId(sourceGoodsId);
        param.setSourceSkuId(sourceSkuId);
        param.setSourceFlatform(sourceFlatform);
        param.setSourceParty(sourceParty);
        return param;
    }
}
