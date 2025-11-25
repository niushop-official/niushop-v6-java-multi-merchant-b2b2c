package com.niu.mall.entity.order.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单关闭配置
 */
@Data
public class MallOrderClose implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer isClose; // 是否关闭
    private Boolean isCloseEr; // 是否关闭
    private Integer closeLength; // 关闭时长

}