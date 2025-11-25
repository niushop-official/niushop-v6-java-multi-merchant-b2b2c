package com.niu.core.service.admin.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.entity.goods.GoodsCategory;
import com.niu.core.mapper.goods.GoodsCategoryMapper;
import com.niu.core.service.admin.goods.IGoodsCategoryService;
import com.niu.core.service.admin.goods.param.EditGoodsCategorySortParam;
import com.niu.core.service.admin.goods.param.GoodsCategoryParam;
import com.niu.core.service.admin.goods.param.GoodsCategorySearchParam;
import com.niu.core.service.admin.goods.vo.GoodsCategoryInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsCategoryListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
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
        QueryWrapper<GoodsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());
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
    @Override
    public JSONArray tree() {
        List<GoodsCategory> list = goodsCategoryMapper.selectList(new QueryWrapper<GoodsCategory>().eq("site_id", RequestUtils.defaultSiteId()));
        if (list == null){
            return null;
        }
        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(list));
        return TreeUtils.listToTree(jsonArray, "category_id", "pid", "child_list");
    }

    /**
     * 获取商品分类树
     *
     * @return
     */
    @Override
    public JSONArray tree(GoodsCategorySearchParam searchParam) {
        QueryWrapper queryWrapper = new QueryWrapper<GoodsCategory>().eq("site_id", RequestUtils.defaultSiteId());
        if (ObjectUtil.isNotNull(searchParam.getIsShow())){
            queryWrapper.eq("is_show", searchParam.getIsShow());
        }
        List<GoodsCategory> list = goodsCategoryMapper.selectList(queryWrapper);
        if (list == null) {
            return null;
        }
        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(list));
        return TreeUtils.listToTree(jsonArray, "category_id", "pid", "child_list");
    }


    /**
     * 商品分类详情
     * @param id 主键
     * @return ShopGoodsCategoryInfoVo
     */
    @Override
    public GoodsCategoryInfoVo info(Integer id) {
        GoodsCategory model = goodsCategoryMapper.selectOne(
                new QueryWrapper<GoodsCategory>()
                    .eq("category_id", id)
                    .eq("site_id", RequestUtils.defaultSiteId()));

        Assert.notNull(model, "分类不存在");

        GoodsCategoryInfoVo vo = new GoodsCategoryInfoVo();
        BeanUtils.copyProperties(model, vo);

        Long childCount = goodsCategoryMapper.selectCount(new QueryWrapper<GoodsCategory>()
                .eq("pid", id));
        vo.setChildCount(childCount == null ? 0 : childCount.intValue());

        return vo;
    }

    /**
     * 商品分类添加
     * @param addParam 参数
     */
    @Override
    public void add(GoodsCategoryParam addParam) {
        QueryWrapper checkQuery = new QueryWrapper<GoodsCategory>()
                .eq("category_name", addParam.getCategoryName())
                .eq("site_id", RequestUtils.defaultSiteId());
        if (addParam.getPid() > 0) {
            checkQuery.eq("pid", addParam.getPid());
        } else {
            checkQuery.eq("level", 1);
        }
        GoodsCategory category = goodsCategoryMapper.selectOne(checkQuery);
        Assert.isNull(category, "分类已存在");

        GoodsCategory model = new GoodsCategory();
        BeanUtils.copyProperties(addParam, model);
        model.setSiteId(RequestUtils.defaultSiteId());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        if (addParam.getPid() > 0) {
            GoodsCategory parentCategory = goodsCategoryMapper.selectOne(new QueryWrapper<GoodsCategory>()
                    .eq("category_id", addParam.getPid()));
            Assert.notNull(parentCategory, "所选父级分类不存在");
            model.setCategoryFullName(parentCategory.getCategoryName() + "/" + addParam.getCategoryName());
            model.setLevel(2);
        } else {
            model.setLevel(1);
            model.setCategoryFullName(addParam.getCategoryName());
        }

        goodsCategoryMapper.insert(model);
    }

    /**
     * 商品分类编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, GoodsCategoryParam editParam) {
        QueryWrapper checkQuery = new QueryWrapper<GoodsCategory>()
                .eq("category_name", editParam.getCategoryName())
                .eq("site_id", RequestUtils.defaultSiteId())
                .ne("category_id", id);
        if (editParam.getPid() > 0) {
            checkQuery.eq("pid", editParam.getPid());
        } else {
            checkQuery.eq("level", 1);
        }
        GoodsCategory category = goodsCategoryMapper.selectOne(checkQuery);
        Assert.isNull(category, "分类已存在");

        GoodsCategory model = goodsCategoryMapper.selectOne(new QueryWrapper<GoodsCategory>()
                .eq("category_id", id)
                .eq("site_id", RequestUtils.defaultSiteId()));
        Assert.notNull(model, "分类不存在");

        if (model.getLevel().equals(1) && editParam.getPid() > 0) {
            Long childCount = goodsCategoryMapper.selectCount(new QueryWrapper<GoodsCategory>()
                    .eq("pid", id));
            if (childCount > 0) throw new CommonException("当前分类存在下级，不可修改该分类上级");
        }

        BeanUtils.copyProperties(editParam, model);
        model.setSiteId(RequestUtils.defaultSiteId());
        model.setUpdateTime(System.currentTimeMillis() / 1000);

        if (editParam.getPid() > 0) {
            GoodsCategory parentCategory = goodsCategoryMapper.selectOne(new QueryWrapper<GoodsCategory>()
                    .eq("category_id", editParam.getPid()));
            Assert.notNull(parentCategory, "所选父级分类不存在");
            model.setCategoryFullName(parentCategory.getCategoryName() + "/" + editParam.getCategoryName());
            model.setLevel(2);
        } else {
            model.setLevel(1);
            model.setCategoryFullName(editParam.getCategoryName());
        }

        goodsCategoryMapper.updateById(model);
    }

    /**
     * 修改分类排序
     *
     * @param editParam
     */
    public void editGoodsCategorySort(EditGoodsCategorySortParam editParam) {
        for (int i = 0; i < editParam.getCategorySortArray().size(); i++) {
            EditGoodsCategorySortParam.Item item = editParam.getCategorySortArray().get(i);
            GoodsCategory model = new GoodsCategory();
            model.setSort(item.getSort());
            goodsCategoryMapper.update(model, new QueryWrapper<GoodsCategory>()
                    .eq("site_id", RequestUtils.defaultSiteId())
                    .eq("category_id", item.getCategoryId()));
        }
    }

    /**
     * 商品分类删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
/*        Long count = mallGoodsMapper.selectCount(new QueryWrapper<MallGoods>()
                .eq("site_id", RequestUtils.defaultSiteId())
                .like("goods_category", "\"" + id + "\""));
        if (count > 0) throw new CommonException("当前分类下存在商品，不可删除");*/

        goodsCategoryMapper.delete(new QueryWrapper<GoodsCategory>().eq("category_id", id).or().eq("pid", id));
    }

}
