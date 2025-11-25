package com.niu.core.service.api.article.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleCategroyParam implements Serializable {

    private Integer categoryId;// 文章分类id
    private Integer siteId;// 站点id

    @NotNull(message = "name参数缺失")
    @NotEmpty(message = "参数名称不能为空")
    private String name;// 栏目名称

    private Integer sort;// 排序
    private Integer isShow;// 是否显示
    private Long createTime;// 创建时间
    private Long updateTime;// 修改时间
}
