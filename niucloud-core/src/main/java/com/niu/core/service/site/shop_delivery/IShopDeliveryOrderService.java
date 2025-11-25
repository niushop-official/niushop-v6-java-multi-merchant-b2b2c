package com.niu.core.service.site.shop_delivery;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.local_delivery.param.LocalDeliveryOrderCloseParam;
import com.niu.core.service.site.local_delivery.param.LocalDeliveryOrderSearchParam;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryOrderInfoVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryOrderListVo;
import com.niu.core.service.site.shop_delivery.param.ShopDeliveryOrderCloseParam;
import com.niu.core.service.site.shop_delivery.param.ShopDeliveryOrderSearchParam;
import com.niu.core.service.site.shop_delivery.vo.ShopDeliveryOrderInfoVo;
import com.niu.core.service.site.shop_delivery.vo.ShopDeliveryOrderListVo;

/**
 * 商家配送服务接口
 */
public interface IShopDeliveryOrderService {

    /**
     * 查询商家配送订单列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsServiceListVo>
     */
    PageResult<ShopDeliveryOrderListVo> page(PageParam pageParam, ShopDeliveryOrderSearchParam searchParam);

    /**
     * 查询商家配送订详情
     *
     */
    ShopDeliveryOrderInfoVo getInfo(Integer id);

    /**
     * 取消订单
     *
     */
    void closeOrder(Integer id, ShopDeliveryOrderCloseParam param);

    /**
     * 完成订单
     *
     */
    void finishOrder(Integer id);

}
