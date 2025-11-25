package com.niu.mall.service.api.order;

import com.niu.core.service.core.delivery.param.LatlngParam;
import com.niu.core.service.core.delivery.vo.StoreListVo;
import com.niu.mall.service.core.marketing.vo.MallMemberCouponListVo;
import com.niu.mall.service.core.order.param.MallOrderCalculateParam;
import com.niu.mall.service.core.order.param.MallOrderCreateParam;
import com.niu.mall.service.core.order.param.OrderCalculateParam;
import com.niu.mall.service.core.order.param.OrderCreateParam;
import com.niu.mall.service.core.order.vo.*;

import java.util.List;
import java.util.Map;

public interface IOrderCreateService {

    /**
     * 计算
     *
     * @param param
     */
    MallOrderCreateDataVo calculate(MallOrderCalculateParam param);

    /**
     * 订单创建
     *
     * @param param
     * @return
     */
    MallOrderCreateResultVo create(MallOrderCreateParam param);

    /**
     * 查询店铺优惠券
     * @return
     */
    Map<Integer, List<OrderCouponListVo>> getCoupon(Map<Integer, Map<String,Object>> body);

    /**
     * 查询平台优惠券
     * @param orderKeys
     * @return
     */
    List<MallMemberCouponListVo> getPlatformCoupon(List<Object> orderKeys);
}
