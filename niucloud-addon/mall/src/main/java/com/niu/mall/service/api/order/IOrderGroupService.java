package com.niu.mall.service.api.order;

import com.niu.mall.service.core.order.param.MallOrderGroupParam;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import com.niu.mall.service.core.order.vo.MallOrderGroupCloseVo;
import com.niu.mall.service.core.order.vo.MallOrderGroupDataVo;
import com.niu.mall.service.core.order.vo.MallOrderGroupVo;

/**
 * 订单分组服务接口
 */
public interface IOrderGroupService {

    /**
     * 通过订单查询订单组
     * @param orderId
     * @return
     */
    MallOrderGroupDataVo getGroupByOrderId(Integer orderId);


    /**
     * 关闭订单组
     * @param closeParam 关闭参数
     */
    void close(Integer groupId, OrderCloseParam closeParam);
}
