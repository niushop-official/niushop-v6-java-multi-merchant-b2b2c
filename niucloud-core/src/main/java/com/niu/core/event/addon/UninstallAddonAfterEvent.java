package com.niu.core.event.addon;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插件卸载成功后事件（对齐 PHP UninstallAddonAfter）
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UninstallAddonAfterEvent extends Event {

    private String addon;

    public UninstallAddonAfterEvent() {
        super();
    }

    public UninstallAddonAfterEvent(String addon) {
        super();
        this.addon = addon;
    }
}
