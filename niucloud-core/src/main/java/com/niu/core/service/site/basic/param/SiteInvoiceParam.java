package com.niu.core.service.site.basic.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 发票表参数
*/
@Data
public class SiteInvoiceParam implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 创建时间开始
     */
    private String[] createTime;

    /** 注册地址 */
    private String address;

    /** 银行账号 */
    private String bankCardNumber;

    /** 开户银行 */
    private String bankName;

    /** 开票人邮箱 */
    private String email;

    /** 名称（发票抬头） */
    private String headerName;

    /** 抬头类型 */
    private Integer headerType;

    /** 发票代码 */
    private String invoiceNumber;

    /** 开票时间 */
    private  String[] invoiceTime;

    /** 发票凭证 */
    private String invoiceVoucher;

    /** 是否开票 */
    private Integer isInvoice;

    /** 会员id */
    private Integer memberId;

    /** 开票人手机号 */
    private String mobile;

    /** 开票金额 */
    private BigDecimal money;

    /** 发票内容 */
    private String name;

    /** 备注 */
    private String remark;

    /** 站点id */
    private Integer siteId;

    /** 是否生效 */
    private Integer status;

    /** 公司税号 */
    private String taxNumber;

    /** 注册电话 */
    private String telephone;

    /** 业务id */
    private Integer tradeId;

    /** 开票分类 order:订单 */
    private String tradeType;

    /** 发票类型 */
    private Integer type;
}
