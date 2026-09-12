package com.niu.core.service.api.shop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.site.SiteStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
* 店铺扩展表详情
*/
@Data
public class SiteShopInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 支付宝账号 */
    private String alipayAccountNo;

    /** 支付宝所属人姓名 */
    private String alipayName;

    /** 支付宝收款码图片 */
    private String alipayPaymentCode;

    /** 提现账号对应姓名 */
    private String bankAccountName;

    /** 提现银行账号 */
    private String bankAccountNo;

    /** 银行地址 */
    private String bankAddress;

    /** 银行名称 */
    private String bankName;

    /** 提现账户类型（1银行卡，2支付宝，3微信，4微信零钱） */
    private Integer bankType;

    /** 营业执照 */
    private String businessLicense;

    /** 商家分类id */
    private Integer categoryId;

    private String categoryName;


    /** 联系电话 */
    private String contactMobile;

    /** 店铺关注数 */
    private Integer followNumber;

    /** 主键 */
    private Integer id;

    /** 是否推荐 */
    private Integer isRecommend;

    /** 是否自营 */
    private Integer isSelf;

    /** 站点id */
    private Integer siteId;

    /** 站点名称 */
    private String siteName;
    /** 分组ID(0:不限制) */
    private Integer groupId;
    /** 站点套餐名称 */
    private String groupName;
    /** 关键字 */
    private String keywords;
    /** 站点类型 */
    private String appType;
    /** 站点logo */
    private String logo;
    /** 简介 */
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
    /** 创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    /** 到期时间（如果是0 无限期） */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long expireTime;
    /** 前台名称*/
    private String frontEndName;
    /** 前台logo */
    private String frontEndLogo;
    /** 前台icon */
    private String frontEndIcon;
    /** 网站图标 */
    private String icon;

    private BigDecimal qualityScore;

    private BigDecimal serviceScore;

    private BigDecimal logisticsScore;

    /** 状态名称 */
    private String statusName;

    private String backgroundH5;
    private String contactName;
    private SiteShop shop;

    private MemberInfo memberInfo;
    private BigDecimal distance;

    /** 店铺二维码配置 */
    private Map<String, Object> qrcodeShopConfig;

    public String getStatusName() {
        return SiteStatusEnum.getNameByCode(this.status);
    }

    @Data
    public static class SiteShop {

        /** 商家分类id */
        private Integer categoryId;

        /** 店铺关注数 */
        private Integer followNumber;

        /** 是否推荐 */
        private Integer isRecommend;

        /** 是否自营 */
        private Integer isSelf;

        /** 站点id */
        private Integer siteId;

        private String categoryName;

        private String groupName;
        private String backgroundH5;
        private String contactName;
    }

    @Data
    public static class MemberInfo{
        private Integer isFollow;
    }

}
