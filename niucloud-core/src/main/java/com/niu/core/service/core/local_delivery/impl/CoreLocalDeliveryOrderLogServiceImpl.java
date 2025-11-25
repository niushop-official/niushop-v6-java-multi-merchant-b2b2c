package com.niu.core.service.core.local_delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrderLog;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderLogMapper;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderLogService;
import com.niu.core.service.core.local_delivery.param.AddLocalDeliveryOrderLogParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 同城配送订单日志服务层
 */
@Service
public class CoreLocalDeliveryOrderLogServiceImpl implements ICoreLocalDeliveryOrderLogService {

    @Resource
    private DeliveryLocalDeliveryOrderLogMapper deliveryLocalDeliveryOrderLogMapper;

    @Override
    public void addLog(AddLocalDeliveryOrderLogParam param) {
        DeliveryLocalDeliveryOrderLog model = new DeliveryLocalDeliveryOrderLog();
        BeanUtil.copyProperties(param, model);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setMainId(ObjectUtil.isNotEmpty(param.getMainId()) ? param.getMainId() : 0);
        model.setMainName(ObjectUtil.isNotEmpty(param.getMainName()) ? param.getMainName() : "");
        model.setMainType(ObjectUtil.isNotEmpty(param.getMainType()) ? param.getMainType() : "");
        deliveryLocalDeliveryOrderLogMapper.insert(model);
    }
}
