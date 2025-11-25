package com.niu.core.service.site.local_delivery;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.goods.param.GoodsServiceSearchParam;
import com.niu.core.service.site.goods.vo.GoodsServiceListVo;
import com.niu.core.service.site.local_delivery.param.LocalDeliveryOrderCloseParam;
import com.niu.core.service.site.local_delivery.param.LocalDeliveryOrderSearchParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryOrderInfoVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryOrderListVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceListVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceVo;

import java.util.List;

/**
 * 同城配送服务接口
 */
public interface ILocalDeliveryOrderService {

    /**
     * 查询本地配送订单列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsServiceListVo>
     */
    PageResult<LocalDeliveryOrderListVo> page(PageParam pageParam, LocalDeliveryOrderSearchParam searchParam);

    /**
     * 查询本地配送订详情
     *
     */
    LocalDeliveryOrderInfoVo getInfo(Integer id);

    /**
     * 取消订单
     *
     */
    void closeOrder(Integer id, LocalDeliveryOrderCloseParam param);

    /**
     * 完成订单
     *
     */
    void finishOrder(Integer id);

    /**
     * 同步订单信息
     *
     */
    void syncOrder(Integer id);
}
