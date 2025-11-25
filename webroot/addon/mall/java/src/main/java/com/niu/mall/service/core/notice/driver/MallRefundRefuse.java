package com.niu.mall.service.core.notice.driver;

import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;

import java.util.HashMap;
import java.util.Map;

public class MallRefundRefuse extends BaseNotice {

    public NoticeDataVo noticeData(Map<String, Object> vars) {
        Integer refundId = (Integer) vars.get("mallRefundId");
        MallOrderRefundMapper mallOrderRefundMapper = SpringContext.bean(MallOrderRefundMapper.class);
        MallOrderRefund refund = mallOrderRefundMapper.selectById(refundId);
        MallOrderMapper mallOrderMapper = SpringContext.bean(MallOrderMapper.class);
        MallOrder order = mallOrderMapper.selectById(refund.getOrderId());

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("order_refund_no", refund.getOrderRefundNo());
        param.put("order_no", order.getOrderNo());
        param.put("refund_money", refund.getApplyMoney());
        param.put("result", "未通过申请");

        NoticeDataVo vo = new NoticeDataVo();
        vo.setMemberId(refund.getMemberId());
        vo.setVars(param);

        return vo;
    }
}
