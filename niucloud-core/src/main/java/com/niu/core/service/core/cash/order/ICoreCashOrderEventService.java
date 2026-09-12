package com.niu.core.service.core.cash.order;

import com.niu.core.service.core.cash.order.param.AfterCashOrderCloseParam;
import com.niu.core.service.core.cash.order.param.AfterCashOrderCreateParam;
import com.niu.core.service.core.cash.order.param.AfterCashOrderPayParam;

public interface ICoreCashOrderEventService {

    void orderCreateAfter(AfterCashOrderCreateParam param);

    void orderPayAfter(AfterCashOrderPayParam param);

    void orderCloseAfter(AfterCashOrderCloseParam param);
}
