package com.niu.core.service.admin.shop.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 商户入驻申请表参数
*/
@Data
public class SiteShopApplyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer applyId;

    /** 审核通过时间 */
    private Long auditTime;

    /** 营业执照图片路径 */
    private String businessLicense;

    /** 店铺分类id */
    private Integer categoryId;

    /** 店铺套餐id */
    private Integer groupId;

    /** 申请会员 */
    private Integer memberId;

    /** 开店状态（1待开店，2已开店） */
    private Integer openStatus;

    /** 拒绝原因 */
    private String refuseReason;

    /** 开店店铺id */
    private Integer siteId;

    /** 商户名称 */
    private String siteName;

    /** 申请状态（1待审核，2已通过，-1已拒绝） */
    private Integer status;

    /** 用户手机号 */
    private String userMobile;

    /** 用户姓名 */
    private String userName;


}
