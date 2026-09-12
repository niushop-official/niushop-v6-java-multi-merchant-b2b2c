package com.niu.mall.service.core.notice.driver;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;

import java.util.HashMap;
import java.util.Map;

public class MallRefundAgree extends BaseNotice {

    public NoticeDataVo noticeData(Map<String, Object> vars) {
        MallNoticeSupport support = SpringContext.bean(MallNoticeSupport.class);
        MallOrderRefund refund = support.resolveRefund(vars.get("refund"), vars.get("mallRefundId"), vars.get("order_refund_no"));
        if (ObjectUtil.isEmpty(refund)) {
            return new NoticeDataVo();
        }
        MallOrder order = support.loadOrderByRefund(refund);

        String wapDomain = support.getCoreSysConfigService().getSceneDomain().getWapUrl();
        String detailPath = "/addon/mall/pages/refund/detail?order_refund_no=" + refund.getOrderRefundNo();

        Map<String, Object> param = new HashMap<>();
        param.put("wechat_page", wapDomain + detailPath);
        param.put("weapp_page", "addon/mall/pages/refund/detail?order_refund_no=" + refund.getOrderRefundNo());
        param.put("order_refund_no", refund.getOrderRefundNo());
        param.put("order_no", order != null ? order.getOrderNo() : "");
        param.put("refund_money", refund.getApplyMoney());
        param.put("result", "已通过申请");
        param.put("url", wapDomain + detailPath);
        param.put("weapp_version", refund.getWeappVersion() != null ? refund.getWeappVersion() : "");

        NoticeDataVo vo = new NoticeDataVo();
        vo.setMemberId(refund.getMemberId());
        vo.setVars(param);
        return vo;
    }
}
