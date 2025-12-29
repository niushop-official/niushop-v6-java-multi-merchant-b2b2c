package com.niu.core.service.site.local_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryService;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.local_delivery.LocalDeliveryOpenEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryServiceMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.site.local_delivery.ILocalDeliveryServiceService;
import com.niu.core.service.site.local_delivery.param.LocalDeliverySearchParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryConfigVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceListVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceVo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * 同城配送服务接口
 * @author gjl
 * @date 2025/10/10
 */
@Log4j2
@Service
public class LocalDeliveryServiceServiceImpl implements ILocalDeliveryServiceService {

    @Resource
    DeliveryStoreMapper deliveryStoreMapper;
    @Resource
    DeliveryLocalDeliveryServiceMapper deliveryLocalDeliveryServiceMapper;

    @Override
    public List<LocalDeliveryServiceListVo> list(LocalDeliverySearchParam param) {
        List<LocalDeliveryServiceListVo> resultList = new ArrayList<>();
        Integer siteId = RequestUtils.siteId();
        DeliveryStore deliveryStore = deliveryStoreMapper.selectOne(new QueryWrapper<DeliveryStore>()
                .eq("site_id", siteId).eq("store_id", param.getStoreId())
                .eq("status", 1));
        JSONObject supportDeliveryJson = new JSONObject();
        if (ObjectUtil.isNotEmpty(deliveryStore) && StringUtils.isNotEmpty(deliveryStore.getSupportDelivery())){
            supportDeliveryJson = JSONUtil.parseObj(deliveryStore.getSupportDelivery());
        }
        JSONObject supportDelivery = supportDeliveryJson;
        List<DeliveryLocalDeliveryService> list = deliveryLocalDeliveryServiceMapper.selectList(new QueryWrapper<DeliveryLocalDeliveryService>()
                .eq("site_id", siteId).eq("is_use", 1));
        if(!list.isEmpty()){
            list.forEach(item -> {
                LocalDeliveryServiceListVo vo = new LocalDeliveryServiceListVo();
                vo.setKey(item.getKey());
                vo.setName(item.getName());
                if (!supportDelivery.isEmpty() && supportDelivery.containsKey(item.getKey())){
                    vo.setOpenStatus(supportDelivery.getJSONObject(item.getKey()).getInt("open_status"));
                }else{
                    vo.setOpenStatus(LocalDeliveryOpenEnum.OPEN_REFUND.getStatus());
                }
                resultList.add( vo);
            });
        }
        return resultList;
    }

    @Override
    public List<LocalDeliveryServiceVo> getList() {

        JSONObject deliveryTypeList = LocalDeliveryTypeEnum.getType();
        
        List<LocalDeliveryServiceVo> list = new ArrayList<>();
        for (Map.Entry<String, Object> entry : deliveryTypeList.entrySet()) {
            String k = entry.getKey();
            JSONObject v = JSONUtil.parseObj(entry.getValue());

            LocalDeliveryConfigVo info = getConfig(k);
            
            LocalDeliveryServiceVo data = new LocalDeliveryServiceVo();
            data.setDeliveryType(k);
            data.setName(v.getStr("name"));
            data.setIsMerchant(v.getInt("is_merchant"));
            data.setIsUse(info.getIsUse());
            data.setComponent(v.getOrDefault("component", "").toString());
            
            Map<String, Object> params = JSONUtil.parseObj(v.get("params"));
            Map<String, LocalDeliveryServiceVo.ParamConfig> dataParams = new HashMap<>();
            if (params != null) {
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    String kParam = param.getKey();
                    String vParam = (String) param.getValue();

                    JSONObject valueParam = JSONUtil.parseObj(info.getParams()).getJSONObject(kParam);
                    LocalDeliveryServiceVo.ParamConfig paramConfig = new LocalDeliveryServiceVo.ParamConfig();
                    paramConfig.setName(vParam);
                    paramConfig.setValue(ObjectUtil.isNotEmpty(valueParam) ?  valueParam.get("value").toString() : "");
                    dataParams.put(kParam, paramConfig);
                }
            }
            data.setParams(dataParams);
            
            list.add(data);
        }
        
        return list;
    }

    @Override
    public LocalDeliveryConfigVo getConfig(String deliveryType) {
        JSONObject deliveryTypeList = LocalDeliveryTypeEnum.getType();
        if (!deliveryTypeList.containsKey(deliveryType)) {
            throw new RuntimeException("配送服务商类型不存在");
        }
        Integer siteId = RequestUtils.siteId();
        
        QueryWrapper<DeliveryLocalDeliveryService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId).eq("`key`", deliveryType);
        DeliveryLocalDeliveryService info = deliveryLocalDeliveryServiceMapper.selectOne(queryWrapper);
        
        JSONObject v = JSONUtil.parseObj(deliveryTypeList.get(deliveryType));

        LocalDeliveryConfigVo data = new LocalDeliveryConfigVo();
        data.setDeliveryType(deliveryType);
        data.setName(v.getOrDefault("name", "").toString());
        data.setIsMerchant(v.getInt("is_merchant"));
        data.setIsUse(ObjectUtil.isNotEmpty(info) &&  ObjectUtil.isNotEmpty(info.getIsUse()) ? info.getIsUse() : v.getInt("is_merchant"));
        
        Map<String, Object> params = JSONUtil.parseObj( v.get("params"));
        JSONObject dataParams = new JSONObject();
        if (params != null) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                String kParam = param.getKey();
                String vParam = (String) param.getValue();
                
                Map<String, Object> paramData = new HashMap<>();
                paramData.put("name", vParam);
                if (info != null && info.getConfig() != null) {
                    try {
                        Map<String, Object> config = JSONUtil.parseObj(info.getConfig());
                        paramData.put("value", config.getOrDefault(kParam, ""));
                    } catch (Exception e) {
                        paramData.put("value", "");
                    }
                } else {
                    paramData.put("value", "");
                }
                dataParams.set(kParam, paramData);
            }
        }
        data.setParams(dataParams.toString());
        return data;
    }

    @Override
    @Transactional
    public void setConfig(String deliveryType, HttpServletRequest request) {
        JSONObject deliveryTypeList = LocalDeliveryTypeEnum.getType();
        if (!deliveryTypeList.containsKey(deliveryType)) {
            throw new RuntimeException("配送服务商类型不存在");
        }
        JSONObject key = deliveryTypeList.getJSONObject(deliveryType);
        JSONObject data = new JSONObject();
        data.set("is_use", "0");

        try (BufferedReader reader = request.getReader()) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            if (StringUtils.isNotEmpty(requestBody)){
                JSONObject requestParams = JSONUtil.parseObj(requestBody);
                for (String entryKey : key.getJSONObject("params").keySet()){
                    String values = requestParams.getStr(entryKey);
                    data.set(entryKey, StringUtils.isNotEmpty(values) ? values : "");
                }
                if (ObjectUtil.isNotEmpty(requestParams.getInt("is_use"))){
                    data.set("is_use", requestParams.getInt("is_use"));
                }
            }

        } catch (IOException e) {
            log.error("Dada回调结果异常,异常原因{}", e.getMessage());
        }

        JSONObject config = new JSONObject();
        for (String entryKey : key.getJSONObject("params").keySet()){
            config.set(entryKey, StringUtils.isNotEmpty(data.getStr(entryKey)) ? data.get(entryKey) : "");
        }
        DeliveryLocalDeliveryService thirdDelivery = deliveryLocalDeliveryServiceMapper.selectOne(new QueryWrapper<DeliveryLocalDeliveryService>()
                .eq("site_id", RequestUtils.siteId()).eq("`key`", deliveryType));
        if (ObjectUtil.isEmpty(thirdDelivery)){
            DeliveryLocalDeliveryService delivery = new DeliveryLocalDeliveryService();
            delivery.setSiteId(RequestUtils.siteId());
            delivery.setKey(deliveryType);
            delivery.setConfig(config.toString());
            delivery.setName(key.getStr("name"));
            delivery.setIsUse(data.getInt("is_use"));
            deliveryLocalDeliveryServiceMapper.insert(delivery);
        }else{
            thirdDelivery.setConfig(config.toString());
            thirdDelivery.setIsUse(data.getInt("is_use"));
            deliveryLocalDeliveryServiceMapper.updateById(thirdDelivery);
        }
    }
}
