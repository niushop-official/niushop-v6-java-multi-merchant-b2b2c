package com.niu.core.service.api.article.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArticeSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;// 文章标题
    private Integer categoryId;// 文章分类id
    //数量
    private Integer limit;

}
