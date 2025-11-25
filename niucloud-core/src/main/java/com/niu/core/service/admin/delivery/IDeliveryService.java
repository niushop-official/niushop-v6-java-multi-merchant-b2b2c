package com.niu.core.service.admin.delivery;

import cn.hutool.json.JSONArray;
import com.niu.core.service.core.delivery.param.DeliverySearchConfigParam;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;

import java.util.List;

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

    AdminDeliveryConfigVo getAdminDeliveryConfig();

    void setAdminDeliveryConfig(AdminDeliveryConfigVo adminDeliveryConfigVo);
}
