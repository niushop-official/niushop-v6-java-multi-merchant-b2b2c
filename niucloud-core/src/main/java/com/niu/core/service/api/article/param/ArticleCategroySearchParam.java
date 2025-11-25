package com.niu.core.service.api.article.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleCategroySearchParam implements Serializable {


    private String name;// 栏目名称
}
