package com.niu.mall.service.api.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.api.goods.vo.MallGoodsCollectVo;

/**
 * 商品收藏接口服务
 */
public interface IGoodsCollectService {

    /**
     * 获取会员收藏列表
     * @return
     */
    PageResult<MallGoodsCollectVo> getMemberGoodsCollectList(PageParam pageParam);

    /**
     * 添加收藏
     * @param goodsId
     */
    void addGoodsCollect(Integer goodsId);

    /**
     * 取消收藏
     * @param goodsId
     */
    void cancelGoodsCollect(Integer goodsId);

    /**
     * 取消收藏
     *
     * @param goodsId
     */
    void cancelGoodsCollect(Integer[] goodsId);
}
