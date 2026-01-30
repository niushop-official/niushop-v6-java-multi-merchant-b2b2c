package com.niu.core.service.api.shop.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.enums.shop.ShopTransferEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* 商家站点提现表详情
*/
@Data
public class SiteShopCashOutInfoNewVo implements Serializable {

private static final long serialVersionUID = 1L;

    /**  */
    private Integer id;
    /** 提现站点id */
    private Integer siteId;
    /** 提现交易号 */
    private String cashOutNo;
    /** 转账提现类型 */
    private String transferType;
    /** 联系人名称 */
    private String transferRealname;
    /** 手机号 */
    private String transferMobile;
    /** 银行名称 */
    private String transferBank;
    /** 收款账号 */
    private String transferAccount;
    /** 失败原因 */
    private String transferFailReason;
    /** 转账时间 */
    private Long transferTime;
    /** 提现申请金额 */
    private BigDecimal applyMoney;
    /** 提现手续费比率 */
    private BigDecimal rate;

    /** 提现手续费 */
    private BigDecimal serviceMoney;
    /** 提现到账金额 */
    private BigDecimal money;
    /** 审核时间 */
    private Long auditTime;
    /** 状态（1待审核 2待转账 3已转账 -1拒绝 -2 已取消） */
    private Integer status;
    /** 备注 */
    private String remark;
    /** 申请时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    /** 拒绝理由 */
    private String refuseReason;
    /** 转账单号 */
    private String transferNo;
    /** 转账收款方(json),主要用于对接在线的打款方式 */
    private String transferPayee;
    /** 收款码图片 */
    private String transferPaymentCode;


    private String accountTypeName;
    private String transferTypeName;
    private String statusName;
    private String transferStatusName;

    public String getStatusName() {
        return ShopCashOutEnum.getStatusByName(status);
    }
    public String getTransferTypeName() {
        if (StringUtils.isNotEmpty(this.getTransferType())) {
            return ShopTransferEnum.getNameByTag( Integer.parseInt(this.getTransferType()));
        }
        return "";
    }


}