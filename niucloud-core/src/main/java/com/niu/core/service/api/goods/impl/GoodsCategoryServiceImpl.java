package com.niu.core.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.entity.goods.GoodsCategory;
import com.niu.core.mapper.goods.GoodsCategoryMapper;
import com.niu.core.service.api.goods.IGoodsCategoryService;
import com.niu.core.service.api.goods.param.GoodsCategorySearchParam;
import com.niu.core.service.api.goods.vo.GoodsCategoryListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 商品分类实现
 */
@Service
public class GoodsCategoryServiceImpl implements IGoodsCategoryService {
        
    @Resource
    GoodsCategoryMapper goodsCategoryMapper;

    /**
     * 商品分类列表
     *
     */
    @Override
    public List<GoodsCategoryListVo> list(GoodsCategorySearchParam searchParam) {

        Integer siteId = RequestUtils.siteId().equals(RequestUtils.defaultSiteId()) ? RequestUtils.defaultSiteId() : RequestUtils.siteId();
        QueryWrapper<GoodsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        if (ObjectUtil.isNotEmpty(searchParam.getCategoryName())) queryWrapper.like("category_name", searchParam.getCategoryName());
        if (ObjectUtil.isNotEmpty(searchParam.getLevel())) queryWrapper.eq("level", searchParam.getLevel());
        queryWrapper.orderByDesc(Arrays.asList("sort", "category_id"));

        List<GoodsCategory> records = goodsCategoryMapper.selectList(queryWrapper);
        List<GoodsCategoryListVo> list = new LinkedList<>();
        for (GoodsCategory item : records) {
            GoodsCategoryListVo vo = new GoodsCategoryListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }

    /**
     * 获取商品分类树
     *
     * @return
     */
    public JSONArray tree(Integer siteId) {

        List<GoodsCategory> list = goodsCategoryMapper.selectList(new QueryWrapper<GoodsCategory>().eq("site_id", siteId));
        if (list == null) return null;

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(list));
        return TreeUtils.listToTree(jsonArray, "category_id", "pid", "child_list");
    }

}
