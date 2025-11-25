package com.niu.core.service.core.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.goods.GoodsCategory;
import com.niu.core.mapper.goods.GoodsCategoryMapper;
import com.niu.core.service.core.goods.ICoreGoodsCategoryService;
import io.swagger.models.auth.In;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoreGoodsCategoryServiceImpl implements ICoreGoodsCategoryService {

    @Resource
    GoodsCategoryMapper goodsCategoryMapper;

    @Override
    public List<Integer> getGoodsMallCategoryIdsChild(Integer goodsCategoryId) {

        GoodsCategory goodsCategory = goodsCategoryMapper.selectById(goodsCategoryId);
        if(ObjectUtil.isEmpty(goodsCategory)) return new ArrayList<>(List.of(goodsCategoryId));
        if(goodsCategory.getLevel() == 3) return new ArrayList<>(List.of(goodsCategoryId));
        if(goodsCategory.getLevel() == 2) {
            List<GoodsCategory> goodsCategories = goodsCategoryMapper.selectList(new QueryWrapper<GoodsCategory>().eq("pid", goodsCategoryId));
            if(ObjectUtil.isNotEmpty(goodsCategories)){
                return goodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
            }
        }
        if(goodsCategory.getLevel() == 1) {
            List<GoodsCategory> goodsCategories = goodsCategoryMapper.selectList(new QueryWrapper<GoodsCategory>().eq("pid", goodsCategoryId));
            if(ObjectUtil.isNotEmpty(goodsCategories)){
                List<Integer> ids = goodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                List<GoodsCategory> goodsCategories1 = goodsCategoryMapper.selectList(new QueryWrapper<GoodsCategory>().in("pid", ids));
                if(ObjectUtil.isNotEmpty(goodsCategories1)) return goodsCategories1.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());

            }
        }
        return new ArrayList<>(List.of(goodsCategoryId));
    }
}
