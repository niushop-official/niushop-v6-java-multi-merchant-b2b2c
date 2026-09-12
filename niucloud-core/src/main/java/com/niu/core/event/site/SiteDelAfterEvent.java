package com.niu.core.event.site;

import com.niu.core.common.component.context.event.Event;
import lombok.Getter;

/**
 * 站点删除后事件
 * 对应 PHP: SiteDelAfter
 */
@Getter
public class SiteDelAfterEvent extends Event {

    private final String type;
    private final String mainType;
    private final Integer uid;
    private final String data;

    public SiteDelAfterEvent(Object source, Integer siteId, String type, String mainType, Integer uid, String data) {
        super(source);
        this.setSiteId(siteId);
        this.setName("SiteDelAfterEvent");
        this.type = type;
        this.mainType = mainType;
        this.uid = uid;
        this.data = data;
    }
}
