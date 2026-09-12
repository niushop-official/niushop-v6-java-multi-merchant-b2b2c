package com.niu.core.service.api.vip_card.order;


import com.niu.core.service.api.vip_card.order.vo.ApiVipCardOrderCreateVo;
import com.niu.core.service.core.vip_card.order.param.CoreVipCardOrderCreateParam;

public interface IApiVipCardOrderCreateService {
    /**
     * 创建付费会员卡订单
     */
    ApiVipCardOrderCreateVo create(CoreVipCardOrderCreateParam param);
}