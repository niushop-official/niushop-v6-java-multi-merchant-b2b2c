package com.niu.core.service.api.cash.order.impl;

import cn.hutool.core.bean.BeanUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.cash.CashOrderLogEnum;
import com.niu.core.service.api.cash.order.IApiCashOrderCreateService;
import com.niu.core.service.api.cash.order.param.ApiCashOrderCalculateParam;
import com.niu.core.service.api.cash.order.param.ApiCashOrderCreateParam;
import com.niu.core.service.api.cash.order.vo.ApiCashOrderCreateVo;
import com.niu.core.service.core.cash.order.ICoreCashOrderCreateService;
import com.niu.core.service.core.cash.order.param.CoreCashOrderCalculateParam;
import com.niu.core.service.core.cash.order.param.CoreCashOrderCreateParam;
import com.niu.core.service.core.cash.order.vo.CashOrderCreateCacheVo;
import com.niu.core.service.core.cash.order.vo.CashOrderCreateResultVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ApiCashOrderCreateServiceImpl implements IApiCashOrderCreateService {

    @Resource
    ICoreCashOrderCreateService coreCashOrderCreateService;

    @Override
    public CashOrderCreateCacheVo calculate(ApiCashOrderCalculateParam param) {
        CoreCashOrderCalculateParam coreParam = new CoreCashOrderCalculateParam();
        BeanUtil.copyProperties(param, coreParam);
        coreParam.setMemberId(RequestUtils.memberId());
        coreParam.setOrderFrom(RequestUtils.channel());
        if (coreParam.getSiteId() == null || coreParam.getSiteId() <= 0) {
            coreParam.setSiteId(RequestUtils.siteId());
        }
        return coreCashOrderCreateService.calculate(coreParam);
    }

    @Override
    public ApiCashOrderCreateVo create(ApiCashOrderCreateParam param) {
        CoreCashOrderCreateParam coreParam = new CoreCashOrderCreateParam();
        coreParam.setOrderKey(param.getOrderKey());
        coreParam.setMemberId(RequestUtils.memberId());
        coreParam.setOrderFrom(RequestUtils.channel());
        coreParam.setMainType(CashOrderLogEnum.MEMBER.getCode());
        coreParam.setMainId(RequestUtils.memberId());

        CashOrderCreateResultVo result = coreCashOrderCreateService.create(coreParam);
        ApiCashOrderCreateVo vo = new ApiCashOrderCreateVo();
        vo.setTradeType(result.getTradeType());
        vo.setOrderId(result.getOrderId());
        return vo;
    }
}
