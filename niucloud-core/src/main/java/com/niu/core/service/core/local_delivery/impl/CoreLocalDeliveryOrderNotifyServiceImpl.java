package com.niu.core.service.core.local_delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.LocalDeliveryLoader;
import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrderLog;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderLogMapper;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderLogService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderNotifyService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryService;
import com.niu.core.service.core.local_delivery.param.AddLocalDeliveryOrderLogParam;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 同城配送订单日志服务层
 */
@Log4j2
@Service
public class CoreLocalDeliveryOrderNotifyServiceImpl implements ICoreLocalDeliveryOrderNotifyService {

    @Resource
    private ICoreLocalDeliveryService coreLocalDeliveryService;
    @Override
    public void notify(Integer siteId, String type,String orderNo, String action, NotifyShopDeliveryOrderParam param) {
        log.info("同城配送订单状态回调参数：{}" , JSONUtil.parseObj(param));
        try {
            Map<String, Object> config = new HashMap<>();
            if(!DeliveryStoreTypeEnum.MERCHANT.getKey().equals(type)){
                Map<String, Object> localDeliveryConfig = coreLocalDeliveryService.getConfig(type,siteId);
                if (localDeliveryConfig.isEmpty()) {
                    String typeName = LocalDeliveryTypeEnum.getName(type) != null ? LocalDeliveryTypeEnum.getName(type) : type;
                    throw new RuntimeException(typeName + "未配置");
                }
                config = JSONUtil.parseObj(localDeliveryConfig.get("config"));
            }
            config.put("siteId", siteId);
            //第三方配送下单 驱动
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(type).init(JSONUtil.parseObj(config));
            driver.notify(action, param);
        }catch (Exception e){
            log.error("同城配送订单状态回调失败：{}" , e.getMessage());
        }
    }
}
