package com.niu.core.service.core.shop_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.loader.local_delivery.param.AddShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;
import com.niu.core.common.utils.DistanceCalculateUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryDeliver;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrder;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryMainTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryOperateEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.order.OrderDeliveryEnum;
import com.niu.core.enums.shop_delivery.ShopDeliveryStatusEnum;
import com.niu.core.mapper.delivery.DeliveryDeliverMapper;
import com.niu.core.mapper.delivery.DeliveryShopDeliveryOrderMapper;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderNotifyService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderCreateService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderLogService;
import com.niu.core.service.core.shop_delivery.param.AddShopDeliveryOrderLogParam;
import com.niu.core.service.core.shop_delivery.vo.ShopDeliveryOrderCreateVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static com.niu.core.common.utils.CommonUtils.createNo;

@Service
public class CoreShopDeliveryOrderCreateServiceImpl implements ICoreShopDeliveryOrderCreateService {

    @Resource
    private DeliveryDeliverMapper deliveryDeliverMapper;
    @Resource
    private DeliveryShopDeliveryOrderMapper deliveryShopDeliveryOrderMapper;
    @Resource
    private SiteAddressMapper siteAddressMapper;
    @Resource
    private ICoreShopDeliveryOrderLogService coreShopDeliveryOrderLogService;
    @Resource
    private ICoreLocalDeliveryOrderNotifyService coreLocalDeliveryOrderNotifyService;

    @Override
    public ShopDeliveryOrderCreateVo createOrder(AddShopDeliveryOrderParam param) {
        Integer siteId = param.getOrderDate().getSiteId();
        DeliveryDeliver deliver = deliveryDeliverMapper.selectById(param.getDeliveryId());
        SiteAddress deliveryAddress = siteAddressMapper.selectOne(
                new QueryWrapper<SiteAddress>()
                        .eq("is_default_delivery", 1)
                        .eq("site_id", siteId).last("limit 1"));

        // 计算收货地址与取货地址的距离
        BigDecimal distance = BigDecimal.ZERO;
        if(StringUtils.isNotEmpty(param.getOrderDate().getTakerLatitude())
                && StringUtils.isNotEmpty(param.getOrderDate().getTakerLongitude())
                && StringUtils.isNotEmpty(deliveryAddress.getLat())
                && StringUtils.isNotEmpty(deliveryAddress.getLng())){

            distance = BigDecimal.valueOf(DistanceCalculateUtils.calculateDistance(
                    Double.parseDouble(param.getOrderDate().getTakerLatitude()),
                    Double.parseDouble(param.getOrderDate().getTakerLongitude()),
                    Double.parseDouble(deliveryAddress.getLat()),
                    Double.parseDouble(deliveryAddress.getLng())
            )).divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);
        }

        //创建商家配送单号
        DeliveryShopDeliveryOrder deliveryOrder = new DeliveryShopDeliveryOrder();
        deliveryOrder.setSiteId(siteId);
        deliveryOrder.setDeliveryNo(createNo());
        deliveryOrder.setOutDeliveryNo(param.getDeliveryNo());
        deliveryOrder.setTradeId(param.getOrderDate().getOrderId());
        deliveryOrder.setTradeNo(param.getTradeNo());
        deliveryOrder.setTradeType(param.getTradeType());
        deliveryOrder.setDeliveryMoney(param.getOrderDate().getDeliveryMoney());

        // 设置配送起始地址
        deliveryOrder.setDeliveryStart(deliveryAddress.getFullAddress());
        deliveryOrder.setDeliveryStartProvinceId(ObjectUtil.isNotEmpty(deliveryAddress.getProvinceId()) ? deliveryAddress.getProvinceId() : 0);
        deliveryOrder.setDeliveryStartCityId(ObjectUtil.isNotEmpty(deliveryAddress.getCityId()) ? deliveryAddress.getCityId() : 0);
        deliveryOrder.setDeliveryStartDistrictId(ObjectUtil.isNotEmpty(deliveryAddress.getDistrictId()) ? deliveryAddress.getDistrictId() : 0);
        deliveryOrder.setDeliveryStartLng(StringUtils.isNotEmpty(deliveryAddress.getLng()) ? deliveryAddress.getLng() : "");
        deliveryOrder.setDeliveryStartLat(StringUtils.isNotEmpty(deliveryAddress.getLat()) ? deliveryAddress.getLat() : "");


        // 设置配送结束地址
        deliveryOrder.setDeliveryEnd(param.getOrderDate().getTakerFullAddress());
        deliveryOrder.setDeliveryEndProvinceId(ObjectUtil.isNotEmpty(param.getOrderDate().getTakerProvinceId()) ? param.getOrderDate().getTakerProvinceId() : 0);
        deliveryOrder.setDeliveryEndCityId(ObjectUtil.isNotEmpty(param.getOrderDate().getTakerCityId()) ? param.getOrderDate().getTakerCityId() : 0);
        deliveryOrder.setDeliveryEndDistrictId(ObjectUtil.isNotEmpty(param.getOrderDate().getTakerDistrictId()) ? param.getOrderDate().getTakerDistrictId() : 0);
        deliveryOrder.setDeliveryEndLng(StringUtils.isNotEmpty(param.getOrderDate().getTakerLongitude()) ? param.getOrderDate().getTakerLongitude() : "");
        deliveryOrder.setDeliveryEndLat(StringUtils.isNotEmpty(param.getOrderDate().getTakerLatitude()) ? param.getOrderDate().getTakerLatitude() : "");

        deliveryOrder.setDeliveryDistance(distance);
        deliveryOrder.setDeliverId(param.getDeliveryId());
        deliveryOrder.setDeliverName(deliver.getDeliverName());
        deliveryOrder.setDeliverMobile(deliver.getDeliverMobile());
        deliveryOrder.setStatus(LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().toString());
        deliveryOrder.setRemark(StringUtils.isNotEmpty(param.getRemark()) ? param.getRemark() : "");
        deliveryOrder.setBody(StringUtils.isNotEmpty(param.getBody()) ? param.getBody() : "");
        //添加配送订单记录
        deliveryShopDeliveryOrderMapper.insert(deliveryOrder);
        if (ObjectUtil.isNotEmpty(deliveryOrder.getId()) && deliveryOrder.getId() > 0){
            //商家订单状态回调
            NotifyShopDeliveryOrderParam notifyParam = new NotifyShopDeliveryOrderParam();
            notifyParam.setOutDeliveryNo(deliveryOrder.getOutDeliveryNo());
            notifyParam.setMainId(deliveryOrder.getDeliverId());
            notifyParam.setMainName(deliveryOrder.getDeliverName());
            notifyParam.setStatus(LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().toString());
            coreLocalDeliveryOrderNotifyService.notify(siteId, DeliveryStoreTypeEnum.MERCHANT.getKey(),deliveryOrder.getOutDeliveryNo(), "order", notifyParam);
            //添加配送订单日志
            LocalDeliveryOperateEnum.OperateResult operateResult = LocalDeliveryOperateEnum.getOperateByStatus(ShopDeliveryStatusEnum.convertStatus(ShopDeliveryStatusEnum.IN_DELIVERY.getStatus()));
            AddShopDeliveryOrderLogParam logParam = new AddShopDeliveryOrderLogParam();
            logParam.setOrderId(deliveryOrder.getId());
            logParam.setMainId(deliveryOrder.getDeliverId());
            logParam.setMainName(deliveryOrder.getDeliverName());
            logParam.setMainType(LocalDeliveryMainTypeEnum.RIDER.getType());
            logParam.setOperate(operateResult.getOperate());
            logParam.setOperateDesc(operateResult.getOperateDesc());
            logParam.setStatus(LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().toString());
            logParam.setRemark(operateResult.getOperateDesc());
            coreShopDeliveryOrderLogService.addLog(logParam);
        }
        ShopDeliveryOrderCreateVo vo = new ShopDeliveryOrderCreateVo();
        vo.setOutDeliveryNo(deliveryOrder.getDeliveryNo());
        vo.setDeliveryMoney(deliveryOrder.getDeliveryMoney());
        vo.setDeliveryDistance(deliveryOrder.getDeliveryDistance());
        vo.setRiderName(deliveryOrder.getDeliverName());
        vo.setRiderMobile(deliveryOrder.getDeliverMobile());
        return vo;
    }
}
