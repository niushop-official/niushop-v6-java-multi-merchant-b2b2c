package com.niu.core.service.admin.vip_card.card;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.vip_card.card.param.AdminModifyStatusParam;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardAddParam;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardEditParam;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardPageParam;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardInfoVo;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardListVo;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardPageVo;

import java.util.List;

public interface IAdminVipCardService {
    PageResult<AdminVipCardPageVo> getPage(PageParam pageParam, AdminVipCardPageParam param);

    List<AdminVipCardListVo> getList(AdminVipCardPageParam param);

    AdminVipCardInfoVo getInfo(Integer cardId);

    Integer add(AdminVipCardAddParam param);

    void edit(Integer cardId, AdminVipCardEditParam param);

    void del(Integer cardId);

    void modifyStatus(AdminModifyStatusParam param);
}