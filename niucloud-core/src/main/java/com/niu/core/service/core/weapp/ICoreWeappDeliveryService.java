package com.niu.core.service.core.weapp;


import com.niu.core.service.core.delivery_weapp.vo.IsTradeManagedVo;

public interface ICoreWeappDeliveryService {

    /**
     * 查询小程序是否已开通发货信息管理服务
     * @return
     */
    IsTradeManagedVo getIsTradeManaged();
}
