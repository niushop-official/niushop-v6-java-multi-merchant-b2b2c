package com.niu.mall.service.core.order;

import com.niu.mall.service.core.order.param.MallOrderGroupParam;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import com.niu.mall.service.core.order.vo.MallOrderGroupCloseVo;
import com.niu.mall.service.core.order.vo.MallOrderGroupDataVo;
import com.niu.mall.service.core.order.vo.MallOrderGroupVo;

/**
 * 订单分组服务接口
 */
public interface ICoreOrderGroupCloseService {


    /**
     * 订单分组编辑
     * @param groupId   主键
     * @param closeParam 编辑参数
     */
    void close(Integer groupId, OrderCloseParam closeParam);

}
