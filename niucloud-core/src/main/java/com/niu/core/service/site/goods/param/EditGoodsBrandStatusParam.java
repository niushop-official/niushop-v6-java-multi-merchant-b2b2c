package com.niu.core.service.site.goods.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品品牌参数
*/
@Data
public class EditGoodsBrandStatusParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "brand_id参数缺失")
    private Integer brandId;

    @NotNull(message = "status参数缺失")
    private Integer status;

}
