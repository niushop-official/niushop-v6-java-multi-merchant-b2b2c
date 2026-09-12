package com.niu.core.service.core.cash.order;

import com.niu.core.service.core.cash.order.param.CoreCashOrderCalculateParam;
import com.niu.core.service.core.cash.order.param.CoreCashOrderCreateParam;
import com.niu.core.service.core.cash.order.vo.CashOrderCreateCacheVo;
import com.niu.core.service.core.cash.order.vo.CashOrderCreateResultVo;

public interface ICoreCashOrderCreateService {

    CashOrderCreateCacheVo calculate(CoreCashOrderCalculateParam param);

    CashOrderCreateResultVo create(CoreCashOrderCreateParam param);
}
