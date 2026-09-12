package com.niu.core.service.admin.goods.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品标签状态参数
 */
@Data
public class EditGoodsLabelStatusParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "label_id参数缺失")
    private Integer labelId;

    @NotNull(message = "status参数缺失")
    private Integer status;
}
