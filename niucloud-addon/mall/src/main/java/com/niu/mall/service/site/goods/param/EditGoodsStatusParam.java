package com.niu.mall.service.site.goods.param;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 商品参数
*/
@Data
public class EditGoodsStatusParam implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer isAll = 0;
    private Map<String, Object> where;
    @NotNull(message = "goods_ids参数缺失")
    private Object goodsIds;

    @NotNull(message = "status参数缺失")
    private Integer status;
}
