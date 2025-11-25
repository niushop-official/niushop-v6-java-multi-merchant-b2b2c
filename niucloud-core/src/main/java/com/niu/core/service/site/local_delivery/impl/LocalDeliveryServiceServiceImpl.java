package com.niu.core.service.site.local_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryService;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.local_delivery.LocalDeliveryOpenEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryServiceMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.site.local_delivery.ILocalDeliveryServiceService;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceListVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 同城配送服务接口
 * @author gjl
 * @date 2025/10/10
 */
@Service
public class LocalDeliveryServiceServiceImpl implements ILocalDeliveryServiceService {

    @Resource
    SiteAddressMapper siteAddressMapper;
    @Resource
    DeliveryLocalDeliveryServiceMapper deliveryLocalDeliveryServiceMapper;

    @Override
    public List<LocalDeliveryServiceListVo> list() {
        List<LocalDeliveryServiceListVo> resultList = new ArrayList<>();
        Integer siteId = RequestUtils.siteId();
        List<SiteAddress> siteAddressList = siteAddressMapper.selectList(new QueryWrapper<SiteAddress>().eq("site_id", siteId).eq("is_delivery_address", 1));
        if(!siteAddressList.isEmpty()){
            siteAddressList = siteAddressList.stream().filter(siteAddress -> siteAddress.getSupportDelivery() != null && JSONUtil.isTypeJSON(siteAddress.getSupportDelivery()) && siteAddress.getSupportDelivery().contains("open_status")).toList();
        }
        List<SiteAddress> newSiteAddressList = siteAddressList;
        List<DeliveryLocalDeliveryService> list = deliveryLocalDeliveryServiceMapper.selectList(new QueryWrapper<DeliveryLocalDeliveryService>().eq("site_id", siteId).eq("is_use", 1));
        if(!list.isEmpty()){
            list.forEach(item -> {
                LocalDeliveryServiceListVo vo = new LocalDeliveryServiceListVo();
                vo.setKey(item.getKey());
                vo.setName(item.getName());
                if(!newSiteAddressList.isEmpty()){
                    vo.setOpenStatus(JSONUtil.parseObj(newSiteAddressList.get(0).getSupportDelivery()).getInt("open_status"));
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

        Map<String, Map<String, Object>> deliveryTypeList = getDeliveryTypeList();
        
        List<LocalDeliveryServiceVo> list = new ArrayList<>();
        Integer siteId = RequestUtils.siteId();
        for (Map.Entry<String, Map<String, Object>> entry : deliveryTypeList.entrySet()) {
            String k = entry.getKey();
            Map<String, Object> v = entry.getValue();

            Map<String, Object> info = getConfig(k);
            
            LocalDeliveryServiceVo data = new LocalDeliveryServiceVo();
            data.setDeliveryType(k);
            data.setName((String) v.get("name"));
            data.setIsMerchant((Integer) v.get("is_merchant"));
            data.setIsUse((Integer) info.get("is_use"));
            data.setComponent((String) v.getOrDefault("component", ""));
            
            Map<String, Object> params = (Map<String, Object>) v.get("params");
            Map<String, LocalDeliveryServiceVo.ParamConfig> dataParams = new HashMap<>();
            if (params != null) {
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    String kParam = param.getKey();
                    String vParam = (String) param.getValue();
                    
                    LocalDeliveryServiceVo.ParamConfig paramConfig = new LocalDeliveryServiceVo.ParamConfig();
                    paramConfig.setName(vParam);
                    paramConfig.setValue(info != null ? 
                        (String) ((Map<String, Object>) info.get("config")).getOrDefault(kParam, "") : "");
                    dataParams.put(kParam, paramConfig);
                }
            }
            data.setParams(dataParams);
            
            list.add(data);
        }
        
        return list;
    }
    

    private Map<String, Map<String, Object>> getDeliveryTypeList() {
        Map<String, Map<String, Object>> list = new HashMap<>();
        Map<String, Object> merchant = new HashMap<>();
        merchant.put("name", "商家配送");
        merchant.put("key", "merchant");
        merchant.put("params", new HashMap<>());
        merchant.put("component", "");
        merchant.put("business_list", "");
        merchant.put("extend_data", new ArrayList<>());
        merchant.put("is_merchant", 1);
        list.put("merchant", merchant);

        
        return list;
    }

    private Map<String, Object> getConfig(String deliveryType) {
        Map<String, Map<String, Object>> deliveryTypeList = getDeliveryTypeList();
        
        if (!deliveryTypeList.containsKey(deliveryType)) {
            throw new RuntimeException("LOCAL_DELIVERY_TYPE_NOT_EXIST");
        }
        
        Integer siteId = RequestUtils.siteId();
        
        QueryWrapper<DeliveryLocalDeliveryService> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId).eq("`key`", deliveryType);
        DeliveryLocalDeliveryService info = deliveryLocalDeliveryServiceMapper.selectOne(queryWrapper);
        
        Map<String, Object> v = deliveryTypeList.get(deliveryType);
        
        Map<String, Object> data = new HashMap<>();
        data.put("delivery_type", deliveryType);
        data.put("name", v.get("name"));
        data.put("is_merchant", v.get("is_merchant"));
        
        if (info != null) {
            data.put("is_use", info.getIsUse());
        } else {
            data.put("is_use", (Integer) v.get("is_merchant") == 1 ? 1 : 0);
        }
        
        Map<String, Object> params = (Map<String, Object>) v.get("params");
        Map<String, Object> dataParams = new HashMap<>();
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
                dataParams.put(kParam, paramData);
            }
        }
        data.put("params", dataParams);

        Map<String, Object> config = new HashMap<>();
        if (info != null && info.getConfig() != null) {
            try {
                config = JSONUtil.parseObj(info.getConfig());
            } catch (Exception e) {
                config = new HashMap<>();
            }
        }
        data.put("config", config);
        
        return data;
    }
}
