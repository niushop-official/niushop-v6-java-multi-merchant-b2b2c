package com.niu.core.service.site.local_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryLocalDelivery;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryService;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.local_delivery.LocalDeliveryOpenEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryServiceMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.site.local_delivery.ILocalDeliveryService;
import com.niu.core.service.site.local_delivery.ILocalDeliveryServiceService;
import com.niu.core.service.site.local_delivery.param.SetLocalDeliveryParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceListVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryStoreVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
public class LocalDeliveryServiceImpl implements ILocalDeliveryService {

    @Resource
    private DeliveryLocalDeliveryMapper deliveryLocalDeliveryMapper;
    @Resource
    private DeliveryStoreMapper deliveryStoreMapper;

    @Override
    public Integer setLocal(SetLocalDeliveryParam param) {
        Integer siteId = RequestUtils.siteId();
        deliveryLocalDeliveryMapper.delete(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", siteId));
        DeliveryLocalDelivery local = new DeliveryLocalDelivery();
        BeanUtils.copyProperties(param, local);
        local.setSiteId(siteId);
        local.setAdvanceDay(0);
        local.setMostDay(7);
        local.setTimeWeek(ObjectUtil.isNotEmpty(param.getTimeWeek()) ? param.getTimeWeek().toString() : "");
        local.setDeliveryTime(ObjectUtil.isNotEmpty(param.getDeliveryTime()) ? param.getDeliveryTime().toString() : "");
        local.setArea("");
        deliveryLocalDeliveryMapper.insert(local);

        if(ObjectUtil.isNotEmpty(param.getAreaData())){
            JSONObject areaData = param.getAreaData();
            for (String key : areaData.keySet()){
                JSONArray area = areaData.getJSONArray(key);
                deliveryStoreMapper.update(new DeliveryStore(), new UpdateWrapper<DeliveryStore>()
                        .eq("store_id",key).set("area", JSONUtil.toJsonStr(area)));
            }
        }
        return local.getLocalId();
    }

    @Override
    public LocalDeliveryStoreVo getLocal(Integer siteId) {
        DeliveryLocalDelivery model = deliveryLocalDeliveryMapper.selectOne(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", siteId));
        LocalDeliveryStoreVo local = new LocalDeliveryStoreVo();
        if(ObjectUtil.isNotEmpty(model)){
            BeanUtils.copyProperties(model, local);
        } else {
            // 收费标准默认值
            local.setBaseDist(BigDecimal.ZERO);
            local.setBasePrice(BigDecimal.ZERO);
            local.setGradDist(BigDecimal.ZERO);
            local.setGradPrice(BigDecimal.ZERO);
            local.setWeightStart(BigDecimal.ZERO);
            local.setWeightUnit(BigDecimal.ZERO);
            local.setWeightPrice(BigDecimal.ZERO);
            local.setFreeGoodsMoney(BigDecimal.ZERO);
        }
        return local;
    }
}
