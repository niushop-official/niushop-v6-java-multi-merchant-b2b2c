package com.niu.core.service.api.article.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
import com.niu.core.service.api.article.IArticleService;
import com.niu.core.service.api.article.param.ArticeSearchParam;
import com.niu.core.service.api.article.param.ArticleParam;
import com.niu.core.service.api.article.vo.ArticleInfoVo;
import com.niu.core.service.api.article.vo.ArticleListVo;
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
        queryWrapper.leftJoin("?_article_category atc ON atc.`category_id` = at.category_id".replace("?_", "ns_"));
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) {
            queryWrapper.like("at.title", searchParam.getTitle());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId())) {
            queryWrapper.eq("at.category_id", searchParam.getCategoryId());
        }
        queryWrapper.eq("at.is_show", 1);
        queryWrapper.eq("at.site_id", RequestUtils.siteId());
        queryWrapper.orderByAsc("at.sort");

        IPage<ArticleListVo> iPage = articleMapper.selectJoinPage(new Page<>(page, limit), ArticleListVo.class,  queryWrapper);
        List<ArticleListVo> list = new LinkedList<>();
        for (ArticleListVo item : iPage.getRecords()) {
            ArticleListVo vo = new ArticleListVo();
            BeanUtils.copyProperties(item, vo);
            String wapDomain = ObjectUtil.isNotEmpty(GlobalConfig.webDomain) ? GlobalConfig.webDomain.replace("#/$#", "") : RequestUtils.getRequestSecure() + "://" + RequestUtils.getRequestDomain();
            vo.setArticleUrl(wapDomain);
            vo.setImageThumbSmall(item.getImage());
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 文章管理列表
     * @param searchParam
     * @return
     */
    @Override
    public List<ArticleListVo> list(ArticeSearchParam searchParam) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) {
            queryWrapper.eq("title", searchParam.getTitle());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryId())) {
            queryWrapper.eq("category_id", searchParam.getCategoryId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getLimit())) {
            queryWrapper.last("limit " + searchParam.getLimit().toString());
        }
        queryWrapper.eq("is_show", 1);
        queryWrapper.orderByAsc("sort");
        List<Article> list = articleMapper.selectList(queryWrapper);
        List<ArticleListVo> listVo = new LinkedList<>();
        for (Article item : list) {
            ArticleListVo vo = new ArticleListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;
    }

    @Override
    public List<ArticleListVo> hot(Integer limit) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.siteId());

        queryWrapper.last("limit " + limit.toString());
        queryWrapper.eq("is_show", 1);
        queryWrapper.orderByAsc("sort");
        List<Article> list = articleMapper.selectList(queryWrapper);
        List<ArticleListVo> listVo = new LinkedList<>();
        for (Article item : list) {
            ArticleListVo vo = new ArticleListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;
    }

    @Override
    public ArticleInfoVo info(Integer id) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "category_id", "site_id", "title", "intro", 
                           "summary", "image", "author", "content", "visit", 
                           "visit_virtual", "is_show", "sort", "create_time", "update_time");
        queryWrapper.eq("id", id);
        queryWrapper.eq("site_id", RequestUtils.siteId());
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

    @Override
    public void intVisit(Integer id) {
        articleMapper.update(null,
            new UpdateWrapper<Article>()
                .setSql("visit = visit + 1")
                .eq("id", id));
    }

}
