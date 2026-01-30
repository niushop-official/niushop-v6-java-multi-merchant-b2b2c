package com.niu.core.service.core.goods.param;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单支付参数
 */
@Data
public class GoodsConfigParam implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer isVerifyBrand;
}
