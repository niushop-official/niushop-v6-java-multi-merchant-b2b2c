package com.niu.core.event.order;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.service.admin.stat.vo.AddStatVo;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统计全局监听
 */
@Data
@AllArgsConstructor
public class CoreStatEvent extends Event {
    private AddStatVo addStatVo;
}
