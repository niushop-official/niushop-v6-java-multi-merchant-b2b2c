package com.niu.core.service.admin.goods.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品品牌参数
*/
@Data
public class GoodsBrandAuditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "brandId参数缺失")
    @NotEmpty(message = "品牌id不能为空")
    private String brandId;

    @NotNull(message = "audit_status参数缺失")
    private Integer auditStatus;

    private String auditReason;
}
