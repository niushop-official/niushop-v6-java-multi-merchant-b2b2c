package com.niu.core.service.core.local_delivery.param;

import lombok.Data;

import java.io.Serializable;

@Data
public class AddLocalDeliveryOrderLogParam implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 订单ID */
    private Integer orderId;

    /** 操作人ID */
    private Integer mainId;

    /** 操作人类型 */
    private String mainType;

    /** 操作人名称 */
    private String mainName;

    /** 操作类型 */
    private String operate;

    /** 操作描述 */
    private String operateDesc;

    /** 操作后配送状态 */
    private String status;

    /** 配送时间 */
    private Long createTime;

    /** 备注 */
    private String remark;
}
