package com.niu.core.service.admin.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品品牌查询参数
 */
@Data
public class GoodsBrandSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String brandName;
    private Integer status;
    private Integer auditStatus;
    private Integer isHot;
    private String order;
    private String sort;
    private String addon;
}
