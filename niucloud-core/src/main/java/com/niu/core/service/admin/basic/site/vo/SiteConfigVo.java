package com.niu.core.service.admin.basic.site.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用设置
 */
@Data
public class SiteConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 是否开启发票功能：1-开启，2-关闭 */
    @JsonProperty("is_invoice")
    private String isInvoice = "2";

    /** 订单万能表单ID */
    @JsonProperty("order_form_id")
    private Object orderFormId = 0;

    /** 注册多少天后成为老用户 */
    @JsonProperty("old_member_length")
    private Integer oldMemberLength = 90;

    /** 拣货时长（分钟） */
    @JsonProperty("picking_length")
    private Integer pickingLength = 10;

    /** 自动接单：1-开启，0-关闭 */
    @JsonProperty("is_auto_accept")
    private Integer isAutoAccept = 0;
}
