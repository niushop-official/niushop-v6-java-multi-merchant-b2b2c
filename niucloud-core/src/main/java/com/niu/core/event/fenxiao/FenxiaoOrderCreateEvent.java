package com.niu.core.event.fenxiao;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分销订单创建（支付后计算佣金）
 * 对应 PHP: FenxiaoOrderCreate
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FenxiaoOrderCreateEvent extends Event {

    /** 业务类型 */
    private String tradeType;
    /** 订单数据 */
    private FenxiaoOrderData orderData;
    /** 订单项列表 */
    private List<FenxiaoOrderGoodsItem> orderGoods;
}
