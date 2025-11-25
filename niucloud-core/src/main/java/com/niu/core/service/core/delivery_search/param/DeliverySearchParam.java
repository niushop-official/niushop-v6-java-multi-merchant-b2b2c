package com.niu.core.service.core.delivery_search.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 配送方式设置参数
*/
@Data
public class DeliverySearchParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 快递公司编码 */
    private String expressNo;

    /** 快递单号 */
    private String logisticNo;

    /** 寄件人or收件人 手机号后四位 */
    private String mobile;

    private Integer companyId;

}
