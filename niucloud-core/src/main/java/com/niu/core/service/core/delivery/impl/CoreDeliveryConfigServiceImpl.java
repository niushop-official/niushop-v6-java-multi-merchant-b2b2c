package com.niu.core.service.core.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
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
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.DeliverySearchConfigVo;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

        List<DeliveryConfigVo> defaultList = new ArrayList<>();
        List<DeliveryConfigVo> data = new ArrayList<>();
        if (site == null) {
            return data;
        }

        // 如果delivery_config为空，使用默认配置
        DeliveryConfigVo express = new DeliveryConfigVo();
        express.setName("物流配送");
        express.setStatus(site.getIsExpress());
        express.setKey("express");
        defaultList.add(express);

        DeliveryConfigVo localDelivery = new DeliveryConfigVo();
        localDelivery.setName("同城配送");
        localDelivery.setStatus(site.getIsLocalDelivery());
        localDelivery.setKey("local_delivery");
        defaultList.add(localDelivery);

        DeliveryConfigVo storeDelivery = new DeliveryConfigVo();
        storeDelivery.setName("门店自提");
        storeDelivery.setStatus(site.getIsStore());
        storeDelivery.setKey("store");
        defaultList.add(storeDelivery);

        Map<String, DeliveryConfigVo> list = new HashMap<>();

        if (StringUtils.isNotEmpty(site.getDeliveryConfig())) {
            // 获取默认列表和配置列表的key集合
            Set<String> defaultListKeys = defaultList.stream().map(DeliveryConfigVo::getKey).collect(Collectors.toSet());
            JSONObject deliveryConfig = JSONUtil.parseObj(site.getDeliveryConfig());
            Set<String> deliveryConfigKeys = deliveryConfig.keySet();

            // 计算差异的key（在默认列表中但不在配置列表中）
            Set<String> diffKeys = new HashSet<>(defaultListKeys);
            diffKeys.removeAll(deliveryConfigKeys);

            // 处理配置列表中的有效项
            for (String value : deliveryConfig.keySet()) {
                if (StringUtils.isNotEmpty(value) && defaultListKeys.contains(value)) {
                    // 从data中获取对应的状态值
                    DeliveryConfigVo vo = new DeliveryConfigVo();
                    vo.setName(deliveryConfig.getJSONObject(value).getStr("name"));
                    Integer status = 0;
                    if ("express".equals(value)) {
                        status = site.getIsExpress();
                    } else if ("local_delivery".equals(value)) {
                        status = site.getIsLocalDelivery();
                    } else if ("store".equals(value)) {
                        status = site.getIsStore();
                    }
                    vo.setStatus(status);
                    vo.setKey(value);
                    list.put(value, vo);
                }
            }

            // 处理差异项，使用默认配置
            for (String key : diffKeys) {
                List<DeliveryConfigVo> valueThis = defaultList.stream().filter(vo -> vo.getKey().equals(key)).toList();
                if(!valueThis.isEmpty()){
                    list.put(key, valueThis.get(0));
                }
            }
        } else {
            for (DeliveryConfigVo value : defaultList) {
                list.put(value.getKey(), value);
            }
        }

        // 根据平台配置更新状态
        for (String key : list.keySet()) {
            if (adminDeliveryList.containsKey(key)) {
                JSONObject adminItem = JSONUtil.parseObj(adminDeliveryList.get(key));
                Integer status = adminItem.getInt("status") == 0 ? 2 : list.get(key).getStatus();
                list.get(key).setStatus(status);
            }
        }
        for (String key : list.keySet()) {
            data.add(list.get(key));
        }
        return data;
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
