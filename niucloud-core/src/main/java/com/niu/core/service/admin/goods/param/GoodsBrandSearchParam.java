package com.niu.core.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品品牌参数
 */
@Data
public class GoodsBrandSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String brandName;

}
