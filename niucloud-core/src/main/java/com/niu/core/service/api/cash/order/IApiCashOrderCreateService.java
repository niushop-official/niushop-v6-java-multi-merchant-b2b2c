package com.niu.core.service.api.cash.order;

import com.niu.core.service.api.cash.order.param.ApiCashOrderCalculateParam;
import com.niu.core.service.api.cash.order.param.ApiCashOrderCreateParam;
import com.niu.core.service.api.cash.order.vo.ApiCashOrderCreateVo;
import com.niu.core.service.core.cash.order.vo.CashOrderCreateCacheVo;

public interface IApiCashOrderCreateService {

    CashOrderCreateCacheVo calculate(ApiCashOrderCalculateParam param);

    ApiCashOrderCreateVo create(ApiCashOrderCreateParam param);
}
