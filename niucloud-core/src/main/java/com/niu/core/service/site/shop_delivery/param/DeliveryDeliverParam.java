package com.niu.core.service.site.shop_delivery.param;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 配送员参数
*/
@Data
public class DeliveryDeliverParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "deliver_name参数缺失")
    @NotEmpty(message = "请输入配送员姓名")
    private String deliverName;

    @NotNull(message = "deliver_mobile参数缺失")
    @NotEmpty(message = "请输入配送员手机号")
    private String deliverMobile;

}
