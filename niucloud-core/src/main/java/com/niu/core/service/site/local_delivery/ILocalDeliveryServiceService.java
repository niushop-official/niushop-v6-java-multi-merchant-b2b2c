package com.niu.core.service.site.local_delivery;

import com.niu.core.service.site.local_delivery.param.LocalDeliverySearchParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryConfigVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceListVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

/**
 * 同城配送服务接口
 */
public interface ILocalDeliveryServiceService {

    /**
     * 获取已启用配送服务商列表
     *
     * @return List<LocaleDeliveryServiceListVo>
     */
    List<LocalDeliveryServiceListVo> list(LocalDeliverySearchParam searchParam);
    
    /**
     * 获取配送服务商列表
     * @return 配送服务商列表
     */
    List<LocalDeliveryServiceVo> getList();

    /**
     * 获取服务商配置
     * @return 配送服务商列表
     */
    LocalDeliveryConfigVo getConfig(String deliveryType);

    /**
     * 设置配置
     * @return 配送服务商列表
     */
    void setConfig(String deliveryType, HttpServletRequest request);
}
