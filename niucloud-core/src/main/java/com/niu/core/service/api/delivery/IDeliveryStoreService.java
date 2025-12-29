package com.niu.core.service.api.delivery;


import com.niu.core.service.api.delivery.param.DeliveryStoreParam;
import com.niu.core.service.api.delivery.param.TradePackageSearchParam;
import com.niu.core.service.api.delivery.vo.DeliveryPackageInfoVo;
import com.niu.core.service.api.delivery.vo.DeliveryStoreListVo;

import java.util.List;

/**
 * 配送包裹服务层
 */
public interface IDeliveryStoreService {

    /**
     * 配送门店列表
     */
    List<DeliveryStoreListVo> getLists(DeliveryStoreParam data);
}
