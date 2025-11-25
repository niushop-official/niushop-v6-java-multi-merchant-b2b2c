package com.niu.core.service.admin.article.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleCategroyListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer categoryId;// 文章分类id
    private Integer siteId;// 站点id
    private String name;// 栏目名称
    private Integer sort;// 排序
    private Integer isShow;// 是否显示
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;// 创建时间
    private Integer articleNum;// 文章数量

}
