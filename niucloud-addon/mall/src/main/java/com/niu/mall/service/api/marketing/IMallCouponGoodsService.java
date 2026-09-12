package com.niu.mall.service.api.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.coupon.MallCouponGoods;
import com.niu.mall.service.api.goods.vo.MallGoodsCouponVo;
import com.niu.core.service.api.marketing.param.MallGoodsCouponSearchParam;

import java.util.List;

/**
 * 优惠券商品或品类关联服务接口
 */
public interface IMallCouponGoodsService {

    void insertAll(List<MallCouponGoods> list);

    /**
     *获取优惠券适用商品列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<MallGoodsCouponVo> getCouponGoodsPageList(PageParam pageParam, MallGoodsCouponSearchParam searchParam);
}
