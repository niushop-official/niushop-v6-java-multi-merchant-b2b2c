package com.niu.mall.listener.point;

import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.event.point.PointDeductEvent;
import com.niu.core.service.core.point_deduct.ICorePointDeductService;
import jakarta.annotation.Resource;

/**
 * 积分返回监听
 */
public class CorePointListener extends AbstractListener {
    @Resource
    private ICorePointDeductService corePointDeductService;

    public void handleEvent(PointDeductEvent event) {
        corePointDeductService.refundPointDeduct(event.getSiteId(), event.getMemberId(),  event.getPoint(), event.getOrderNo());
    }
}
