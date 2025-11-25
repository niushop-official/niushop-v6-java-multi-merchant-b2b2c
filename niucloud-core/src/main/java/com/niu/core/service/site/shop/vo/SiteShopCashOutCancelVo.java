package com.niu.core.service.site.shop.vo;

import lombok.Data;

import java.io.Serializable;

/**
* 店铺扩展表详情
*/
@Data
public class SiteShopCashOutCancelVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态 */
    private Integer status;

    private Long updateTime;

    private Long cancelTime;

}