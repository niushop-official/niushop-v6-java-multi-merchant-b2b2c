package com.niu.core.service.admin.article;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.article.param.ArticeSearchParam;
import com.niu.core.service.admin.article.param.ArticleParam;
import com.niu.core.service.admin.article.vo.ArticleInfoVo;
import com.niu.core.service.admin.article.vo.ArticleListVo;
import com.niu.core.service.admin.article.vo.ArticlePageVo;

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
     * @return PageResult<ShopGoodsAttrListVo>
     */
    List<ArticleListVo> list(ArticeSearchParam searchParam);

    /**
     * 文章详情
     *
     * @param id 主键ID
     * @return WebAdvInfoVo
     */
    ArticleInfoVo info(Integer id);

    /**
     * 文章添加
     *
     * @param addParam 添加参数
     */
    void add(ArticleParam addParam);

    /**
     * 文章编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ArticleParam editParam);

    /**
     * 文章删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 文章选择
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ArticleListVo>
     */
    PageResult<ArticlePageVo> pageList(PageParam pageParam, ArticeSearchParam searchParam);
}
