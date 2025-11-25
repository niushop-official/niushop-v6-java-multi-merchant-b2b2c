package com.niu.core.service.api.article;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.article.param.ArticleCategroyParam;
import com.niu.core.service.api.article.param.ArticleCategroySearchParam;
import com.niu.core.service.api.article.vo.ArticleCategroyInfoVo;
import com.niu.core.service.api.article.vo.ArticleCategroyListVo;

import java.util.List;

public interface IArticleCategroyService {

    /**
     * 文章栏目列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ArticleCategroyListVo>
     */
    PageResult<ArticleCategroyListVo> page(PageParam pageParam, ArticleCategroySearchParam searchParam);

    /**
     * 查询所有文章栏目
     *
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsAttrListVo>
     */
    List<ArticleCategroyListVo> list(ArticleCategroySearchParam searchParam);

    /**
     * 文章栏目详情
     *
     * @param id 主键ID
     * @return ArticleCategroyInfoVo
     */
    ArticleCategroyInfoVo info(Integer id);

}
