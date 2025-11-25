package com.niu.core.service.site.local_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryLocalDelivery;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryService;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.local_delivery.LocalDeliveryOpenEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryServiceMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.site.local_delivery.ILocalDeliveryService;
import com.niu.core.service.site.local_delivery.ILocalDeliveryServiceService;
import com.niu.core.service.site.local_delivery.param.SetLocalDeliveryParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceListVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceVo;
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

    @Override
    public Integer setLocal(SetLocalDeliveryParam param) {
        Integer siteId = RequestUtils.siteId();
        deliveryLocalDeliveryMapper.delete(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", siteId));
        DeliveryLocalDelivery local = new DeliveryLocalDelivery();
        BeanUtils.copyProperties(param, local);
        local.setSiteId(siteId);
        local.setAdvanceDay(0);
        local.setMostDay(7);
        local.setArea(ObjectUtil.isNotEmpty(param.getArea()) ? param.getArea().toString() : "");
        local.setCenter(ObjectUtil.isNotEmpty(param.getCenter()) ? param.getCenter().toString() : "");
        local.setDeliveryTime(ObjectUtil.isNotEmpty(param.getDeliveryTime()) ? param.getDeliveryTime().toString() : "");
        
        deliveryLocalDeliveryMapper.insert(local);
        return local.getLocalId();
    }

    @Override
    public LocalDeliveryVo getLocal(Integer siteId) {
        DeliveryLocalDelivery model = deliveryLocalDeliveryMapper.selectOne(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", siteId));
        LocalDeliveryVo local = new LocalDeliveryVo();
        if(ObjectUtil.isNotEmpty(model)){
            BeanUtils.copyProperties(model, local);
        } else {
            // 设置默认值
            local.setArea("");
            local.setCenter("");
            local.setDeliveryTime("");
            local.setTimeIsOpen(0); // 配送时间
            local.setTimeType(0); // 时间
            local.setTimeWeek(""); // 营业时间
            local.setTimeInterval(30); //时段设置
            local.setAdvanceDay(0); // 时间选择需提前多少天
            local.setMostDay(7); // 最多可预约多少天
            local.setStartTime(0L); // 当日的起始时间
            local.setEndTime(0L); // 当日的营业结束时间
            // 收费标准默认值
            local.setBaseDist(BigDecimal.ZERO);
            local.setBasePrice(BigDecimal.ZERO);
            local.setGradDist(BigDecimal.ZERO);
            local.setGradPrice(BigDecimal.ZERO);
            local.setWeightStart(BigDecimal.ZERO);
            local.setWeightUnit(BigDecimal.ZERO);
            local.setWeightPrice(BigDecimal.ZERO);
        }
        return local;
    }
}
