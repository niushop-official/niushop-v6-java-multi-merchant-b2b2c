package com.niu.mall.service.admin.goods;

import com.niu.mall.entity.goods.MallGoodsSpec;

import java.util.List;

/**
 * 商品规格项/值服务接口
 */
public interface IMallGoodsSpecService {

    void insertAll(List<MallGoodsSpec> list);
}
