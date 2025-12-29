package com.niu.mall.service.site.order.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单参数
*/
@Data
public class OrderPickUpParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String deliveryType = "store";

    private Integer orderId;

    private Integer[] orderGoodsIds;

    private String remark;

}
