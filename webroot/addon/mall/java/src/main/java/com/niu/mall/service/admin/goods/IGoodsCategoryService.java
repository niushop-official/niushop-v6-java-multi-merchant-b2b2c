package com.niu.mall.service.admin.goods;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.mall.service.api.goods.param.MallGoodsCategorySearchParam;
import com.niu.mall.service.api.goods.vo.MallGoodsCategoryListVo;
import com.niu.mall.service.core.goods.vo.GoodsCategoryConfigVo;

import java.util.List;

public interface IGoodsCategoryService {


    /**
     * 获取商品分类树
     *
     * @return
     */
    JSONArray tree();

    /**
     * 获取商品分类列表
     *
     * @param searchParam
     */
    List<MallGoodsCategoryListVo> getList(MallGoodsCategorySearchParam searchParam);

    /**
     * 获取商品分类配置
     *
     * @return
     */
    GoodsCategoryConfigVo getGoodsCategoryConfig();


    void setGoodsCategoryConfig(JSONObject data);
}
