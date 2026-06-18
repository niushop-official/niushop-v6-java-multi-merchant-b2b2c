package com.niu.mall.service.site.goods.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.mall.enums.goods.GoodsImportStatusEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import lombok.Data;

@Data
public class MallGoodsImportRowVo {
    private Integer id;
    private Integer recordId;
    private String goodsId;
    private String skuId;
    private String goodsType;
    private String data;
    private Integer status;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    private Integer relateGoodsId;
    private Integer relateSkuId;

    private String statusName;
    private String goodsTypeName;

    public String getStatusName() {
        return GoodsImportStatusEnum.getNameByValue(this.getStatus());
    }

    public String getGoodsTypeName() {
        return GoodsTypeEnum.getNameByType(this.getGoodsType());
    }

}
