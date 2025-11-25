package com.niu.mall.service.admin.order;

import com.niu.mall.service.admin.order.vo.MallOrderDeliveryInfoVo;
import com.niu.mall.service.admin.order.vo.MallOrderDeliveryListVo;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;

import java.util.List;

/**
 * 订单发货服务接口
 */
public interface IMallOrderDeliveryService {

    void delivery(OrderDeliveryParam orderDeliveryParam);

    List<MallOrderDeliveryListVo> getDeliveryPackageList(Integer orderId);

    MallOrderDeliveryInfoVo getDeliveryPackage(Integer id, String mobile);



}
