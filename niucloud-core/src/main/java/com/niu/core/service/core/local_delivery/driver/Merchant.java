package com.niu.core.service.core.local_delivery.driver;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.Result;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.param.AddShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.CloseShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.FinishShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrder;
import com.niu.core.mapper.delivery.DeliveryShopDeliveryOrderMapper;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderCloseService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderCreateService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderFinishService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderMessageService;
import com.niu.core.service.core.shop_delivery.vo.ShopDeliveryOrderCreateVo;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家配送驱动，对齐 PHP core\local_delivery\Merchant
 */
public class Merchant extends BaseLocalDelivery {

    public Integer siteId;

    @Override
    public BaseLocalDelivery init(JSONObject config) {
        this.siteId = ObjectUtil.isNotEmpty(config) && config.containsKey("siteId") ? config.getInt("siteId") : 0;
        this.config = config;
        return this;
    }

    @Override
    public ShopDeliveryOrderCreateVo createOrder(AddShopDeliveryOrderParam param) {
        ICoreShopDeliveryOrderCreateService coreShopDeliveryOrderCreateService = SpringContext.bean(ICoreShopDeliveryOrderCreateService.class);
        return coreShopDeliveryOrderCreateService.createOrder(param);
    }

    @Override
    public ShopDeliveryOrderCreateVo reCreateOrder(AddShopDeliveryOrderParam param) {
        return null;
    }

    @Override
    public ShopDeliveryOrderCreateVo calculate(AddShopDeliveryOrderParam param) {
        return null;
    }

    @Override
    public JSONObject createOrderAfterCalculate(String deliveryThirdOrderNo) {
        return JSONUtil.parseObj("{}");
    }

    @Override
    public Map<String, Object> queryOrderInfo(Map<String, Object> requestData) {
        return null;
    }

    @Override
    public DeliveryShopDeliveryOrder closeOrder(CloseShopDeliveryOrderParam param) {
        param.setOutDeliveryNo(param.getDeliveryNo());
        ICoreShopDeliveryOrderCloseService coreShopDeliveryOrderCloseService = SpringContext.bean(ICoreShopDeliveryOrderCloseService.class);
        DeliveryShopDeliveryOrder deliveryShopDeliveryOrder = coreShopDeliveryOrderCloseService.closeOrder(param);
        if (ObjectUtil.isNotEmpty(deliveryShopDeliveryOrder)) {
            deliveryShopDeliveryOrder.setCancelTime(System.currentTimeMillis() / 1000);
        } else {
            deliveryShopDeliveryOrder = null;
        }
        return deliveryShopDeliveryOrder;
    }

    @Override
    public DeliveryShopDeliveryOrder finishOrder(FinishShopDeliveryOrderParam param) {
        param.setOutDeliveryNo(param.getDeliveryNo());
        ICoreShopDeliveryOrderFinishService coreShopDeliveryOrderFinishService = SpringContext.bean(ICoreShopDeliveryOrderFinishService.class);
        DeliveryShopDeliveryOrder deliveryShopDeliveryOrder = coreShopDeliveryOrderFinishService.finishOrder(param);
        // 对齐 PHP Merchant::finishOrder：成功仅回传 finish_time，避免整实体 copy 污染同城配送单
        if (ObjectUtil.isNotEmpty(deliveryShopDeliveryOrder)) {
            DeliveryShopDeliveryOrder response = new DeliveryShopDeliveryOrder();
            response.setFinishTime(System.currentTimeMillis() / 1000);
            return response;
        }
        return null;
    }

    /**
     * 对齐 PHP Merchant::getTransporterPosition
     * 商家配送无实时轨迹，以取货点坐标作为骑手位置，并返回配送员信息
     */
    @Override
    public Map<String, Object> getTransporterPosition(Map<String, Object> requestData) {
        Map<String, Object> empty = new HashMap<>();
        if (requestData == null || ObjectUtil.isEmpty(requestData.get("delivery_no"))) {
            return empty;
        }
        String deliveryNo = String.valueOf(requestData.get("delivery_no"));
        DeliveryShopDeliveryOrderMapper mapper = SpringContext.bean(DeliveryShopDeliveryOrderMapper.class);
        // PHP: where(['out_delivery_no' => $data['delivery_no']])
        DeliveryShopDeliveryOrder order = mapper.selectOne(new LambdaQueryWrapper<DeliveryShopDeliveryOrder>()
                .eq(DeliveryShopDeliveryOrder::getOutDeliveryNo, deliveryNo)
                .orderByDesc(DeliveryShopDeliveryOrder::getId)
                .last("limit 1"));
        if (order == null) {
            order = mapper.selectOne(new LambdaQueryWrapper<DeliveryShopDeliveryOrder>()
                    .eq(DeliveryShopDeliveryOrder::getDeliveryNo, deliveryNo)
                    .orderByDesc(DeliveryShopDeliveryOrder::getId)
                    .last("limit 1"));
        }
        if (order == null) {
            return empty;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("rider_latitude", ObjectUtil.defaultIfNull(order.getDeliveryStartLat(), ""));
        result.put("rider_longitude", ObjectUtil.defaultIfNull(order.getDeliveryStartLng(), ""));
        result.put("rider_name", ObjectUtil.defaultIfNull(order.getDeliverName(), ""));
        result.put("rider_mobile", ObjectUtil.defaultIfNull(order.getDeliverMobile(), ""));
        return result;
    }

    @Override
    public Map<String, Object> addShop(Map<String, Object> requestData) {
        return null;
    }

    @Override
    public Map<String, Object> editShop(Map<String, Object> requestData) {
        return null;
    }

    @Override
    public Result notify(String action, NotifyShopDeliveryOrderParam param) {
        try {
            switch (action) {
                case "order":
                    ICoreShopDeliveryOrderMessageService coreShopDeliveryOrderMessageService = SpringContext.bean(ICoreShopDeliveryOrderMessageService.class);
                    coreShopDeliveryOrderMessageService.orderNotify(param);
                    break;
            }
            return Result.success();
        } catch (RuntimeException e) {
            // 不可吞掉：嵌套事务失败后吞异常会变成 rollback-only，掩盖真实错误
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
