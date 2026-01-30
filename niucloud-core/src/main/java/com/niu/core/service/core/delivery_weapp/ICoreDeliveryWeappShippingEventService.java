package com.niu.core.service.core.delivery_weapp;

import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import com.niu.core.service.core.delivery_weapp.param.WeappShippingCreateParam;
import com.niu.core.service.core.delivery_weapp.param.WeappShippingParam;
import com.niu.core.service.core.delivery_weapp.vo.WeappShippingEventVo;
import com.niu.core.service.core.delivery_weapp.vo.WeappShippingVo;

import java.util.List;

/**
 * 小程序发货信息服务层
 */
public interface ICoreDeliveryWeappShippingEventService {

    /**
     * 发货信息录入前处理
     */
    WeappShippingEventVo beforeWeappShipping(WeappShippingParam param);

    /**
     * 微信小程序发货
     */
    Boolean weappShipping(WeappShippingParam param);

    /**
     * 发货信息录入后处理
     */
    void afterWeappShipping(Integer shippingId, Integer splitId, WxMaOrderShippingInfoBaseResponse  response,String errorMsg);

}
