package com.niu.mall.service.api.cart.param;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 购物车参数
*/
@Data
public class MallCartEditParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id = 0;

    @NotNull(message = "goodsId参数缺失")
    private Integer goodsId;

    @NotNull(message = "skuId参数缺失")
    private Integer skuId;

    @NotNull(message = "num参数缺失")
    @Min(value = 1, message = "数量最小不能小于1")
    private Integer num;

}
