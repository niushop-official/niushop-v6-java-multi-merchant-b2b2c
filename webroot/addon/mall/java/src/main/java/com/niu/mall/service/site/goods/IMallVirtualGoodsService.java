package com.niu.mall.service.site.goods;

import com.niu.mall.service.site.goods.param.*;
import com.niu.mall.service.site.goods.vo.MallVirtualGoodsInitVo;

/**
 * 虚拟商品接口
 */
public interface IMallVirtualGoodsService {

    /**
     * 商品添加
     *
     * @param addParam 添加参数
     */
    void add(MallVirtualGoodsParam addParam);

    /**
     * 商品编辑
     *
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MallVirtualGoodsParam editParam);

    /**
     * 获取商品添加/编辑数据
     *
     * @param goodsId
     * @return
     */
    MallVirtualGoodsInitVo getInit(Integer goodsId);
}
