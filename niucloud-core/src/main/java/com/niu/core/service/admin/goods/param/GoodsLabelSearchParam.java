package com.niu.core.service.admin.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品标签查询参数
 */
@Data
public class GoodsLabelSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String labelName;

    private Integer groupId;

    private String order;

    private String sort;
}
