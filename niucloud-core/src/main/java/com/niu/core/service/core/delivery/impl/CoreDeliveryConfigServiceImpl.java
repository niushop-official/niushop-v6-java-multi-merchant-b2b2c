package com.niu.core.service.core.delivery.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryLocalDelivery;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        String deliveryConfigStr = site.getDeliveryConfig();
        if (deliveryConfigStr == null || deliveryConfigStr.trim().isEmpty()) {
            // 如果delivery_config为空，使用默认配置
            DeliveryConfigVo express = new DeliveryConfigVo();
            express.setName("物流配送");
            express.setStatus(site.getIsExpress());
            express.setKey("express");
            data.add(express);

            DeliveryConfigVo localDelivery = new DeliveryConfigVo();
            localDelivery.setName("同城配送");
            localDelivery.setStatus(site.getIsLocalDelivery());
            localDelivery.setKey("local_delivery");
            data.add(localDelivery);
        } else {
            // 如果delivery_config不为空，解析配置
            JSONObject deliveryConfig = JSONUtil.parseObj(deliveryConfigStr);
            for (String key : deliveryConfig.keySet()) {
                JSONObject configItem = deliveryConfig.getJSONObject(key);
                DeliveryConfigVo vo = new DeliveryConfigVo();
                vo.setName(configItem.getStr("name"));
                vo.setKey(configItem.getStr("key"));
                String statusField = "is_" + key;
                Integer status = null;
                if ("express".equals(key)) {
                    status = site.getIsExpress();
                } else if ("local_delivery".equals(key)) {
                    status = site.getIsLocalDelivery();
                } else if ("store".equals(key)) {
                    status = site.getIsStore();
                }
                vo.setStatus(status);
                data.add(vo);
            }
        }

            // 根据平台配置更新状态
            for (DeliveryConfigVo item : data) {
                String key = item.getKey();
                if (adminDeliveryList.containsKey(key)) {
                    Map<String, Object> adminItem = (Map<String, Object>) adminDeliveryList.get(key);
                    Integer adminStatus = (Integer) adminItem.get("status");
                    if (adminStatus != null && adminStatus == 0) {
                        item.setStatus(0);
                    }}
            }

        return data;
    }

    @Override
    public void setConfig(Integer siteId, JSONObject data) {
        Map<String, Object> adminDeliveryList = deliverycoreConfig.getDeliveryList();
        QueryWrapper<DeliveryLocalDelivery> localQueryWrapper = new QueryWrapper<>();
        localQueryWrapper.eq("site_id", siteId);
            DeliveryLocalDelivery local = deliveryLocalDeliveryMapper.selectOne(localQueryWrapper);

        QueryWrapper<SiteAddress> siteAddressQueryWrapper = new QueryWrapper<>();
        siteAddressQueryWrapper.eq("is_default_delivery", 1).eq("site_id", siteId);
        SiteAddress siteAddress = siteAddressMapper.selectOne(siteAddressQueryWrapper);

        Map<String, Object> saveData = new HashMap<>();

        if (data != null && !data.isEmpty()) {
            // 创建数据副本用于处理
            JSONObject dataCopy = new JSONObject(data);

            for (String key : dataCopy.keySet()) {
                Object value = dataCopy.get(key);
                if (value instanceof Map) {
                    Map<String, Object> v = (Map<String, Object>) value;

                    String itemKey = (String) v.get("key");
                    Integer status = (Integer) v.get("status");
                    if (itemKey != null && status != null) {
                        saveData.put("is_" + itemKey, status);
                    }

                    if (status != null && status == 1) {
                        if ("local_delivery".equals(itemKey)) {
                            if (local == null) {
                                throw new RuntimeException("同城配送未配置，请先完成配置");
                            }
                            if (siteAddress == null) {
                                throw new RuntimeException("未配置默认发货地址");
                            }
                        }

                        // 平台状态验证
                        if (adminDeliveryList.containsKey(itemKey)) {
                            Map<String, Object> adminItem = (Map<String, Object>) adminDeliveryList.get(itemKey);
                            Integer adminStatus = (Integer) adminItem.get("status");
                            if (adminStatus != null && adminStatus == 0) {
                                String adminName = (String) adminItem.get("name");
                                throw new RuntimeException("平台未启用" + adminName);
                            }
                        }
                    }

                    v.remove("status");
                }
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
