package com.niu.mall.service.api.marketing;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.api.marketing.param.MallDiscountListParam;
import com.niu.mall.service.api.marketing.vo.MallDiscountGoodsInfoVo;
import com.niu.mall.service.api.marketing.vo.MallDiscountGoodsListVo;
import com.niu.mall.service.api.marketing.vo.MallDiscountListVo;

import java.util.List;
import java.util.Map;

/**
 * 优惠券商品或品类关联服务接口
 */
public interface IMallDiscountService {

    MallDiscountGoodsInfoVo getInfoByGoods(Integer goodsId);

    JSONArray getDiscountBannerConfig();

    PageResult<MallDiscountGoodsListVo> getGoodsPage(PageParam pageParam, Integer activeId);

    List<MallDiscountListVo> getList(MallDiscountListParam mallDiscountListParam);

    Map<String, Object> detail(Integer marketId);
}