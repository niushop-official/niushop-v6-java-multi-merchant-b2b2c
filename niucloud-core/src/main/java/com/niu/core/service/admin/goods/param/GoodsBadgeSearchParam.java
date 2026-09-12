package com.niu.core.service.admin.goods.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品角标查询参数
 */
@Data
public class GoodsBadgeSearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String badgeName;

    private Integer groupId;

    private String order;

    private String sort;
}
