package com.niu.core.service.core.delivery_package;


import com.niu.core.service.core.delivery_search.param.DeliverySearchParam;
import com.niu.core.service.core.delivery_search.vo.DeliveryTraceVo;
import com.niu.core.service.core.delivery_package.vo.DeliveryPackageVo;

import java.util.List;
import java.util.Map;

/**
 * 物流查询服务层
 */
public interface ICoreDeliveryPackageService {

    String create(Map<String, Object> data, String tradeType);

    List<DeliveryPackageVo> getDeliveryPackageByCodes(List<String> codes, String tradeType);
    
    List<DeliveryPackageVo> getPackageListOfTrade(Integer orderId, String tradeType);

}
