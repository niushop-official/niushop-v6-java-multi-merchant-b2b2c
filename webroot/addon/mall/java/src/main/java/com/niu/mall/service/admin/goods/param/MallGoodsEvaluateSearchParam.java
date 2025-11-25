package com.niu.mall.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品评价参数
 */
@Data
public class MallGoodsEvaluateSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String goodsName;

}
