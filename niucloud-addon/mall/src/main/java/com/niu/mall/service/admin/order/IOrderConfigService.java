package com.niu.mall.service.admin.order;

import com.niu.mall.service.core.order.param.OrderConfigParam;
import com.niu.mall.service.core.order.vo.OrderConfigVo;

/**
 * 订单设置接口
 */
public interface IOrderConfigService {

    void setConfig(OrderConfigParam orderConfigParam);

    OrderConfigVo getConfig();

}
