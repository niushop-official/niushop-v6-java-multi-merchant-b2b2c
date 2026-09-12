package com.niu.mall.listener.order;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.order.CoreStatEvent;
import com.niu.mall.service.core.shop.ICoreStatService;
import com.niu.mall.service.core.shop.param.AddStatParam;
import org.springframework.stereotype.Component;

/**
 * 订单全局增加统计
 */
@Component
@EventListen("mall")
public class OrderCoreStatListener extends AbstractListener {

    public void handleEvent(CoreStatEvent event) {
        int siteId = RequestUtils.siteId();
        if (ObjectUtil.isNotEmpty(event.getSiteId())) {
            siteId = event.getSiteId();
        }

        AddStatParam param = BeanUtil.copyProperties(event.getAddStatVo(), AddStatParam.class);
        SpringContext.bean(ICoreStatService.class).addStat(siteId, param);
    }
}
