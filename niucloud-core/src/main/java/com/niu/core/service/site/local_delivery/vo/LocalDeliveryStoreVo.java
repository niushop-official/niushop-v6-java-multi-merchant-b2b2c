package com.niu.core.service.site.local_delivery.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.math.BigDecimal;

/**
 * LocaleDeliveryServiceListVo列表视图
 */
@Data
public class LocalDeliveryStoreVo {
    private String feeType; // 费用类型
    private BigDecimal baseDist; // 多少km内
    private BigDecimal basePrice; // 配送费用
    private BigDecimal gradDist; // 每超出多少km内
    private BigDecimal gradPrice; // 配送费用
    private BigDecimal weightStart; // 重量多少内不额外收费
    private BigDecimal weightUnit; // 每超出多少kg
    private BigDecimal weightPrice; // 价格
    private BigDecimal freeGoodsMoney;
}
