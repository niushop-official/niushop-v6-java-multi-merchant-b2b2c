package com.niu.core.service.admin.site.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.site.SiteCategory;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.mapper.site.SiteCategoryMapper;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.service.admin.site.vo.SiteCategoryInfoVo;
import com.niu.core.service.admin.site.vo.SiteCategoryListVo;
import com.niu.core.enums.shop.ShopGroupCreateTypeEnum;
import com.niu.core.service.core.site.ICoreSiteCategoryService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 行业类目服务实现类
 */
@Service
public class CoreSiteCategoryServiceImpl implements ICoreSiteCategoryService {

    @Resource
    private SiteCategoryMapper siteCategoryMapper;

    @Resource
    private SiteGroupMapper siteGroupMapper;

    @Override
    public PageResult<SiteCategoryListVo> page(PageParam pageParam, String name, String addon) {
        LambdaQueryWrapper<SiteCategory> queryWrapper = new LambdaQueryWrapper<>();

        if (addon != null && !addon.isEmpty()) {
            queryWrapper.eq(SiteCategory::getAddon, addon);
        }
        if (name != null && !name.isEmpty()) {
            queryWrapper.like(SiteCategory::getName, name);
        }

        queryWrapper.orderByDesc(SiteCategory::getSort).orderByDesc(SiteCategory::getCreateTime);

        Page<SiteCategory> page = new Page<>(pageParam.getPage().longValue(), pageParam.getLimit().longValue());
        Page<SiteCategory> resultPage = siteCategoryMapper.selectPage(page, queryWrapper);

        // 转换为VO
        Page<SiteCategoryListVo> voPage = new Page<>(pageParam.getPage().longValue(), pageParam.getLimit().longValue());
        voPage.setTotal(resultPage.getTotal());
        List<SiteCategoryListVo> voList = new ArrayList<>();
        for (SiteCategory entity : resultPage.getRecords()) {
            SiteCategoryListVo vo = new SiteCategoryListVo();
            BeanUtils.copyProperties(entity, vo);
            voList.add(vo);
        }
        voPage.setRecords(voList);

        return PageResult.build(voPage);
    }

    @Override
    public List<SiteCategoryListVo> topList(String addon) {
        LambdaQueryWrapper<SiteCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteCategory::getPid, 0);

        if (addon != null && !addon.isEmpty()) {
            queryWrapper.eq(SiteCategory::getAddon, addon);
        }

        queryWrapper.orderByDesc(SiteCategory::getSort).orderByDesc(SiteCategory::getCreateTime);

        List<SiteCategory> entityList = siteCategoryMapper.selectList(queryWrapper);
        List<SiteCategoryListVo> voList = new ArrayList<>();
        for (SiteCategory entity : entityList) {
            SiteCategoryListVo vo = new SiteCategoryListVo();
            BeanUtils.copyProperties(entity, vo);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public List<SiteCategoryListVo> getChildList(Integer pid) {
        LambdaQueryWrapper<SiteCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteCategory::getPid, pid);
        queryWrapper.orderByDesc(SiteCategory::getSort).orderByDesc(SiteCategory::getCreateTime);

        List<SiteCategory> entityList = siteCategoryMapper.selectList(queryWrapper);
        List<SiteCategoryListVo> voList = new ArrayList<>();
        for (SiteCategory entity : entityList) {
            SiteCategoryListVo vo = new SiteCategoryListVo();
            BeanUtils.copyProperties(entity, vo);
            voList.add(vo);
        }
        return voList;
    }

    @Override
    public List<Integer> getChildIds(List<Integer> pid) {
        List<Integer> childIds = new ArrayList<>(siteCategoryMapper.selectList(new LambdaQueryWrapper<SiteCategory>()
                        .select(SiteCategory::getCategoryId)
                        .in(SiteCategory::getPid, pid))
                .stream().map(SiteCategory::getCategoryId).toList());
        if (!childIds.isEmpty()) {
            childIds.addAll(getChildIds(childIds));
        }
        return childIds;
    }

    @Override
    public SiteCategoryInfoVo info(Integer id) {
        SiteCategory siteCategory = siteCategoryMapper.selectById(id);
        if (siteCategory != null && siteCategory.getPid() > 0) {
            // 查询子分类数量
            LambdaQueryWrapper<SiteCategory> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SiteCategory::getPid, id);
            Long childCount = siteCategoryMapper.selectCount(queryWrapper);
            siteCategory.setHasChildren(childCount > 0);
        }

        if (siteCategory != null) {
            SiteCategoryInfoVo vo = new SiteCategoryInfoVo();
            BeanUtils.copyProperties(siteCategory, vo);
            // 复制hasChildren属性
            vo.setHasChildren(siteCategory.getHasChildren());
            return vo;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(SiteCategory siteCategory) {
        // 检查名称是否重复
        LambdaQueryWrapper<SiteCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteCategory::getName, siteCategory.getName())
                .eq(SiteCategory::getAddon, siteCategory.getAddon());
        if (siteCategoryMapper.exists(queryWrapper)) {
            throw new CommonException("类目已存在，请重新输入");
        }

        // 设置默认值
        if (siteCategory.getPid() == null) {
            siteCategory.setPid(0);
        }
        // 顶级分类校验套餐是否存在
        assertTopCategoryGroupExists(siteCategory);
        siteCategory.setLevel(siteCategory.getPid() > 0 ? 2 : 1);
        if (siteCategory.getSort() == null) {
            siteCategory.setSort(0);
        }
        if (siteCategory.getStatus() == null) {
            siteCategory.setStatus(1);
        }
        if (siteCategory.getServiceRate() == null) {
            siteCategory.setServiceRate(BigDecimal.ZERO);
        }
        if (siteCategory.getGroupId() == null) {
            siteCategory.setGroupId(0);
        }
        siteCategory.setCreateTime(DateUtils.currTime());
        siteCategory.setUpdateTime(DateUtils.currTime());

        siteCategoryMapper.insert(siteCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Integer id, SiteCategory siteCategory) {
        // 检查名称是否重复（排除自身）
        LambdaQueryWrapper<SiteCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteCategory::getName, siteCategory.getName())
                .eq(SiteCategory::getAddon, siteCategory.getAddon());
        queryWrapper.ne(SiteCategory::getCategoryId, id);
        if (siteCategoryMapper.exists(queryWrapper)) {
            throw new CommonException("类目已存在，请重新输入");
        }

        if (siteCategory.getPid() == null) {
            siteCategory.setPid(0);
        }
        // 顶级分类校验套餐是否存在
        assertTopCategoryGroupExists(siteCategory);

        siteCategory.setCategoryId(id);
        siteCategory.setLevel(siteCategory.getPid() > 0 ? 2 : 1);
        siteCategory.setUpdateTime(DateUtils.currTime());

        siteCategoryMapper.updateById(siteCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(Integer id) {
        // 检查是否有子分类
        LambdaQueryWrapper<SiteCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteCategory::getPid, id);
        if (siteCategoryMapper.exists(queryWrapper)) {
            throw new CommonException("该类目下有子分类，无法删除");
        }

        siteCategoryMapper.deleteById(id);
    }

    @Override
    public List<SiteCategoryListVo> tree(String name, String addon) {
        LambdaQueryWrapper<SiteCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteCategory::getPid, 0);

        if (addon != null && !addon.isEmpty()) {
            queryWrapper.eq(SiteCategory::getAddon, addon);
        }
        if (name != null && !name.isEmpty()) {
            queryWrapper.like(SiteCategory::getName, name);
        }

        queryWrapper.orderByAsc(SiteCategory::getSort);

        List<SiteCategory> list = siteCategoryMapper.selectList(queryWrapper);
        List<SiteCategoryListVo> voList = new ArrayList<>();

        for (SiteCategory item : list) {
            LambdaQueryWrapper<SiteCategory> childWrapper = new LambdaQueryWrapper<>();
            childWrapper.eq(SiteCategory::getPid, item.getCategoryId());
            childWrapper.orderByAsc(SiteCategory::getSort);
            List<SiteCategory> childEntityList = siteCategoryMapper.selectList(childWrapper);

            SiteCategoryListVo vo = new SiteCategoryListVo();
            BeanUtils.copyProperties(item, vo);

            List<SiteCategoryListVo> childVoList = new ArrayList<>();
            for (SiteCategory childItem : childEntityList) {
                SiteCategoryListVo childVo = new SiteCategoryListVo();
                BeanUtils.copyProperties(childItem, childVo);
                childVoList.add(childVo);
            }
            vo.setChildList(childVoList);
            vo.setHasChildren(!childVoList.isEmpty());

            voList.add(vo);
        }

        return voList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSort(List<SiteCategory> siteCategoryList) {
        for (SiteCategory item : siteCategoryList) {
            SiteCategory siteCategory = new SiteCategory();
            siteCategory.setCategoryId(item.getCategoryId());
            siteCategory.setSort(item.getSort());
            siteCategory.setUpdateTime(DateUtils.currTime());

            siteCategoryMapper.updateById(siteCategory);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setBanner(Integer id, String bannerList) {
        SiteCategory siteCategory = new SiteCategory();
        siteCategory.setCategoryId(id);
        siteCategory.setBannerList(bannerList);
        siteCategory.setUpdateTime(DateUtils.currTime());

        siteCategoryMapper.updateById(siteCategory);
    }

    /**
     * 顶级行业类目必须绑定有效套餐
     */
    private void assertTopCategoryGroupExists(SiteCategory siteCategory) {
        Integer pid = siteCategory.getPid();
        if (pid != null && pid > 0) {
            return;
        }
        Integer groupId = siteCategory.getGroupId();
        if (groupId == null || groupId <= 0) {
            throw new CommonException("套餐不存在");
        }
        SiteGroup siteGroup = siteGroupMapper.selectById(groupId);
        if (siteGroup == null) {
            throw new CommonException("套餐不存在");
        }
    }

    /**
     * 根据行业类目 id 解析店铺套餐（对齐 PHP CoreSiteCategoryService.getGroupInfoByCategoryId）
     */
    @Override
    public SiteGroup getGroupInfoByCategoryId(Integer categoryId) {
        SiteCategory siteCategory = siteCategoryMapper.selectById(categoryId);
        if (siteCategory == null) {
            throw new CommonException("行业类目不存在");
        }

        Integer groupId = siteCategory.getGroupId();
        String addon = siteCategory.getAddon();

        // group_id 为空时回退到 create_by=app 的默认套餐
        SiteGroup siteGroup;
        if (groupId == null || groupId == 0) {
            LambdaQueryWrapper<SiteGroup> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SiteGroup::getApp, JSONUtil.toJsonStr(List.of(addon)));
            queryWrapper.eq(SiteGroup::getCreateBy, ShopGroupCreateTypeEnum.APP.getStatus());
            siteGroup = siteGroupMapper.selectOne(queryWrapper);
        } else {
            siteGroup = siteGroupMapper.selectById(groupId);
        }

        if (siteGroup == null) {
            throw new CommonException("套餐不存在");
        }

        return siteGroup;
    }

    @Override
    public BigDecimal getServiceRateByCategoryId(Integer categoryId) {
        SiteCategory siteCategory = siteCategoryMapper.selectById(categoryId);
        if (siteCategory == null) {
            throw new CommonException("行业类目不存在");
        }

        return siteCategory.getServiceRate();
    }
}