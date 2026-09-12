package com.niu.core.service.api.vip_card.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.vip_card.order.param.ApiVipCardOrderPageParam;
import com.niu.core.service.api.vip_card.order.vo.ApiVipCardOrderDetailVo;
import com.niu.core.service.api.vip_card.order.vo.ApiVipCardOrderPageVo;

import java.util.Map;

public interface IApiVipCardOrderService {

    /**
     * 会员卡订单分页列表
     */
    PageResult<ApiVipCardOrderPageVo> getPage(PageParam pageParam, ApiVipCardOrderPageParam param);

    /**
     * 会员卡订单详情
     */
    ApiVipCardOrderDetailVo getDetail(Integer orderId);

    /**
     * 获取订单状态字典
     */
    Map<Integer, Map<String, Object>> getStatus();
}