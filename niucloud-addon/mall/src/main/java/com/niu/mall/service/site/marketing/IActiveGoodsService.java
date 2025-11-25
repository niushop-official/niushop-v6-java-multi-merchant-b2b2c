package com.niu.mall.service.site.marketing;

import com.niu.mall.entity.active.MallActiveGoods;

import java.util.List;

/**
 * 店铺营销活动服务接口
 */
public interface IActiveGoodsService {

    void insertAll(List<MallActiveGoods> list);

}
