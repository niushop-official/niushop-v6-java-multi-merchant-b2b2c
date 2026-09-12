package com.niu.core.service.core.local_delivery.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.LocalDeliveryLoader;
import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderNotifyService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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
            Map<String, Object> config =  coreLocalDeliveryService.getConfig(type,siteId);
            config.put("siteId", siteId);
            //第三方配送下单 驱动
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(type).init(JSONUtil.parseObj(config));
            Result<?> result = driver.notify(action, param);
            if (result != null && result.getCode() != null && result.getCode() != 1) {
                throw new CommonException(StrUtil.blankToDefault(result.getMsg(), "同城配送订单状态回调失败"));
            }
        } catch (RuntimeException e) {
            log.error("同城配送订单状态回调失败：{}" , e.getMessage());
            // 事务内吞异常会导致 rollback-only，必须向上抛出真实原因
            throw e;
        } catch (Exception e) {
            log.error("同城配送订单状态回调失败：{}" , e.getMessage());
            throw new CommonException(e.getMessage());
        }
    }
}
