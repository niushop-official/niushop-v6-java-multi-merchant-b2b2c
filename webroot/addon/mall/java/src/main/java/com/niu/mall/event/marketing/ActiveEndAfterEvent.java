package com.niu.mall.event.marketing;

import com.niu.core.common.component.context.event.Event;
import com.niu.mall.entity.active.MallActive;
import lombok.Data;

@Data
public class ActiveEndAfterEvent extends Event {
    private MallActive active;
}
