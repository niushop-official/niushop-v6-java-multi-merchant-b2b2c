package com.niu.core.service.site.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品参数参数
 */
@Data
public class GoodsAttrSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String attrName;

}
