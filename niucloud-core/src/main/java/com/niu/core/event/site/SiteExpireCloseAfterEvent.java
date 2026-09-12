package com.niu.core.event.site;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 站点到期或关闭后事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SiteExpireCloseAfterEvent extends Event {

    public SiteExpireCloseAfterEvent() {
        super();
        this.setName("SiteExpireCloseAfterEvent");
    }
}
