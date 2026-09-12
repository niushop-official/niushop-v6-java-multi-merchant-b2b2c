package com.niu.core.entity.site;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 站点实体
 */
@Data
public class Site implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="site_id", type= IdType.AUTO)
    private Integer siteId;

    /** 站点名称 */
    private String siteName;

    /** 分组ID(0:不限制) */
    private Integer groupId;

    /** 站点类型 */
    private String appType;

    /** 站点logo */
    private String logo;

    /** 简介 */
    @TableField(value = "`desc`")
    private String desc;

    /** 状态 1-正常 0-体验期 2-已到期 */
    private Integer status;

    /** 纬度 */
    private String latitude;

    /** 经度 */
    private String longitude;

    /** 省 */
    private Integer provinceId;

    /** 市 */
    private Integer cityId;

    /** 区 */
    private Integer districtId;

    /** 详细地址 */
    private String address;

    /** 完整地址 */
    private String fullAddress;

    /** 客服电话 */
    private String phone;

    /** 营业时间 */
    private String businessHours;

    /** 营业状态（0已打烊，1营业中） */
    private Integer businessState;

    /** 创建时间 */
    private Long createTime;

    /** 到期时间（如果是0 无限期） */
    private Long expireTime; //

    /** 前台名称 */
    private String frontEndName;

    /** 前台logo */
    private String frontEndLogo;

    /** 网站图标 */
    private String icon;

    /** 最大会员码值 */
    private Integer memberNo;

    /** 站点应用 */
    private String app;

    /** 站点包含的插件 */
    private String addons;

    /** 站点已执行初始化方法的插件 */
    private String initalledAddon;

    /** 站点域名 */
    private String siteDomain;

    /** 是否支持物流配送 */
    private Integer isExpress;

    /** 是否支持同城配送 */
    private Integer isLocalDelivery;

    /** 是否支持门店自提 */
    private Integer isStore;

    /** 配送设置 */
    private String deliveryConfig;

    /** 行业类目ID集合(JSON格式) */
    private String categoryIds;

    private Integer isinit;

    /** 联系人姓名 */
    private String contactName;

    /** 联系电话 */
    private String contactMobile;

    /** 当前余额 */
    private BigDecimal money;

    /** 累计结算 */
    private BigDecimal moneyGet;

    /** 提现中 */
    private BigDecimal moneyCashOuting;

    /** 营业执照 */
    private String businessLicense;

}