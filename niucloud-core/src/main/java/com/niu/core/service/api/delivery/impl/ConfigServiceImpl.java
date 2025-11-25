package com.niu.core.service.api.delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.delivery.DeliveryLocalDelivery;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.service.api.delivery.IConfigService;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ConfigServiceImpl implements IConfigService {

    @Resource
    ICoreDeliveryConfigService coreDeliveryConfigService;
    @Resource
    DeliveryLocalDeliveryMapper deliveryLocalDeliveryMapper;

    @Override
    public AdminDeliveryConfigVo getAdminDeliveryConfig() {
        return coreDeliveryConfigService.getAdminDeliveryConfig();
    }

    @Override
    public LocalDeliveryVo getLocal(Integer siteId) {
        DeliveryLocalDelivery model = deliveryLocalDeliveryMapper.selectOne(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", siteId));
        LocalDeliveryVo local = new LocalDeliveryVo();
        if(ObjectUtil.isNotEmpty( model)){
            BeanUtils.copyProperties(model, local);
        }else{
            local.setArea( "");
            local.setCenter("");
            local.setDeliveryTime("");
        }
        return local;
    }
}
