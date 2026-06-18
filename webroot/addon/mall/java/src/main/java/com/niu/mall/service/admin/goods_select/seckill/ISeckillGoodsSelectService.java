package com.niu.mall.service.admin.goods_select.seckill;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.goods.vo.SelectGoodsListVo;
import com.niu.mall.service.admin.goods_select.seckill.param.SeckillGoodsSelectParam;

public interface ISeckillGoodsSelectService {

    PageResult<SelectGoodsListVo> getGoodsList(PageParam pageParam, SeckillGoodsSelectParam param);
}
