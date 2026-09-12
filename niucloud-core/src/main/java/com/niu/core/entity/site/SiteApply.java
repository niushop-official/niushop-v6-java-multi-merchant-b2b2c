package com.niu.core.entity.site;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 商户入驻申请表实体
 * v120迁移：数据从 site_shop_apply 表迁移到此表
 */
@Data
// @TableName(value = "ns_site_apply")
public class SiteApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "apply_id", type = IdType.AUTO)
    private Integer applyId;

    /**
     * 开店店铺id
     */
    private Integer siteId;

    /**
     * 申请会员
     */
    private Integer memberId;

    /**
     * 商户名称
     */
    private String siteName;

    /**
     * 用户姓名
     */
    private String userName;

    /**
     * 用户手机号
     */
    private String userMobile;

    /**
     * 所属行业id
     */
    private Integer categoryId;

    /**
     * 子级行业id
     */
    private Integer childCategoryId;

    /**
     * 营业执照图片路径
     */
    private String businessLicense;

    /**
     * 申请状态（1待审核，2已通过，-1已拒绝）
     */
    private Integer status;

    /**
     * 申请时间
     */
    private Long createTime;

    /**
     * 修改时间
     */
    private Long updateTime;

    /**
     * 审核通过时间
     */
    private Long auditTime;

    /**
     * 拒绝原因
     */
    private String refuseReason;

    /**
     * 开店状态（1待开店，2已开店）
     */
    private Integer openStatus;

    /**
     * 店铺登录账号
     */
    private String account;

    /**
     * 万能表单记录id
     */
    private Integer formRecordId;

    /**
     * 店铺登录密码
     */
    private String password;

    /**
     * 店铺用户id
     */
    private Integer uid;

    /**
     * 所属应用
     */
    private String addon;

    /**
     * 申请方式  member 客户端用户申请  site 站点端用户申请
     */
    private String applyType;

    /**
     * 商圈id
     */
    private Integer circleId;
}
