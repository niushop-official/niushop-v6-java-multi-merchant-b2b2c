package com.niu.mall.service.admin.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.goods.param.GoodsOutPutParam;
import com.niu.mall.service.admin.goods.vo.SelectGoodsListVo;

import java.util.Map;

public interface IOfferGoodsService {

    PageResult<SelectGoodsListVo> getOfferGoodsList(PageParam pageParam, GoodsOutPutParam searchParam);

    SelectGoodsListVo getOfferGoodsInfo(Integer goodsId);


}
