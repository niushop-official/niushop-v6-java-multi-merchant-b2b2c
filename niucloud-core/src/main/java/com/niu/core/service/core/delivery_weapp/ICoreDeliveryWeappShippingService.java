package com.niu.core.service.core.delivery_weapp;

import com.niu.core.service.core.delivery_weapp.param.WeappShippingCreateParam;
import com.niu.core.service.core.delivery_weapp.vo.WeappShippingVo;

import java.util.List;

/**
 * 小程序发货信息服务层
 */
public interface ICoreDeliveryWeappShippingService {

    /**
     * 创建小程序发货信息
     */
    Integer create(WeappShippingCreateParam param);

    /**
     * 根据发货信息id获取小程序发货信息
     */
    WeappShippingVo getShippingInfoById(List<String> siteIds, Integer shippingId);

    /**
     * 通过业务信息获取小程序发货信息
     */
    WeappShippingVo getShippingInfoByTrade(Integer tradeId, String tradeType);

}
