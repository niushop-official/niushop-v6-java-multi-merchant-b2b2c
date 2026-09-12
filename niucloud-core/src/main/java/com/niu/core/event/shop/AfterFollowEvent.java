package com.niu.core.event.shop;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

@Data
public class AfterFollowEvent extends ApplicationEvent {
    private final Integer siteId;
    private final String addon;
    private final Integer followNumber;

    public AfterFollowEvent(Object source, Integer siteId, String addon, Integer followNumber) {
        super(source);
        this.siteId = siteId;
        this.addon = addon;
        this.followNumber = followNumber;
    }
}
