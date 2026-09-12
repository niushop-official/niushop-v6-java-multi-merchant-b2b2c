package com.niu.core.event;

import com.niu.core.entity.site.SiteApply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 添加商户入驻申请事件
 */
@Getter
public class AddSiteApplyEvent extends ApplicationEvent {

    private final SiteApply siteApply;

    private final Object formData;

    public AddSiteApplyEvent(Object source, SiteApply siteApply, Object formData) {
        super(source);
        this.siteApply = siteApply;
        this.formData = formData;
    }

    public AddSiteApplyEvent(SiteApply siteApply, Object formData) {
        super(siteApply);
        this.siteApply = siteApply;
        this.formData = formData;
    }
}
