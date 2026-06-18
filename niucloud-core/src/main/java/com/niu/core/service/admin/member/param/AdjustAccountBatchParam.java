package com.niu.core.service.admin.member.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 会员参数
 */
@Data
public class AdjustAccountBatchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "member_ids参数缺失")
    private List<Integer> memberIds;

    @NotNull(message = "account_data参数缺失")
    private BigDecimal accountData = BigDecimal.ZERO;

    private String memo = "";

    @NotNull(message = "account_type参数缺失")
    private String accountType;

    private String adjust;

    private Integer adjustType = 1;
}
