package com.niu.core.service.core.shop_delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.loader.local_delivery.param.AddShopDeliveryOrderParam;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryDeliver;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrderLog;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrder;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrderLog;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.mapper.delivery.DeliveryDeliverMapper;
import com.niu.core.mapper.delivery.DeliveryShopDeliveryOrderLogMapper;
import com.niu.core.mapper.delivery.DeliveryShopDeliveryOrderMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderCreateService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderLogService;
import com.niu.core.service.core.shop_delivery.param.AddShopDeliveryOrderLogParam;
import com.niu.core.service.core.shop_delivery.vo.ShopDeliveryOrderCreateVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.niu.core.common.utils.CommonUtils.createNo;

@Service
public class CoreShopDeliveryOrderLogServiceImpl implements ICoreShopDeliveryOrderLogService {

    @Resource
    private DeliveryShopDeliveryOrderLogMapper deliveryShopDeliveryOrderLogMapper;


    @Override
    public void addLog(AddShopDeliveryOrderLogParam param) {
        DeliveryShopDeliveryOrderLog model = new DeliveryShopDeliveryOrderLog();
        BeanUtil.copyProperties(param, model);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setMainId(ObjectUtil.isNotEmpty(param.getMainId()) ? param.getMainId() : 0);
        model.setMainName(ObjectUtil.isNotEmpty(param.getMainName()) ? param.getMainName() : "");
        model.setMainType(ObjectUtil.isNotEmpty(param.getMainType()) ? param.getMainType() : "");
        deliveryShopDeliveryOrderLogMapper.insert(model);
    }
}
