package com.niu.core.service.core.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.goods.GoodsCategory;
import com.niu.core.mapper.goods.GoodsCategoryMapper;
import com.niu.core.service.core.goods.ICoreGoodsCategoryService;
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
        List<Integer> categoryIds = new ArrayList<>();
        categoryIds.add(goodsCategoryId);
        GoodsCategory goodsCategory = goodsCategoryMapper.selectById(goodsCategoryId);
        if (ObjectUtil.isEmpty(goodsCategory)) return categoryIds;
        if (goodsCategory.getLevel() == 3) return categoryIds;
        if (goodsCategory.getLevel() == 2) {
            List<GoodsCategory> goodsCategories = goodsCategoryMapper.selectList(new QueryWrapper<GoodsCategory>().eq("pid", goodsCategoryId));
            if (ObjectUtil.isNotEmpty(goodsCategories)) {
                categoryIds.addAll(goodsCategories.stream().map(GoodsCategory::getCategoryId).toList());
                return categoryIds;
            }
        }
        if (goodsCategory.getLevel() == 1) {
            List<GoodsCategory> goodsCategories = goodsCategoryMapper.selectList(new QueryWrapper<GoodsCategory>().eq("pid", goodsCategoryId));
            if (!goodsCategories.isEmpty()) {
                categoryIds.addAll(goodsCategories.stream().map(GoodsCategory::getCategoryId).toList());
                if (ObjectUtil.isNotEmpty(goodsCategories)) {
                    List<Integer> ids = goodsCategories.stream().map(GoodsCategory::getCategoryId).collect(Collectors.toList());
                    List<GoodsCategory> goodsCategories1 = goodsCategoryMapper.selectList(new QueryWrapper<GoodsCategory>().in("pid", ids));
                    if (ObjectUtil.isNotEmpty(goodsCategories1)) {
                        categoryIds.addAll(goodsCategories1.stream().map(GoodsCategory::getCategoryId).toList());
                    }
                }
            }
            return categoryIds;
        }
        return categoryIds;
        /* 递归查询当前分类所有子类
        WITH RECURSIVE category_tree AS (
            SELECT category_id, pid, category_name, 0 AS depth
            FROM ns_goods_category
            WHERE category_id = 8

            UNION ALL

            SELECT c.category_id, c.pid, c.category_name, ct.depth + 1
            FROM ns_goods_category c
            INNER JOIN category_tree ct ON c.pid = ct.category_id
        )
        SELECT * FROM category_tree ORDER BY depth, category_id;
        */
    }
}
