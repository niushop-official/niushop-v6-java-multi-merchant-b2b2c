package com.niu.core.service.core.delivery_store;

import com.niu.core.service.core.delivery_store.param.DeliveryShopOpenParam;
import com.niu.core.service.core.delivery_store.vo.DeliveryStoreListVo;
import com.niu.core.service.core.delivery_store.param.DeliveryStoreShopEditParam;

import java.util.List;

/**
 * 配送门店服务层
 */
public interface ICoreDeliveryStoreService {
    
    /**
     * 获取配送服务商列表
     */
    List<DeliveryStoreListVo> deliveryServiceList(Integer id);

    /**
     * 服务商开通门店
     */
    void deliveryShopOpen(Integer id,DeliveryShopOpenParam param);

    /**
     * 服务商门店品类修改
     * @param id 地址ID
     * @param param 修改参数
     */
    void deliveryShopEdit(Integer id, DeliveryStoreShopEditParam param);
}
