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
public interface ICoreConfigService {


    Map<String, Object> getDeliveryList();
}
