package com.niu.mall.service.api.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.api.order.param.DeliveryPackageSearchParam;
import com.niu.mall.service.api.order.param.MallOrderSearchParam;
import com.niu.mall.service.api.order.vo.*;

import java.util.Map;

/**
 * 订单服务接口
 */
public interface IOrderService {


    /**
     * 订单列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderListVo>
     */
    MallOrderListPageVo page(PageParam pageParam, MallOrderSearchParam searchParam);

    /**
     * 订单详情
     * @param orderId 主键ID
     * @return ShopOrderInfoVo
     */
    MallOrderInfoVo detail(Integer orderId, Map<String, String> allParams);

    /**
     * 获取订单数量
     *
     * @return
     */
    OrderNumVo getNum();

    /**
     * 关闭订单
     *
     * @param id
     */
    Object close(Integer id);

    /**
     * 订单完成
     *
     * @param id
     */
    void orderFinish(Integer id);


    /**
     * 物流信息
     *
     */
    DeliveryPackageVo getPackage(DeliveryPackageSearchParam searchParam);
}
