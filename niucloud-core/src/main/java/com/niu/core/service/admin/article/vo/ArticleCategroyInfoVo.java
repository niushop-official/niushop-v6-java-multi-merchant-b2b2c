package com.niu.core.service.admin.article.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleCategroyInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer categoryId;// 文章分类id
    private Integer siteId;// 站点id
    private  String name;// 分类名称
    private Integer sort;// 排序
    private Integer isShow;// 是否显示
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;// 创建时间
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;// 修改时间
}
