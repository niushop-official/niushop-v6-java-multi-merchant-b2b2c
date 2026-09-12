package com.niu.core.service.api.cash.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.cash.order.param.ApiCashOrderPageParam;
import com.niu.core.service.api.cash.order.vo.ApiCashOrderDetailVo;
import com.niu.core.service.api.cash.order.vo.ApiCashOrderPageVo;

import java.util.Map;

public interface IApiCashOrderService {

    PageResult<ApiCashOrderPageVo> getPage(PageParam pageParam, ApiCashOrderPageParam param);

    ApiCashOrderDetailVo getDetail(Integer orderId);

    Map<Integer, Map<String, Object>> getStatus();
}
