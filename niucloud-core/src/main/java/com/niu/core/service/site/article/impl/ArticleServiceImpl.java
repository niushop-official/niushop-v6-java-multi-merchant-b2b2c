package com.niu.core.service.site.article.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.article.Article;
import com.niu.core.entity.article.ArticleCategory;
import com.niu.core.mapper.article.ArticleMapper;
import com.niu.core.service.site.article.IArticleService;
import com.niu.core.service.site.article.param.ArticeSearchParam;
import com.niu.core.service.site.article.vo.ArticleInfoVo;
import com.niu.core.service.site.article.vo.ArticleListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

/**
 * 文章管理
 */
@Service
public class ArticleServiceImpl implements IArticleService {

    @Resource
    ArticleMapper articleMapper;

    /**
     * 文章管理分页
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public PageResult<ArticleListVo> page(PageParam pageParam, ArticeSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<Article> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("at");
        queryWrapper.select("at.id, atc.name as category_name, at.site_id,at.category_id,at.title,at.intro,at.summary,at.image,at.author,at.content,at.visit,at.visit_virtual,at.is_show,at.sort,at.create_time,at.update_time");
        queryWrapper.leftJoin("?_article_category atc ON atc.`category_id` = at.category_id".replace("?_", GlobalConfig.tablePrefix));
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) {
            queryWrapper.like("at.title", searchParam.getTitle());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId())) {
            queryWrapper.eq("at.category_id", searchParam.getCategoryId());
        }
        queryWrapper.eq("at.site_id", RequestUtils.defaultSiteId());
        queryWrapper.eq("at.is_show", 1);
        queryWrapper.orderByAsc("at.create_time");

        IPage<ArticleListVo> iPage = articleMapper.selectJoinPage(new Page<>(page, limit), ArticleListVo.class,  queryWrapper);
        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }


    /**
     *  文章管理详情
     * @param id 主键ID
     * @return
     */
    @Override
    public ArticleInfoVo info(Integer id) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "category_id", "site_id", "title", "intro",
                "summary", "image", "author", "content", "visit",
                "visit_virtual", "is_show", "sort", "create_time", "update_time");
        queryWrapper.eq("id", id);
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        queryWrapper.last("LIMIT 1");

        Article model = articleMapper.selectOne(queryWrapper);
        Assert.notNull(model, "文章不存在");
        ArticleInfoVo vo = new ArticleInfoVo();
        BeanUtils.copyProperties(model, vo);
        if (model.getCategoryId() != null) {
            ArticleCategory category = articleMapper.selectCategoryById(model.getCategoryId());
            if (category != null) {
                ArticleInfoVo.ArticleCategoryVo categoryVo = new ArticleInfoVo.ArticleCategoryVo();
                categoryVo.setCategoryId(category.getCategoryId());
                categoryVo.setName(category.getName());
                categoryVo.setSort(category.getSort());
                categoryVo.setIsShow(category.getIsShow());
                vo.setArticleCategory(categoryVo);
            }
        }
        if (ObjectUtil.isNotEmpty(model.getImage())) {
            vo.setImageThumbBig(model.getImage());
        }
        return vo;
    }
}
