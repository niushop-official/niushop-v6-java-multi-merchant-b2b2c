package com.niu.mall.service.admin.goods.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑商品标签参数
 */
@Data
public class EditGoodsLabelParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID列表
     */
    private List<Integer> goodsIds;

    /**
     * 标签ID列表
     */
    private List<Integer> labelIds;
}
