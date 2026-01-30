package com.niu.core.service.core.delivery.vo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopGoodsAttr视图
 */
@Data
public class AdminDeliveryConfigVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer isShow = 0;

    private Integer isExpress = 1;

    private Integer isLocalDelivery = 0;

    private Integer isLocalDeliveryStoreSelect = 0;

    private Integer isStore = 0;

    private Integer isShowPolyline = 0;
}
