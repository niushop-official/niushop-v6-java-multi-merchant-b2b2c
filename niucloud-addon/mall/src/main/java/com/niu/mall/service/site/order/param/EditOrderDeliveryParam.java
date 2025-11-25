package com.niu.mall.service.site.order.param;

import lombok.Data;

import java.io.Serializable;
@Data
public class EditOrderDeliveryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String deliveryType;//配送方式

    private String deliveryWay;//发货方式

    private Integer electronicSheetId;//物流公司

    private Integer expressCompanyId;//面单模板

    private String expressNumber;//物流单号

    private Object orderGoodsIds;//商品ID

    private Integer orderId;//订单ID

}
