package com.niu.core.service.admin.recharge.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.core.recharge.order.vo.RechargeGiftContentVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class RechargeListVo {
    private Integer  packageId;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 充值金额
     */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal price;

    /**
     * 赠送金额
     */
    @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
    private BigDecimal giveMoney;

    /**
     * 赠送积分
     */
    private Integer point;

    /**
     * 赠送成长值
     */
    private Integer growth;

    /**
     * 状态（0关闭 1开启）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /**
     * 排序号
     */
    private Integer sort;
    /**
     * 赠品json
     */
    @JsonSerialize(using = BeanJsonSerializer.StringToJSONObjectSerializer.class)
    private String giftJson;

    /**
     * 发放数量
     */
    private Integer saleNum;

    private List<RechargeGiftContentVo> giftContent;

}
