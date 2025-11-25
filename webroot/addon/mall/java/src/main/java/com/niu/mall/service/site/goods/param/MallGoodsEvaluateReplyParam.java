package com.niu.mall.service.site.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品评价参数
*/
@Data
public class MallGoodsEvaluateReplyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer evaluateId;

    private String explainFirst;

}
