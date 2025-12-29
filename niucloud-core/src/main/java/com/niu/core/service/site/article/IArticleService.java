package com.niu.core.service.site.article;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.article.param.ArticeSearchParam;
import com.niu.core.service.site.article.vo.ArticleInfoVo;
import com.niu.core.service.site.article.vo.ArticleListVo;

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
     * 文章详情
     *
     * @param id 主键ID
     * @return WebAdvInfoVo
     */
    ArticleInfoVo info(Integer id);
}
