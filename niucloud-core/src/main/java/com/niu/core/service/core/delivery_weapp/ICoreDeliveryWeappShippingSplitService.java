package com.niu.core.service.core.delivery_weapp;

import com.niu.core.service.core.delivery_weapp.param.WeappShippingSpliteCreateParam;
import com.niu.core.service.core.delivery_weapp.vo.WeappShippingSplitVo;


/**
 * 小程序发货信息服务层
 */
public interface ICoreDeliveryWeappShippingSplitService {

    /**
     * 创建小程序发货信息
     */
    Integer create(WeappShippingSpliteCreateParam param);

    /**
     * 通过业务信息获取小程序发货信息
     */
    WeappShippingSplitVo getShippingInfoByTrade(Integer siteId, Integer splitTradeId, String splitTradeType);
}
