package com.niu.mall.service.admin.goods.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class MallGoodsRankSelectPageResult extends PageResult<MallGoodsRankSelectPageInfo> {
    private List<Integer> verifyRankIds;

    public MallGoodsRankSelectPageResult(Page<?> page, List<MallGoodsRankSelectPageInfo> list, List<Integer> verifyRankIds) {
        super(page.getCurrent(), page.getSize(), page.getTotal(), list);
        this.verifyRankIds = verifyRankIds;
    }
}
