package com.niu.core.service.api.delivery;

import com.niu.core.service.core.delivery.param.DeliverySearchConfigParam;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryVo;

/**
 * 配送方式相关接口
 */
public interface IConfigService {

    AdminDeliveryConfigVo getAdminDeliveryConfig();


    LocalDeliveryVo getLocal(Integer siteId);
}
