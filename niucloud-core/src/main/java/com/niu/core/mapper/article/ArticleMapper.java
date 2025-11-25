package com.niu.core.mapper.article;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.article.Article;
import com.niu.core.entity.article.ArticleCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleMapper extends MPJBaseMapper<Article> {
    
    /**
     * 根据分类ID查询文章分类信息
     * 对应PHP的 with('articleCategory')
     * 
     * @param categoryId 分类ID
     * @return 文章分类对象
     */
    @Select("SELECT category_id, name, sort, is_show FROM ns_article_category WHERE category_id = #{categoryId}")
    ArticleCategory selectCategoryById(Integer categoryId);
}
