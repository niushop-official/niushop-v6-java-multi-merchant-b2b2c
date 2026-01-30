
package com.niu.core.service.core.local_delivery;

import com.niu.core.service.api.delivery.param.TrackOfLocalServiceParam;
import com.niu.core.service.api.delivery.vo.TrackOfLocalServiceVo;

/**
 * 同城配送事件服务接口
 */
public interface ICoreLocalDeliveryOrderService {
    
    /**
     * 同步订单信息
     */
    Integer syncOrder(Integer id);

    /**
     * 检查当前交易下是否有在进行中的配送订单
     * @param tradeType
     * @param tradeNo
     * @return
     */
    Boolean getUnderwayDeliveryOrder(String tradeType, String tradeNo);

    /**
     * 获取配送轨迹
     */
    TrackOfLocalServiceVo getTrackInfo(TrackOfLocalServiceParam param);

}
