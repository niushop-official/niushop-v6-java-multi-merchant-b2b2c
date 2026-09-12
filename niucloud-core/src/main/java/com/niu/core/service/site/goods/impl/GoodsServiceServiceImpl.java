package com.niu.core.service.site.goods.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.goods.GoodsService;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.goods.GoodsServiceMapper;
import com.niu.core.service.site.goods.param.GoodsServiceParam;
import com.niu.core.service.site.goods.param.GoodsServiceSearchParam;
import com.niu.core.service.site.goods.IGoodsServiceService;
import com.niu.core.service.site.goods.vo.GoodsServiceInfoVo;
import com.niu.core.service.site.goods.vo.GoodsServiceListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 商品服务实现
 */
@Service
public class GoodsServiceServiceImpl implements IGoodsServiceService {
        
    @Resource
    GoodsServiceMapper mallGoodsServiceMapper;

    @Resource
    AddonMapper addonMapper;

    /**
     * 商品服务分页列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsServiceListVo>
     */
    @Override
    public PageResult<GoodsServiceListVo> page(PageParam pageParam, GoodsServiceSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<GoodsService> queryWrapper = new QueryWrapper<>();
        //queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        queryWrapper.orderByDesc("service_id");
        if (ObjectUtil.isNotNull(searchParam.getServiceName())) queryWrapper.like("service_name", searchParam.getServiceName());

        IPage<GoodsService> iPage = mallGoodsServiceMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<GoodsServiceListVo> list = new LinkedList<>();
        for (GoodsService item : iPage.getRecords()) {
            GoodsServiceListVo vo = new GoodsServiceListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 商品服务列表
     * @param searchParam 搜索参数
     * @return List<ShopGoodsServiceListVo>
     */
    @Override
    public List<GoodsServiceListVo> list(GoodsServiceSearchParam searchParam) {
        if (StrUtil.isNotBlank(searchParam.getAddon())) {
            return dedupeServiceList(getAddonServiceList(searchParam.getAddon()));
        }
        QueryWrapper<GoodsService> queryWrapper = new QueryWrapper<>();
        //queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
        queryWrapper.orderByDesc("service_id");
        if (ObjectUtil.isNotNull(searchParam.getServiceName())) queryWrapper.like("service_name", searchParam.getServiceName());

        List<GoodsService> record = mallGoodsServiceMapper.selectList(queryWrapper);
        List<GoodsServiceListVo> list = new LinkedList<>();
        for (GoodsService item : record) {
            GoodsServiceListVo vo = new GoodsServiceListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return dedupeServiceList(list);
    }

    /**
     * 商品服务详情
     * @param id 主键
     * @return ShopGoodsServiceInfoVo
     */
    @Override
    public GoodsServiceInfoVo info(Integer id) {
        GoodsService model = mallGoodsServiceMapper.selectOne(
                new QueryWrapper<GoodsService>()
                    .eq("service_id", id));

        Assert.notNull(model, "服务不存在");

        GoodsServiceInfoVo vo = new GoodsServiceInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    /**
     * 商品服务添加
     * @param addParam 参数
     */
    @Override
    public void add(GoodsServiceParam addParam) {
        GoodsService model = new GoodsService();

        model.setSiteId(RequestUtils.defaultSiteId());
        model.setServiceName(addParam.getServiceName());
        model.setImage(addParam.getImage());
        model.setDesc(addParam.getDesc());
        model.setCreateTime(System.currentTimeMillis() / 1000);

        mallGoodsServiceMapper.insert(model);
    }

    /**
     * 商品服务编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, GoodsServiceParam editParam) {
        GoodsService model = mallGoodsServiceMapper.selectOne(
                new QueryWrapper<GoodsService>()
                        .eq("service_id", id)
                        .eq("site_id", RequestUtils.defaultSiteId()));

        Assert.notNull(model, "服务不存在!");

        model.setServiceName(editParam.getServiceName());
        model.setImage(editParam.getImage());
        model.setDesc(editParam.getDesc());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        mallGoodsServiceMapper.updateById(model);
    }

    /**
     * 商品服务删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        mallGoodsServiceMapper.delete(new QueryWrapper<GoodsService>().eq("service_id", id).eq("site_id", RequestUtils.defaultSiteId()));
    }

    /**
     * 获取符合条件的插件多个数据
     *
     * @param addon 插件标识
     * @return 商品服务列表
     */
    @Override
    public List<GoodsServiceListVo> getAddonServiceList(String addon) {
        QueryWrapper<GoodsService> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("addon", "\"" + addon + "\"");
        queryWrapper.orderByDesc("create_time");
        List<GoodsService> records = mallGoodsServiceMapper.selectList(queryWrapper);
        return convertAddonServiceList(records);
    }

    private List<GoodsServiceListVo> convertAddonServiceList(List<GoodsService> records) {
        if (CollUtil.isEmpty(records)) {
            return Collections.emptyList();
        }

        Set<String> addonKeys = new HashSet<>();
        for (GoodsService item : records) {
            addonKeys.addAll(parseAddonKeys(item.getAddon()));
        }

        Map<String, String> addonTitleMap = new HashMap<>();
        if (CollUtil.isNotEmpty(addonKeys)) {
            List<Addon> addons = addonMapper.selectList(
                    new LambdaQueryWrapper<Addon>().in(Addon::getKey, addonKeys));
            for (Addon addon : addons) {
                addonTitleMap.put(addon.getKey(), addon.getTitle());
            }
        }

        List<GoodsServiceListVo> list = new ArrayList<>();
        for (GoodsService item : records) {
            GoodsServiceListVo vo = new GoodsServiceListVo();
            BeanUtils.copyProperties(item, vo);
            List<String> keys = parseAddonKeys(item.getAddon());
            vo.setAddon(keys);
            vo.setAddonName(keys.stream()
                    .map(key -> addonTitleMap.getOrDefault(key, ""))
                    .collect(Collectors.toList()));
            list.add(vo);
        }
        return list;
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

    /**
     * 按服务名称去重，保留排序后最先出现的一条
     */
    private List<GoodsServiceListVo> dedupeServiceList(List<GoodsServiceListVo> list) {
        if (CollUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        Map<String, GoodsServiceListVo> dedupMap = new LinkedHashMap<>();
        for (GoodsServiceListVo item : list) {
            String key = item.getServiceName();
            if (StrUtil.isBlank(key) || dedupMap.containsKey(key)) {
                continue;
            }
            dedupMap.put(key, item);
        }
        return new ArrayList<>(dedupMap.values());
    }

}
