package com.niu.mall.service.core.order;

import com.niu.mall.service.core.order.param.AddOrderLogParam;

import java.util.List;

public interface ICoreOrderLogService {

    /**
     * 添加订单日志
     *
     * @param data
     */
    void add(AddOrderLogParam data);

    void addAll(List<AddOrderLogParam> data);


}
