package com.niu.core.service.api.delivery;


import com.niu.core.service.api.delivery.param.OrderNotifyParam;
import com.niu.core.service.api.delivery.param.OrderServiceParam;
import com.niu.core.service.api.delivery.param.TrackOfLocalServiceParam;
import com.niu.core.service.api.delivery.vo.OrderServiceVo;
import com.niu.core.service.api.delivery.vo.RiderLocationVo;
import com.niu.core.service.api.delivery.vo.TrackOfLocalServiceVo;

import java.util.Map;

/**
 * 配送订单服务层
 * 对应 PHP: app\service\api\delivery\OrderService
 */
public interface IOrderService {

    /**
     * 配送订单服务层
     */
    OrderServiceVo getDeliveryTrack(OrderServiceParam data);

    /**
     * 获取同城配送轨迹（对齐 PHP getTrackOfLocalDeliveryOrder）
     */
    TrackOfLocalServiceVo getTrackOfLocalDeliveryOrder(TrackOfLocalServiceParam data);

    /**
     * 获取骑手位置（对齐 PHP getRiderLocation）
     */
    RiderLocationVo getRiderLocation(TrackOfLocalServiceParam data);

    /**
     * 同城配送订单状态回调（对齐 PHP Order@notify）
     */
    void notify(OrderNotifyParam data, Map<String, String> allParams);
}
