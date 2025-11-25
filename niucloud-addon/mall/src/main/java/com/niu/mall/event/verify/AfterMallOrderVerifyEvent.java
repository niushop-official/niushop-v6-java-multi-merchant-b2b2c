package com.niu.mall.event.verify;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;

@Data
public class AfterMallOrderVerifyEvent extends Event {

    private Integer orderGoodsId;
}