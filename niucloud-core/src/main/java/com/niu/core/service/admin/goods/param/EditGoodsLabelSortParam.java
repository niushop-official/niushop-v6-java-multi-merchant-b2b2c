package com.niu.core.service.admin.goods.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品标签排序参数
 */
@Data
public class EditGoodsLabelSortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "label_id参数缺失")
    private Integer labelId;

    @NotNull(message = "sort参数缺失")
    private Integer sort;
}
