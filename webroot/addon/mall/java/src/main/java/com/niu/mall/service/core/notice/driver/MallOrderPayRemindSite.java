package com.niu.mall.service.core.notice.driver;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.utils.StringUtils;
import com.niu.mall.entity.order.MallOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家端订单支付成功通知
 */
public class MallOrderPayRemindSite extends BaseNotice {

    @Override
    public NoticeDataVo noticeData(Map<String, Object> vars) {
        MallNoticeSupport support = SpringContext.bean(MallNoticeSupport.class);
        MallOrder order = support.resolveOrder(vars.get("order"));
        if (order == null) {
            order = support.resolveOrder(vars.get("order_id"));
        }
        if (ObjectUtil.isEmpty(order)) {
            return new NoticeDataVo();
        }

        Map<String, Object> param = new HashMap<>();
        param.put("body", StringUtils.substring(order.getBody(), 0, 14));
        param.put("order_no", StrUtil.subSufByLength(order.getOrderNo(), 4));

        NoticeDataVo vo = new NoticeDataVo();
        vo.setUid(support.getNoticeUid(order.getSiteId()));
        vo.setVars(param);
        return vo;
    }
}
