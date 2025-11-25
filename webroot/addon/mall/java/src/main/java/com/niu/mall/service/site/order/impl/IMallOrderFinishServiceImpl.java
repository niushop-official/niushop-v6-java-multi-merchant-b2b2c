package com.niu.mall.service.site.order.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.enums.order.OrderLogMainTypeEnum;
import com.niu.mall.service.site.order.IMallOrderFinishService;
import com.niu.mall.service.core.order.ICoreOrderFinishService;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class IMallOrderFinishServiceImpl implements IMallOrderFinishService {

    @Resource
    ICoreOrderFinishService coreOrderFinishService;

    @Override
    public void finish(Integer orderId) {
        OrderFinishParam finishParam = new OrderFinishParam();
        finishParam.setOrderId(orderId);
        finishParam.setSiteId(RequestUtils.siteId());
        finishParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        finishParam.setMainId(RequestUtils.uid());
        coreOrderFinishService.finish(finishParam);

    }
}
