package com.niu.mall.service.core.refund.vo;

import com.niu.mall.entity.order.MallOrder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRefundMoneyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal refundMoney;
    private MallOrder orderData;
    private List<MallOrderGoods> orderGoodsList;

    @Data
    public static class MallOrderGoods extends com.niu.mall.entity.order.MallOrderGoods {
        private Integer isRefundDelivery;

        private BigDecimal itemRefundMoney;
    }
}
