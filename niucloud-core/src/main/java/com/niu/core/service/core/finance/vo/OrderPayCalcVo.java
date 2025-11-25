package com.niu.core.service.core.finance.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * GoodsConfigVo视图
 */
@Data
public class OrderPayCalcVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal mallMoney;

    private BigDecimal shopSeparateMoney;
}
