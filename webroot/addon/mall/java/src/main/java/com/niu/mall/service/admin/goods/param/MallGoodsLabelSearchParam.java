package com.niu.mall.service.admin.goods.param;
import lombok.Data;
import java.io.Serializable;

/**
 * 商品标签参数
 */
@Data
public class MallGoodsLabelSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String labelName;

}
