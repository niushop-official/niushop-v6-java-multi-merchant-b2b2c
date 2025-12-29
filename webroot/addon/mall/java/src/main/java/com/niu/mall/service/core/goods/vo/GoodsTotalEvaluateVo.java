package com.niu.mall.service.core.goods.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * GoodsTotalEvaluateVo
 */
@Data
public class GoodsTotalEvaluateVo implements Serializable {

    private BigDecimal totalQualityScore;
    private BigDecimal totalServiceScore;
    private BigDecimal totalLogisticsScore;
    private Integer evaluateNum = 0;
}
