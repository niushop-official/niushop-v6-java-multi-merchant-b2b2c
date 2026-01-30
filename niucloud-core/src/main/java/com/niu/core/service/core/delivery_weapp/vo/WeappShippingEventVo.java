package com.niu.core.service.core.delivery_weapp.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class WeappShippingEventVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer shippingId;

    private Integer splitId;
}
