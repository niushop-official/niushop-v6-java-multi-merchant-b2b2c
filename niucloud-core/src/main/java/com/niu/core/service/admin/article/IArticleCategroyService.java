package com.niu.core.service.admin.article;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.article.param.ArticleCategroyParam;
import com.niu.core.service.admin.article.param.ArticleCategroySearchParam;
import com.niu.core.service.admin.article.vo.ArticleCategroyInfoVo;
import com.niu.core.service.admin.article.vo.ArticleCategroyListVo;

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

    /**
     * 文章栏目添加
     *
     * @param addParam 添加参数
     */
    void add(ArticleCategroyParam addParam);

    /**
     * 文章栏目编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, ArticleCategroyParam editParam);

    /**
     * 文章栏目删除
     *
     * @param id 主键ID
     */
    void del(Integer id);
}
