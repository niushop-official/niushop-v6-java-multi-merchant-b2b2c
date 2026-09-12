package com.niu.core.event;

import com.niu.core.entity.site.SiteApply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 编辑商户入驻申请事件
 */
@Getter
public class EditSiteApplyEvent extends ApplicationEvent {

    private final SiteApply siteApply;

    private final Object formData;

    public EditSiteApplyEvent(Object source, SiteApply siteApply, Object formData) {
        super(source);
        this.siteApply = siteApply;
        this.formData = formData;
    }

    public EditSiteApplyEvent(SiteApply siteApply, Object formData) {
        super(siteApply);
        this.siteApply = siteApply;
        this.formData = formData;
    }
}
