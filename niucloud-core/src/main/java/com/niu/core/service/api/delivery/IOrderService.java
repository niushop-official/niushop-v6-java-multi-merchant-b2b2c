package com.niu.core.service.api.delivery;


import com.niu.core.service.api.delivery.param.OrderNotifyParam;
import com.niu.core.service.api.delivery.param.OrderServiceParam;
import com.niu.core.service.api.delivery.param.TrackOfLocalServiceParam;
import com.niu.core.service.api.delivery.vo.OrderServiceVo;
import com.niu.core.service.api.delivery.vo.TrackOfLocalServiceVo;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 配送订单服务层
 */
public interface IOrderService {

    /**
     * 配送订单服务层
     */
    OrderServiceVo getDeliveryTrack(OrderServiceParam data);

    /**
     * 获取配送轨迹
     */
    TrackOfLocalServiceVo getTrackOfLocalDeliveryOrder(TrackOfLocalServiceParam data);

    /**
     * 同城配送订单状态回调
     */
    void notify(OrderNotifyParam data, Map<String, String> allParams);
}
