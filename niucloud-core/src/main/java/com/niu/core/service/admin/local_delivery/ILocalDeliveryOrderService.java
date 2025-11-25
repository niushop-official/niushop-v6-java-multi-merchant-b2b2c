package com.niu.core.service.admin.local_delivery;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.local_delivery.param.LocalDeliveryOrderSearchParam;
import com.niu.core.service.admin.local_delivery.vo.LocalDeliveryOrderInfoVo;
import com.niu.core.service.admin.local_delivery.vo.LocalDeliveryOrderListVo;

import java.util.Map;

/**
 * 同城配送相关接口
 */
public interface ILocalDeliveryOrderService {

    /**
     * 查询本地配送订单列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<WebAdvListVo>
     */
    PageResult<LocalDeliveryOrderListVo> page(PageParam pageParam, LocalDeliveryOrderSearchParam searchParam);

    /**
     * 查询本地配送订单详情
     * @param id id
     * @return PageResult<WebAdvListVo>
     */
    LocalDeliveryOrderInfoVo info(Integer  id);

}
