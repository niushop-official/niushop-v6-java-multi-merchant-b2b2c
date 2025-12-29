package com.niu.core.common.loader.local_delivery.param;

import cn.hutool.json.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class NotifyShopDeliveryOrderParam implements Serializable {
    private static final long serialVersionUID = 1L;

    private String outDeliveryNo;
    /** 操作人ID */
    private Integer mainId;

    /** 操作人名称 */
    private String mainName;


    private String cancelReason;

    private JSONObject notifyData;

    /** 状态 */
    private String status;
    private String notifyUrl;
}
