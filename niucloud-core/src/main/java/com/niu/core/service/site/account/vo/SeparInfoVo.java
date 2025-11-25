package com.niu.core.service.site.account.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.account.SeparStatusEnum;
import com.niu.core.enums.account.SeparTradeTypeEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* 详情
*/
@Data
public class SeparInfoVo implements Serializable {

private static final long serialVersionUID = 1L;


    /** 失败原因 */
    private String failReason;


    /**  */
    private Integer id;


    /** 交易流水号(主要用于交互支付单据) */
    private String outTradeNo;


    /**  */
    private String payType;


    /** 分账数据   总额**   平台手续费** 商户分账** */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String separData;

    /** 分账时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long separTime;

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;


    /** 站点id */
    private Integer siteId;


    /** 站点名称 */
    private String siteName;


    /** 分账状态  wait 待分账 dealing  分账中success成功过  fail  失败 */
    private String status;


    /** 业务id */
    private Integer tradeId;


    /** 业务订单金额 */
    private BigDecimal tradeMoney;


    /** 业务单号 */
    private String tradeNo;

    /** 业务类型 */
    private String tradeType;

    private String payTypeName;

    private String tradeTypeName;

    private String statusName;

    public String getStatusName() {
        return SeparStatusEnum.getName(this.status);
    }

    public String getPayTypeName() {
        return PayTypeEnum.getType().getByPath(this.payType + ".name", String.class);
    }

    public String getTradeTypeName() {
        return SeparTradeTypeEnum.getType().getStr(this.tradeType, "");
    }

}