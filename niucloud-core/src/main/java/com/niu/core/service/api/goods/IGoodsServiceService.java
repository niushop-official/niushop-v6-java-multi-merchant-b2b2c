package com.niu.core.service.api.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.api.goods.param.MallGoodsSearchParam;
import com.niu.core.service.api.goods.vo.GoodsServiceListVo;
import com.niu.core.service.api.shop.param.ShopSearchParam;
import com.niu.core.service.api.shop.vo.SiteShopCategoryListVo;
import com.niu.core.service.api.shop.vo.SiteShopListVo;

import java.util.List;

/**
 * 商品服务服务接口
 */
public interface IGoodsServiceService {

    /**
     * 商品服务列表
     *
     * @return List<ShopGoodsServiceListVo>
     */
    List<GoodsServiceListVo> getAll();

    PageResult<SiteShopListVo> shopList(ShopSearchParam searchParam);

    List<SiteShopListVo> component(ShopSearchParam shopSearchParam);

    List<SiteShopCategoryListVo> shopCategory();
}
