package com.niu.core.service.admin.shop.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 商户入驻申请表参数
*/
@Data
public class SiteShopApplyVerifyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 申请状态（1待审核，2已通过，-1已拒绝） */
    private Integer status;

    /** 拒绝原因 */
    private String refuseReason = "";



}
