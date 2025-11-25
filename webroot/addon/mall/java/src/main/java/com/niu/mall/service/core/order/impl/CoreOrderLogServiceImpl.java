package com.niu.mall.service.core.order.impl;

import cn.hutool.core.bean.BeanUtil;
import com.niu.mall.entity.order.MallOrderLog;
import com.niu.mall.mapper.order.MallOrderLogMapper;
import com.niu.mall.service.core.order.ICoreOrderLogService;
import com.niu.mall.service.core.order.param.AddOrderLogParam;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

import java.util.List;

@Service
public class CoreOrderLogServiceImpl implements ICoreOrderLogService {

    @Resource
    MallOrderLogMapper mallOrderLogMapper;

    @Override
    public void add(AddOrderLogParam data) {
        MallOrderLog model = new MallOrderLog();
        BeanUtil.copyProperties(data, model);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        mallOrderLogMapper.insert(model);
    }

    @Override
    public void addAll(List<AddOrderLogParam> data) {
        for (AddOrderLogParam addOrderLogParam : data) {
            this.add(addOrderLogParam);
        }
    }
}
