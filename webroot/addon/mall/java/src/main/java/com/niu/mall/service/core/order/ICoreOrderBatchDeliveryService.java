package com.niu.mall.service.core.order;

import com.niu.mall.service.site.order.param.MallOrderAddBatchDeliveryParam;
import com.niu.mall.service.core.order.param.OrderBatchDeliveryParam;
import com.niu.mall.service.core.order.vo.OrderBatchDeliveryVo;

public interface ICoreOrderBatchDeliveryService {

    /**
     * 添加
     * @param param
     */
    void add(MallOrderAddBatchDeliveryParam param);

    /**
     * 执行任务
     * @param id
     * @param siteId
     */
    void execute(Integer id, Integer siteId);

    /**
     * 批量发货
     * @param param
     * @return
     */
    OrderBatchDeliveryVo delivery(OrderBatchDeliveryParam param);
}
