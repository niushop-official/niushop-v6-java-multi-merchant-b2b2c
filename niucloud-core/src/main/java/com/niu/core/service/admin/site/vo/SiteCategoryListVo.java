package com.niu.core.service.admin.site.vo;



import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 行业类目列表视图
 */
@Data
public class SiteCategoryListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer categoryId; // 行业类目ID
    private String name; // 行业名称
    private Integer level; // 层级：1=一级 2=二级
    private Integer pid; // 父级ID，一级为0
    private Integer sort; // 排序
    private Integer status; // 状态：1=启用 0=禁用

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    @JsonDeserialize(using = BeanJsonSerializer.UnixMillisDeserializer.class)
    private Long createTime; // 创建时间

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    @JsonDeserialize(using = BeanJsonSerializer.UnixMillisDeserializer.class)
    private Long updateTime; // 更新时间

    private String addon; // 归属应用
    private BigDecimal serviceRate; // 服务费比率
    private Integer groupId; // 套餐id
    private String bannerList; // 轮播图（JSON格式）

    private List<SiteCategoryListVo> childList;
    private Boolean hasChildren;
}