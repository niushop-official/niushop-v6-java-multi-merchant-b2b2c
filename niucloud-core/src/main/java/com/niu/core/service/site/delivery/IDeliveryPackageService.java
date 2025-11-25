package com.niu.core.service.site.delivery;


import com.niu.core.service.site.delivery.param.TradePackageSearchParam;
import com.niu.core.service.site.delivery.vo.DeliveryPackageInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryPackageListVo;

import java.util.List;

/**
 * 配送包裹服务层
 */
public interface IDeliveryPackageService {

    /**
     * 获取订单发货包裹列表
     */
    List<DeliveryPackageListVo> getDeliveryPackageList(String tradeNo, String tradeType);

    /**
     * 物流信息
     */
    DeliveryPackageInfoVo getDeliveryPackage(TradePackageSearchParam data, String tradeType);
}
