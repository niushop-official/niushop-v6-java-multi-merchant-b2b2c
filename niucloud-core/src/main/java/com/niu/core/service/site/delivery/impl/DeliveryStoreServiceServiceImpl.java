
package com.niu.core.service.site.delivery.impl;

import com.niu.core.service.core.delivery_store.ICoreDeliveryStoreService;
import com.niu.core.service.core.delivery_store.param.DeliveryShopOpenParam;
import com.niu.core.service.site.delivery.IDeliveryStoreServiceService;
import com.niu.core.service.core.delivery_store.param.DeliveryStoreShopEditParam;
import com.niu.core.service.core.delivery_store.vo.DeliveryStoreListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 配送门店服务实现
 */
@Service
public class DeliveryStoreServiceServiceImpl implements IDeliveryStoreServiceService {

    @Resource
    private ICoreDeliveryStoreService coreDeliveryStoreService;

    @Override
    public List<DeliveryStoreListVo> deliveryServiceList(Integer id) {
        return coreDeliveryStoreService.deliveryServiceList(id);
    }


    @Override
    public void deliveryShopOpen(Integer id, DeliveryShopOpenParam param) {
        coreDeliveryStoreService.deliveryShopOpen(id,param);
    }

    /**
     * 服务商门店品类修改
     * @param id 地址ID
     * @param param 修改参数
     */
    @Override
    public void deliveryShopEdit(Integer id, DeliveryStoreShopEditParam param) {
        coreDeliveryStoreService.deliveryShopEdit(id,param);
    }


}
