package com.niu.core.service.core.notice.driver;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderService;

import java.util.HashMap;
import java.util.Map;

/**
 * 付费会员卡到期提醒
 */
public class VipCardBuyExpireRemind extends BaseNotice {

    @Override
    public NoticeDataVo noticeData(Map<String, Object> vars) {
        Integer orderId = Integer.parseInt(vars.get("order_id").toString());
        MemberVipCardOrder order = SpringContext.bean(ICoreVipCardOrderService.class).orderInfo(orderId);
        if (ObjectUtil.isEmpty(order)) {
            return new NoticeDataVo();
        }

        Map<String, Object> param = new HashMap<>();
        param.put("weapp_page", "app/pages/vipcard/index");
        param.put("card_name", strSub2(vars.get("card_name")));
        param.put("expire_time", formatTime(vars.get("expire_time")));
        param.put("create_time", formatTime(vars.get("create_time")));
        param.put("weapp_version", order.getWeappVersion() != null ? order.getWeappVersion() : "");

        NoticeDataVo vo = new NoticeDataVo();
        vo.setMemberId(order.getMemberId());
        vo.setVars(param);
        return vo;
    }

    private String strSub2(Object value) {
        if (value == null) {
            return "";
        }
        String str = value.toString();
        if (str.length() > 10) {
            return StringUtils.substring(str, 0, 10) + "...";
        }
        return str;
    }

    private Object formatTime(Object time) {
        if (time == null) {
            return "";
        }
        if (time instanceof Number) {
            return DateUtil.date(((Number) time).longValue() * 1000);
        }
        return time;
    }
}
