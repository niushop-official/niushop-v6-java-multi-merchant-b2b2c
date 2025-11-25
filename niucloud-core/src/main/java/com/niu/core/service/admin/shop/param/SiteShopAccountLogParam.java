package com.niu.core.service.admin.shop.param;

import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商家结算账单记录表参数
*/
@Data
public class SiteShopAccountLogParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 来源类型（订单结算，提现） */
    private String fromType;

    /** 用户id */
    private Integer memberId;

    /** 备注信息 */
    private String memo;

    /** 变动账单金额 */
    private BigDecimal money;

    /** 变动后的账户余额 */
    private BigDecimal moneySum;

    /** 关联Id（结算id，提现id） */
    private String relatedId;

    /** 站点id */
    private Integer siteId;


}
