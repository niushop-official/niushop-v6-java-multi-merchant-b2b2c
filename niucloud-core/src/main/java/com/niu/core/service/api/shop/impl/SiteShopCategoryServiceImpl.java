package com.niu.core.service.api.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopCategory;
import com.niu.core.mapper.shop.SiteShopCategoryMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.service.api.shop.ISiteShopCategoryService;
import com.niu.core.service.api.shop.param.SiteShopCategoryParam;
import com.niu.core.service.api.shop.param.SiteShopCategorySearchParam;
import com.niu.core.service.api.shop.vo.SiteShopCategoryInfoVo;
import com.niu.core.service.api.shop.vo.SiteShopCategoryListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;


/**
 * 站点分类实现
 */
@Service
public class SiteShopCategoryServiceImpl implements ISiteShopCategoryService {
        
    @Resource
    SiteShopCategoryMapper siteShopCategoryMapper;

    @Resource
    SiteShopMapper siteShopMapper;


    @Override
    public PageResult<SiteShopCategoryListVo> page(PageParam pageParam, SiteShopCategorySearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<SiteShopCategory> queryWrapper = new QueryWrapper<>();

        if (ObjectUtil.isNotEmpty(searchParam.getCategoryName())) {
        queryWrapper.eq("category_name", searchParam.getCategoryName());
        }

        queryWrapper.orderByAsc("sort");

        IPage<SiteShopCategory> iPage = siteShopCategoryMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SiteShopCategoryListVo> list = new LinkedList<>();
        for (SiteShopCategory item : iPage.getRecords()) {
            SiteShopCategoryListVo vo = new SiteShopCategoryListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<SiteShopCategoryListVo> list(SiteShopCategorySearchParam searchParam) {
        QueryWrapper<SiteShopCategory> queryWrapper = new QueryWrapper<>();

        if (ObjectUtil.isNotEmpty(searchParam.getCategoryName())) {
            queryWrapper.eq("category_name", searchParam.getCategoryName());
        }

        queryWrapper.orderByAsc("sort");

        List<SiteShopCategory> list = siteShopCategoryMapper.selectList(queryWrapper);
        List<SiteShopCategoryListVo> listVo = new LinkedList<>();
        for (SiteShopCategory item : list) {
            SiteShopCategoryListVo vo = new SiteShopCategoryListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;
    }

    @Override
    public List<SiteShopCategoryListVo> all() {

        List<SiteShopCategory> list = siteShopCategoryMapper.selectList(new QueryWrapper<SiteShopCategory>().orderByAsc("sort"));
        List<SiteShopCategoryListVo> listVo = new LinkedList<>();
        for (SiteShopCategory item : list) {
            SiteShopCategoryListVo vo = new SiteShopCategoryListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;
    }

    @Override
    public SiteShopCategoryInfoVo info(Integer id) {
        SiteShopCategory model = siteShopCategoryMapper.selectOne(
        new QueryWrapper<SiteShopCategory>()
        .eq("category_id", id));


        Assert.notNull(model, "站点分类不存在");

        SiteShopCategoryInfoVo vo = new SiteShopCategoryInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    @Override
    public void add(SiteShopCategoryParam addParam) {

        SiteShopCategory model = new SiteShopCategory();

        model.setCategoryName(addParam.getCategoryName());
        model.setCommissionRate(addParam.getCommissionRate());
        model.setImage(addParam.getImage());
        model.setSort(addParam.getSort());


        siteShopCategoryMapper.insert(model);
    }

    @Override
    public void edit(Integer id, SiteShopCategoryParam editParam) {

        SiteShopCategory model = siteShopCategoryMapper.selectOne(
                new QueryWrapper<SiteShopCategory>()
                        .eq("category_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "站点分类不存在");
        model.setCategoryName(editParam.getCategoryName());
        model.setCommissionRate(editParam.getCommissionRate());
        model.setImage(editParam.getImage());
        model.setSort(editParam.getSort());
        siteShopMapper.update(null, new UpdateWrapper<SiteShop>().eq("category_id", id).set("commission_rate", editParam.getCommissionRate()));
        siteShopCategoryMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        Long count = siteShopMapper.selectCount(new QueryWrapper<SiteShop>().eq("category_id", id));
        if(count > 0) throw new AdminException("分类正在使用中，不能删除!");
        siteShopCategoryMapper.delete(new QueryWrapper<SiteShopCategory>().eq("category_id", id));
    }

}
