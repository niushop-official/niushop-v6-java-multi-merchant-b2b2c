package com.niu.core.event.common;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用事件，用于无相互依赖关系定义
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonEvent extends Event {



    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 定义数据对象
     */
    private Object data;


    /**
     * 关联会员id
     */
    private Integer memberId;



}
