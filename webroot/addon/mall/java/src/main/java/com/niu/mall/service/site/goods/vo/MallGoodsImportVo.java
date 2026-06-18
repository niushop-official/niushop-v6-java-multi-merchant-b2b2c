package com.niu.mall.service.site.goods.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.mall.enums.goods.GoodsImportStatusEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import lombok.Data;

@Data
public class MallGoodsImportVo {

    private String statusName;
    private String goodsTypeName;

    private Integer recordId;
    private Integer siteId;
    private Integer mainId;
    private String filePath;
    private String goodsType;
    private Integer totalNum;
    private Integer successNum;
    private Integer failNum;
    private Integer status;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    private String output;
    private String failOutput;
    private String failRemark;
    private Integer duration;

    public String getStatusName() {
        return GoodsImportStatusEnum.getNameByValue(this.getStatus());
    }

    public String getGoodsTypeName() {
        return GoodsTypeEnum.getNameByType(this.getGoodsType());
    }
}
