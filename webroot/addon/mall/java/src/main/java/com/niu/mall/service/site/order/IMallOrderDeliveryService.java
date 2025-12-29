package com.niu.mall.service.site.order;

import com.niu.mall.service.site.order.param.OrderPickUpParam;
import com.niu.mall.service.site.order.param.lOrderDeliveryFeeParam;
import com.niu.mall.service.site.order.vo.MallOrderDeliveryInfoVo;
import com.niu.mall.service.site.order.vo.MallOrderDeliveryListVo;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单发货服务接口
 */
public interface IMallOrderDeliveryService {

    void delivery(OrderDeliveryParam orderDeliveryParam);

    List<MallOrderDeliveryListVo> getDeliveryPackageList(Integer orderId);

    MallOrderDeliveryInfoVo getDeliveryPackage(Integer id, String mobile);

    BigDecimal getDeliveryFee(lOrderDeliveryFeeParam param);

    void orderPickUp(OrderPickUpParam param);
}
