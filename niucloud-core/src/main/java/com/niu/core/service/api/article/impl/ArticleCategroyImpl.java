package com.niu.core.service.api.article.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.article.ArticleCategory;
import com.niu.core.mapper.article.ArticleCategoryMapper;
import com.niu.core.service.api.article.IArticleCategroyService;
import com.niu.core.service.api.article.param.ArticleCategroyParam;
import com.niu.core.service.api.article.param.ArticleCategroySearchParam;
import com.niu.core.service.api.article.vo.ArticleCategroyInfoVo;
import com.niu.core.service.api.article.vo.ArticleCategroyListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

/**
 * 文章栏目
 */
@Service
public class ArticleCategroyImpl implements IArticleCategroyService {

    @Resource
    ArticleCategoryMapper articleCategoryMapper;

    /**
     *  文章栏目分页
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return
     */
    @Override
    public PageResult<ArticleCategroyListVo> page(PageParam pageParam, ArticleCategroySearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<ArticleCategory> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("atc");
        queryWrapper.select("atc.category_id, atc.name, atc.site_id,atc.is_show,atc.sort,atc.create_time,COUNT( at.id ) AS articleNum");
        queryWrapper.leftJoin("?_article at ON at.`category_id` = atc.category_id".replace("?_", GlobalConfig.tablePrefix));

        if (ObjectUtil.isNotEmpty(searchParam.getName())) {
            queryWrapper.like("atc.name", searchParam.getName());
        }

        queryWrapper.eq("atc.site_id", RequestUtils.defaultSiteId());
        queryWrapper.groupBy("atc.category_id");
        queryWrapper.orderByAsc("atc.sort");

        IPage<ArticleCategroyListVo> iPage = articleCategoryMapper.selectJoinPage(new Page<>(page, limit), ArticleCategroyListVo.class, queryWrapper);
        List<ArticleCategroyListVo> list = new LinkedList<>();
        for (ArticleCategroyListVo item : iPage.getRecords()) {
            ArticleCategroyListVo vo = new ArticleCategroyListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);

    }


    /**
     *  文章栏目列表
     * @param searchParam 搜索参数
     * @return
     */
    @Override
    public List<ArticleCategroyListVo> list(ArticleCategroySearchParam searchParam) {
        QueryWrapper<ArticleCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        queryWrapper.eq("is_show", 1);
        queryWrapper.orderByAsc("sort");
        List<ArticleCategory> list = articleCategoryMapper.selectList(queryWrapper);
        List<ArticleCategroyListVo> listVo = new LinkedList<>();
        for (ArticleCategory item : list) {
            ArticleCategroyListVo vo = new ArticleCategroyListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;
    }

    /**
     *  文章栏目详情
     * @param id 文章栏目ID
     * @return
     */
    @Override
    public ArticleCategroyInfoVo info(Integer id) {
        ArticleCategory model = articleCategoryMapper.selectOne(
                new QueryWrapper<ArticleCategory>()
                        .eq("category_id", id)
                        .eq("site_id", RequestUtils.defaultSiteId()));

        Assert.notNull(model, "文章栏目不存在");
        ArticleCategroyInfoVo vo = new ArticleCategroyInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }
    
}
