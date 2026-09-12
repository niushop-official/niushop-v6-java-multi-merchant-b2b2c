package com.niu.core.service.api.recharge.impl;


import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.recharge.RechargeOrderLogEnum;
import com.niu.core.service.api.recharge.IRechargeOrderCreateService;
import com.niu.core.service.core.recharge.order.ICoreOrderCreateService;
import com.niu.core.service.core.recharge.order.param.RechargeOrderCreateParam;
import com.niu.core.service.core.recharge.order.vo.RechargeOrderCreateVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RechargeOrderCreateServiceImpl implements IRechargeOrderCreateService {

    @Resource
    ICoreOrderCreateService coreOrderCreateService;

    /**
     * 充值订单创建
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RechargeOrderCreateVo create(RechargeOrderCreateParam createParam) {
        createParam.setOrderFrom(RequestUtils.channel());
        createParam.setSiteId(ObjectUtil.defaultIfNull(createParam.getSiteId(), 0));
        createParam.setMemberId(RequestUtils.memberId());
        createParam.setMainType(RechargeOrderLogEnum.MEMBER.getType());
        createParam.setMainId(RequestUtils.memberId());
        return coreOrderCreateService.create(createParam);
    }
}
