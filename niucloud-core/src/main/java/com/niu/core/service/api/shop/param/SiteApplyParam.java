package com.niu.core.service.api.shop.param;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 商户入驻申请参数
 */
@Data
public class SiteApplyParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Integer applyId;

    /**
     * 开店店铺id
     */
    private Integer siteId;

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
    private Integer status = 1;

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

    /**
     * 行业类目ID集合
     */
    private List<Integer> categoryIds;

    /**
     * 手机验证码key
     */
    private String mobileKey;

    /**
     * 手机验证码
     */
    private String mobileCode;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 店铺套餐id
     */
    private Integer groupId;

    private String groupName;

    /**
     * 申请会员
     */
    private Integer memberId;

    /**
     * 表单数据
     */
    @JsonProperty("form_data")
    private Object formData;
}
