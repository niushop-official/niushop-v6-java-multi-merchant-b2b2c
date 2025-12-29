package com.niu.core.service.admin.member.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Member视图
 */
@Data
public class SumBalanceVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal balance;  // 可用余额
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal money;  // 可用余额（可提现）
}
