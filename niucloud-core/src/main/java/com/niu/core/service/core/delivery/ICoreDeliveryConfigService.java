package com.niu.core.service.core.delivery;

import cn.hutool.json.JSONObject;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;

import java.util.List;
import java.util.Map;

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

    /**
     * 按站点业务类型过滤配送方式（对齐 PHP CoreDeliveryService::getDeliveryTypeBySiteId）
     * <p>通过 TradeType 事件读取各业务类型的 hidden_delivery_type 并移除对应 key
     *
     * @param deliveryTypeList 原始配送方式（key -> 名称或配置对象）
     * @param siteId           站点ID
     * @return 过滤后的配送方式
     */
    <T> Map<String, T> getDeliveryTypeBySiteId(Map<String, T> deliveryTypeList, Integer siteId);
}
