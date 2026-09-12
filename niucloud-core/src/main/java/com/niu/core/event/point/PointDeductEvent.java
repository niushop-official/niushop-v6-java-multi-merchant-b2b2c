package com.niu.core.event.point;

import com.niu.core.common.component.context.event.Event;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PointDeductEvent extends Event {
    private Integer memberId;
    private BigDecimal point;
    private  String orderNo;
}
