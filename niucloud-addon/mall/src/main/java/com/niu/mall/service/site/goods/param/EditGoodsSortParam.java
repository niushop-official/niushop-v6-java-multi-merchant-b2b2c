package com.niu.mall.service.site.goods.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商品参数
*/
@Data
public class EditGoodsSortParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "goods_id参数缺失")
    private Integer goodsId;

    private Integer shopSort;
}
