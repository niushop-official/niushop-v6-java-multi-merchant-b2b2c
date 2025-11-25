package com.niu.mall.entity.order.config;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单完成配置
 */
@Data
public class MallOrderFinish implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer isFinish; // 是否自动完成
    private Boolean isFinishEr; // 是否自动完成
    private Integer finishLength; // 完成时长

}