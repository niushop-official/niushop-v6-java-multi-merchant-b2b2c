package com.niu.core.service.api.delivery;


import com.niu.core.service.api.delivery.param.TradePackageSearchParam;
import com.niu.core.service.api.delivery.vo.DeliveryPackageInfoVo;

/**
 * 配送包裹服务层
 */
public interface IDeliveryPackageService {

    /**
     * 物流信息
     */
    DeliveryPackageInfoVo getDeliveryPackage(TradePackageSearchParam data, String tradeType);
}
