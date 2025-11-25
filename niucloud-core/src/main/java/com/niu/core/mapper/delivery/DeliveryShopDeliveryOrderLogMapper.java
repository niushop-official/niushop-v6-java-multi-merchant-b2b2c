package com.niu.core.mapper.delivery;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrderLog;
import com.niu.core.entity.delivery.DeliveryShopDeliveryOrderLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 同城配送订单日志 Mapper
 */
@Mapper
public interface DeliveryShopDeliveryOrderLogMapper extends MPJBaseMapper<DeliveryShopDeliveryOrderLog> {
}