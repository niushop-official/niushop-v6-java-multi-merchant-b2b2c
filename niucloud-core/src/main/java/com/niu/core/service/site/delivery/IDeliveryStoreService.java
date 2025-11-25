package com.niu.core.service.site.delivery;

import com.niu.core.service.core.delivery_store.param.DeliveryStoreShopEditParam;
import com.niu.core.service.core.delivery_store.param.DeliveryShopOpenParam;
import com.niu.core.service.core.delivery_store.vo.DeliveryStoreListVo;

import java.util.List;

/**
 * 配送方式相关接口
 */
public interface IDeliveryStoreService {

    /**
     * 获取配送服务商列表
     * @param id 地址ID
     * @return 配送服务商列表
     */
    List<DeliveryStoreListVo> deliveryServiceList(Integer id);

    /**
     * 服务商开通门店
     * @param id 地址ID
     * @param param 开通参数
     */
    void deliveryShopOpen(Integer id, DeliveryShopOpenParam param);

    /**
     * 服务商门店品类修改
     * @param id 地址ID
     * @param param 修改参数
     */
    void deliveryShopEdit(Integer id, DeliveryStoreShopEditParam param);

}
