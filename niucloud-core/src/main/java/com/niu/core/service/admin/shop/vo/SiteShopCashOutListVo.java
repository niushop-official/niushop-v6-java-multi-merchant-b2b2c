package com.niu.core.service.admin.shop.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import java.io.Serializable;

import com.niu.core.entity.site.Site;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.enums.shop.ShopTransferEnum;
import lombok.Data;
import java.math.BigDecimal;
/**
* 商家站点提现表列表
*/
@Data
public class SiteShopCashOutListVo implements Serializable {

private static final long serialVersionUID = 1L;

        /** 提现账户类型 */
        private String accountType;

        /** 提现申请金额 */
        private BigDecimal applyMoney;

        /** 审核时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long auditTime;

        /** 取消时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long cancelTime;

        /** 提现交易号 */
        private String cashOutNo;


        /** 申请时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;


        /** 转账方式 */
        private String finalTransferType;

        /**  */
        private Integer id;

        /** 提现到账金额 */
        private BigDecimal money;

        /** 提现手续费比率 */
        private BigDecimal rate;

        /** 拒绝理由 */
        private String refuseReason;

        /** 备注 */
        private String remark;

        /** 提现手续费 */
        private BigDecimal serviceMoney;

        /** 提现站点id */
        private Integer siteId;

        private String siteName;


        /** 前台名称 */
        private String frontEndName;

        /** 前台logo */
        private String frontEndLogo;

        /** 前台icon */
        private String frontEndIcon;

        /** 网站图标 */
        private String icon;

        @JsonProperty("siteInfo")
        private SiteInfo siteInfo;

        /** 状态（1待审核 2待转账 3已转账 -1拒绝 -2 已取消） */
        private Integer status;

        /** 收款账号 */
        private String transferAccount;

        /** 银行名称 */
        private String transferBank;

        /** 失败原因 */
        private String transferFailReason;

        /** 手机号 */
        private String transferMobile;

        /** 转账单号 */
        private String transferNo;

        /** 转账收款方(json),主要用于对接在线的打款方式 */

        private String transferPayee;

        /** 收款码图片 */
        private String transferPaymentCode;

        /** 联系人名称 */
        private String transferRealname;

        /** 转账状态 */
        private String transferStatus;

        /** 转账时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long transferTime;

        /** 转账提现类型 */
        private String transferType;
        private String phone;           // 联系电话
        /** 转账凭证 */
        private String transferVoucher;
        /*提现方式*/
        private String transferTypeName;
        /*提现状态*/
        private String statusName;
        @Data
        public static class SiteInfo {

                private Integer siteId;

                private String siteName;

                /** 前台名称 */
                private String frontEndName;

                /** 前台logo */
                private String frontEndLogo;

                /** 前台icon */
                private String frontEndIcon;

                private String phone;           // 联系电话
                /** 网站图标 */
                private String icon;

        }




}
