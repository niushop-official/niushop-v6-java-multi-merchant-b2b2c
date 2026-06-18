package com.niu.mall.service.site.order;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.order.param.MallOrderAddBatchDeliveryParam;
import com.niu.mall.service.site.order.param.MallOrderBatchDeliverySearchParam;
import com.niu.mall.service.site.order.vo.MallOrderBatchDeliveryInfoVo;
import com.niu.mall.service.site.order.vo.MallOrderBatchDeliveryListVo;

/**
 * 订单批量发货服务接口
 */
public interface IMallOrderBatchDeliveryService {


    /**
     * 订单批量发货列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderBatchDeliveryListVo>
     */
    PageResult<MallOrderBatchDeliveryListVo> list(PageParam pageParam, MallOrderBatchDeliverySearchParam searchParam);

    /**
     * 订单批量发货详情
     *
     * @param id 主键ID
     * @return ShopOrderBatchDeliveryInfoVo
     */
    MallOrderBatchDeliveryInfoVo info(Integer id);

    void addBatchOrderDelivery(MallOrderAddBatchDeliveryParam param);

    /**
     * 手动执行任务
     *
     * @param id 批量发货ID
     */
    void execute(Integer id);
}
