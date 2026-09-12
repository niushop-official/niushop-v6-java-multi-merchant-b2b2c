package com.niu.core.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.entity.goods.GoodsCategory;
import com.niu.core.event.goods.CheckGoodsCountEventDefiner;
import com.niu.core.mapper.goods.GoodsCategoryMapper;
import com.niu.core.service.api.goods.IGoodsCategoryService;
import com.niu.core.service.api.goods.param.GoodsCategorySearchParam;
import com.niu.core.service.api.goods.vo.GoodsCategoryListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;


/**
 * 商品分类实现
 */
@Service
public class GoodsCategoryServiceImpl implements IGoodsCategoryService {

    @Resource
    GoodsCategoryMapper goodsCategoryMapper;

    @Resource
    ICoreSiteService coreSiteService;

    /**
     * 商品分类列表
     *
     */
    @Override
    public List<GoodsCategoryListVo> list(GoodsCategorySearchParam searchParam) {
        Integer siteId = searchParam.getSiteId() == null ? 0 : searchParam.getSiteId();

        QueryWrapper<GoodsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("category_id", "category_name", "image", "level", "pid", "category_full_name");
        queryWrapper.eq("is_show", 1);
        // PHP: site_id > 0 才按站点过滤
        if (siteId > 0) {
            queryWrapper.eq("site_id", siteId);
        }
        if (ObjectUtil.isNotEmpty(searchParam.getPid())) {
            queryWrapper.eq("pid", searchParam.getPid());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getLevel())) {
            queryWrapper.eq("level", searchParam.getLevel());
        }
        if (StrUtil.isNotBlank(searchParam.getAddon())) {
            queryWrapper.eq("addon", searchParam.getAddon());
        }
        if (StrUtil.isNotBlank(searchParam.getCategoryName())) {
            queryWrapper.like("category_name", searchParam.getCategoryName());
        }
        // PHP: sort desc, create_time desc
        queryWrapper.orderByDesc("sort").orderByDesc("create_time");

        List<GoodsCategory> records = goodsCategoryMapper.selectList(queryWrapper);
        List<GoodsCategoryListVo> list = new LinkedList<>();
        // 站点安装 waimai 时，过滤掉没有关联商品的分类
        boolean filterByWaimaiGoods = coreSiteService.getAddonKeysBySiteId(siteId).contains("waimai");
        for (GoodsCategory item : records) {
            if (filterByWaimaiGoods && !hasGoodsInCategory(siteId, item.getCategoryId())) {
                continue;
            }
            GoodsCategoryListVo vo = new GoodsCategoryListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    private boolean hasGoodsInCategory(Integer siteId, Integer categoryId) {
        if (siteId == null || siteId <= 0 || categoryId == null || categoryId <= 0) {
            return false;
        }
        CheckGoodsCountEventDefiner.CheckGoodsCountEvent event = new CheckGoodsCountEventDefiner.CheckGoodsCountEvent();
        event.setSiteId(siteId);
        event.setCategoryId(categoryId);
        List<CheckGoodsCountEventDefiner.CheckGoodsCountResult> results = EventAndSubscribeOfPublisher.publishAndCallback(event);
        for (CheckGoodsCountEventDefiner.CheckGoodsCountResult result : results) {
            if (result != null && result.getCount() != null && result.getCount() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取商品分类树
     * 对齐 PHP GoodsCategoryService::getTree
     */
    @Override
    public JSONArray tree(GoodsCategorySearchParam searchParam) {
        Integer siteId = searchParam.getSiteId();
        QueryWrapper<GoodsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("category_id", "category_name", "image", "level", "pid", "category_full_name");
        queryWrapper.eq("is_show", 1)
                .eq(StrUtil.isNotEmpty(searchParam.getAddon()), "addon", searchParam.getAddon());
        if (siteId != null && siteId > 0) {
            queryWrapper.eq("site_id", siteId);
        } else {
            queryWrapper.eq("site_id", RequestUtils.siteId());
        }
        queryWrapper.orderByDesc("sort").orderByDesc("create_time");
        List<GoodsCategory> list = goodsCategoryMapper.selectList(queryWrapper);
        if (list == null) {
            return null;
        }

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(list));
        return TreeUtils.listToTree(jsonArray, "category_id", "pid", "child_list");
    }

}
