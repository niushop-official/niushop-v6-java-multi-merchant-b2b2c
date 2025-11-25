package com.niu.core.service.admin.stat.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
@Data
public class StatNewsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;// 文章id

    private String title;// 文章标题

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;// 创建时间

}
