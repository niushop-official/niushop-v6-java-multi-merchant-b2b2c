package com.niu.core.common.loader.local_delivery;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.loader.local_delivery.param.AddShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.CloseShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.FinishShopDeliveryOrderParam;
import com.niu.core.common.loader.local_delivery.param.NotifyShopDeliveryOrderParam;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.common.loader.pay.param.RefundParam;
import com.niu.core.common.loader.pay.param.TransferParam;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrder;
import com.niu.core.entity.pay.Pay;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import com.niu.core.service.core.pay.vo.TransferQueryVo;
import com.niu.core.service.core.shop_delivery.vo.ShopDeliveryOrderCreateVo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
    public abstract ShopDeliveryOrderCreateVo createOrder(AddShopDeliveryOrderParam param);

    public abstract void reCreateOrder();

    public abstract void calculate();

    public abstract void createOrderAfterCalculate();

    public abstract Map<String, Object> queryOrderInfo(Map<String, Object> requestData);

    public abstract DeliveryShopDeliveryOrder closeOrder(CloseShopDeliveryOrderParam  param);

    public abstract DeliveryShopDeliveryOrder finishOrder(FinishShopDeliveryOrderParam param);

    public abstract void getTransporterPosition();

    public abstract Map<String, Object> addShop(Map<String, Object>  params);

    public abstract Map<String, Object> editShop(Map<String, Object>  params);


    /**
     * 回调通知
     * @param action
     * @return
     */
    public abstract void notify(String action, NotifyShopDeliveryOrderParam param);

    public JSONObject getConfig() {
        return config;
    }
}
