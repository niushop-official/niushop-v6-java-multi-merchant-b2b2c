package com.niu.core.service.api.article.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;// 文章id
    private Integer siteId;// 站点id
    private Integer categoryId;// 文章分类id
    private String title;// 文章标题
    private String intro;// 文章简介
    private String summary;// 文章摘要
    private String image;// 文章图片
    private String author;// 文章作者
    private String content;// 文章内容
    private Integer visit;// 实际浏览量
    private Integer visitVirtual;// 初始浏览量
    private Integer isShow;// 文章是否显示
    private Integer sort;// 排序
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;// 创建时间
    private String categoryName;// 栏目名称
    private String articleUrl; // 文章链接
    private String imageThumbSmall;// 缩略图小图
}
