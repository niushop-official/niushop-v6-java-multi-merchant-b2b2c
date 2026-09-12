package com.niu.mall.service.core.order;

import com.niu.core.service.core.marketing.vo.MallMemberCouponListVo;
import com.niu.mall.service.core.order.param.MallOrderCalculateParam;
import com.niu.mall.service.core.order.param.MallOrderCreateParam;
import com.niu.mall.service.core.order.param.OrderCalculateParam;
import com.niu.mall.service.core.order.param.OrderCreateParam;
import com.niu.mall.service.core.order.vo.*;

import java.util.List;
import java.util.Map;

public interface ICoreMallOrderCreateService {

    /**
     * 计算
     *
     * @param param
     */
    MallOrderCreateDataVo calculate(MallOrderCalculateParam param); // 入参 param.body 按 siteId 分组；返回 MallOrderCreateDataVo（含 orderList/orderHandleList 多站点快照）

    /**
     * 创建
     *
     * @param param
     * @return
     */
    MallOrderCreateResultVo create(MallOrderCreateParam param); // 入参 param 含各站点 orderKey；返回 MallOrderCreateResultVo（tradeNo/子单列表等聚合结果）

    /**
     * 查询店铺优惠券
     * @return
     */
    Map<Integer, List<OrderCouponListVo>> getCoupon(Map<Integer, Map<String,Object>> body); // 入参 key=siteId；返回 Map<siteId, 该站点可用店铺券列表>

    /**
     * 查询平台优惠券
     * @param param 与 calculate 相同结构的参数（至少含 body）
     * @return
     */
    List<MallMemberCouponListVo> getPlatformCoupon(MallOrderCalculateParam param); // 入参 param 同 calculate；返回 List<MallMemberCouponListVo> 跨站点可用平台券
}
