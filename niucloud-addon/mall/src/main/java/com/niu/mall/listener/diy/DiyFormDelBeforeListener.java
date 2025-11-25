package com.niu.mall.listener.diy;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.event.diy.DiyFormDelBeforeEventDefiner;
import com.niu.mall.service.admin.diy.IMallDiyFormService;
import org.springframework.stereotype.Component;

@Component
@EventListen("mall")
@EventCallback("mall")
public class DiyFormDelBeforeListener extends DiyFormDelBeforeEventDefiner {

    private IMallDiyFormService mallDiyFormService() {
        return SpringContext.bean(IMallDiyFormService.class);
    }
    @Override
    public DiyFromDelBeforeResult handleCallback(DiyFormDelBeforeEvent diyFormDelBeforeEvent) {
        DiyFromDelBeforeResult result = new DiyFromDelBeforeResult();
        if(mallDiyFormService().diyFromDelBefore(diyFormDelBeforeEvent.getFormId())){
            result.setAllowOperate(true);
        }else{
            result.setAllowOperate(false);
        }
        return result;
    }
}
