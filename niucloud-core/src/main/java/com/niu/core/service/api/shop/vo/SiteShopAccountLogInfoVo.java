package com.niu.core.service.api.shop.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* 商家结算账单记录表详情
*/
@Data
public class SiteShopAccountLogInfoVo implements Serializable {

private static final long serialVersionUID = 1L;


    /** 创建时间 */
    private Long createTime;


    /** 来源类型（订单结算，提现） */
    private String fromType;


    /**  */
    private Integer id;


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