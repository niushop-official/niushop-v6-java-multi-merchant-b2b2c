package com.niu.core.service.api.vip_card.order.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.vip_card.VipCardOrderLogEnum;
import com.niu.core.service.api.vip_card.order.IApiVipCardOrderCreateService;
import com.niu.core.service.api.vip_card.order.vo.ApiVipCardOrderCreateVo;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderCreateService;
import com.niu.core.service.core.vip_card.order.param.CoreVipCardOrderCreateParam;
import com.niu.core.service.core.vip_card.order.vo.CoreVipCardOrderCreateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiVipCardOrderCreateServiceImpl implements IApiVipCardOrderCreateService {

    @Autowired
    private ICoreVipCardOrderCreateService coreOrderCreateService;

    @Override
    public ApiVipCardOrderCreateVo create(CoreVipCardOrderCreateParam param) {
        param.setMemberId(RequestUtils.memberId());
        param.setSiteId(RequestUtils.siteId());
        param.setOrderFrom(RequestUtils.channel());
        param.setWeappVersion(RequestUtils.weappVersion());
        param.setMainType(VipCardOrderLogEnum.MEMBER.getCode());
        param.setMainId(RequestUtils.memberId());

        // 调用核心服务创建订单
        CoreVipCardOrderCreateResult result = coreOrderCreateService.create(param);

        // 封装返回结果
        ApiVipCardOrderCreateVo vo = new ApiVipCardOrderCreateVo();
        vo.setTradeType(result.getTradeType());
        vo.setTradeId(result.getTradeId());
        return vo;
    }
}