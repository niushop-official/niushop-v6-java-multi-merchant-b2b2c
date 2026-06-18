package com.niu.mall.service.admin.marketing.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class FullReductionSearchParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String title;

    private String status;

    private String memberType;

    private String goodsType;

    private String type;

    @JsonDeserialize(using = BeanJsonSerializer.UnixMillisDeserializer.class)
    private Long startTime;

    @JsonDeserialize(using = BeanJsonSerializer.UnixMillisDeserializer.class)
    private Long endTime;

}