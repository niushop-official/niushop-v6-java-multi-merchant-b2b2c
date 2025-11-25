package com.niu.core.event.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.common.domain.PageParam;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.order.param.MallOrderSearchParam;
import com.niu.core.service.api.order.vo.InvoiceListVo;
import com.niu.core.service.core.sys.ICoreUploadService;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public abstract class OrderInvoiceEventDefiner extends CallbackListener<OrderInvoiceEventDefiner.OrderInvoiceEvent> {

    /**
     * 事件主题定义
     */
    @Data
    public static class OrderInvoiceEvent extends Event {
        private String tradeType;
        private Integer tradeId;

    }

    /**
     * 事件结果定义
     */
    @Data
    public static class OrderInvoiceResult extends EventResult {
        private InvoiceListVo.OrderInfo orderList;

    }

    // 事件响应
    public abstract OrderInvoiceResult handleCallback(OrderInvoiceEvent event);

}
