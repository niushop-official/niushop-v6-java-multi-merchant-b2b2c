package com.niu.core.event.goods;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 品牌删除前检查
 * 对应 PHP: BeforeBrandDelete
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BeforeBrandDeleteEvent extends Event {

    private Integer brandId;
}
