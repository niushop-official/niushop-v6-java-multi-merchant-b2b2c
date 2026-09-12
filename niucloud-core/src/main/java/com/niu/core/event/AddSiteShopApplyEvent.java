package com.niu.core.event;

import com.niu.core.entity.shop.SiteShopApply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 商户入驻申请提交后事件
 * 用于在申请提交后触发后续业务处理，如保存表单数据等
 */
@Getter
/**
 * @deprecated 请使用 {@link com.niu.core.event.AddSiteApplyEvent}
 */
@Deprecated
public class AddSiteShopApplyEvent extends ApplicationEvent {

    private final SiteShopApply siteShopApply;

    public AddSiteShopApplyEvent(SiteShopApply siteShopApply) {
        super(siteShopApply);
        this.siteShopApply = siteShopApply;
    }
}