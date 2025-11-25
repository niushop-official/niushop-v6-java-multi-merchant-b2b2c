package com.niu.core.service.site.delivery;

import com.niu.core.service.core.delivery.param.DeliverySearchConfigParam;
import com.niu.core.service.site.delivery.param.DeliveryConfigParam;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;

import java.util.List;
import java.util.Map;

/**
 * 配送方式相关接口
 */
public interface IDeliveryService {


    /**
     * 获取物流查询接口配置
     */
    DeliverySearchConfigVo getDeliverySearchConfig();

    /**
     * 设置物流查询接口配置
     * @param data
     */
    void setDeliverySearchConfig(DeliverySearchConfigParam data);

    List<DeliveryConfigVo> getDeliveryList();

    Map<String, String> getDeliveryType();

    /**
     * 设置配送配置
     * @param param 配送配置参数
     */
    void setConfig(DeliveryConfigParam param);

}
