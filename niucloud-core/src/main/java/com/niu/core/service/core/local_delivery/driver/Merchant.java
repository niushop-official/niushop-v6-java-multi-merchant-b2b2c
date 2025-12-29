package com.niu.core.service.core.local_delivery.driver;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.domain.Result;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.param.AddShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.CloseShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.FinishShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrder;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderCloseService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderCreateService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderFinishService;
import com.niu.core.service.core.shop_delivery.ICoreShopDeliveryOrderMessageService;
import com.niu.core.service.core.shop_delivery.vo.ShopDeliveryOrderCreateVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;

import java.util.HashMap;
import java.util.Map;

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
        if(ObjectUtil.isNotEmpty(deliveryShopDeliveryOrder)){
            deliveryShopDeliveryOrder.setCancelTime(System.currentTimeMillis()/1000);
        }else{
            deliveryShopDeliveryOrder = null;
        }
        return deliveryShopDeliveryOrder;
    }

    @Override
    public DeliveryShopDeliveryOrder finishOrder(FinishShopDeliveryOrderParam  param) {
        param.setOutDeliveryNo(param.getDeliveryNo());
        ICoreShopDeliveryOrderFinishService coreShopDeliveryOrderFinishService = SpringContext.bean(ICoreShopDeliveryOrderFinishService.class);
        DeliveryShopDeliveryOrder deliveryShopDeliveryOrder = coreShopDeliveryOrderFinishService.finishOrder(param);
        if(ObjectUtil.isNotEmpty(deliveryShopDeliveryOrder)){
            deliveryShopDeliveryOrder.setFinishTime(System.currentTimeMillis()/1000);
        }else{
            deliveryShopDeliveryOrder = null;
        }
        return deliveryShopDeliveryOrder;
    }

    @Override
    public Map<String, Object> getTransporterPosition(Map<String, Object> requestData) {
        return null;
    }

    @Override
    public Map<String, Object> addShop( Map<String, Object> requestData) {
        return null;
    }

    @Override
    public Map<String, Object> editShop( Map<String, Object> requestData) {
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
        }catch (Exception e){
            return Result.fail(e.getMessage());
        }
    }
}
