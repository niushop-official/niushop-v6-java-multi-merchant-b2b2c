package com.niu.core.service.api.goods;

import cn.hutool.json.JSONArray;
import com.niu.core.service.api.goods.param.GoodsCategorySearchParam;
import com.niu.core.service.api.goods.vo.GoodsCategoryListVo;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface IGoodsCategoryService {

    /**
     * 商品分类列表
     * @return List<ShopGoodsCategoryListVo>
     */
    List<GoodsCategoryListVo> list(GoodsCategorySearchParam searchParam);

    /**
     * 获取商品分类树
     *
     * @return
     */
    JSONArray tree(GoodsCategorySearchParam searchParam);


}
