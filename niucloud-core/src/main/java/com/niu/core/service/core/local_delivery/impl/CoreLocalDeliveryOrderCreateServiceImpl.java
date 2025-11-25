package com.niu.core.service.core.local_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.LocalDeliveryLoader;
import com.niu.core.common.loader.local_delivery.param.AddShopDeliveryOrderParam;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.*;
import com.niu.core.enums.order.OrderDeliveryEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderCreateService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderLogService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryService;
import com.niu.core.service.core.local_delivery.param.AddLocalDeliveryOrderLogParam;
import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCreateParam;
import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCreateSave;
import com.niu.core.service.core.shop_delivery.vo.ShopDeliveryOrderCreateVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 同城配送事件服务实现
 */
@Service
public class CoreLocalDeliveryOrderCreateServiceImpl implements ICoreLocalDeliveryOrderCreateService {

    @Resource
    private DeliveryLocalDeliveryOrderMapper deliveryLocalDeliveryOrderMapper;
    @Resource
    private ICoreLocalDeliveryService coreLocalDeliveryService;
    @Resource
    private SiteAddressMapper siteAddressMapper;
    @Resource
    private ICoreLocalDeliveryOrderLogService coreLocalDeliveryOrderLogService;

    @Override
    @Transactional
    public void create(LocalDeliveryOrderCreateParam param) {
        LocalDeliveryOrderCreateParam.OrderDataInfo orderData = param.getOrderData();
        Integer siteId = orderData.getSiteId();
        Integer storeId = orderData.getTakeStoreId();
        String localDeliveryNo = param.getLocalDeliveryNo();
        String deliveryService = param.getLocalDeliveryType();
        Map<String, Object> config = checkDeliveryService(siteId, deliveryService);

        LocalDeliveryOrderCreateSave save = new LocalDeliveryOrderCreateSave();
        save.setSiteId(siteId);
        save.setStoreId(storeId);
        save.setDeliveryService(deliveryService);
        save.setDeliveryNo(localDeliveryNo);
        save.setStatus(LocalDeliveryStatusEnum.ORDER_ACCEPTED.getStatus());
        save.setTradeId(orderData.getOrderId());
        save.setTradeNo(orderData.getOrderNo());
        save.setTradeType(orderData.getOrderType());
        save.setDeliveryEnd(orderData.getTakerFullAddress());
        save.setDeliveryEndProvinceId(orderData.getTakerProvinceId());
        save.setDeliveryEndCityId(orderData.getTakerCityId());
        save.setDeliveryEndDistrictId(orderData.getTakerDistrictId());
        save.setDeliveryEndLng(orderData.getTakerLongitude());
        save.setDeliveryEndLat(orderData.getTakerLatitude());
        save.setBody(orderData.getBody());

        Integer orderId = createOrder(save);
        if(orderId != null){
            if(StringUtils.isNotEmpty(param.getStoreNo())){
                config.put("store_no", param.getStoreNo());
            }
            config.put("siteId",siteId);
            //第三方配送下单 驱动
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(deliveryService).init(JSONUtil.parseObj(config));

            AddShopDeliveryOrderParam addShopDeliveryOrderParam = new AddShopDeliveryOrderParam();
            AddShopDeliveryOrderParam.OrderDateInfo vo = new AddShopDeliveryOrderParam.OrderDateInfo();
            BeanUtils.copyProperties(orderData, vo);
            addShopDeliveryOrderParam.setDeliveryId(param.getLocalDeliveryId());
            addShopDeliveryOrderParam.setOrderDate(vo);
            addShopDeliveryOrderParam.setDeliveryNo(localDeliveryNo);
            addShopDeliveryOrderParam.setRemark(param.getRemark());
            addShopDeliveryOrderParam.setBody(param.getBody());
            ShopDeliveryOrderCreateVo responseData = driver.createOrder(addShopDeliveryOrderParam);

            if(ObjectUtil.isEmpty( responseData)){
                throw new CommonException("呼叫配送失败");
            }

            DeliveryLocalDeliveryOrder update = new DeliveryLocalDeliveryOrder();
            update.setId(orderId);
            update.setOutDeliveryNo(responseData.getOutDeliveryNo());
            update.setDeliveryMoney(responseData.getDeliveryMoney());
            update.setDeliveryDistance(responseData.getDeliveryDistance());
            update.setRiderName(responseData.getRiderName());
            update.setRiderMobile(responseData.getRiderMobile());
            deliveryLocalDeliveryOrderMapper.updateById(update);
        }
    }

    private Integer createOrder(LocalDeliveryOrderCreateSave vo){

        // 获取站点地址
        SiteAddress siteAddress = siteAddressMapper.selectOne(new QueryWrapper<SiteAddress>()
                .eq("site_id", vo.getSiteId()).eq("is_default_delivery", 1).last("limit 1"));

        // 构建订单数据
        DeliveryLocalDeliveryOrder order = new DeliveryLocalDeliveryOrder();
        order.setSiteId(vo.getSiteId());
        order.setDeliveryService(vo.getDeliveryService());
        order.setDeliveryNo(vo.getDeliveryNo());
        order.setOutDeliveryNo("");
        order.setDeliveryMoney(BigDecimal.ZERO);
        order.setDeliveryDistance(BigDecimal.ZERO);
        order.setStatus(vo.getStatus().toString());
        order.setOutStatus("");
        order.setRiderName("");
        order.setRiderMobile("");
        order.setTradeId(ObjectUtil.isNotEmpty(vo.getTradeId()) ? vo.getTradeId() : 0);
        order.setTradeNo(vo.getTradeNo());
        order.setTradeType(vo.getTradeType());

        // 设置配送起始地址信息
        order.setDeliveryStart(siteAddress.getFullAddress());
        order.setDeliveryStartProvinceId(siteAddress.getProvinceId());
        order.setDeliveryStartCityId(siteAddress.getCityId());
        order.setDeliveryStartDistrictId(siteAddress.getDistrictId());
        order.setDeliveryStartLng(siteAddress.getLng());
        order.setDeliveryStartLat(siteAddress.getLat());

        // 设置配送结束地址信息
        order.setDeliveryEnd(vo.getDeliveryEnd());
        order.setDeliveryEndProvinceId(vo.getDeliveryEndProvinceId());
        order.setDeliveryEndCityId(vo.getDeliveryEndCityId());
        order.setDeliveryEndDistrictId(vo.getDeliveryEndDistrictId());
        order.setDeliveryEndLng(vo.getDeliveryEndLng());
        order.setDeliveryEndLat(vo.getDeliveryEndLat());

        order.setRemark("");
        order.setBody(vo.getBody());
        order.setCreateTime(System.currentTimeMillis() / 1000);

        // 保存订单记录
        deliveryLocalDeliveryOrderMapper.insert(order);
        //添加配送订单日志
        LocalDeliveryOperateEnum.OperateResult operateResult = LocalDeliveryOperateEnum.getOperateByStatus(vo.getStatus());
        AddLocalDeliveryOrderLogParam addLocalDeliveryOrderLogParam = new AddLocalDeliveryOrderLogParam();
        addLocalDeliveryOrderLogParam.setOrderId(order.getId());
        addLocalDeliveryOrderLogParam.setMainId(0);
        addLocalDeliveryOrderLogParam.setMainType(OrderDeliveryEnum.STORE.getValue());
        addLocalDeliveryOrderLogParam.setOperate(operateResult.getOperate());
        addLocalDeliveryOrderLogParam.setOperateDesc(operateResult.getOperateDesc());
        addLocalDeliveryOrderLogParam.setStatus(order.getStatus());
        addLocalDeliveryOrderLogParam.setRemark(operateResult.getOperateDesc());
        coreLocalDeliveryOrderLogService.addLog(addLocalDeliveryOrderLogParam);
        return order.getId();
    }

    private Map<String, Object> checkDeliveryService(Integer siteId, String deliveryService){
        Map<String, Object> config = new HashMap<>();
        SiteAddress siteAddress = siteAddressMapper.selectOne(new QueryWrapper<SiteAddress>()
                .eq("site_id", siteId).eq("is_default_delivery", 1).last("limit 1"));
        if (ObjectUtil.isEmpty(siteAddress)) throw new CommonException("NOT_SET_DEFAULT_DELIVERY_ADDRESS");

        if(!DeliveryStoreTypeEnum.MERCHANT.getKey().equals(deliveryService)){
            String deliveryServiceName = LocalDeliveryTypeEnum.getName(deliveryService);
            Map<String, Object> localDeliveryConfig = coreLocalDeliveryService.getConfig(deliveryService);
            if (localDeliveryConfig.isEmpty() || (ObjectUtil.isNotEmpty(localDeliveryConfig) && !localDeliveryConfig.containsKey("config")))   throw new RuntimeException(deliveryServiceName + "未配置");
            if (StringUtils.isEmpty(siteAddress.getSupportDelivery()) || !siteAddress.getSupportDelivery().contains(deliveryService)){
                throw new CommonException("门店未开通"+deliveryServiceName);
            }
            if(siteAddress.getSupportDelivery().contains(deliveryService) && siteAddress.getSupportDelivery().contains("open_status")){
                String openStatus = JSONUtil.parseObj(siteAddress.getSupportDelivery()).getJSONObject(deliveryService).getStr("open_status");
                if(!LocalDeliveryOpenEnum.OPEN_PASS.getStatus().toString().equals(openStatus)){
                    throw new CommonException("门店未开通"+deliveryServiceName);
                }
            }
            config = (Map<String, Object>) localDeliveryConfig.getOrDefault("config", new HashMap<>());
        }

        config.put("shop_no", siteAddress.getShopNo());
        return config;
    }
}
