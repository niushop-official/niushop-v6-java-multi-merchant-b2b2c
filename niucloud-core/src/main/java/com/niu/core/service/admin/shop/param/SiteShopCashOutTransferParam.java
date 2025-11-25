package com.niu.core.service.admin.shop.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商家站点提现表参数
*/
@Data
public class SiteShopCashOutTransferParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 提现账户类型 */
    private String accountType;

    /** 提现申请金额 */
    private BigDecimal applyMoney;

    /** 审核时间 */
    private Long auditTime;

    /** 取消时间 */
    private Long cancelTime;

    /** 提现交易号 */
    private String cashOutNo;

    /** 转账方式 */
    private String finalTransferType;

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
    private Long transferTime;

    /** 转账提现类型 */
    private String transferType;

    /** 转账凭证 */
    private String transferVoucher;


}
