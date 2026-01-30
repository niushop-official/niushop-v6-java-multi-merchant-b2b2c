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

public class MallOrderRemind extends BaseNotice {

    public NoticeDataVo noticeData(Map<String, Object> vars) {
        MallOrder order = (MallOrder) vars.get("order");

        if(ObjectUtil.isNotEmpty(order.getStatus()) && OrderStatusEnum.WAIT_PAY.getStatus().equals(Integer.parseInt(order.getStatus()))){
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("body", StringUtils.substring(order.getBody(), 0, 10) + "...");
            param.put("order_no", order.getOrderNo());
            param.put("weapp_version", order.getWeappVersion());
            param.put("order_money", order.getOrderMoney());
            param.put("timeout", DateUtil.date(order.getTimeout()));
            param.put("wechat_page", "/addon/mall/pages/order/detail?order_id=" + order.getOrderId());
            param.put("weapp_page", "addon/mall/pages/order/detail?order_id=" + order.getOrderId());

            NoticeDataVo vo = new NoticeDataVo();
            vo.setMemberId(order.getMemberId());
            vo.setVars(param);
            return vo;
        }
        return new NoticeDataVo();
    }
}
