package com.niu.mall.service.core.order.impl;

import cn.hutool.core.bean.BeanUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.mall.entity.order.MallOrderDeliveryContent;
import com.niu.mall.mapper.order.MallOrderDeliveryContentMapper;
import com.niu.mall.service.core.order.ICoreOrderDeliveryContentService;
import com.niu.mall.service.core.order.param.OrderDeliveryContentParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CoreOrderDeliveryContentServiceImpl implements ICoreOrderDeliveryContentService {
    @Resource
    private MallOrderDeliveryContentMapper orderDeliveryContentMapper;

    @Override
    public void add(OrderDeliveryContentParam param) {
        MallOrderDeliveryContent content = BeanUtil.copyProperties(param, MallOrderDeliveryContent.class);
        content.setCreateTime(System.currentTimeMillis() / 1000);
        int i = orderDeliveryContentMapper.insert(content);
        if (i <= 0) {
            throw new CommonException("发货失败");
        }
    }
}
