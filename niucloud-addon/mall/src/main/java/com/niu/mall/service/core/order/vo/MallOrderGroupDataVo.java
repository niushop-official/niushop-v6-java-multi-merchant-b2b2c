package com.niu.mall.service.core.order.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单分组实体
 */
@Data
public class MallOrderGroupDataVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String error;

    private List<MallOrderListVo> orderList;

    private Integer groupId;

    private Boolean isAllowClose = false;

    private Boolean isAllowPay = false;

    private String takerFullAddress;

}