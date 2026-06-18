package com.niu.mall.service.core.marketing;

import com.niu.mall.service.admin.marketing.vo.FullGiftInfoVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftInitVo;
import com.niu.mall.service.core.marketing.param.FullGiftCalculateParam;
import com.niu.mall.service.core.marketing.vo.FullGiftCalculateVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ICoreFullGiftService {

    /**
     * 获取符合条件的满赠活动
     *
     * @param siteId        站点ID
     * @param goodsIds      商品ID列表
     * @param memberLevelId 会员等级ID
     * @param orderTotal    订单总金额
     * @return 满赠活动列表
     */
    List<Map<String, Object>> getActiveFullGift(Integer siteId, List<Integer> goodsIds, Integer memberLevelId, BigDecimal orderTotal);

    /**
     * 计算满赠优惠
     *
     * @param siteId        站点ID
     * @param goodsIds      商品ID列表
     * @param memberLevelId 会员等级ID
     * @param orderTotal    订单总金额
     * @return 满赠优惠信息
     */
    Map<String, Object> calculateFullGift(Integer siteId, List<Integer> goodsIds, Integer memberLevelId, BigDecimal orderTotal);

    /**
     * 检查商品是否参与满赠活动
     *
     * @param siteId   站点ID
     * @param goodsIds 商品ID列表
     * @return 检查结果
     */
    Map<String, Object> checkGoodsFullGift(Integer siteId, List<Integer> goodsIds);

    //===================================同步php

    /**
     * 获取商品匹配的所有满赠活动信息（订单调用）
     *
     * @param calculateParam 计算参数
     * @return 所有符合条件的活动列表
     */
    List<FullGiftCalculateVo> getFullGiftInfo(FullGiftCalculateParam calculateParam);

    /**
     * 单商品快速校验（仅返回活动基础信息）
     *
     * @param goodsId  商品ID
     * @param skuId    SKU ID
     * @param memberId 会员ID
     * @param siteId   站点ID
     * @return 匹配的活动信息（无则返回空Map）
     */
    Map<String, Object> getFullGiftInfoOnlyCheck(Integer goodsId,
                                                 Integer skuId,
                                                 Integer memberId,
                                                 Integer siteId);

    //================================通用查询

    FullGiftInitVo init();

    FullGiftInfoVo info(Integer id);

    List<Map<String, Object>> selectGoodsAndSkuByFullGiftId(Integer fullGiftId);

    List<Map<String, Object>> selectGoodsAndSkuBySkuList(List<Integer> idList);
}