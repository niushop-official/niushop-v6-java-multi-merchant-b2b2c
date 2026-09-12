package com.niu.core.service.admin.goods.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.service.admin.goods.IGoodsServiceService;
import com.niu.core.service.admin.goods.param.GoodsServiceParam;
import com.niu.core.service.admin.goods.param.GoodsServiceSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsServiceInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsServiceListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 商品服务实现
 */
@Service
public class GoodsServiceServiceImpl implements IGoodsServiceService {

    @Resource
    GoodsServiceMapper goodsServiceMapper;
    @Resource
    private AddonMapper addonMapper;

    /**
     * 商品服务分页列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsServiceListVo>
     */
    @Override
    public PageResult<GoodsServiceListVo> page(PageParam pageParam, GoodsServiceSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<GoodsService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        queryWrapper.orderByDesc("service_id");
        if (ObjectUtil.isNotNull(searchParam.getServiceName()))
            queryWrapper.like("service_name", searchParam.getServiceName());

        IPage<GoodsService> iPage = goodsServiceMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<GoodsServiceListVo> list = convertToVoList(iPage.getRecords());
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品服务列表
     *
     * @param searchParam 搜索参数
     * @return List<ShopGoodsServiceListVo>
     */
    @Override
    public List<GoodsServiceListVo> list(GoodsServiceSearchParam searchParam) {
        QueryWrapper<GoodsService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        queryWrapper.orderByDesc("service_id");
        if (ObjectUtil.isNotNull(searchParam.getServiceName()))
            queryWrapper.like("service_name", searchParam.getServiceName());

        List<GoodsService> record = goodsServiceMapper.selectList(queryWrapper);
        List<GoodsServiceListVo> list = convertToVoList(record);
        return list;
    }

    /**
     * 商品服务详情
     *
     * @param id 主键
     * @return ShopGoodsServiceInfoVo
     */
    @Override
    public GoodsServiceInfoVo info(Integer id) {
        GoodsService model = goodsServiceMapper.selectOne(
                new QueryWrapper<GoodsService>()
                        .eq("service_id", id)
                        .eq("site_id", RequestUtils.defaultSiteId()));

        Assert.notNull(model, "服务不存在");

        GoodsServiceInfoVo vo = new GoodsServiceInfoVo();
        BeanUtils.copyProperties(model, vo);
        List<String> addonKeys = parseAddonKeys(model.getAddon());
        vo.setAddon(addonKeys);
        vo.setAddonName(getAddonNames(addonKeys));
        return vo;
    }

    /**
     * 商品服务添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(GoodsServiceParam addParam) {
        GoodsService model = new GoodsService();

        model.setSiteId(RequestUtils.defaultSiteId());
        model.setServiceName(addParam.getServiceName());
        model.setImage(addParam.getImage());
        model.setDesc(addParam.getDesc());
        model.setAddon(formatAddon(addParam.getAddon()));
        model.setCreateTime(System.currentTimeMillis() / 1000);

        goodsServiceMapper.insert(model);
    }

    /**
     * 商品服务编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, GoodsServiceParam editParam) {
        GoodsService model = goodsServiceMapper.selectOne(
                new QueryWrapper<GoodsService>()
                        .eq("service_id", id)
                        .eq("site_id", RequestUtils.defaultSiteId()));

        Assert.notNull(model, "服务不存在!");

        model.setServiceName(editParam.getServiceName());
        model.setImage(editParam.getImage());
        model.setDesc(editParam.getDesc());
        model.setAddon(formatAddon(editParam.getAddon()));
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        goodsServiceMapper.updateById(model);
    }

    /**
     * 商品服务删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        goodsServiceMapper.delete(new QueryWrapper<GoodsService>().eq("service_id", id).eq("site_id", RequestUtils.defaultSiteId()));
    }

    /**
     * 检查服务名称唯一性
     */
    @Override
    public boolean checkServiceNameUnique(String serviceName, Integer excludeId) {
        QueryWrapper<GoodsService> wrapper = new QueryWrapper<GoodsService>()
                .eq("service_name", serviceName)
                .eq("site_id", RequestUtils.defaultSiteId());
        if (excludeId != null) {
            wrapper.ne("service_id", excludeId);
        }
        return goodsServiceMapper.selectCount(wrapper) > 0;
    }

    // ========== 私有辅助方法 ==========

    private List<GoodsServiceListVo> convertToVoList(List<GoodsService> records) {
        if (CollUtil.isEmpty(records)) {
            return Collections.emptyList();
        }
        // 收集所有 addon 键（去重）
        Set<String> addonKeys = new HashSet<>();
        for (GoodsService item : records) {
            addonKeys.addAll(parseAddonKeys(item.getAddon()));
        }

        // 批量查询 addon 信息
        Map<String, String> addonTitleMap = new HashMap<>();
        if (CollUtil.isNotEmpty(addonKeys)) {
            List<Addon> addons = addonMapper.selectList(
                    new LambdaQueryWrapper<Addon>().in(Addon::getKey, addonKeys));
            for (Addon addon : addons) {
                addonTitleMap.put(addon.getKey(), addon.getTitle());
            }
        }

        List<GoodsServiceListVo> voList = new ArrayList<>();
        for (GoodsService item : records) {
            GoodsServiceListVo vo = new GoodsServiceListVo();
            BeanUtils.copyProperties(item, vo);
            List<String> keys = parseAddonKeys(item.getAddon());
            vo.setAddon(keys);
            vo.setAddonName(keys.stream()
                    .map(key -> addonTitleMap.getOrDefault(key, ""))
                    .collect(Collectors.toList()));
            voList.add(vo);
        }
        return voList;
    }

    private String formatAddon(List<String> addon) {
        if (CollUtil.isEmpty(addon)) {
            return "";
        }
        return JSONUtil.toJsonStr(addon);
    }

    private List<String> parseAddonKeys(String addon) {
        if (StrUtil.isBlank(addon)) {
            return Collections.emptyList();
        }
        try {
            return JSONUtil.parseArray(addon).toList(String.class);
        } catch (Exception e) {
            return Collections.singletonList(addon);
        }
    }

    private List<String> getAddonNames(List<String> addonKeys) {
        if (CollUtil.isEmpty(addonKeys)) {
            return Collections.emptyList();
        }
        List<Addon> addons = addonMapper.selectList(
                new LambdaQueryWrapper<Addon>().in(Addon::getKey, addonKeys));
        Map<String, String> addonTitleMap = new HashMap<>();
        for (Addon addon : addons) {
            addonTitleMap.put(addon.getKey(), addon.getTitle());
        }
        return addonKeys.stream()
                .map(key -> addonTitleMap.getOrDefault(key, ""))
                .collect(Collectors.toList());
    }
}
