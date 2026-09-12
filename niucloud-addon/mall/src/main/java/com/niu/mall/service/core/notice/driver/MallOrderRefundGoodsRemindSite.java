package com.niu.mall.service.core.notice.driver;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.mall.entity.order.MallOrderRefund;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家端退货申请通知
 */
public class MallOrderRefundGoodsRemindSite extends BaseNotice {

    @Override
    public NoticeDataVo noticeData(Map<String, Object> vars) {
        MallNoticeSupport support = SpringContext.bean(MallNoticeSupport.class);
        MallOrderRefund refund = support.resolveRefund(vars.get("refund"), vars.get("mallRefundId"), vars.get("order_refund_no"));
        if (ObjectUtil.isEmpty(refund)) {
            return new NoticeDataVo();
        }

        Map<String, Object> param = new HashMap<>();
        param.put("order_refund_no", StrUtil.subSufByLength(refund.getOrderRefundNo(), 4));

        NoticeDataVo vo = new NoticeDataVo();
        vo.setUid(support.getNoticeUid(refund.getSiteId()));
        vo.setVars(param);
        return vo;
    }
}
