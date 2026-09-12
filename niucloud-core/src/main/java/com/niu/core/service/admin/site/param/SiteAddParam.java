package com.niu.core.service.admin.site.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加站点参数
 */
@Data
public class SiteAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mobile;

    // ====== 站点基础信息 ======
    @NotBlank(message = "siteName不能为空")
    private String siteName;

    private String phone;
    private String frontEndLogo;
    private String latitude;
    private String longitude;

    private Integer provinceId = 0;
    private Integer cityId = 0;
    private Integer districtId = 0;
    private String address;
    private String fullAddress;

    private String expireTime;          // 过期时间戳（0表示永久）
    private String siteDomain;

    // ====== 行业分类（必填） ======
    private Integer groupId;
    private Integer categoryId;

    // ====== 用户信息（用于创建管理员） ======
    private Integer uid;              // 若提供，则使用现有用户
    private String username;          // 若 uid 为空，则必填
    private String password;          // 若 uid 为空，则必填
    private String realName;          // 真实姓名
    private String headImg;           // 头像
    private Integer status = 1;       // 用户状态

    // ====== 其他（非必填） ======
    private Integer siteId;           // 指定站点ID（用于特殊场景）
    private Integer applyId;          // 入驻申请ID（用于更新申请状态）


    private Integer isSelf;        // 是否自营

    private Integer childCategoryId;
    private Integer circleId;
    private String businessLicense;
    /** 联系人姓名 */
    private String contactName;
}
