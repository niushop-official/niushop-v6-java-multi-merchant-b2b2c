package com.niu.core.event.site;

import com.niu.core.common.component.context.event.Event;
import lombok.Getter;

/**
 * 站点开启后事件
 * 对应 PHP: SiteOpenAfter
 */
@Getter
public class SiteOpenAfterEvent extends Event {

    private final String type;
    private final String mainType;
    private final Integer uid;
    private final String data;

    public SiteOpenAfterEvent(Object source, Integer siteId, String type, String mainType, Integer uid, String data) {
        super(source);
        this.setSiteId(siteId);
        this.setName("SiteOpenAfterEvent");
        this.type = type;
        this.mainType = mainType;
        this.uid = uid;
        this.data = data;
    }
}
