package com.niu.core.common.loader.local_delivery;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.common.loader.local_delivery.param.AddShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.CloseShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.FinishShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrder;
import com.niu.core.service.core.shop_delivery.vo.ShopDeliveryOrderCreateVo;

import java.util.Map;

/**
 * 同城配送抽象类
 */
public abstract class BaseLocalDelivery {

    protected JSONObject config;

    /**
     * @param driver
     * @return
     */
    public static BaseLocalDelivery forName(String driver) {
        BaseLocalDelivery base = null;
        try {
            Class<?> clazz = ClassLoaderUtil.loadClass(driver);
            base = (BaseLocalDelivery) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base;
    }

    public abstract BaseLocalDelivery init(JSONObject config);

    /**
     * 创建订单
     * @param param
     */
    public abstract ShopDeliveryOrderCreateVo createOrder(AddShopDeliveryOrderParam param) throws Exception;

    public abstract ShopDeliveryOrderCreateVo reCreateOrder(AddShopDeliveryOrderParam param) throws Exception;

    public abstract ShopDeliveryOrderCreateVo calculate(AddShopDeliveryOrderParam param) throws Exception;

    public abstract JSONObject createOrderAfterCalculate(String deliveryThirdOrderNo) throws Exception;

    public abstract Map<String, Object> queryOrderInfo(Map<String, Object> requestData) throws Exception;

    public abstract DeliveryShopDeliveryOrder closeOrder(CloseShopDeliveryOrderParam  param) throws Exception;

    public abstract DeliveryShopDeliveryOrder finishOrder(FinishShopDeliveryOrderParam param);

    public abstract Map<String, Object> getTransporterPosition(Map<String, Object> requestData) throws Exception;

    public abstract Map<String, Object> addShop(Map<String, Object>  params) throws Exception;

    public abstract Map<String, Object> editShop(Map<String, Object>  params) throws Exception;


    /**
     * 回调通知
     * @param action
     * @return
     */
    public abstract Result notify(String action, NotifyShopDeliveryOrderParam param);
}
