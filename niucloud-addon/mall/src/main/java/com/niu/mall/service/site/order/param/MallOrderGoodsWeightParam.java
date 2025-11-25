package com.niu.mall.service.site.order.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 订单批量发货参数
*/
@Data
public class MallOrderGoodsWeightParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> order_goods_ids;

}
