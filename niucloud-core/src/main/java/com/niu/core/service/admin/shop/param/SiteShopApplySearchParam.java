package com.niu.core.service.admin.shop.param;

import lombok.Data;
import java.io.Serializable;

/**
 * 商户入驻申请表参数
 */
@Data
public class SiteShopApplySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 申请时间 */
    private String[] createTime;

    /** 店铺套餐id */
    private Integer groupId;


    /** 申请分类id */
    private Integer categoryId;

    /** 申请状态（1待审核，2已通过，-1已拒绝） */
    private Integer status;

    private String keywords;

}
