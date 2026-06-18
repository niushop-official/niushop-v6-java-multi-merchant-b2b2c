package com.niu.mall.service.site.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.order.vo.MallOrderInfoVo;
import com.niu.mall.service.api.order.vo.OrderNumVo;
import com.niu.mall.service.site.order.param.*;
import com.niu.mall.service.site.order.vo.EditDeliveryVo;
import com.niu.mall.service.site.order.vo.MallOrderListVo;
import com.niu.mall.service.site.order.vo.MallOrderMemberConsumeListVo;

import java.math.BigDecimal;

/**
 * 订单服务接口
 */
public interface IMallOrderService {


    /**
     * 订单列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderListVo>
     */
    PageResult<MallOrderListVo> page(PageParam pageParam, MallOrderSearchParam searchParam);

    /**
     * 订单详情
     *
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

    /**
     * 获取订单商品重量
     *
     * @param param
     * @return
     */
    BigDecimal getSelectOrderGoodsWeight(MallOrderGoodsWeightParam param);

    /**
     * 修改订单配送
     *
     */
    OrderNumVo getOrderCount();

    /**
     * 删除
     *
     */
    void delete(MallOrderDeleteParam param);

    /**
     * 会员消费记录
     *
     * @param pageParam
     * @param memberId
     * @return
     */
    PageResult<MallOrderMemberConsumeListVo> consumelists(PageParam pageParam, Integer memberId);

}
