package com.niu.core.service.core.vip_card.order;


import com.niu.core.service.core.vip_card.order.param.CoreVipCardOrderCreateParam;
import com.niu.core.service.core.vip_card.order.vo.CoreVipCardOrderCreateResult;

public interface ICoreVipCardOrderCreateService {
    CoreVipCardOrderCreateResult create(CoreVipCardOrderCreateParam param);
}