package com.niu.core.service.core.delivery_store.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.LocalDeliveryLoader;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryService;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.delivery.DeliveryStoreStatusEnum;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryServiceMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.delivery_store.ICoreDeliveryStoreService;
import com.niu.core.service.core.delivery_store.param.DeliveryShopOpenParam;
import com.niu.core.service.core.delivery_store.param.DeliveryStoreShopEditParam;
import com.niu.core.service.core.delivery_store.param.DeliverySysStoreAddParam;
import com.niu.core.service.core.delivery_store.vo.DeliveryStoreListVo;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.niu.core.common.utils.CommonUtils.createNo;

/**
 * 物流查询服务层
 */
@Log4j2
@Service
public class CoreDeliveryStoreServiceImpl implements ICoreDeliveryStoreService {

    @Resource
    private DeliveryStoreMapper deliveryStoreMapper;
    @Resource
    private DeliveryLocalDeliveryServiceMapper deliveryLocalDeliveryServiceMapper;
    @Resource
    private ICoreLocalDeliveryService coreLocalDeliveryService;
    @Resource
    private SiteMapper siteMapper;
    @Resource
    private SiteShopMapper siteShopMapper;

    @Override
    public List<DeliveryStoreListVo> deliveryServiceList(Integer id) {
        DeliveryStore info = deliveryStoreMapper.selectById(id);
        if (info == null) {
            throw new RuntimeException("提货点不存在");
        }
        Map<String, Map<String, Object>> list = jsonToNestedMap(LocalDeliveryTypeEnum.getType());
        QueryWrapper<DeliveryLocalDeliveryService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", info.getSiteId())
                .in("`key`", list.keySet())
                .eq("is_use", 1);
        List<DeliveryLocalDeliveryService> enabledServices = deliveryLocalDeliveryServiceMapper.selectList(queryWrapper);
        Set<String> keys = enabledServices.stream()
                .map(DeliveryLocalDeliveryService::getKey)
                .collect(Collectors.toSet());
        JSONObject supportDeliveryArray = new JSONObject();
        if (info.getSupportDelivery() != null && !info.getSupportDelivery().trim().isEmpty()) {
            try {
                supportDeliveryArray = JSONUtil.parseObj(info.getSupportDelivery());
            } catch (Exception e) {
                supportDeliveryArray = new JSONObject();
            }
        }
        List<DeliveryStoreListVo> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : list.entrySet()) {
            String k = entry.getKey();
            Map<String, Object> v = entry.getValue();
            DeliveryStoreListVo vo = new DeliveryStoreListVo();
            vo.setKey(k);
            vo.setName((String) v.get("name"));
            vo.setIsMerchant((Integer) v.get("is_merchant") == 1);
            vo.setComponent(ObjectUtil.isNotEmpty(v.get("component")) ? v.get("component").toString() : "");
            vo.setBusinessList(ObjectUtil.isNotEmpty(v.get("business_list")) ? v.get("business_list").toString() : "");
            if ((Integer) v.get("is_merchant") == 1) {
                vo.setOpenStatus(DeliveryStoreStatusEnum.OPEN_PASS.getStatus());
                vo.setBusiness("");
                vo.setEditStatus("");
                vo.setReason("");
            } else {
                if (keys.contains(k)) {
                    JSONObject supportInfoObj = JSONUtil.parseObj(supportDeliveryArray.get(k));
                    vo.setOpenStatus(supportInfoObj.get("open_status") != null ? supportInfoObj.getInt("open_status") : null);
                    vo.setBusiness(supportInfoObj.get("business") != null ? (String) supportInfoObj.getStr("business") : "");
                    vo.setEditStatus(StringUtils.isNotEmpty(supportInfoObj.getStr("edit_status")) ? supportInfoObj.getInt("edit_status").toString() : "");
                    vo.setReason(supportInfoObj.get("reason") != null ? supportInfoObj.getStr("reason") : "");
                } else {
                    continue;
                }
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public void deliveryShopOpen(Integer id,DeliveryShopOpenParam param) {
        DeliveryStore info = deliveryStoreMapper.selectById(id);
        if (info == null) {
            throw new RuntimeException("提货点不存在");
        }
        String deliveryType = param.getDeliveryType();
        Map<String, Object> config = coreLocalDeliveryService.getConfig(deliveryType,info.getSiteId());
        try {
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("delivery_type", deliveryType);
            requestData.put("business", param.getBusiness());
            requestData.put("delivery_store", info);
            //第三方配送下单 驱动
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(deliveryType).init(JSONUtil.parseObj(config));
            Map<String, Object> responseData = driver.addShop(requestData);

            if (!responseData.isEmpty()) {
                JSONObject supportDelivery = JSONUtil.parseObj(responseData.get("support_delivery"));
                if (!supportDelivery.isEmpty()) {
                    Map<String, Object> currentSupportDelivery = new HashMap<>();
                    if (StringUtils.isNotEmpty(info.getSupportDelivery())) {
                        currentSupportDelivery = JSONUtil.parseObj(info.getSupportDelivery());
                    }
                    currentSupportDelivery.putAll(supportDelivery);
                    info.setSupportDelivery(JSONUtil.toJsonStr(currentSupportDelivery));
                }

                if (param.getExtendData() != null) {
                    Map<String, Object> currentExtendData = new HashMap<>();
                    if (info.getExtendData() != null && !info.getExtendData().isEmpty()) {
                        try {
                            currentExtendData = JSONUtil.parseObj(info.getExtendData());
                        } catch (Exception e) {
                            // 如果解析失败，使用空Map
                            currentExtendData = new HashMap<>();
                        }
                    }
                    currentExtendData.putAll(param.getExtendData());
                    info.setExtendData(JSON.toJSONString(currentExtendData));
                }

                info.setStoreId(id);
                deliveryStoreMapper.updateById(info);
            }
        }catch (Exception e){
            throw new RuntimeException( StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "添加店铺失败");
        }
    }

    @Override
    public void deliveryShopEdit(Integer id, DeliveryStoreShopEditParam param) {
        DeliveryStore info = deliveryStoreMapper.selectById(id);
        if (info == null) {
            throw new RuntimeException("提货点不存在");
        }
        String deliveryType = param.getDeliveryType();
        Map<String, Object> config = coreLocalDeliveryService.getConfig(deliveryType,info.getSiteId());

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("delivery_type", deliveryType);
        requestData.put("business", param.getBusiness());
        requestData.put("delivery_store", info);
        try {
            //第三方配送下单 驱动
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(deliveryType).init(JSONUtil.parseObj(config));
            Map<String, Object> responseData = driver.editShop(requestData);
            if (!responseData.isEmpty()) {
                JSONObject supportDelivery = (JSONObject) responseData.get("support_delivery");
                info.setSupportDelivery(supportDelivery.toString());
                deliveryStoreMapper.updateById( info);
            }
        }catch (Exception e){
            throw new RuntimeException("编辑店铺失败");
        }
    }

    @Override
    @Transactional
    public void batchDeliveryShopEdit(DeliveryStore store) {
        if (ObjectUtil.isEmpty( store)) {
            throw new RuntimeException("提货点不存在");
        }
        try {
            JSONObject supportDelivery = StringUtils.isNotEmpty(store.getSupportDelivery()) ? JSONUtil.parseObj(store.getSupportDelivery()) : new JSONObject();
            JSONObject deliveryTypeObj = LocalDeliveryTypeEnum.getType();

            for (String deliveryType : deliveryTypeObj.keySet()) {
                if (!DeliveryStoreTypeEnum.MERCHANT.getKey().equals(deliveryType) && ObjectUtil.isNotEmpty(supportDelivery.getStr(deliveryType)) && supportDelivery.getStr(deliveryType).contains("open_status")){
                    JSONObject supportDeliveryArray = JSONUtil.parseObj(supportDelivery.getStr(deliveryType));
                    if(DeliveryStoreStatusEnum.OPEN_PASS.getStatus().equals(supportDeliveryArray.getInt("open_status"))){
                        Map<String, Object> config = coreLocalDeliveryService.getConfig(deliveryType,store.getSiteId());
                        Map<String, Object> requestData = new HashMap<>();
                        requestData.put("delivery_type", deliveryType);
                        requestData.put("business", supportDeliveryArray.getStr("business"));
                        requestData.put("delivery_store", store);

                        //第三方配送下单 驱动
                        BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(deliveryType).init(JSONUtil.parseObj(config));
                        Map<String, Object> responseData = driver.editShop(requestData);
                        if (!responseData.isEmpty()) {
                            JSONObject supportDeliveryResponse = JSONUtil.parseObj(responseData.get("support_delivery"));
                            supportDeliveryArray.set("edit_status", supportDeliveryResponse.get("edit_status"));
                            supportDeliveryArray.set("reason", supportDeliveryResponse.get("reason"));
                            supportDelivery.set(deliveryType, supportDeliveryArray);

                        }
                    }
                }
            }
            store.setSupportDelivery(supportDelivery.toString());
            deliveryStoreMapper.updateById(store);
        }catch (Exception e){
            log.error("配送门店信息批量修改:{}", e.getMessage());
        }
    }

    @Override
    @Transactional
    public Integer createSysStore(DeliverySysStoreAddParam param) {
        DeliveryStore data = new DeliveryStore();
        BeanUtils.copyProperties(param,data);
        data.setStoreNo(createNo());
        data.setSupportLocalDelivery(0);
        data.setSupportStore(0);
        data.setStatus(0);
        data.setArea("");
        data.setIsSystem(1);
        deliveryStoreMapper.insert(data);
        return data.getStoreId();
    }

    @Override
    public Integer editSysStore(Integer id) {
        Site site = siteMapper.selectById(id);
        SiteShop siteShop = siteShopMapper.selectOne(new QueryWrapper<SiteShop>().eq("site_id",id).last("limit 1"));
        DeliveryStore data = new DeliveryStore();
        data.setStoreName(site.getSiteName());
        data.setContactName(ObjectUtil.defaultIfEmpty(site.getContactName(), ""));
        data.setMobile(site.getPhone());
        data.setTradeTime(site.getBusinessHours());
        data.setProvinceId(site.getProvinceId());
        data.setCityId(site.getCityId());
        data.setDistrictId(site.getDistrictId());
        data.setAddress(site.getAddress());
        data.setFullAddress(site.getFullAddress());
        data.setLatitude(site.getLatitude());
        data.setLongitude(site.getLongitude());
        DeliveryStore store = deliveryStoreMapper.selectOne(new QueryWrapper<DeliveryStore>().eq("site_id",id)
                .eq("is_system",1));
        if(ObjectUtil.isNotEmpty(store)){
            Integer storeId = store.getStoreId();
            BeanUtils.copyProperties(data,store);
            store.setStoreId(storeId);
            deliveryStoreMapper.updateById(store);
        }
        return 0;
    }

    public static Map<String, Map<String, Object>> jsonToNestedMap(JSONObject json) {
        Map<String, Map<String, Object>> resultMap = new HashMap<>();

        for (String key : json.keySet()) {
            Object value = json.get(key);

            if (value instanceof JSONObject nestedJson) {
                // 递归处理嵌套的JSONObject
                Map<String, Object> nestedMap = new HashMap<>();

                for (String nestedKey : nestedJson.keySet()) {
                    nestedMap.put(nestedKey, nestedJson.get(nestedKey));
                }
                resultMap.put(key, nestedMap);
            }
        }
        return resultMap;
    }
}
