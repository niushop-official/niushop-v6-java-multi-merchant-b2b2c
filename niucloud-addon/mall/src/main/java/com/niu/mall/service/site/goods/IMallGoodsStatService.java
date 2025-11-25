package com.niu.mall.service.site.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.goods.param.GoodsStatGetRankParam;
import com.niu.mall.service.site.goods.param.MallGoodsStatDateParam;
import com.niu.mall.service.site.goods.vo.GoodsStatRankInfo;
import com.niu.mall.service.site.goods.vo.MallGoodsStatBasicVo;
import com.niu.mall.service.site.goods.vo.MallGoodsStatTrendVo;

import java.util.Map;

/**
 * 商品数据统计服务接口
 */
public interface IMallGoodsStatService {
    void syncStatGoods();

    MallGoodsStatBasicVo getBasic(MallGoodsStatDateParam param);

    MallGoodsStatTrendVo getTrend(MallGoodsStatDateParam param);

    PageResult<GoodsStatRankInfo> getRank(PageParam pageParam, GoodsStatGetRankParam param);

    Map<String, String> getType();
}
