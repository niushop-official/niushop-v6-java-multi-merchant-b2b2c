package com.niu.mall.listener.marketing;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.mall.enums.active.ActiveClassEnum;
import com.niu.mall.event.marketing.ActiveStartAfterEvent;
import com.niu.mall.service.site.marketing.IMallDiscountService;
import org.springframework.stereotype.Component;

@EventListen("mall")
@Component
public class DiscountActiveStartListener extends AbstractListener {

    private IMallDiscountService shopDiscountService() {
        return SpringContext.bean(IMallDiscountService.class);
    }

    public void handleEvent(ActiveStartAfterEvent event) {
        if (event.getActive().getActiveClass().equals(ActiveClassEnum.DISCOUNT.getType())) {
            shopDiscountService().discountStartAfter(event.getActive().getActiveId());
        }
    }
}
