package com.niu.core.service.site.shop.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.entity.site.SitePayment;
import com.niu.core.entity.site.SitePaymentApply;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SiteShopAccountLogVo {
    /** 创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 来源类型（订单结算，提现） */
    private String fromType;

    private Integer id;

    /** 用户id */
    private Integer memberId;

    /** 备注信息 */
    private String memo;

    /** 变动账单金额 */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal money;

    /** 变动后的账户余额 */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal moneySum;

    /** 关联Id（结算id，提现id） */
    private String relatedId;

    /** 站点id */
    private Integer siteId;
}
