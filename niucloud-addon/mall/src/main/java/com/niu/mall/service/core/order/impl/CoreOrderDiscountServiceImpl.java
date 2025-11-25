package com.niu.mall.service.core.order.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.mall.entity.order.MallOrderDiscount;
import com.niu.mall.mapper.order.MallOrderDiscountMapper;
import com.niu.mall.service.core.order.ICoreOrderDiscountService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoreOrderDiscountServiceImpl extends ServiceImpl<MallOrderDiscountMapper, MallOrderDiscount> implements ICoreOrderDiscountService {

    @Override
    public void inserAll(List<MallOrderDiscount> list) {
        super.saveBatch(list);
    }
}
