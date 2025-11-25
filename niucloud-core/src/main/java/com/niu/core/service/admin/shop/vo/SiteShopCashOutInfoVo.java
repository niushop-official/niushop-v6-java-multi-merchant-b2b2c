package com.niu.core.service.admin.shop.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;

import java.io.Serializable;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商家站点提现表详情
 */
@Data
public class SiteShopCashOutInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

     // 基本字段 - PHP中指定的8个字段
     private BigDecimal applyMoney;    // 申请金额
     private BigDecimal money;         // 实际金额
     private BigDecimal serviceMoney;  // 服务费
     private Integer status;           // 状态
     @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
     private Long createTime;          // 创建时间
     @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
     private Long auditTime;           // 审核时间
     private String transferType;      // 转账类型 ("1")
     @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
     private Long transferTime;        // 转账时间
 
     // 必要的id字段
     private Integer id;               // 记录ID
     private Integer siteId;           // 站点ID
     
     // 计算属性 - PHP中的append
     private String statusName;        // 状态名称 ("待审核")
     private String transferTypeName;  // 转账方式名称 ("银行卡")
     
     // 其他在前端显示需要的字段
     private String accountType;       // 账户类型 ("money")
     private String cashOutNo;         // 提现单号
     private String finalTransferType;
     private String refuseReason;      // 拒绝原因
     private String remark;            // 备注
     private String transferAccount;   // 转账账号
     private String transferBank;      // 银行名称
     private String transferFailReason;
     private String transferMobile;
     private String transferNo;
     private String transferPayee;
     private String transferPaymentCode;
     private String transferRealname;  // 收款人姓名
     private String transferStatus;
     private String transferVoucher;   // 转账凭证
     private BigDecimal rate;          // 费率
     @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
     private Long updateTime;          // 更新时间
     @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
     private Long cancelTime;          // 取消时间
 
     // 关联站点信息 - PHP中的withJoin
     @JsonProperty("siteInfo")
     private SiteInfoVO siteInfo;
 
     @Data
     public static class SiteInfoVO {
         private Integer siteId;         // 站点ID
         private String siteName;        // 站点名称
         private Integer groupId;        // 分组ID
         private String keywords;
         private String appType;         // 应用类型
         private String addons;
         private String address;         // 详细地址
         private String app;             // 应用列表
         private String businessHours;   // 营业时间
         private Integer cityId;         // 城市ID
         private String createTime;      // 站点创建时间
         private String desc;
         private Integer districtId;     // 区县ID
         private Long expireTime;        // 过期时间戳
         private String frontEndLogo;    // 前端LOGO路径
         private String frontEndName;
         private String fullAddress;     // 完整地址
         private String icon;
         private Object initalledAddon;
         private String latitude;        // 纬度
         private String logo;            // LOGO路径
         private String longitude;       // 经度
         private String memberNo;
         private String phone;           // 联系电话
         private Integer provinceId;     // 省份ID
         private String siteDomain;
         private Integer status;         // 站点状态
    }

}