package com.niu.mall.service.api.goods.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

@Data
public class GoodsLabelListVo {

    private Integer labelId;

    private String labelName;

    private String styleType;

    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String colorJson;

    private String icon;

    private String memo;
}
