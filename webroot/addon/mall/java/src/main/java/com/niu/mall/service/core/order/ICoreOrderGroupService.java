package com.niu.mall.service.core.order;

import com.niu.mall.service.core.order.param.MallOrderGroupParam;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import com.niu.mall.service.core.order.vo.MallOrderGroupCloseVo;
import com.niu.mall.service.core.order.vo.MallOrderGroupDataVo;
import com.niu.mall.service.core.order.vo.MallOrderGroupVo;

/**
 * 订单分组服务接口
 */
public interface ICoreOrderGroupService {


    /**
     * 订单分组详情
     * @param groupId 主键ID
     * @return ShopGoodsLabelInfoVo
     */
    MallOrderGroupVo getInfo(Integer groupId);

    /**
     * 通过订单查询订单组
     * @param orderId
     * @return
     */
    MallOrderGroupDataVo getGroupByOrderId(Integer orderId);

    /**
     * 订单分组添加
     * @param addParam 添加参数
     */
    void add(MallOrderGroupParam addParam);

    /**
     * 订单分组取消
     * @param groupId 主键ID
     */
    void cancel(Integer groupId);

    /**
     * 校验尝试关闭订单组
     * @param orderId
     * @return
     */
    MallOrderGroupCloseVo checkCloseByOrderId(Integer orderId);
}
