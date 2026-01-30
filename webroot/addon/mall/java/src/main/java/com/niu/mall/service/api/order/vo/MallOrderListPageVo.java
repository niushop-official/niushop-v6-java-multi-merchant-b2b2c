package com.niu.mall.service.api.order.vo;

import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.api.goods.vo.MallGoodsRankGoodsListInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MallOrderListPageVo extends PageResult<MallOrderListVo> {
    private Integer mchId;
    private Boolean isTradeManaged;

    public MallOrderListPageVo(PageResult<MallOrderListVo> iPage, Integer mchId, Boolean isTradeManaged) {
        this.isTradeManaged = isTradeManaged;
        this.mchId = mchId;
        this.setData(iPage.getData());
        this.setTotal(iPage.getTotal());
        this.setPerPage(iPage.getPerPage());
        this.setCurrentPage(iPage.getCurrentPage());
    }
}
