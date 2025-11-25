package com.niu.mall.service.admin.order;

import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.order.param.*;
import com.niu.core.common.domain.PageParam;
import com.niu.mall.service.admin.order.vo.EditDeliveryVo;
import com.niu.mall.service.admin.order.vo.MallOrderInfoVo;
import com.niu.mall.service.admin.order.vo.MallOrderListVo;
import com.niu.mall.service.admin.order.vo.SettlementInfoListVo;

/**
 * 订单服务接口
 */
public interface IMallOrderService {

    PageResult<MallOrderListVo> page(PageParam pageParam, MallOrderSearchParam searchParam);

    /**
     * 订单详情
     * @param id 主键ID
     * @return ShopOrderInfoVo
     */
    MallOrderInfoVo detail(Integer id);

    /**
     * 关闭订单
     *
     * @param id
     */
    void close(Integer id);

    /**
     * 设置商家备注
     *
     * @param param
     */
    void setShopRemark(SetShopRemarkParam param);

    /**
     * 订单调价
     *
     * @param param
     */
    void editPrice(EditPriceParam param);

    /**
     * 获取订单配送修改信息
     *
     * @param param
     * @return
     */
    EditDeliveryVo getEditDeliveryData(EditDeliveryParam param);

    /**
     * 修改订单配送
     *
     * @param param
     */
    void editDelivery(EditDeliveryParam param);

}
