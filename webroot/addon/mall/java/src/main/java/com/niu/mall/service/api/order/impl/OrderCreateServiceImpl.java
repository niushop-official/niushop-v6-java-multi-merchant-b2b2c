package com.niu.mall.service.api.order.impl;

import cn.hutool.core.bean.BeanUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.mall.enums.order.OrderLogEnum;
import com.niu.mall.service.api.order.IOrderCreateService;
import com.niu.core.service.core.marketing.vo.MallMemberCouponListVo;
import com.niu.mall.service.core.order.ICoreMallOrderCreateService;
import com.niu.mall.service.core.order.param.MallOrderCalculateParam;
import com.niu.mall.service.core.order.param.MallOrderCreateParam;
import com.niu.mall.service.core.order.vo.MallOrderCreateDataVo;
import com.niu.mall.service.core.order.vo.MallOrderCreateResultVo;
import com.niu.mall.service.core.order.vo.OrderCouponListVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderCreateServiceImpl implements IOrderCreateService {

    @Resource
    ICoreMallOrderCreateService coreOrderCreateService;

    /**
     * 计算
     */
    @Override
    public MallOrderCreateDataVo calculate(MallOrderCalculateParam param) {
        param.setMemberId(RequestUtils.memberId());
        param.setOrderFrom(RequestUtils.channel());
        return coreOrderCreateService.calculate(param);
    }

    /**
     * 订单创建
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
     */
    @Override
    public Map<Integer, List<OrderCouponListVo>> getCoupon(Map<Integer, Map<String, Object>> body) {
        return coreOrderCreateService.getCoupon(body);
    }

    /**
     * 查询平台优惠券
     */
    @Override
    public List<MallMemberCouponListVo> getPlatformCoupon(Map<String, Object> request) {
        MallOrderCalculateParam param = parseCalculateParam(request);
        return getPlatformCoupon(param);
    }

    @Override
    public List<MallMemberCouponListVo> getPlatformCoupon(MallOrderCalculateParam param) {
        if (param == null) {
            param = new MallOrderCalculateParam();
        }
        param.setMemberId(RequestUtils.memberId());
        param.setOrderFrom(RequestUtils.channel());
        return coreOrderCreateService.getPlatformCoupon(param);
    }

    private MallOrderCalculateParam parseCalculateParam(Map<String, Object> request) {
        if (request == null || request.isEmpty()) {
            return new MallOrderCalculateParam();
        }
        MallOrderCalculateParam param = BeanUtil.toBean(request, MallOrderCalculateParam.class);
        return param != null ? param : new MallOrderCalculateParam();
    }
}
