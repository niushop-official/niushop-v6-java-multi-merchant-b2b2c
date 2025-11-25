package com.niu.core.service.site.goods.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品品牌参数
*/
@Data
public class GoodsBrandParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "brand_name参数缺失")
    @NotEmpty(message = "品牌名称不能为空")
    private String brandName;

    @NotNull(message = "logo参数缺失")
    private String logo;

    @NotNull(message = "desc参数缺失")
    private String desc;

    @NotNull(message = "sort参数缺失")
    private String sort;

}
