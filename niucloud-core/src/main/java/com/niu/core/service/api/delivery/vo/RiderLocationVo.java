package com.niu.core.service.api.delivery.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 骑手位置
 * 对应 PHP CoreLocalDeliveryOrderService::getRiderLocation 返回值
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiderLocationVo {
    private String latitude;
    private String longitude;
}
