package com.niu.core.service.core.goods.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 是否开启品牌审核
 */
@Data
public class CoreGoodsConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否开启品牌审核
     */
    private Integer isVerifyBrand;
}