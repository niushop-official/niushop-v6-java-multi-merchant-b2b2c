package com.niu.core.listener.separ;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.entity.separ_account.SeparAccountDetail;
import com.niu.core.enums.account.SeparStatusEnum;
import com.niu.core.event.separ.SeparAccountModel;
import com.niu.core.event.separ.ToSeparEvent;
import com.niu.core.service.core.separ_account.ICoreSeparAccountService;
import com.niu.core.service.core.separ_account.param.SeparNotifyParam;
import com.niu.core.service.core.shop.ICoreShopAccountService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@EventListen("core")
@Component
public class ToSeparListener extends AbstractListener {

    private ICoreShopAccountService accountService() {
        return SpringContext.bean(ICoreShopAccountService.class);
    }

    private ICoreSeparAccountService separAccountService() {
        return SpringContext.bean(ICoreSeparAccountService.class);
    }

    /**
     * 监听事件
     *
     * @param event
     */
    public void handleEvent(ToSeparEvent event) {
        SeparAccountModel separAccount = event.getAccount();
        String[] balanceSeparPayType = new String[]{ "balancepay", "wechatpay", "alipay"};
        if(Arrays.asList(balanceSeparPayType).contains(separAccount.getPayType())){
            //            余额支付、微信支付、支付宝支付
            if(ObjectUtil.isNotEmpty(separAccount.getDetail())){

                for (SeparAccountDetail detail : separAccount.getDetail()){
                    if(detail.getValue().compareTo(BigDecimal.ZERO) > 0){
                        accountService().addLog(detail.getMainId(), detail.getValue(), detail.getValueSource(), detail.getMemo(), separAccount.getTradeId().toString());
                    }
                }
            }

            SeparNotifyParam param = new SeparNotifyParam();
            param.setId(separAccount.getId());
            param.setStatus(SeparStatusEnum.SUCCESS);
            param.setFailReason("");
            separAccountService().notify(param);

        }



    }

}