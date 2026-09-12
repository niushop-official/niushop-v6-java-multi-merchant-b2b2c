package com.niu.mall.service.api.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.mall.entity.goods.MallGoodsRank;
import com.niu.mall.service.admin.goods.param.MallGoodsRankSearchParam;
import com.niu.mall.service.api.goods.param.GoodsRankComponentParam;
import com.niu.mall.service.api.goods.param.MallGoodsRankGoodsPageParam;
import com.niu.mall.service.api.goods.vo.GoodsRankComponentVo;
import com.niu.mall.service.api.goods.vo.GoodsRankResultVo;
import com.niu.mall.service.api.goods.vo.MallGoodsRankGoodsPageVo;
import com.niu.mall.service.api.goods.vo.MallGoodsRankSearchListVo;
import com.niu.mall.service.core.goods.vo.ShopGoodsRankConfigVo;

import java.util.List;

public interface IGoodsRankService {
    ShopGoodsRankConfigVo getGoodsRankConfig();

    List<MallGoodsRank> page(MallGoodsRankSearchParam searchParam);

    MallGoodsRankGoodsPageVo getGoodsPage(PageParam pageParam, MallGoodsRankGoodsPageParam param);

    GoodsRankComponentVo getRankComponents(PageParam pageParam, GoodsRankComponentParam param);

    List<MallGoodsRankSearchListVo> getRankListOfSearch();

    GoodsRankResultVo getGoodsRank(Integer goodsId);
}
