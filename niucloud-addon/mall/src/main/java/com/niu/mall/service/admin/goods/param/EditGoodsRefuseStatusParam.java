package com.niu.mall.service.admin.goods.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
@Data
public class EditGoodsRefuseStatusParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "goods_id参数缺失")
    private Integer goodsIds;

    @NotNull(message = "audit_reason参数缺失")
    private String auditReason;

}
