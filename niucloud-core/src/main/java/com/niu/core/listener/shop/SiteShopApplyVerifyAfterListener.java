package com.niu.core.listener.shop;

import com.niu.core.common.annotation.EventListen;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.core.enums.shop.ShopApplyEnum;
import com.niu.core.event.shop.SiteShopApplyVerifyAfterEvent;
import com.niu.core.service.core.operation_log.IOperationLogService;
import org.springframework.stereotype.Component;

/**
 * 商户入驻申请审核后监听器
 * 用于记录商户入驻审核的操作日志
 */
@EventListen("core")
@Component
public class SiteShopApplyVerifyAfterListener extends AbstractListener {

    private static final String MALL_SHOP_VERIFY = "mall_shop_verify";
    private static final String SYSTEM = "system";

    /**
     * 监听事件
     *
     * @param event 审核后事件
     */
    public void handleEvent(SiteShopApplyVerifyAfterEvent event) {
        IOperationLogService operationLogService = SpringContext.bean(IOperationLogService.class);
        
        // 构建操作日志数据
        String statusName = ShopApplyEnum.getNameByStatus(event.getStatus());
        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("商户入驻申请审核");
        if (event.getStatus().equals(ShopApplyEnum.PASS.getValue())) {
            dataBuilder.append("通过");
        } else if (event.getStatus().equals(ShopApplyEnum.REFUSE.getValue())) {
            dataBuilder.append("驳回");
            if (event.getRefuseReason() != null && !event.getRefuseReason().isEmpty()) {
                dataBuilder.append("，原因：").append(event.getRefuseReason());
            }
        }
        
        // 记录操作日志
        operationLogService.addOperationLog(
            MALL_SHOP_VERIFY,
            SYSTEM,
            event.getUid(),
            dataBuilder.toString()
        );
    }
}
