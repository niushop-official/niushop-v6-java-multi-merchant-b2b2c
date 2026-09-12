package com.niu.core.service.api.shop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;

/**
 * 商户入驻申请列表VO
 */
@Data
public class SiteApplyListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer applyId;

    /** 开店店铺id */
    private Integer siteId;

    /** 申请会员 */
    private Integer memberId;

    /** 会员昵称 */
    private String nickname;

    /** 商户名称 */
    private String siteName;

    /** 用户姓名 */
    private String userName;

    /** 用户手机号 */
    private String userMobile;

    /** 所属行业id */
    private Integer categoryId;

    /** 所属行业名称 */
    private String categoryName;

    /** 子级行业id */
    private Integer childCategoryId;

    /** 子级行业名称 */
    private String childCategoryName;

    /** 营业执照图片路径 */
    private String businessLicense;

    /** 申请状态（1待审核，2已通过，-1已拒绝） */
    private Integer status;

    /** 申请状态名称 */
    private String statusName;

    /** 申请时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 修改时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    /** 审核通过时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long auditTime;

    /** 拒绝原因 */
    private String refuseReason;

    /** 开店状态（1待开店，2已开店） */
    private Integer openStatus;

    /** 开店状态名称 */
    private String openStatusName;

    /** 店铺套餐id */
    private Integer groupId;

    /** 店铺套餐名称 */
    private String groupName;

    /** 店铺用户id */
    private Integer uid;

    /** 行业类目ID集合(JSON格式) */
    private String categoryIds;

    /** 行业类目ID集合 - 转换后的List */
    private java.util.List<Integer> categoryIdList;
}
