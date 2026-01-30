package com.niu.mall.service.api.order.impl;

import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.enums.order.OrderLogActionEnum;
import com.niu.mall.enums.order.OrderLogEnum;
import com.niu.mall.service.api.order.IOrderCreateService;
import com.niu.mall.service.core.delivery.ICoreStoreService;
import com.niu.core.service.core.delivery.param.LatlngParam;
import com.niu.core.service.core.delivery.vo.StoreListVo;
import com.niu.mall.service.core.marketing.vo.MallMemberCouponListVo;
import com.niu.mall.service.core.order.ICoreMallOrderCreateService;
import com.niu.mall.service.core.order.ICoreOrderCreateService;
import com.niu.mall.service.core.order.param.MallOrderCalculateParam;
import com.niu.mall.service.core.order.param.MallOrderCreateParam;
import com.niu.mall.service.core.order.param.OrderCalculateParam;
import com.niu.mall.service.core.order.param.OrderCreateParam;
import com.niu.mall.service.core.order.vo.*;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class OrderCreateServiceImpl implements IOrderCreateService {

    @Resource
    ICoreMallOrderCreateService coreOrderCreateService;


    /**
     * 计算
     *
     * @param param
     */
    @Override
    public MallOrderCreateDataVo calculate(MallOrderCalculateParam param) {
        param.setMemberId(RequestUtils.memberId());
        param.setOrderFrom(RequestUtils.channel());
        return coreOrderCreateService.calculate(param);
    }

    /**
     * 订单创建
     *
     * @param param
     * @return
     */
    @Override
    public MallOrderCreateResultVo create(MallOrderCreateParam param) {
        param.setMemberId(RequestUtils.memberId());
        param.setOrderFrom(RequestUtils.channel());
        param.setMainType(OrderLogEnum.MEMBER.getName());
        param.setMainId(RequestUtils.memberId().longValue());
        param.setWeappVersion(RequestUtils.weappVersion());
        return coreOrderCreateService.create(param);
    }

    /**
     * 查询店铺优惠券
     * @return
     */
    @Override
    public Map<Integer, List<OrderCouponListVo>> getCoupon(Map<Integer, Map<String,Object>> body) {
        return coreOrderCreateService.getCoupon(body);
    }

    /**
     * 查询平台优惠券
     * @param orderKeys
     * @return
     */
    @Override
    public List<MallMemberCouponListVo> getPlatformCoupon(List<Object> orderKeys) {
        return coreOrderCreateService.getPlatformCoupon(orderKeys);
    }

}
