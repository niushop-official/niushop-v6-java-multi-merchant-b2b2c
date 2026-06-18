package com.niu.mall.service.site.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * ShopOrder列表视图
 */
@Data
public class MallOrderMemberConsumeListVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int orderId;
    private String orderNo;
    private String goodsMoney;
    private String orderMoney;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long payTime;
    private String takerName;
    private String takerMobile;
    private String takerFullAddress;

}
