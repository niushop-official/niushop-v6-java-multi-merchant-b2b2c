package com.niu.mall.service.core.order.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class OrderBatchDeliveryVo {
    private int successNum;
    private int failNum;
    private int totalNum;
    //    private List<OrderBatchDeliveryResultRow> totalList = Collections.emptyList();
//    private List<OrderBatchDeliveryResultRow> failList = Collections.emptyList();
    private List<?> totalList = Collections.emptyList();
    private List<?> failList = Collections.emptyList();
    private String failRemark;
}
