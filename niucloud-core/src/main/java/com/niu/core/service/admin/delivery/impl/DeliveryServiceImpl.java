package com.niu.core.service.admin.delivery.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.param.DeliverySearchConfigParam;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.core.service.admin.delivery.IDeliveryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;


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
        coreDeliveryConfigService.setDeliverySearchConfig(JSONUtil.parseObj(data));
    }

    @Override
    public AdminDeliveryConfigVo getAdminDeliveryConfig() {
        return coreDeliveryConfigService.getAdminDeliveryConfig();
    }

    @Override
    public void setAdminDeliveryConfig(AdminDeliveryConfigVo adminDeliveryConfigVo) {
        coreDeliveryConfigService.setAdminDeliveryConfig(adminDeliveryConfigVo);
    }
}
