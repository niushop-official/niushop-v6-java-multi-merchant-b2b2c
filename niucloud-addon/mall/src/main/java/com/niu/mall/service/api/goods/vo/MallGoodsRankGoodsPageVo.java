package com.niu.mall.service.api.goods.vo;

import com.niu.core.common.domain.PageResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MallGoodsRankGoodsPageVo extends PageResult<MallGoodsRankGoodsListInfo> {
    private Integer rankId;
    private String name;

    public MallGoodsRankGoodsPageVo(PageResult<MallGoodsRankGoodsListInfo> iPage, Integer rankId, String name) {
        this.rankId = rankId;
        this.name = name;
        this.setData(iPage.getData());
        this.setTotal(iPage.getTotal());
        this.setPerPage(iPage.getPerPage());
        this.setCurrentPage(iPage.getCurrentPage());
    }
}
