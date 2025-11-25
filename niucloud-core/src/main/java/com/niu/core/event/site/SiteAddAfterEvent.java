package com.niu.core.event.site;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 自定义事件（采用SpringBoot的事件）
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SiteAddAfterEvent extends Event {

    private Site site;

    private SiteGroup siteGroup;

    /** 主应用列表 */
    private List<String> mainApp;

    /** 站点插件列表 */
    private List<String> siteAddons;
}