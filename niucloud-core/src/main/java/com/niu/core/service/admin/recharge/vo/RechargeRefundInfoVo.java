package com.niu.core.service.admin.recharge.vo;

import com.niu.core.entity.recharge.RechargeOrderItem;
import com.niu.core.entity.recharge.RechargeOrderItemRefund;
import com.niu.core.enums.pay.RefundStatusEnum;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import lombok.Data;

import java.io.Serializable;

@Data
public class RechargeRefundInfoVo extends RechargeOrderItemRefund implements Serializable {

    private static final long serialVersionUID = 1L;

    private RechargeOrderItem item;

    private String statusName;

    private MemberBriefInfoVo member;

    private String orderFromName;

    public String getStatusName() {
        return RefundStatusEnum.getNameByStatus(this.getStatus().toString());
    }

}
