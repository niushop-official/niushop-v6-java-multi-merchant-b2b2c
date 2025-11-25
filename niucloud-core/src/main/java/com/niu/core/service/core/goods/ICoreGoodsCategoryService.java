package com.niu.core.service.core.goods;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface ICoreGoodsCategoryService {

    List<Integer> getGoodsMallCategoryIdsChild(Integer goodsCategoryId);

}
