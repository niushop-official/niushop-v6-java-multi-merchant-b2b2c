package com.niu.mall.service.api.cart;

import com.niu.mall.service.api.cart.param.MallCartCalculateParam;
import com.niu.mall.service.api.cart.param.MallCartDeleteParam;
import com.niu.mall.service.api.cart.param.MallCartEditParam;
import com.niu.mall.service.api.cart.param.MallCartParam;
import com.niu.mall.service.api.cart.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 购物车服务接口
 */
public interface IMallCartService {



    /**
     * 购物车添加
     * @param addParam 添加参数
     */
    void add(MallCartParam addParam);

    /**
     * 购物车列表
     * @return PageResult<MallCartListApiVo>
     */
    Map<Integer, MallCartListApiVo> list();

    /**
     * 购物车商品列表
     * @return PageResult<MallCartGoodsListApiVo>
     */
    Map<Integer, MallCartGoodsListApiVo> goodsList();



    /**
     * 购物车编辑
     * @param editParam 编辑参数
     */
    void edit(MallCartEditParam editParam);

    /**
     * 购物车删除
     * @param param 主键ID
     */
    void del(MallCartDeleteParam param);

    /**
     * 清空购物车
     */
    void clear();

    /**
     * 获取购物车数量
     *
     * @param goodsId
     * @return
     */
    Integer getSum(Integer goodsId);


}
