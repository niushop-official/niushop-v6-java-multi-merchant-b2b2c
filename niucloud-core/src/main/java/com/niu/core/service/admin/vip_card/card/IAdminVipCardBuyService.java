package com.niu.core.service.admin.vip_card.card;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardBuyPageParam;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardBuyPageVo;

public interface IAdminVipCardBuyService {
    PageResult<AdminVipCardBuyPageVo> getPage(PageParam pageParam, AdminVipCardBuyPageParam param);

    AdminVipCardBuyPageVo getInfo(Integer id);
}