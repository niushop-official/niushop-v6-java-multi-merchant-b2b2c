package com.niu.core.service.admin.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteO2o;
import com.niu.core.entity.shop.SiteSupply;
import com.niu.core.entity.site.SiteApply;
import com.niu.core.entity.site.SiteCategory;
import com.niu.core.mapper.shop.SiteO2oMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.shop.SiteSupplyMapper;
import com.niu.core.mapper.site.SiteApplyMapper;
import com.niu.core.mapper.site.SiteCategoryMapper;
import com.niu.core.service.admin.shop.ISiteShopCategoryService;
import com.niu.core.service.admin.shop.param.SiteShopCategoryParam;
import com.niu.core.service.admin.shop.param.SiteShopCategorySearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopCategoryInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopCategoryListVo;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

import java.util.LinkedList;
import java.util.List;


/**
 * 站点分类实现（兼容层，底层已迁移至 site_category）
 */
@Service
public class SiteShopCategoryServiceImpl implements ISiteShopCategoryService {

    @Resource
    SiteCategoryMapper siteCategoryMapper;

    @Resource
    SiteShopMapper siteShopMapper;

    @Resource
    SiteSupplyMapper siteSupplyMapper;

    @Resource
    SiteO2oMapper siteO2oMapper;

    @Resource
    SiteApplyMapper siteApplyMapper;

    private SiteShopCategoryListVo toListVo(SiteCategory category) {
        SiteShopCategoryListVo vo = new SiteShopCategoryListVo();
        vo.setCategoryId(category.getCategoryId());
        vo.setCategoryName(category.getName());
        vo.setCommissionRate(category.getServiceRate());
        vo.setSort(category.getSort());
        vo.setCreateTime(category.getCreateTime());
        return vo;
    }

    private SiteShopCategoryInfoVo toInfoVo(SiteCategory category) {
        SiteShopCategoryInfoVo vo = new SiteShopCategoryInfoVo();
        vo.setCategoryId(category.getCategoryId());
        vo.setCategoryName(category.getName());
        vo.setCommissionRate(category.getServiceRate());
        vo.setSort(category.getSort());
        vo.setCreateTime(category.getCreateTime());
        vo.setUpdateTime(category.getUpdateTime());
        return vo;
    }

    private QueryWrapper<SiteCategory> buildQueryWrapper(SiteShopCategorySearchParam searchParam) {
        QueryWrapper<SiteCategory> queryWrapper = new QueryWrapper<>();
        if (searchParam != null && ObjectUtil.isNotEmpty(searchParam.getCategoryName())) {
            queryWrapper.eq("name", searchParam.getCategoryName());
        }
        queryWrapper.orderByAsc("sort");
        return queryWrapper;
    }

    @Override
    public PageResult<SiteShopCategoryListVo> page(PageParam pageParam, SiteShopCategorySearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        IPage<SiteCategory> iPage = siteCategoryMapper.selectPage(new Page<>(page, limit), buildQueryWrapper(searchParam));
        List<SiteShopCategoryListVo> list = new LinkedList<>();
        for (SiteCategory item : iPage.getRecords()) {
            list.add(toListVo(item));
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<SiteShopCategoryListVo> list(SiteShopCategorySearchParam searchParam) {
        List<SiteShopCategoryListVo> listVo = new LinkedList<>();
        for (SiteCategory item : siteCategoryMapper.selectList(buildQueryWrapper(searchParam))) {
            listVo.add(toListVo(item));
        }
        return listVo;
    }

    @Override
    public List<SiteShopCategoryListVo> all() {
        List<SiteShopCategoryListVo> listVo = new LinkedList<>();
        for (SiteCategory item : siteCategoryMapper.selectList(new QueryWrapper<SiteCategory>().orderByAsc("sort"))) {
            listVo.add(toListVo(item));
        }
        return listVo;
    }

    @Override
    public SiteShopCategoryInfoVo info(Integer id) {
        SiteCategory model = siteCategoryMapper.selectOne(new QueryWrapper<SiteCategory>().eq("category_id", id));
        Assert.notNull(model, "站点分类不存在");
        return toInfoVo(model);
    }

    @Override
    public void add(SiteShopCategoryParam addParam) {
        SiteCategory model = new SiteCategory();
        model.setName(addParam.getCategoryName());
        model.setServiceRate(addParam.getCommissionRate());
        model.setSort(addParam.getSort());
        model.setLevel(1);
        model.setPid(0);
        model.setStatus(1);
        model.setAddon("mall");
        model.setCreateTime(DateUtils.currTime());
        model.setUpdateTime(DateUtils.currTime());
        siteCategoryMapper.insert(model);
    }

    @Override
    public void edit(Integer id, SiteShopCategoryParam editParam) {
        SiteCategory model = siteCategoryMapper.selectOne(new QueryWrapper<SiteCategory>().eq("category_id", id));
        Assert.notNull(model, "站点分类不存在");
        model.setName(editParam.getCategoryName());
        model.setServiceRate(editParam.getCommissionRate());
        model.setSort(editParam.getSort());
        model.setUpdateTime(DateUtils.currTime());
        siteShopMapper.update(null, new UpdateWrapper<SiteShop>().eq("category_id", id).set("service_rate", editParam.getCommissionRate()));
        siteSupplyMapper.update(null, new UpdateWrapper<SiteSupply>().eq("category_id", id).set("service_rate", editParam.getCommissionRate()));
        siteO2oMapper.update(null, new UpdateWrapper<SiteO2o>().eq("category_id", id).set("service_rate", editParam.getCommissionRate()));
        siteCategoryMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        Long shopCount = siteShopMapper.selectCount(new QueryWrapper<SiteShop>().eq("category_id", id));
        Long supplyCount = siteSupplyMapper.selectCount(new QueryWrapper<SiteSupply>().eq("category_id", id));
        Long o2oCount = siteO2oMapper.selectCount(new QueryWrapper<SiteO2o>().eq("category_id", id));
        Long applyCount = siteApplyMapper.selectCount(new QueryWrapper<SiteApply>().eq("category_id", id));
        if (shopCount > 0 || supplyCount > 0 || o2oCount > 0 || applyCount > 0) {
            throw new AdminException("分类正在使用中，不能删除!");
        }
        siteCategoryMapper.delete(new QueryWrapper<SiteCategory>().eq("category_id", id));
    }

}
