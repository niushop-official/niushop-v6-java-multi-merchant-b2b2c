package com.niu.core.service.core.local_delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.LocalDeliveryLoader;
import com.niu.core.common.loader.local_delivery.param.AddShopDeliveryOrderParam;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.*;
import com.niu.core.enums.order.OrderDeliveryEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderCreateService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderLogService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryService;
import com.niu.core.service.core.local_delivery.param.AddLocalDeliveryOrderLogParam;
import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCreateParam;
import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCreateSave;
import com.niu.core.service.core.shop_delivery.vo.ShopDeliveryOrderCreateVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.niu.core.common.utils.CommonUtils.createNo;

/**
 * 同城配送事件服务实现
 */
@Log4j2
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
    @Resource
    private DeliveryStoreMapper deliveryStoreMapper;

    @Override
    @Transactional
    public void create(LocalDeliveryOrderCreateParam param) {
        LocalDeliveryOrderCreateParam.OrderDataInfo orderData = param.getOrderData();
        Integer siteId = orderData.getSiteId();
        Integer storeId = ObjectUtil.isEmpty(param.getStoreId()) ? orderData.getTakeStoreId() : param.getStoreId();
        String localDeliveryNo = param.getLocalDeliveryNo();
        String deliveryService = param.getLocalDeliveryType();
        Map<String, Object> config = checkDeliveryService(siteId,storeId, deliveryService);

        LocalDeliveryOrderCreateSave save = new LocalDeliveryOrderCreateSave();
        save.setSiteId(siteId);
        save.setStoreId(storeId);
        save.setDeliveryService(deliveryService);
        save.setDeliveryNo(localDeliveryNo);
        save.setStatus(LocalDeliveryStatusEnum.ORDER_ACCEPTED.getStatus());
        save.setTradeId(orderData.getOrderId());
        save.setTradeNo(param.getTradeNo());
        save.setTradeType(param.getTradeType());
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
                config.put("shop_no", param.getStoreNo());
            }
            config.put("siteId",siteId);
            //第三方配送下单 驱动
            try {
                BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(deliveryService).init(JSONUtil.parseObj(config));
                List<AddShopDeliveryOrderParam.OrderGoodsInfo> orderGoodsData = new ArrayList<>();
                for (int i = 0; i < param.getOrderGoodsData().size(); i++){
                    AddShopDeliveryOrderParam.OrderGoodsInfo goods = new AddShopDeliveryOrderParam.OrderGoodsInfo();
                    BeanUtils.copyProperties(param.getOrderGoodsData().get(i), goods);
                    orderGoodsData.add(goods);
                }
                AddShopDeliveryOrderParam addShopDeliveryOrderParam = new AddShopDeliveryOrderParam();
                AddShopDeliveryOrderParam.OrderDateInfo vo = new AddShopDeliveryOrderParam.OrderDateInfo();

                BeanUtils.copyProperties(orderData, vo);
                addShopDeliveryOrderParam.setDeliveryId(param.getLocalDeliveryId());
                addShopDeliveryOrderParam.setOrderDate(vo);
                addShopDeliveryOrderParam.setOrderGoodsData(orderGoodsData);
                addShopDeliveryOrderParam.setDeliveryNo(localDeliveryNo);
                addShopDeliveryOrderParam.setRemark(param.getRemark());
                addShopDeliveryOrderParam.setBody(param.getBody());
                addShopDeliveryOrderParam.setCreateParam(BeanUtil.beanToMap(param));
                addShopDeliveryOrderParam.setNotifyUrl(coreLocalDeliveryService.buildNotifyUrl(siteId.toString(), deliveryService, localDeliveryNo, null));
                addShopDeliveryOrderParam.setTradeNo(param.getTradeNo());
                addShopDeliveryOrderParam.setTradeType(param.getTradeType());
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
            }catch (Exception e){
                throw new CommonException(e.getMessage());
            }
        }
    }

    @Override
    public BigDecimal getDeliveryFee(LocalDeliveryOrderCreateParam param) {
        Integer siteId = param.getOrderData().getSiteId();
        Integer storeId = ObjectUtil.isEmpty(param.getStoreId()) ? param.getOrderData().getTakeStoreId() : param.getStoreId();
        if(ObjectUtil.isEmpty(param.getLocalDeliveryType())){
            throw new CommonException("请选择配送平台");
        }
        String deliveryService = param.getLocalDeliveryType();
        Map<String, Object> config = checkDeliveryService(siteId,storeId, deliveryService);
        String deliveryNo = createNo();
        BigDecimal deliveryFee = BigDecimal.ZERO;
        try {
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(deliveryService).init(JSONUtil.parseObj(config));
            List<AddShopDeliveryOrderParam.OrderGoodsInfo> orderGoodsData = new ArrayList<>();
            for (int i = 0; i < param.getOrderGoodsData().size(); i++){
                AddShopDeliveryOrderParam.OrderGoodsInfo goods = new AddShopDeliveryOrderParam.OrderGoodsInfo();
                BeanUtils.copyProperties(param.getOrderGoodsData().get(i), goods);
                orderGoodsData.add(goods);
            }

            AddShopDeliveryOrderParam addShopDeliveryOrderParam = new AddShopDeliveryOrderParam();
            AddShopDeliveryOrderParam.OrderDateInfo vo = new AddShopDeliveryOrderParam.OrderDateInfo();
            BeanUtils.copyProperties(param.getOrderData(), vo);
            addShopDeliveryOrderParam.setOrderDate(vo);
            addShopDeliveryOrderParam.setDeliveryNo(deliveryNo);
            addShopDeliveryOrderParam.setCreateParam(BeanUtil.beanToMap(param));
            addShopDeliveryOrderParam.setOrderGoodsData(orderGoodsData);
            addShopDeliveryOrderParam.setNotifyUrl(coreLocalDeliveryService.buildNotifyUrl(siteId.toString(), deliveryService, "", null));
            ShopDeliveryOrderCreateVo responseData = driver.calculate(addShopDeliveryOrderParam);
            if(ObjectUtil.isNotEmpty(responseData) && ObjectUtil.isNotEmpty(responseData.getDeliveryMoney()) ){
                deliveryFee = responseData.getDeliveryMoney();
            }
        }catch (Exception e){
            throw new CommonException(e.getMessage());
        }
        return deliveryFee;
    }

    private Integer createOrder(LocalDeliveryOrderCreateSave vo){

        // 获取站点地址
        DeliveryStore siteAddress = deliveryStoreMapper.selectOne(new QueryWrapper<DeliveryStore>()
                .eq("site_id", vo.getSiteId()).eq("store_id", vo.getStoreId()).last("limit 1"));

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
        order.setDeliveryStartLng(siteAddress.getLongitude());
        order.setDeliveryStartLat(siteAddress.getLatitude());

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

    private Map<String, Object> checkDeliveryService(Integer siteId, Integer storeId, String deliveryService){
        Map<String, Object> config = new HashMap<>();
        DeliveryStore store = deliveryStoreMapper.selectOne(new QueryWrapper<DeliveryStore>()
                .eq("site_id", siteId).eq("store_id", storeId).last("limit 1"));
        if (ObjectUtil.isEmpty(store)) throw new CommonException("提货点不存在");
        if(!DeliveryStoreTypeEnum.MERCHANT.getKey().equals(deliveryService)){
            String deliveryServiceName = LocalDeliveryTypeEnum.getName(deliveryService);
            config = coreLocalDeliveryService.getConfig(deliveryService,siteId);

            if(store.getSupportDelivery().contains(deliveryService) && store.getSupportDelivery().contains("open_status")){
                String openStatus = JSONUtil.parseObj(store.getSupportDelivery()).getJSONObject(deliveryService).getStr("open_status");
                if(!LocalDeliveryOpenEnum.OPEN_PASS.getStatus().toString().equals(openStatus)){
                    throw new CommonException("门店未开通"+deliveryServiceName);
                }
            }

        }
        config.put("shop_no", store.getStoreNo());
        return config;
    }
}
