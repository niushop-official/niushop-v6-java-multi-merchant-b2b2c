package com.niu.core.entity.article;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class ArticleCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value="category_id", type= IdType.AUTO)
    private Integer categoryId;// 文章分类id
    private Integer siteId;// 站点id
    private  String name;// 分类名称
    private Integer sort;// 排序
    private Integer isShow;// 是否显示
    private Long createTime;// 创建时间
    private Long updateTime;// 修改时间

}
