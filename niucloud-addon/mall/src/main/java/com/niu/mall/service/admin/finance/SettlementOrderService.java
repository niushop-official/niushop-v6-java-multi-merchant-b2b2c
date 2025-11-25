package com.niu.mall.service.admin.finance;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.finance.param.SettlementSelectParam;
import com.niu.mall.service.admin.finance.vo.SettlementDataVo;
import com.niu.mall.service.admin.finance.vo.SettlementOrderListVo;
import com.niu.mall.service.admin.goods.param.MallGoodsSearchParam;

/**
 * 结算订单服务接口
 */
public interface SettlementOrderService {

    /**
     * 结算订单分页列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsListVo>
     */
    PageResult<SettlementOrderListVo> getPage(PageParam pageParam, SettlementSelectParam searchParam);

    /**
     * 结算金额统计查询
     *
     */
    SettlementDataVo getSettlementData();
}