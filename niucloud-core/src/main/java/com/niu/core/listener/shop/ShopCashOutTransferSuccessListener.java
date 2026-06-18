package com.niu.core.listener.shop;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.entity.pay.PayTransfer;
import com.niu.core.enums.cashout.CashOutTypeEnum;
import com.niu.core.event.pay.TransferSuccessEvent;
import com.niu.core.service.core.shop.ICoreShopCashOutService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 店铺提现转账成功
 */
@EventListen("core")
@Component
public class ShopCashOutTransferSuccessListener extends AbstractListener {

    @Resource
    ICoreShopCashOutService coreShopCashOutService;

    public void handleEvent(TransferSuccessEvent event) {
        PayTransfer transfer = event.getTransfer();
        if (CashOutTypeEnum.SHOP_CASH_OUT.getName().equals(transfer.getTradeType())) {
            coreShopCashOutService.transferFinish(transfer.getTransferNo());
        }
    }
}
