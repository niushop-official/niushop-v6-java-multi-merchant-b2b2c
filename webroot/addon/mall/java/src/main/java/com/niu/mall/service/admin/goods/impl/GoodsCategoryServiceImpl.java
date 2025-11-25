package com.niu.mall.service.admin.goods.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.entity.goods.GoodsCategory;
import com.niu.core.mapper.goods.GoodsCategoryMapper;
import com.niu.mall.service.admin.goods.IGoodsCategoryService;
import com.niu.mall.service.api.goods.param.MallGoodsCategorySearchParam;
import com.niu.mall.service.api.goods.vo.MallGoodsCategoryListVo;
import com.niu.mall.service.core.goods.ICoreGoodsConfigService;
import com.niu.mall.service.core.goods.vo.GoodsCategoryConfigVo;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class GoodsCategoryServiceImpl implements IGoodsCategoryService {

    @Resource
    ICoreGoodsConfigService coreGoodsConfigService;

    @Resource
    GoodsCategoryMapper goodsCategoryMapper;

    /**
     * 获取商品分类配置
     *
     * @return
     */
    public GoodsCategoryConfigVo getGoodsCategoryConfig() {

        return coreGoodsConfigService.getGoodsCategoryConfig();
    }

    /**
     * 获取商品分类树
     *
     * @return
     */
    public JSONArray tree() {
        QueryWrapper queryWrapper = new QueryWrapper<GoodsCategory>().eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("is_show", 1);
        queryWrapper.orderByDesc("sort");
        List<GoodsCategory> list = goodsCategoryMapper.selectList(queryWrapper);
        if (list == null) return null;

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(list));
        return TreeUtils.listToTree(jsonArray, "category_id", "pid", "child_list");
    }

    /**
     * 获取商品分类列表
     *
     * @param searchParam
     * @return
     */
    public List<MallGoodsCategoryListVo> getList(MallGoodsCategorySearchParam searchParam) {
        QueryWrapper queryWrapper = new QueryWrapper<GoodsCategory>().eq("site_id", RequestUtils.siteId());
        queryWrapper.eq("is_show", 1);
        queryWrapper.orderByDesc("sort");
        if (searchParam.getPid() != null && searchParam.getPid() > 0) queryWrapper.eq("pid", searchParam.getPid());
        return CollectUtils.convert(goodsCategoryMapper.selectList(queryWrapper), MallGoodsCategoryListVo.class);
    }

    /**
     * 设置商品分类配置
     * @param data
     */
    @Override
    public void setGoodsCategoryConfig(JSONObject data) {
        coreGoodsConfigService.setGoodsCategoryConfig(data);
    }
}
