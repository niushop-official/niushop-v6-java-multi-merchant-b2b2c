package com.niu.core.service.admin.goods.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 商品品牌参数
*/
@Data
public class GoodsBrandMoveParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer brandId;

}
