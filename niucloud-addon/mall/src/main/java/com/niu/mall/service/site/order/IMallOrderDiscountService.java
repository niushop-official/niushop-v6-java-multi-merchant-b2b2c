package com.niu.mall.service.site.order;

import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.order.param.MallOrderDiscountParam;
import com.niu.mall.service.site.order.param.MallOrderDiscountSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.mall.service.site.order.vo.MallOrderDiscountInfoVo;
import com.niu.mall.service.site.order.vo.MallOrderDiscountListVo;

/**
 * 订单优惠服务接口
 */
public interface IMallOrderDiscountService {


    /**
     * 订单优惠列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderDiscountListVo>
     */
    PageResult<MallOrderDiscountListVo> list(PageParam pageParam, MallOrderDiscountSearchParam searchParam);

    /**
     * 订单优惠详情
     * @param id 主键ID
     * @return ShopOrderDiscountInfoVo
     */
    MallOrderDiscountInfoVo info(Integer id);

    /**
     * 订单优惠添加
     * @param addParam 添加参数
     */
    void add(MallOrderDiscountParam addParam);

    /**
     * 订单优惠编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MallOrderDiscountParam editParam);

    /**
     * 订单优惠删除
     * @param id 主键ID
     */
    void del(Integer id);

}
