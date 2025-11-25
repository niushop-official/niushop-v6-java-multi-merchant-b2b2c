package com.niu.core.service.api.article;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.article.param.ArticeSearchParam;
import com.niu.core.service.api.article.param.ArticleParam;
import com.niu.core.service.api.article.vo.ArticleInfoVo;
import com.niu.core.service.api.article.vo.ArticleListVo;
import io.swagger.models.auth.In;

import java.util.List;

public interface IArticleService {

    /**
     * 文章列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ArticleListVo>
     */
    PageResult<ArticleListVo> page(PageParam pageParam, ArticeSearchParam searchParam);

    /**
     * 查询所有文章列表
     *
     * @param searchParam 搜索参数
     * @return PageResult<ArticleListVo>
     */
    List<ArticleListVo> list(ArticeSearchParam searchParam);

    /**
     * 查询所有文章列表
     *
     * @return PageResult<ArticleListVo>
     */
    List<ArticleListVo> hot(Integer limit);

    /**
     * 文章详情
     *
     * @param id 主键ID
     * @return WebAdvInfoVo
     */
    ArticleInfoVo info(Integer id);

    void intVisit(Integer id);
}
