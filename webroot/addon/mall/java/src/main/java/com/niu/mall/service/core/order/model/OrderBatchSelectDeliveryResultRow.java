package com.niu.mall.service.core.order.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class OrderBatchSelectDeliveryResultRow {
    @ExcelProperty(value = "订单编号", order = 1)
    private String orderNo;
    @ExcelProperty(value = "订单ID", order = 2)
    private String orderId;
    @ExcelProperty(value = "订单项ID", order = 3)
    private String orderItemId;
    @ExcelProperty(value = "发货方式", order = 4)
    private String deliveryType;
    @ExcelProperty(value = "结果", order = 5)
    private String result;
    @ExcelProperty(value = "失败原因", order = 6)
    private String remark;
}
