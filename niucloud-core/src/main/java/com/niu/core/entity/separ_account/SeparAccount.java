package com.niu.core.entity.separ_account;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import lombok.Data;
import java.math.BigDecimal;

/**
* 实体
*/
@Data
public class SeparAccount implements Serializable {

private static final long serialVersionUID = 1L;

    /**  */
    private Long createTime;

    /** 失败原因 */
    private String failReason;

    /**  */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 交易流水号(主要用于交互支付单据) */
    private String outTradeNo;

    /**  */
    private String payType;

    /** 订单来源 订单表的order_from */
    private String tradeScene;

    /** 分账数据   总额**   平台手续费** 商户分账** */
    private String separData;

    /** 分账时间 */
    private Long separTime;

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

}