package com.niu.core.entity.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 商户入驻申请表实体（v120 已迁移至 site_apply，此类仅保留兼容）
 * @deprecated 请使用 {@link com.niu.core.entity.site.SiteApply}
 */
@Deprecated
@Data
public class SiteShopApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "apply_id", type = IdType.AUTO)
    private Integer applyId;

    /**
     * 审核通过时间
     */
    private Long auditTime;

    /**
     * 营业执照图片路径
     */
    private String businessLicense;

    /**
     * 店铺分类id
     */
    private Integer categoryId;

    /**
     * 申请时间
     */
    private Long createTime;

    /**
     * 店铺套餐id
     */
    private Integer groupId;

    /**
     * 申请会员
     */
    private Integer memberId;

    /**
     * 开店状态（1待开店，2已开店）
     */
    private Integer openStatus;

    /**
     * 拒绝原因
     */
    private String refuseReason;

    /**
     * 开店店铺id
     */
    private Integer siteId;

    /**
     * 商户名称
     */
    private String siteName;

    /**
     * 申请状态（1待审核，2已通过，-1已拒绝）
     */
    private Integer status;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 用户手机号
     */
    private String userMobile;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户账号
     */
    private String account;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 万能表单记录id
     */
    private Integer formRecordId;

    private Integer uid;
}