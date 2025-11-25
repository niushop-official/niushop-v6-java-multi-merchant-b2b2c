package com.niu.mall.service.site.order;

import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.order.param.MallOrderDiscountGoodsParam;
import com.niu.mall.service.site.order.param.MallOrderDiscountGoodsSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.mall.service.site.order.vo.MallOrderDiscountGoodsInfoVo;
import com.niu.mall.service.site.order.vo.MallOrderDiscountGoodsListVo;

/**
 * 订单项优惠服务接口
 */
public interface IMallOrderDiscountGoodsService {


    /**
     * 订单项优惠列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderDiscountGoodsListVo>
     */
    PageResult<MallOrderDiscountGoodsListVo> list(PageParam pageParam, MallOrderDiscountGoodsSearchParam searchParam);

    /**
     * 订单项优惠详情
     * @param id 主键ID
     * @return ShopOrderDiscountGoodsInfoVo
     */
    MallOrderDiscountGoodsInfoVo info(Integer id);

    /**
     * 订单项优惠添加
     * @param addParam 添加参数
     */
    void add(MallOrderDiscountGoodsParam addParam);

    /**
     * 订单项优惠编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MallOrderDiscountGoodsParam editParam);

    /**
     * 订单项优惠删除
     * @param id 主键ID
     */
    void del(Integer id);

}
