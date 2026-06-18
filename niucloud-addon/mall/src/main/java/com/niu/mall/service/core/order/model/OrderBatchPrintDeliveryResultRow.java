package com.niu.mall.service.core.order.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class OrderBatchPrintDeliveryResultRow {
    @ExcelProperty(value = "订单编号", order = 1)
    private String orderNo;
    @ExcelProperty(value = "订单ID", order = 2)
    private String orderId;
    @ExcelProperty(value = "订单项ID", order = 3)
    private String orderItemId;
    @ExcelProperty(value = "发货方式", order = 4)
    private String deliveryType;
    @ExcelProperty(value = "快递公司", order = 5)
    private String expressCompanyName;
    @ExcelProperty(value = "电子面单", order = 6)
    private String electronicSheetName;
    @ExcelProperty(value = "发货地址", order = 7)
    private String address;
    @ExcelProperty(value = "结果", order = 8)
    private String result;
    @ExcelProperty(value = "失败原因", order = 9)
    private String remark;
}
