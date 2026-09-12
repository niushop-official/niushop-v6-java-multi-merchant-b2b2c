package com.niu.core.service.core.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryLocalDelivery;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.local_delivery.LocalDeliveryStoreTypeEnum;
import com.niu.core.event.trade.TradeTypeEventDefiner;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 商品分类实现
 */
@Service
public class CoreDeliveryConfigServiceImpl implements ICoreDeliveryConfigService {

    @Resource
    ICoreConfigService coreConfigService;
    @Resource
    SiteMapper siteMapper;
    @Resource
    com.niu.core.service.core.delivery.ICoreConfigService deliverycoreConfig;
    @Resource
    DeliveryLocalDeliveryMapper deliveryLocalDeliveryMapper;
    @Resource
    SiteAddressMapper siteAddressMapper;
    @Resource
    ICoreSiteService coreSiteService;
    @Autowired
    private DeliveryStoreMapper deliveryStoreMapper;

    /**
     * 获取物流查询接口配置
     *
     */
    public DeliverySearchConfigVo getDeliverySearchConfig() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "DELIVERY_INTERFACE");
        if (config.isEmpty()) return new DeliverySearchConfigVo();

        return JSONUtil.toBean(config, DeliverySearchConfigVo.class);
    }

    /**
     * 设置物流查询接口配置
     *
     * @param data
     */
    public void setDeliverySearchConfig(JSONObject data) {
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "DELIVERY_INTERFACE", data);
    }

    @Override
    public AdminDeliveryConfigVo getAdminDeliveryConfig() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), "DELIVERY_CONFIG");
        if (config.isEmpty()) return new AdminDeliveryConfigVo();


        return JSONUtil.toBean(config, AdminDeliveryConfigVo.class);
    }

    @Override
    public void setAdminDeliveryConfig(AdminDeliveryConfigVo adminDeliveryConfigVo) {
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), "DELIVERY_CONFIG", JSONUtil.parseObj(adminDeliveryConfigVo));
    }

    @Override
    public List<DeliveryConfigVo> getDeliveryConfig(Integer siteId) {
        //平台配置
        Map<String, Object> adminDeliveryList = deliverycoreConfig.getDeliveryList();

        // 查询站点配置
        QueryWrapper<Site> siteQueryWrapper = new QueryWrapper<>();
        siteQueryWrapper.eq("site_id", siteId)
                .select("is_express", "is_local_delivery", "is_store", "delivery_config");
        Site site = siteMapper.selectOne(siteQueryWrapper);

        List<DeliveryConfigVo> data = new ArrayList<>();
        if (site == null) {
            return data;
        }

        Map<String, DeliveryConfigVo> defaultMap = new LinkedHashMap<>();
        DeliveryConfigVo express = new DeliveryConfigVo();
        express.setName("物流配送");
        express.setStatus(site.getIsExpress());
        express.setKey("express");
        defaultMap.put("express", express);

        DeliveryConfigVo localDelivery = new DeliveryConfigVo();
        localDelivery.setName("同城配送");
        localDelivery.setStatus(site.getIsLocalDelivery());
        localDelivery.setKey("local_delivery");
        defaultMap.put("local_delivery", localDelivery);

        DeliveryConfigVo storeDelivery = new DeliveryConfigVo();
        storeDelivery.setName("门店自提");
        storeDelivery.setStatus(site.getIsStore());
        storeDelivery.setKey("store");
        defaultMap.put("store", storeDelivery);

        // 对齐 PHP：按业务类型 hidden_delivery_type 过滤（如 FDS 隐藏 express）
        defaultMap = getDeliveryTypeBySiteId(defaultMap, siteId);

        Map<String, DeliveryConfigVo> list = new LinkedHashMap<>();
        if (StringUtils.isNotEmpty(site.getDeliveryConfig())) {
            applyDeliveryConfig(site, defaultMap, list);
        } else {
            list.putAll(defaultMap);
        }

        // 根据平台配置更新状态
        for (String key : list.keySet()) {
            if (adminDeliveryList.containsKey(key)) {
                JSONObject adminItem = JSONUtil.parseObj(adminDeliveryList.get(key));
                Integer status = adminItem.getInt("status") == 0 ? 2 : list.get(key).getStatus();
                list.get(key).setStatus(status);
            }
        }
        // 按默认顺序输出
        for (String key : defaultMap.keySet()) {
            if (ObjectUtil.isNotEmpty(list.get(key))) {
                data.add(list.get(key));
            }
        }
        return data;
    }

    /**
     * 对齐 PHP：delivery_config 为 [{key,name}, ...] 数组；同时兼容 {key: {name}} 对象格式
     */
    private void applyDeliveryConfig(Site site, Map<String, DeliveryConfigVo> defaultMap, Map<String, DeliveryConfigVo> list) {
        Set<String> defaultListKeys = defaultMap.keySet();
        Object parsed = JSONUtil.parse(site.getDeliveryConfig());
        List<JSONObject> configItems = new ArrayList<>();
        if (parsed instanceof cn.hutool.json.JSONArray jsonArray) {
            for (int i = 0; i < jsonArray.size(); i++) {
                Object item = jsonArray.get(i);
                if (item instanceof JSONObject jsonObject) {
                    configItems.add(jsonObject);
                } else if (item != null) {
                    configItems.add(JSONUtil.parseObj(item));
                }
            }
        } else if (parsed instanceof JSONObject jsonObject) {
            // 兼容对象格式：{ local_delivery: {name, key}, store: {...} }
            for (String key : jsonObject.keySet()) {
                if (!defaultListKeys.contains(key)) {
                    continue;
                }
                Object value = jsonObject.get(key);
                JSONObject item = value instanceof JSONObject jo ? jo : JSONUtil.parseObj(value);
                if (StrUtil.isBlank(item.getStr("key"))) {
                    item.set("key", key);
                }
                configItems.add(item);
            }
        }

        Set<String> deliveryConfigKeys = new HashSet<>();
        for (JSONObject value : configItems) {
            String key = value.getStr("key");
            if (StrUtil.isBlank(key) || !defaultListKeys.contains(key)) {
                continue;
            }
            deliveryConfigKeys.add(key);
            DeliveryConfigVo vo = new DeliveryConfigVo();
            vo.setKey(key);
            vo.setName(StrUtil.blankToDefault(value.getStr("name"), defaultMap.get(key).getName()));
            vo.setStatus(resolveDeliveryStatus(site, key));
            list.put(key, vo);
        }
        for (String key : defaultListKeys) {
            if (!deliveryConfigKeys.contains(key)) {
                list.put(key, defaultMap.get(key));
            }
        }
    }

    private Integer resolveDeliveryStatus(Site site, String key) {
        return switch (key) {
            case "express" -> site.getIsExpress();
            case "local_delivery" -> site.getIsLocalDelivery();
            case "store" -> site.getIsStore();
            default -> 0;
        };
    }

    /**
     * 对齐 PHP CoreDeliveryService::getDeliveryTypeBySiteId
     */
    @Override
    public <T> Map<String, T> getDeliveryTypeBySiteId(Map<String, T> deliveryTypeList, Integer siteId) {
        if (deliveryTypeList == null || deliveryTypeList.isEmpty() || siteId == null || siteId <= 0) {
            return deliveryTypeList == null ? new LinkedHashMap<>() : deliveryTypeList;
        }
        Map<String, T> originalDeliveryTypes = new LinkedHashMap<>(deliveryTypeList);
        Map<String, T> result = new LinkedHashMap<>(originalDeliveryTypes);
        List<String> tradeTypes = coreSiteService.getSiteTradeType(siteId);
        if (ObjectUtil.isEmpty(tradeTypes)) {
            return result;
        }
        for (String tradeType : tradeTypes) {
            if (StrUtil.isBlank(tradeType)) {
                continue;
            }
            TradeTypeEventDefiner.TradeTypeEvent event = new TradeTypeEventDefiner.TradeTypeEvent();
            event.setTradeType(tradeType);
            List<TradeTypeEventDefiner.TradeTypeResult> results = EventAndSubscribeOfPublisher.publishAndCallback(event);
            if (ObjectUtil.isEmpty(results)) {
                continue;
            }
            for (TradeTypeEventDefiner.TradeTypeResult tradeResult : results) {
                if (tradeResult == null || tradeResult.getConfig() == null) {
                    continue;
                }
                Object hiddenObj = tradeResult.getConfig().get("hidden_delivery_type");
                Collection<?> hiddenList = hiddenObj instanceof Collection<?> values ? values : List.of();
                // Match PHP: an unrestricted trade type restores all delivery types.
                if (hiddenList.isEmpty()) {
                    return originalDeliveryTypes;
                }
                for (Object item : hiddenList) {
                    if (item != null) {
                        result.remove(item.toString());
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void setConfig(Integer siteId, JSONObject data) {
        Map<String, Object> adminDeliveryList = deliverycoreConfig.getDeliveryList();
        QueryWrapper<DeliveryLocalDelivery> localQueryWrapper = new QueryWrapper<>();
        localQueryWrapper.eq("site_id", siteId);
        DeliveryLocalDelivery local = deliveryLocalDeliveryMapper.selectOne(localQueryWrapper);

        Map<String, Object> saveData = new HashMap<>();

        if (data != null && !data.isEmpty()) {
            // 创建数据副本用于处理
            JSONObject dataCopy = new JSONObject(data);

            for (String key : dataCopy.keySet()) {
                JSONObject value = JSONUtil.parseObj(dataCopy.get(key));
                saveData.put("is_" + key, value.getStr("status"));
                if (1 == value.getInt("status")) {
                    if (LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType().equals(key)) {
                        if (ObjectUtil.isEmpty(local)) {
                            throw new RuntimeException("同城配送未配置，请先完成配置");
                        }
                        DeliveryStore store = deliveryStoreMapper.selectOne(new QueryWrapper<DeliveryStore>().eq("site_id", siteId).eq("status", 1)
                                .eq("support_local_delivery", 1).last("limit 1"));
                        if (ObjectUtil.isEmpty(store)) {
                            throw new RuntimeException("未配置同城配送门店");
                        }
                    }
                    if (LocalDeliveryStoreTypeEnum.STORE.getType().equals(key)) {
                        DeliveryStore store = deliveryStoreMapper.selectOne(new QueryWrapper<DeliveryStore>().eq("site_id", siteId).eq("status", 1)
                                .eq("support_store", 1).last("limit 1"));
                        if (ObjectUtil.isEmpty(store)) {
                            throw new RuntimeException("未配置自提门店");
                        }
                    }
                    // 平台状态验证
                    if (adminDeliveryList.containsKey(key)) {
                        JSONObject adminItem = JSONUtil.parseObj(adminDeliveryList.get(key));
                        Integer adminStatus = adminItem.getInt("status");
                        if (adminStatus == 0) {
                            throw new RuntimeException("平台未启用" + adminItem.getStr("name"));
                        }
                    }
                }
                dataCopy.remove("status");
            }
            saveData.put("delivery_config", JSONUtil.toJsonStr(dataCopy));
        }

        UpdateWrapper<Site> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("site_id", siteId);
        for (Map.Entry<String, Object> entry : saveData.entrySet()) {
            updateWrapper.set(entry.getKey(), entry.getValue());
        }
        siteMapper.update(null, updateWrapper);
    }
}
