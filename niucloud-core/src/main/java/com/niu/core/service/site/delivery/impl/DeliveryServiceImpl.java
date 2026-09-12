package com.niu.core.service.site.delivery.impl;

import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.order.OrderDeliveryEnum;
import com.niu.core.service.site.delivery.IDeliveryService;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.param.DeliverySearchConfigParam;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.core.service.site.delivery.param.DeliveryConfigParam;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.Map;


/**
 * 配送员实现
 */
@Service
public class DeliveryServiceImpl implements IDeliveryService {
        
    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;

    /**
     * 获取物流查询接口配置
     */
    @Override
    public DeliverySearchConfigVo getDeliverySearchConfig() {
        return coreDeliveryConfigService.getDeliverySearchConfig();
    }

    /**
     * 设置物流查询接口配置
     */
    @Override
    public void setDeliverySearchConfig(DeliverySearchConfigParam data) {
        coreDeliveryConfigService.setDeliverySearchConfig( JSONUtil.parseObj(data));
    }

    @Override
    public List<DeliveryConfigVo> getDeliveryList() {
        return coreDeliveryConfigService.getDeliveryConfig(RequestUtils.siteId());
    }

    @Override
    public Map<String, String> getDeliveryType() {
        // 对齐 PHP DeliveryService::getDeliveryType
        Map<String, String> deliveryTypeList = OrderDeliveryEnum.getType();
        return coreDeliveryConfigService.getDeliveryTypeBySiteId(deliveryTypeList, RequestUtils.siteId());
    }

    @Override
    public void setConfig(DeliveryConfigParam param) {
        cn.hutool.json.JSONObject data = new cn.hutool.json.JSONObject();
        if (param.getValue() != null) {
            for (int i = 0; i < param.getValue().size(); i++) {
                cn.hutool.json.JSONObject item = param.getValue().getJSONObject(i);
                String key = item.getStr("key");
                if (key != null) {
                    data.put(key, item);
                }
            }
        }
        coreDeliveryConfigService.setConfig(RequestUtils.siteId(), data);
    }
}
