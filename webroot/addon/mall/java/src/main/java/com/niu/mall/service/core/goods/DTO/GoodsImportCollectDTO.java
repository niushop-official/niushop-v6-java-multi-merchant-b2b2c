package com.niu.mall.service.core.goods.DTO;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GoodsImportCollectDTO {

    public GoodsImportCollectDTO(List<Map<String, Object>> rawData, Integer bizType, String goodsType) {
        this.rawData = rawData;
        this.bizType = bizType;
        this.goodsType = goodsType;
    }

    private List<Map<String, Object>> rawData;
    private Integer bizType;
    private String goodsType;
}
