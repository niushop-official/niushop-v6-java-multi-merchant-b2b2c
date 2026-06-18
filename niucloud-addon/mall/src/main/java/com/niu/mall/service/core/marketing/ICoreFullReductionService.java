package com.niu.mall.service.core.marketing;

import com.niu.mall.service.admin.marketing.vo.FullReductionInfoVo;
import com.niu.mall.service.admin.marketing.vo.FullReductionInitVo;
import com.niu.mall.service.core.marketing.param.FullReductionCalculateParam;
import com.niu.mall.service.core.marketing.vo.FullReductionCalculateVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ICoreFullReductionService {

    /**
     * 获取符合条件的满减活动
     *
     * @param siteId        站点ID
     * @param goodsIds      商品ID列表
     * @param memberLevelId 会员等级ID
     * @param orderTotal    订单总金额
     * @param orderGoodsNum 订单商品总数
     * @return 满减活动列表
     */
    List<Map<String, Object>> getActiveFullReduction(Integer siteId, List<Integer> goodsIds, Integer memberLevelId, BigDecimal orderTotal, Integer orderGoodsNum);

    /**
     * 计算满减优惠
     *
     * @param siteId        站点ID
     * @param goodsIds      商品ID列表
     * @param memberLevelId 会员等级ID
     * @param orderTotal    订单总金额
     * @param orderGoodsNum 订单商品总数
     * @return 满减优惠信息
     */
    Map<String, Object> calculateFullReduction(Integer siteId, List<Integer> goodsIds, Integer memberLevelId, BigDecimal orderTotal, Integer orderGoodsNum);

    /**
     * 检查商品是否参与满减活动
     *
     * @param siteId   站点ID
     * @param goodsIds 商品ID列表
     * @return 检查结果
     */
    Map<String, Object> checkGoodsFullReduction(Integer siteId, List<Integer> goodsIds);

    //===================================同步php

    /**
     * 获取商品匹配的所有满减活动信息（订单调用）
     *
     * @param calculateParam 计算参数
     * @return 所有符合条件的活动列表
     */
    List<FullReductionCalculateVo> getFullReductionInfo(FullReductionCalculateParam calculateParam);

    /**
     * 单商品专用：检测单个商品是否可参与满减（返回活动基础信息）
     *
     * @param goodsId  商品ID
     * @param skuId    SKU ID
     * @param memberId 会员ID
     * @param siteId   站点ID
     * @return 符合返回活动信息，否则返回空
     */
    FullReductionCalculateVo getFullReductionInfoOnlyCheck(Integer goodsId, Integer skuId, Integer memberId, Integer siteId);

    /**
     * 计算满减优惠金额（与下单页 OrderCreateHelper 逻辑一致）
     *
     * @param type           满减类型：num-满件，amount-满元
     * @param ruleType       规则类型：ladder-阶梯，cycle-循环
     * @param promotionType  优惠类型：reduction-减价，discount-折扣
     * @param ruleJson       满减规则列表
     * @param matchedTotal   满件时为件数，满元时为实付金额
     * @param goodsPayMoney  商品实付总额（满件折扣阶梯计算时使用，且作为优惠上限）
     * @return 满减优惠金额
     */
    BigDecimal calculateFullReductionDiscount(String type, String ruleType, String promotionType,
                                              List<Map<String, Object>> ruleJson,
                                              BigDecimal matchedTotal, BigDecimal goodsPayMoney);

    //================================通用查询

    /**
     * 获取满减活动详情
     *
     * @param id 满减活动ID
     * @return FullReductionInfoVo 满减活动详情信息
     */
    FullReductionInfoVo info(Integer id);

    /**
     * 获取满减活动初始化数据
     * <p>
     * 返回满减活动所需的枚举数据，包括状态、类型、商品类型、规则类型、会员类型、优惠类型等
     *
     * @return FullReductionInitVo 初始化数据
     */
    FullReductionInitVo init();

    List<Map<String, Object>> selectGoodsAndSkuByFullReductionId(Integer fullReductionId);

    List<Map<String, Object>> selectGoodsAndSkuBySkuList(List<Integer> idList);

}