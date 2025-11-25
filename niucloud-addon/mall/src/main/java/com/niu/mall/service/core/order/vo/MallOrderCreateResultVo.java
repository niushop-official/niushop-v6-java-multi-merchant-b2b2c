package com.niu.mall.service.core.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单配置数据
 */
@Data
public class MallOrderCreateResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tradeType;

    private Integer tradeId;

}
