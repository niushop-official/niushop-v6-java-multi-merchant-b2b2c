package com.niu.mall.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 订单虚拟发货内容表
 */
@Data
public class MallOrderDeliveryContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id; // id
    private Integer siteId; // 站点id
    private Integer tradeId; //业务id
    private String tradeType; // 业务类型
    private String tradeNo; // 业务编号
    private String content; // 发货内容或核销码
    private Long createTime; // 创建时间

}