package com.niu.core.service.core.cash.order;

import com.niu.core.service.core.cash.order.param.CashOrderLogParam;

import java.util.List;

public interface ICoreCashOrderLogService {

    void add(CashOrderLogParam param);

    void addAll(List<CashOrderLogParam> params);
}
