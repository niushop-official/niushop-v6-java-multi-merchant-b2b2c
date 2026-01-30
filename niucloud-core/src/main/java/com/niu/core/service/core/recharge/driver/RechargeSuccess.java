package com.niu.core.service.core.recharge.driver;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.loader.notice.BaseNotice;
import com.niu.core.common.loader.notice.NoticeDataVo;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.recharge.order.ICoreOrderService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RechargeSuccess extends BaseNotice {
    public MemberMapper memberMapper() {
        return SpringContext.bean(MemberMapper.class);
    }
    public ICoreOrderService coreOrderService() {
        return SpringContext.bean(ICoreOrderService.class);
    }

    public NoticeDataVo noticeData(Map<String, Object> vars) {
        Integer orderId = Integer.parseInt(vars.get("order_id").toString());

        RechargeOrder order = coreOrderService().orderInfo(orderId);
        if(ObjectUtil.isNotEmpty(order)){
            Member member = memberMapper().selectOne(new QueryWrapper<Member>().select("balance").eq("member_id", order.getMemberId()));

            Map<String, Object> param = new HashMap<String, Object>();
            param.put("balance", member.getBalance());
            param.put("price", order.getOrderMoney());
            param.put("trade_no", order.getOrderNo());
            param.put("time", DateUtil.date(order.getPayTime() * 1000));
            param.put("weapp_version",order.getWeappVersion());
            param.put("wechat_page", "");
            param.put("weapp_page", "");
            param.put("miniprogram", "");

            NoticeDataVo vo = new NoticeDataVo();
            vo.setMemberId(order.getMemberId());
            vo.setVars(param);
            return vo;
        }

        return new NoticeDataVo();
    }
}
