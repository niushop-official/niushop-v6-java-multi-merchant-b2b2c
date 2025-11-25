package com.niu.mall.event.refund;

import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import lombok.Data;

@Data
public class SyncSupplyRefundEditEvent extends Event {

    private MallOrderRefund refundData;
    private MallOrderGoods orderGoodsData;
    private String refundType;
    private String reason;
    private String remark;
    private String voucher;
}