package com.niu.core.service.site.local_delivery;

import com.niu.core.service.site.local_delivery.param.SetLocalDeliveryParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceListVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryVo;

import java.util.List;

/**
 * 同城配送服务接口
 */
public interface ILocalDeliveryService {

    /**
     * 设置同城配送
     */
    Integer setLocal(SetLocalDeliveryParam param);

    /**
     * 获取同城配送设置
     */
    LocalDeliveryVo getLocal(Integer siteId);
}
