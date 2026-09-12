package com.niu.mall.service.core.notice.driver;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.order.OrderStatusEnum;

import java.util.HashMap;
import java.util.Map;

public class MallOrderDelivery extends BaseNotice {

    public NoticeDataVo noticeData(Map<String, Object> vars) {
        MallNoticeSupport support = SpringContext.bean(MallNoticeSupport.class);
        MallOrder order = support.resolveOrder(vars.get("order"));
        if (order == null) {
            order = support.resolveOrder(vars.get("order_id"));
        }
        if (ObjectUtil.isEmpty(order) || !OrderStatusEnum.WAIT_TAKE.getStatus().equals(Integer.parseInt(order.getStatus()))) {
            return new NoticeDataVo();
        }
        String wapDomain = support.getCoreSysConfigService().getSceneDomain().getWapUrl();
        String detailPath = "/addon/mall/pages/order/detail?order_id=" + order.getOrderId();

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("wechat_page", wapDomain + detailPath);
        param.put("weapp_page", "addon/mall/pages/order/detail?order_id=" + order.getOrderId());
        param.put("body", StringUtils.substring(order.getBody(), 0, 10) + "...");
        param.put("order_no", order.getOrderNo());
        param.put("order_money", order.getOrderMoney());
        param.put("delivery_time", order.getDeliveryTime() != null ? DateUtil.date(order.getDeliveryTime() * 1000) : "");
        param.put("url", wapDomain + detailPath);
        param.put("weapp_version", order.getWeappVersion() != null ? order.getWeappVersion() : "");

        NoticeDataVo vo = new NoticeDataVo();
        vo.setMemberId(order.getMemberId());
        vo.setVars(param);
        return vo;
    }
}
