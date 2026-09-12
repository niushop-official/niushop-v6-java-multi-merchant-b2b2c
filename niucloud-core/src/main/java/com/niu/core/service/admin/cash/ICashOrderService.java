package com.niu.core.service.admin.cash;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.cash.param.CashOrderSearchParam;
import com.niu.core.service.admin.cash.vo.CashOrderDetailVo;
import com.niu.core.service.admin.cash.vo.CashOrderListVo;

import java.util.Map;

public interface ICashOrderService {

    /**
     * 买单订单分页列表
     *
     * @param siteId 站点ID，平台端传 null 不过滤
     */
    PageResult<CashOrderListVo> getPage(PageParam pageParam, CashOrderSearchParam param, Integer siteId);

    /**
     * 买单订单详情
     *
     * @param siteId 站点ID，平台端传 null 不过滤
     */
    CashOrderDetailVo getDetail(Integer orderId, Integer siteId);

    /**
     * 买单订单状态
     */
    Map<Integer, Map<String, Object>> getStatus();
}
