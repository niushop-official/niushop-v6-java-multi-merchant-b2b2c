package com.niu.core.service.admin.article.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleCategroySearchParam implements Serializable {


    private String name;// 栏目名称
}
