package com.niu.core.service.admin.vip_card.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.vip_card.order.param.AdminVipCardOrderPageParam;
import com.niu.core.service.admin.vip_card.order.vo.AdminVipCardOrderDetailVo;
import com.niu.core.service.admin.vip_card.order.vo.AdminVipCardOrderPageVo;

public interface IAdminVipCardOrderService {
    PageResult<AdminVipCardOrderPageVo> getPage(PageParam pageParam, AdminVipCardOrderPageParam param);

    AdminVipCardOrderDetailVo getInfo(Integer orderId);
}