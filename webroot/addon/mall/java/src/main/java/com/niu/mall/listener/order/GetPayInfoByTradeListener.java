package com.niu.mall.listener.order;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.annotation.EventListen;
import com.niu.core.event.common.CommonEventDefiner;
import com.niu.core.event.pay.GetPayInfoByTradeDefiner;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@EventCallback("mall")
public class GetPayInfoByTradeListener extends GetPayInfoByTradeDefiner {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Override
    public GetPayInfoByTradeResult handleCallback(GetPayInfoByTradeEvent event) {
        // 创建结果对象
        GetPayInfoByTradeResult result = new GetPayInfoByTradeResult();
        result.setOutTradeNo("");
        if(event.getTradeId() != null){
            QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("order_id", event.getTradeId());
            queryWrapper.eq("site_id", event.getSiteId());
            MallOrder supplyOrder = mallOrderMapper.selectOne(queryWrapper);
            String outTradeNo = "";
            if(supplyOrder != null && ObjectUtil.isNotEmpty(supplyOrder.getOutTradeNo()) ){
                outTradeNo = supplyOrder.getOutTradeNo();
            }
            result.setOutTradeNo(outTradeNo);
        }
        return result;
    }
}
