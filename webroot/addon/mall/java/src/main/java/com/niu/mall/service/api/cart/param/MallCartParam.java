package com.niu.mall.service.api.cart.param;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.*;

/**
 * 购物车参数
*/
@Data
public class MallCartParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id = 0;

    @NotNull(message = "goodsId参数缺失")
    private Integer goodsId;

    @NotNull(message = "skuId参数缺失")
    private Integer skuId;

    @NotNull(message = "num参数缺失")
    @Min(value = 1, message = "数量最小不能小于1")
    private Integer num;

    private Integer marketType = 0;

    private Integer marketTypeId = 0;

    private String invalidRemark = "";

    private Integer status = 1;

}
