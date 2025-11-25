package com.niu.core.service.core.delivery;

import cn.hutool.json.JSONObject;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;

import java.util.List;

/**
 * 配送服务接口
 */
public interface ICoreDeliveryConfigService {


    /**
     * 获取物流查询接口配置
     *
     */
    DeliverySearchConfigVo getDeliverySearchConfig();

    /**
     * 设置物流查询接口配置
     *
     * @param data
     */
    void setDeliverySearchConfig(JSONObject data);

    AdminDeliveryConfigVo getAdminDeliveryConfig();

    void setAdminDeliveryConfig(AdminDeliveryConfigVo adminDeliveryConfigVo);

    List<DeliveryConfigVo> getDeliveryConfig(Integer siteId);

    void setConfig(Integer id, JSONObject entries);
}
