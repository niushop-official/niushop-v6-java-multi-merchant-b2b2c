package com.niu.core.service.core.local_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.LocalDeliveryLoader;
import com.niu.core.common.loader.local_delivery.param.CloseShopDeliveryOrderParam;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrder;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryOrderCancelReasonEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderCloseService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryService;
import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCloseParam;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 同城配送事件服务实现
 */
@Service
public class CoreLocalDeliveryOrderCloseServiceImpl implements ICoreLocalDeliveryOrderCloseService {

    @Resource
    private DeliveryLocalDeliveryOrderMapper deliveryLocalDeliveryOrderMapper;
    @Resource
    private ICoreLocalDeliveryService coreLocalDeliveryService;

    @Override
    @Transactional
    public void closeOrder(LocalDeliveryOrderCloseParam  param) {
        QueryWrapper<DeliveryLocalDeliveryOrder> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(param.getId())){
            queryWrapper.eq("id", param.getId());
        }else{
            queryWrapper.like("trade_no", param.getTradeNo());
            queryWrapper.eq("trade_type", param.getTradeType());
            queryWrapper.last("limit 1");
        }
        DeliveryLocalDeliveryOrder info = deliveryLocalDeliveryOrderMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty( info)) throw new CommonException("DELIVERY_ORDER_NOT_EXIT");
        String deliveryService = info.getDeliveryService();

        DeliveryLocalDeliveryOrder updateData = new DeliveryLocalDeliveryOrder();
        updateData.setRemark(ObjectUtil.isNotEmpty(param.getCancelReasonId()) && param.getCancelReasonId() > 0 ? LocalDeliveryOrderCancelReasonEnum.getTypeName(param.getCancelReasonId().toString(),deliveryService) : param.getCancelReason());

        Map<String, Object> config = new HashMap<>();
        config.put("siteId", info.getSiteId());
        //第三方配送下单 驱动
        BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(deliveryService).init(JSONUtil.parseObj(config));

        CloseShopDeliveryOrderParam addShopDeliveryOrderParam = new CloseShopDeliveryOrderParam();
        addShopDeliveryOrderParam.setDeliveryNo(info.getDeliveryNo());
        addShopDeliveryOrderParam.setCancelReason(param.getCancelReason());
        addShopDeliveryOrderParam.setStatus(info.getStatus());
        addShopDeliveryOrderParam.setMainId(param.getMainId());
        addShopDeliveryOrderParam.setMainName(param.getMainName());
        DeliveryShopDeliveryOrder responseData = driver.closeOrder(addShopDeliveryOrderParam);

        if(ObjectUtil.isNotEmpty( responseData)){
            BeanUtils.copyProperties(responseData, updateData);
            updateData.setId(info.getId());
            deliveryLocalDeliveryOrderMapper.updateById(updateData);
        }
    }
}
