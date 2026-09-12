package com.niu.core.event.addon;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件安装成功后事件（对齐 PHP InstallAddonAfter）
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InstallAddonAfterEvent extends Event {

    private String addon;

    public InstallAddonAfterEvent() {
        super();
    }

    public InstallAddonAfterEvent(String addon) {
        super();
        this.addon = addon;
    }
}
