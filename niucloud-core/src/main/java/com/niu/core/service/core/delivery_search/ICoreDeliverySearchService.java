package com.niu.core.service.core.delivery_search;


import com.niu.core.service.core.delivery_search.param.DeliverySearchParam;
import com.niu.core.service.core.delivery_search.vo.DeliveryTraceVo;

/**
 * 物流查询服务层
 */
public interface ICoreDeliverySearchService {
    
    /**
     * 查询物流信息
     */
    DeliveryTraceVo deliverySearch(DeliverySearchParam param);
}
