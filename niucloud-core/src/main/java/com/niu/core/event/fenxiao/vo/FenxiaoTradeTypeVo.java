package com.niu.core.event.fenxiao.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 分销业务类型配置
 */
@Data
public class FenxiaoTradeTypeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务名称
     */
    private String name;

    /**
     * 业务类型标识
     */
    private String type;

    /**
     * 商品详情页路径
     */
    private String goodsDetailPath;

    /**
     * 订单详情页路径
     */
    private String orderDetailPath;
}
