package com.niu.core.service.core.marketing.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.component.context.event.CallbackPublisher;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.event.goods.FullReductionGoodsEventDefiner;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.entity.fullactive.MallFullReduction;
import com.niu.core.entity.fullactive.MallFullReductionGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.core.enums.fullactive.*;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.core.mapper.fullactive.FullReductionGoodsMapper;
import com.niu.core.mapper.fullactive.FullReductionMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.core.service.admin.marketing.param.FullReductionParam;
import com.niu.core.service.admin.marketing.vo.FullReductionInfoVo;
import com.niu.core.service.admin.marketing.vo.FullReductionInitVo;
import com.niu.core.service.core.marketing.ICoreFullReductionService;
import com.niu.core.service.core.marketing.param.FullReductionCalculateParam;
import com.niu.core.service.core.marketing.vo.FullReductionCalculateVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class CoreFullReductionServiceImpl implements ICoreFullReductionService {

    @Resource
    private FullReductionMapper fullReductionMapper;

    @Resource
    private FullReductionGoodsMapper fullReductionGoodsMapper;

    @Resource
    private MallGoodsSkuMapper skuMapper;

    @Resource
    private ICoreSiteService coreSiteService;

    @Override
    public List<Map<String, Object>> getActiveFullReduction(Integer siteId, List<Integer> goodsIds, Integer memberLevelId, BigDecimal orderTotal, Integer orderGoodsNum) {
        Long currentTime = System.currentTimeMillis() / 1000;

        MPJLambdaWrapper<MallFullReduction> queryWrapper = new MPJLambdaWrapper<MallFullReduction>()
                .selectAll(MallFullReduction.class)
                .eq(MallFullReduction::getSiteId, siteId)
                .eq(MallFullReduction::getStatus, FullReductionStatusEnum.PROCESS.getStatus())
                .le(MallFullReduction::getStartTime, currentTime)
                .ge(MallFullReduction::getEndTime, currentTime)
                .orderByAsc(MallFullReduction::getSort)
                .orderByDesc(MallFullReduction::getCreateTime);

        List<MallFullReduction> fullReductionList = fullReductionMapper.selectList(queryWrapper);

        if (ObjectUtil.isEmpty(fullReductionList)) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> activeFullReductionList = new ArrayList<>();

        for (MallFullReduction fullReduction : fullReductionList) {
            if (checkMemberCondition(fullReduction, memberLevelId)) {
                List<Integer> activeGoodsIds = getActiveGoodsIds(fullReduction.getId());

                if (checkGoodsCondition(fullReduction, goodsIds, activeGoodsIds)) {
                    Map<String, Object> reductionInfo = BeanUtil.beanToMap(fullReduction);
                    reductionInfo.put("goods_ids", activeGoodsIds);
                    activeFullReductionList.add(reductionInfo);
                }
            }
        }

        return activeFullReductionList;
    }

    @Override
    public Map<String, Object> calculateFullReduction(Integer siteId, List<Integer> goodsIds, Integer memberLevelId, BigDecimal orderTotal, Integer orderGoodsNum) {
        List<Map<String, Object>> activeFullReductionList = getActiveFullReduction(siteId, goodsIds, memberLevelId, orderTotal, orderGoodsNum);

        if (ObjectUtil.isEmpty(activeFullReductionList)) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> matchedReductions = new ArrayList<>();
        BigDecimal maxDiscount = BigDecimal.ZERO;

        for (Map<String, Object> reductionInfo : activeFullReductionList) {
            String ruleJson = (String) reductionInfo.get("ruleJson");
            List<Map> rawRules = JSONUtil.toList(ruleJson, Map.class);
            List<Map<String, Object>> rules = rawRules.stream()
                    .map(m -> (Map<String, Object>) m)
                    .collect(Collectors.toList());

            for (Map<String, Object> rule : rules) {
                BigDecimal conditionValue = new BigDecimal(rule.get("count").toString());
                boolean conditionMet = false;

                if (FullReductionTypeEnum.AMOUNT.getType().equals(reductionInfo.get("type"))) {
                    conditionMet = orderTotal.compareTo(conditionValue) >= 0;
                } else if (FullReductionTypeEnum.NUM.getType().equals(reductionInfo.get("type"))) {
                    conditionMet = orderGoodsNum >= conditionValue.intValue();
                }

                if (conditionMet) {
                    String promotionType = rule.get("promotion_type").toString();
                    BigDecimal discountValue = new BigDecimal(rule.get("discount").toString());

                    if ("discount".equals(promotionType)) {
                        discountValue = orderTotal.multiply(BigDecimal.ONE.subtract(discountValue.divide(new BigDecimal("10"))));
                    }

                    if (discountValue.compareTo(maxDiscount) > 0) {
                        maxDiscount = discountValue;
                        matchedReductions.clear();

                        Map<String, Object> matchedReduction = new HashMap<>();
                        matchedReduction.put("full_reduction_id", reductionInfo.get("fullReductionId"));
                        matchedReduction.put("title", reductionInfo.get("title"));
                        matchedReduction.put("rule_type", reductionInfo.get("ruleType"));
                        matchedReduction.put("type", reductionInfo.get("type"));
                        matchedReduction.put("rule", rule);
                        matchedReduction.put("discount_money", discountValue);
                        matchedReductions.add(matchedReduction);
                    }
                }
            }
        }

        if (ObjectUtil.isNotEmpty(matchedReductions)) {
            result.put("full_reduction_list", matchedReductions);
            result.put("max_discount_money", maxDiscount);
        }

        return result;
    }

    @Override
    public Map<String, Object> checkGoodsFullReduction(Integer siteId, List<Integer> goodsIds) {
        Map<String, Object> result = new HashMap<>();
        List<Integer> conflictGoodsIds = new ArrayList<>();

        Long currentTime = System.currentTimeMillis() / 1000;

        MPJLambdaWrapper<MallFullReduction> queryWrapper = new MPJLambdaWrapper<MallFullReduction>()
                .selectAll(MallFullReduction.class)
                .eq(MallFullReduction::getSiteId, siteId)
                .eq(MallFullReduction::getStatus, FullReductionStatusEnum.PROCESS.getStatus())
                .le(MallFullReduction::getStartTime, currentTime)
                .ge(MallFullReduction::getEndTime, currentTime);

        List<MallFullReduction> fullReductionList = fullReductionMapper.selectList(queryWrapper);

        for (MallFullReduction fullReduction : fullReductionList) {
            if (FullReductionGoodsTypeEnum.SELECTED.getType().equals(fullReduction.getGoodsType())) {
                List<Integer> activeGoodsIds = getActiveGoodsIds(fullReduction.getId());

                for (Integer goodsId : goodsIds) {
                    if (activeGoodsIds.contains(goodsId)) {
                        conflictGoodsIds.add(goodsId);
                    }
                }
            }
        }

        result.put("conflict_goods_ids", conflictGoodsIds);
        result.put("has_conflict", ObjectUtil.isNotEmpty(conflictGoodsIds));

        return result;
    }

    private boolean checkMemberCondition(MallFullReduction fullReduction, Integer memberLevelId) {
        if (FullReductionMemberTypeEnum.ALL.getType().equals(fullReduction.getMemberType())) {
            return true;
        }

        if (FullReductionMemberTypeEnum.LABEL.getType().equals(fullReduction.getMemberType())) {
            String memberTypeJson = fullReduction.getMemberTypeJson();
            if (ObjectUtil.isNotEmpty(memberTypeJson)) {
                List<Integer> memberLevelIds = JSONUtil.toList(memberTypeJson, Integer.class);
                return memberLevelIds.contains(memberLevelId);
            }
        }

        return false;
    }

    private boolean checkGoodsCondition(MallFullReduction fullReduction, List<Integer> goodsIds, List<Integer> activeGoodsIds) {
        if (FullReductionGoodsTypeEnum.ALL.getType().equals(fullReduction.getGoodsType())) {
            return true;
        }

        if (FullReductionGoodsTypeEnum.SELECTED.getType().equals(fullReduction.getGoodsType())) {
            return goodsIds.stream().anyMatch(activeGoodsIds::contains);
        }

        if (FullReductionGoodsTypeEnum.SELECTED_NOT.getType().equals(fullReduction.getGoodsType())) {
            return goodsIds.stream().noneMatch(activeGoodsIds::contains);
        }

        return false;
    }

    private List<Integer> getActiveGoodsIds(Integer fullReductionId) {
        LambdaQueryWrapper<MallFullReductionGoods> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(MallFullReductionGoods::getFullReductionId, fullReductionId);
        List<MallFullReductionGoods> fullReductionGoodsList = fullReductionGoodsMapper.selectList(queryWrapper);

        return fullReductionGoodsList.stream()
                .map(MallFullReductionGoods::getGoodsId)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<FullReductionCalculateVo> getFullReductionInfo1(FullReductionCalculateParam calculateParam) {
        Integer siteId = calculateParam.getSiteId();
        List<FullReductionCalculateParam.GoodsSkuData> goodsSkuData = calculateParam.getGoodsSkuData();
        Integer memberId = calculateParam.getMemberId();
        boolean checkRule = calculateParam.isCheckRule();

        if (ObjectUtil.isEmpty(goodsSkuData)) {
            return new ArrayList<>();
        }

        List<Integer> goodsIds = goodsSkuData.stream()
                .map(FullReductionCalculateParam.GoodsSkuData::getGoodsId)
                .distinct()
                .collect(Collectors.toList());

        BigDecimal orderTotal = goodsSkuData.stream()
                .map(FullReductionCalculateParam.GoodsSkuData::getPayMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer orderGoodsNum = goodsSkuData.stream()
                .mapToInt(item -> item.getNum() != null ? item.getNum() : 1)
                .sum();

        Long currentTime = System.currentTimeMillis() / 1000;

        MPJLambdaWrapper<MallFullReduction> queryWrapper = new MPJLambdaWrapper<MallFullReduction>()
                .selectAll(MallFullReduction.class)
                .eq(MallFullReduction::getSiteId, siteId)
                .eq(MallFullReduction::getStatus, FullReductionStatusEnum.PROCESS.getStatus())
                .le(MallFullReduction::getStartTime, currentTime)
                .ge(MallFullReduction::getEndTime, currentTime)
                .orderByAsc(MallFullReduction::getSort)
                .orderByDesc(MallFullReduction::getCreateTime);

        List<MallFullReduction> fullReductionList = fullReductionMapper.selectList(queryWrapper);

        if (ObjectUtil.isEmpty(fullReductionList)) {
            return new ArrayList<>();
        }

        List<FullReductionCalculateVo> result = new ArrayList<>();

        for (MallFullReduction fullReduction : fullReductionList) {
            List<Integer> activeGoodsIds = getActiveGoodsIds(fullReduction.getId());

            if (!checkGoodsCondition(fullReduction, goodsIds, activeGoodsIds)) {
                continue;
            }

            List<FullReductionCalculateParam.GoodsSkuData> matchedGoods = getMatchedGoods(goodsSkuData, activeGoodsIds, fullReduction.getGoodsType());

            if (ObjectUtil.isEmpty(matchedGoods)) {
                continue;
            }

            BigDecimal matchedTotal = matchedGoods.stream()
                    .map(FullReductionCalculateParam.GoodsSkuData::getPayMoney)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Integer matchedNum = matchedGoods.stream()
                    .mapToInt(item -> item.getNum() != null ? item.getNum() : 1)
                    .sum();

            String ruleJson = fullReduction.getRuleJson();
            List<Map> rawRules = JSONUtil.toList(ruleJson, Map.class);
            List<Map<String, Object>> rules = rawRules.stream()
                    .map(m -> (Map<String, Object>) m)
                    .collect(Collectors.toList());

            for (Map<String, Object> rule : rules) {
                BigDecimal conditionValue = new BigDecimal(rule.get("count").toString());
                boolean conditionMet = false;

                if (FullReductionTypeEnum.AMOUNT.getType().equals(fullReduction.getType())) {
                    conditionMet = matchedTotal.compareTo(conditionValue) >= 0;
                } else if (FullReductionTypeEnum.NUM.getType().equals(fullReduction.getType())) {
                    conditionMet = matchedNum >= conditionValue.intValue();
                }

                if (conditionMet) {
                    String promotionType = rule.get("promotion_type").toString();
                    BigDecimal discountValue = new BigDecimal(rule.get("discount").toString());

                    FullReductionCalculateVo calculateVo = new FullReductionCalculateVo();
                    calculateVo.setId(fullReduction.getId());
                    calculateVo.setTitle(fullReduction.getTitle());
                    calculateVo.setType(fullReduction.getType());
                    calculateVo.setMatchedTotal(matchedTotal);

                    if (FullReductionPromotionTypeEnum.DISCOUNT.getType().equals(promotionType)) {
                        calculateVo.setDiscountAmount(discountValue);
                        calculateVo.setDiscountRate(BigDecimal.ZERO);
                    } else if (FullReductionPromotionTypeEnum.REDUCTION.getType().equals(promotionType)) {
                        calculateVo.setDiscountAmount(BigDecimal.ZERO);
                        calculateVo.setDiscountRate(discountValue);
                    }

                    List<FullReductionCalculateVo.MatchedGoods> matchedGoodsList = new ArrayList<>();
                    for (FullReductionCalculateParam.GoodsSkuData skuData : matchedGoods) {
                        FullReductionCalculateVo.MatchedGoods matchedGoodsItem = new FullReductionCalculateVo.MatchedGoods();
                        matchedGoodsItem.setGoodsId(skuData.getGoodsId());
                        matchedGoodsItem.setSkuId(skuData.getSkuId());
                        matchedGoodsItem.setPayMoney(skuData.getPayMoney());
                        matchedGoodsItem.setSkuKey(skuData.getSkuKey());
                        matchedGoodsList.add(matchedGoodsItem);
                    }
                    calculateVo.setMatchedGoods(matchedGoodsList);

                    result.add(calculateVo);
                    break;
                }
            }
        }

        return result;
    }

    private List<FullReductionCalculateParam.GoodsSkuData> getMatchedGoods(
            List<FullReductionCalculateParam.GoodsSkuData> goodsSkuData,
            List<Integer> activeGoodsIds,
            String goodsType) {

        if (FullReductionGoodsTypeEnum.ALL.getType().equals(goodsType)) {
            return new ArrayList<>(goodsSkuData);
        }

        if (FullReductionGoodsTypeEnum.SELECTED.getType().equals(goodsType)) {
            return goodsSkuData.stream()
                    .filter(item -> activeGoodsIds.contains(item.getGoodsId()))
                    .collect(Collectors.toList());
        }

        if (FullReductionGoodsTypeEnum.SELECTED_NOT.getType().equals(goodsType)) {
            return goodsSkuData.stream()
                    .filter(item -> !activeGoodsIds.contains(item.getGoodsId()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    //===================================同步php

    private static final List<String> CHECK_PRIORITY = Arrays.asList(
            FullReductionGoodsTypeEnum.SELECTED.getType(),
            FullReductionGoodsTypeEnum.SELECTED_NOT.getType(),
            FullReductionGoodsTypeEnum.ALL.getType()
    );

    @Resource
    private MemberMapper memberMapper;

    // 当前请求上下文（线程局部，避免成员变量冲突）
    private final ThreadLocal<Integer> currentSiteId = new ThreadLocal<>();
    private final ThreadLocal<Integer> currentMemberId = new ThreadLocal<>();
    private final ThreadLocal<Boolean> currentCheckRule = new ThreadLocal<>();
    private final ThreadLocal<List<FullReductionCalculateParam.GoodsSkuData>> currentGoodsList = new ThreadLocal<>();
    private final ThreadLocal<Set<String>> usedSkuKeys = ThreadLocal.withInitial(HashSet::new);


    @Override
    public List<FullReductionCalculateVo> getFullReductionInfo(FullReductionCalculateParam calculateParam) {
        // 初始化上下文
        currentSiteId.set(calculateParam.getSiteId());
        currentMemberId.set(calculateParam.getMemberId());
        currentCheckRule.set(calculateParam.isCheckRule());
        currentGoodsList.set(formatGoodsSkuData(calculateParam.getGoodsSkuData()));
        usedSkuKeys.get().clear();

        if (CollectionUtils.isEmpty(currentGoodsList.get())) {
            return Collections.emptyList();
        }

        // 获取会员信息
        Member member = memberMapper.selectById(calculateParam.getMemberId());
        Map<String, Object> memberInfo = member == null ? Collections.emptyMap() : convertMemberToMap(member);

        // 获取所有生效活动并按优先级匹配
        Map<String, List<FullReductionInfoVo>> groupedActiveList = getEffectiveFullReductionList();
        List<FullReductionCalculateVo> result = new ArrayList<>();
        for (String goodsType : CHECK_PRIORITY) {
            List<FullReductionInfoVo> actives = groupedActiveList.get(goodsType);
            if (actives != null && !actives.isEmpty()) {
                List<FullReductionCalculateVo> matched = checkActiveByType(actives, goodsType, memberInfo);
                if (!CollectionUtils.isEmpty(matched)) {
                    result.addAll(matched);
                }
            }
        }
        return result;
    }

    @Override
    public FullReductionCalculateVo getFullReductionInfoOnlyCheck(Integer goodsId, Integer skuId, Integer memberId, Integer siteId) {
        currentSiteId.set(siteId);
        currentMemberId.set(memberId);
        currentCheckRule.set(true); // 此处默认校验规则

        // 查询当前站点所有生效活动
        long now = System.currentTimeMillis() / 1000;
        List<MallFullReduction> activeList = fullReductionMapper.selectList(
                new LambdaQueryWrapper<MallFullReduction>()
                        .eq(MallFullReduction::getSiteId, siteId)
                        .eq(MallFullReduction::getStatus, FullReductionStatusEnum.PROCESS.getStatus())
                        .le(MallFullReduction::getStartTime, now)
                        .ge(MallFullReduction::getEndTime, now)
        );
        if (CollectionUtils.isEmpty(activeList)) {
            return null;
        }
        List<FullReductionInfoVo> infos = BeanUtil.copyToList(activeList, FullReductionInfoVo.class);

        // 获取会员信息
        Member member = memberMapper.selectById(memberId);
        Map<String, Object> memberInfo = member == null ? Collections.emptyMap() : convertMemberToMap(member);

        // 查询所有活动关联的商品SKU（批量）
        List<Integer> activeIds = activeList.stream().map(MallFullReduction::getId).collect(Collectors.toList());
        List<MallFullReductionGoods> allRelation = fullReductionGoodsMapper.selectList(
                new LambdaQueryWrapper<MallFullReductionGoods>()
                        .eq(MallFullReductionGoods::getSiteId, siteId)
                        .in(MallFullReductionGoods::getFullReductionId, activeIds)
        );
        Map<Integer, Set<String>> relationSkuMap = new HashMap<>();
        for (MallFullReductionGoods goods : allRelation) {
            relationSkuMap.computeIfAbsent(goods.getFullReductionId(), k -> new HashSet<>())
                    .add(goods.getGoodsId() + "_" + goods.getSkuId());
        }

        String skuKey = goodsId + "_" + skuId;
        for (FullReductionInfoVo active : infos) {
            // 会员权限校验
            if (!checkMemberPermission(active, memberInfo)) continue;
            Set<String> relationSkus = relationSkuMap.getOrDefault(active.getId(), Collections.emptySet());
            boolean match = false;
            FullReductionGoodsTypeEnum goodsTypeEnum = FullReductionGoodsTypeEnum.getEnumByType(active.getGoodsType());
            if (goodsTypeEnum != null) {
                match = switch (goodsTypeEnum) {
                    case SELECTED -> relationSkus.contains(skuKey);
                    case SELECTED_NOT -> !relationSkus.contains(skuKey);
                    case ALL -> true;
                };
            }
            if (match) {
                FullReductionCalculateVo result = new FullReductionCalculateVo();
                result.setId(active.getId());
                result.setTitle(active.getTitle());
                result.setGoodsType(active.getGoodsType());
                result.setMemberType(active.getMemberType());
                result.setMemberTypeJson(active.getMemberTypeJson());
                result.setPromotionType(active.getPromotionType());
                result.setRuleType(active.getRuleType());
                result.setType(active.getType());
                result.setRuleJson(active.getRuleJson());
                result.setStartTime(active.getStartTime());
                result.setEndTime(active.getEndTime());
                return result;
            }
        }
        return null;
    }

    // ---------- 私有方法 ----------

    private List<FullReductionCalculateParam.GoodsSkuData> formatGoodsSkuData(List<FullReductionCalculateParam.GoodsSkuData> goodsSkuData) {
        if (CollectionUtils.isEmpty(goodsSkuData)) return Collections.emptyList();
        List<FullReductionCalculateParam.GoodsSkuData> result = new ArrayList<>();
        for (FullReductionCalculateParam.GoodsSkuData item : goodsSkuData) {
            if (item.getGoodsId() == null || item.getSkuId() == null ||
                    item.getPayMoney() == null || item.getPayMoney().compareTo(BigDecimal.ZERO) <= 0 ||
                    item.getNum() == null || item.getNum() <= 0) {
                continue;
            }
            item.setSkuKey(item.getGoodsId() + "_" + item.getSkuId());
            result.add(item);
        }
        return result;
    }

    private Map<String, List<FullReductionInfoVo>> getEffectiveFullReductionList() {
        long now = System.currentTimeMillis() / 1000;
        Integer siteId = currentSiteId.get();
        List<MallFullReduction> list = fullReductionMapper.selectList(
                new LambdaQueryWrapper<MallFullReduction>()
                        .eq(MallFullReduction::getSiteId, siteId)
                        .eq(MallFullReduction::getStatus, FullReductionStatusEnum.PROCESS.getStatus())
                        .le(MallFullReduction::getStartTime, now)
                        .ge(MallFullReduction::getEndTime, now)
                        .select(MallFullReduction::getId, MallFullReduction::getGoodsType, MallFullReduction::getTitle,
                                MallFullReduction::getMemberType, MallFullReduction::getMemberTypeJson,
                                MallFullReduction::getRuleType, MallFullReduction::getRuleJson, MallFullReduction::getType,
                                MallFullReduction::getPromotionType, MallFullReduction::getSort)
        );
        List<FullReductionInfoVo> infos = BeanUtil.copyToList(list, FullReductionInfoVo.class);
        // 解析 rule_json，补充 minThreshold
        for (FullReductionInfoVo active : infos) {
            BigDecimal minThreshold = getMinThreshold(active.getRuleJson());
            active.setMinThreshold(minThreshold);
        }
        // 按商品类型分组，并按 sort 排序
        Map<String, List<FullReductionInfoVo>> grouped = infos.stream()
                .collect(Collectors.groupingBy(FullReductionInfoVo::getGoodsType));
        for (List<FullReductionInfoVo> typeList : grouped.values()) {
            typeList.sort(Comparator.comparing(FullReductionInfoVo::getSort));
        }
        return grouped;
    }

    private BigDecimal getMinThreshold(String ruleJsonStr) {
        if (!StringUtils.hasText(ruleJsonStr)) return BigDecimal.ZERO;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> rules = objectMapper.readValue(ruleJsonStr, new TypeReference<>() {
            });
            BigDecimal min = null;
            for (Map<String, Object> rule : rules) {
                Object amount = rule.get("amount");
                if (amount != null) {
                    BigDecimal val = new BigDecimal(amount.toString());
                    if (val.compareTo(BigDecimal.ZERO) > 0) {
                        if (min == null || val.compareTo(min) < 0) min = val;
                    }
                }
            }
            return min == null ? BigDecimal.ZERO : min;
        } catch (Exception e) {
            log.error("解析 rule_json 失败: {}", ruleJsonStr, e);
            return BigDecimal.ZERO;
        }
    }

    private List<FullReductionCalculateVo> checkActiveByType(List<FullReductionInfoVo> actives, String goodsType,
                                                             Map<String, Object> memberInfo) {
        List<FullReductionCalculateVo> result = new ArrayList<>();
        List<FullReductionCalculateParam.GoodsSkuData> allGoods = currentGoodsList.get();
        Set<String> usedKeys = usedSkuKeys.get();
        for (FullReductionInfoVo active : actives) {
            // 会员权限校验
            if (!checkMemberPermission(active, memberInfo)) continue;
            // 获取该活动允许参与的商品（排除已使用）
            List<FullReductionCalculateVo.MatchedGoods> matchedGoods = getMatchedGoodsForActive(active.getId(), goodsType, allGoods, usedKeys);
            if (CollectionUtils.isEmpty(matchedGoods)) continue;
            // 校验阈值（如果需要）
            if (currentCheckRule.get()) {
                if (!checkAmountOrNumThreshold(active, matchedGoods)) continue;
            }
            // 解析最优满减规则
            Map<String, Object> reduceInfo = parseBestReduceRule(active, matchedGoods);
            // 锁定商品
            for (FullReductionCalculateVo.MatchedGoods g : matchedGoods) {
                usedKeys.add(g.getSkuKey());
            }
            // 组装返回 VO
            FullReductionCalculateVo vo = new FullReductionCalculateVo();
            vo.setId(active.getId());
            vo.setTitle(active.getTitle());
            vo.setGoodsType(active.getGoodsType());
            vo.setMemberType(active.getMemberType());
            vo.setMemberTypeJson(active.getMemberTypeJson());
            vo.setRuleType(active.getRuleType());
            vo.setRuleJson(active.getRuleJson());
            vo.setType(active.getType());
            vo.setPromotionType(active.getPromotionType());
            vo.setSort(active.getSort());
            vo.setMinThreshold(active.getMinThreshold());
            vo.setReduceInfo(reduceInfo);
            // 计算 matched_total
            if (FullReductionTypeEnum.AMOUNT.getType().equals(active.getType())) {
                BigDecimal total = matchedGoods.stream()
                        .map(FullReductionCalculateVo.MatchedGoods::getPayMoney)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                vo.setMatchedTotal(total);
            } else {
                int total = matchedGoods.stream().mapToInt(FullReductionCalculateVo.MatchedGoods::getNum).sum();
                vo.setMatchedTotal(BigDecimal.valueOf(total));
            }
            vo.setMatchedGoods(matchedGoods);
            result.add(vo);
        }
        return result;
    }

    private List<FullReductionCalculateVo.MatchedGoods> getMatchedGoodsForActive(Integer reductionId, String goodsType,
                                                                                 List<FullReductionCalculateParam.GoodsSkuData> allGoods, Set<String> usedKeys) {
        // 获取活动关联的 SKU_KEY 集合
        Set<String> relationSkus = new HashSet<>();
        if (!FullReductionGoodsTypeEnum.ALL.getType().equals(goodsType)) {
            List<MallFullReductionGoods> goodsList = fullReductionGoodsMapper.selectList(
                    new LambdaQueryWrapper<MallFullReductionGoods>()
                            .eq(MallFullReductionGoods::getFullReductionId, reductionId)
                            .eq(MallFullReductionGoods::getSiteId, currentSiteId.get())
            );
            for (MallFullReductionGoods g : goodsList) {
                relationSkus.add(g.getGoodsId() + "_" + g.getSkuId());
            }
        }
        List<FullReductionCalculateVo.MatchedGoods> matched = new ArrayList<>();
        for (FullReductionCalculateParam.GoodsSkuData item : allGoods) {
            if (usedKeys.contains(item.getSkuKey())) continue;
            boolean match = false;
            FullReductionGoodsTypeEnum goodsTypeEnum = FullReductionGoodsTypeEnum.getEnumByType(goodsType);
            if (goodsTypeEnum != null) {
                match = switch (goodsTypeEnum) {
                    case SELECTED -> relationSkus.contains(item.getSkuKey());
                    case SELECTED_NOT -> !relationSkus.contains(item.getSkuKey());
                    case ALL -> true;
                };
            }
            if (match) {
                FullReductionCalculateVo.MatchedGoods mg = new FullReductionCalculateVo.MatchedGoods();
                mg.setGoodsId(item.getGoodsId());
                mg.setSkuId(item.getSkuId());
                mg.setPayMoney(item.getPayMoney());
                mg.setNum(item.getNum());
                mg.setSkuKey(item.getSkuKey());
                matched.add(mg);
            }
        }
        return matched;
    }

    private boolean checkAmountOrNumThreshold(FullReductionInfoVo active, List<FullReductionCalculateVo.MatchedGoods> matchedGoods) {
        BigDecimal minThreshold = active.getMinThreshold();
        if (minThreshold == null || minThreshold.compareTo(BigDecimal.ZERO) <= 0) return false;
        if (FullReductionTypeEnum.AMOUNT.getType().equals(active.getType())) {
            BigDecimal total = matchedGoods.stream()
                    .map(FullReductionCalculateVo.MatchedGoods::getPayMoney)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return total.compareTo(minThreshold) >= 0;
        } else if (FullReductionTypeEnum.NUM.getType().equals(active.getType())) {
            int total = matchedGoods.stream().mapToInt(FullReductionCalculateVo.MatchedGoods::getNum).sum();
            return BigDecimal.valueOf(total).compareTo(minThreshold) >= 0;
        }
        return false;
    }

    private Map<String, Object> parseBestReduceRule(FullReductionInfoVo active, List<FullReductionCalculateVo.MatchedGoods> matchedGoods) {
        String ruleJsonStr = active.getRuleJson();
        if (!StringUtils.hasText(ruleJsonStr)) return Collections.emptyMap();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> rules = objectMapper.readValue(ruleJsonStr, new TypeReference<>() {
            });
            if (rules.isEmpty()) return Collections.emptyMap();
            BigDecimal currentValue;
            if (FullReductionTypeEnum.AMOUNT.getType().equals(active.getType())) {
                currentValue = matchedGoods.stream()
                        .map(FullReductionCalculateVo.MatchedGoods::getPayMoney)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            } else {
                int total = matchedGoods.stream().mapToInt(FullReductionCalculateVo.MatchedGoods::getNum).sum();
                currentValue = BigDecimal.valueOf(total);
            }
            int maxLevel = -1;
            Map<String, Object> bestRule = null;
            for (Map<String, Object> rule : rules) {
                BigDecimal threshold = new BigDecimal(rule.get("amount").toString());
                int level = (Integer) rule.getOrDefault("level", 0);
                if (!currentCheckRule.get()) {
                    currentValue = threshold; // 当不校验规则时，直接用阈值作为当前值（与原逻辑一致）
                }
                if (currentValue.compareTo(threshold) >= 0 && level > maxLevel) {
                    maxLevel = level;
                    bestRule = rule;
                }
            }
            return bestRule == null ? Collections.emptyMap() : bestRule;
        } catch (Exception e) {
            log.error("解析规则失败", e);
            return Collections.emptyMap();
        }
    }

    /**
     * 校验指定会员是否可参与活动
     *
     * @param active
     * @param memberInfo
     * @return
     */
    private boolean checkMemberPermission(FullReductionInfoVo active, Map<String, Object> memberInfo) {
        String memberType = active.getMemberType();
        if (FullReductionMemberTypeEnum.ALL.getType().equals(memberType)) return true;
        if (memberInfo == null || memberInfo.isEmpty()) return false;
        if (FullReductionMemberTypeEnum.LABEL.getType().equals(memberType)) {
            String labelIdsStr = active.getMemberTypeJson();
            Set<Integer> labelIds = parseIds(labelIdsStr);
            Object memberLabel = memberInfo.getOrDefault("member_label_ids", memberInfo.get("member_label"));
            Set<Integer> memberLabelIds = parseIds(memberLabel == null ? "" : memberLabel.toString());
            return !Collections.disjoint(labelIds, memberLabelIds);
        }
        if (FullReductionMemberTypeEnum.LEVEL.getType().equals(memberType)) {
            String levelIdsStr = active.getMemberTypeJson();
            Set<Integer> levelIds = parseIds(levelIdsStr);
            Integer memberLevelId = (Integer) memberInfo.getOrDefault("level_id", memberInfo.get("member_level"));
            return memberLevelId != null && levelIds.contains(memberLevelId);
        }
        return false;
    }

    /**
     * ID数组字符串变为集合
     *
     * @param str "["1", "2",...]"
     * @return [1, 2,...]
     */
    private Set<Integer> parseIds(String str) {
        if (!StringUtils.hasText(str)) return Collections.emptySet();

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Integer> list = mapper.readValue(str, new TypeReference<>() {
            });
            return new HashSet<>(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
    }

    @Override
    public BigDecimal calculateFullReductionDiscount(String type, String ruleType, String promotionType,
                                                     List<FullReductionParam.Rule> ruleJson,
                                                     BigDecimal matchedTotal, BigDecimal goodsPayMoney) {
        if (CollectionUtils.isEmpty(ruleJson) || matchedTotal == null
                || matchedTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (goodsPayMoney == null) {
            goodsPayMoney = BigDecimal.ZERO;
        }

        BigDecimal fullReductionMoney = BigDecimal.ZERO;
        if (FullReductionTypeEnum.NUM.getType().equals(type)) {
            if (FullReductionRuleTypeEnum.LADDER.getType().equals(ruleType)) {
                for (FullReductionParam.Rule rule : ruleJson) {
                    BigDecimal amount = ObjectUtil.defaultIfNull(rule.getAmount(), BigDecimal.ZERO);
                    if (matchedTotal.compareTo(amount) >= 0) {
                        fullReductionMoney = ObjectUtil.defaultIfNull(rule.getReduce(), BigDecimal.ZERO);
                    }
                }
                if (FullReductionPromotionTypeEnum.DISCOUNT.getType().equals(promotionType)) {
                    BigDecimal discountRate = fullReductionMoney.divide(BigDecimal.TEN, 2, RoundingMode.DOWN);
                    fullReductionMoney = goodsPayMoney.multiply(BigDecimal.ONE.subtract(discountRate))
                            .setScale(2, RoundingMode.DOWN);
                }
            } else {
                BigDecimal firstAmount = ObjectUtil.defaultIfNull(ruleJson.get(0).getAmount(), BigDecimal.ZERO);
                BigDecimal firstReduce = ObjectUtil.defaultIfNull(ruleJson.get(0).getReduce(), BigDecimal.ZERO);
                long cycles = matchedTotal.divide(firstAmount, 0, RoundingMode.DOWN).longValue();
                fullReductionMoney = BigDecimal.valueOf(cycles).multiply(firstReduce)
                        .setScale(2, RoundingMode.DOWN);
            }
        } else {
            if (FullReductionRuleTypeEnum.LADDER.getType().equals(ruleType)) {
                for (FullReductionParam.Rule rule : ruleJson) {
                    BigDecimal amount = ObjectUtil.defaultIfNull(rule.getAmount(), BigDecimal.ZERO);
                    if (matchedTotal.compareTo(amount) >= 0) {
                        fullReductionMoney = ObjectUtil.defaultIfNull(rule.getReduce(), BigDecimal.ZERO);
                    }
                }
                if (FullReductionPromotionTypeEnum.DISCOUNT.getType().equals(promotionType)) {
                    BigDecimal discountRate = fullReductionMoney.divide(BigDecimal.TEN, 4, RoundingMode.DOWN);
                    fullReductionMoney = matchedTotal.multiply(BigDecimal.ONE.subtract(discountRate))
                            .setScale(4, RoundingMode.DOWN);
                }
            } else {
                BigDecimal firstAmount = ObjectUtil.defaultIfNull(ruleJson.get(0).getAmount(), BigDecimal.ZERO);
                BigDecimal firstReduce = ObjectUtil.defaultIfNull(ruleJson.get(0).getReduce(), BigDecimal.ZERO);
                long cycles = matchedTotal.divide(firstAmount, 0, RoundingMode.DOWN).longValue();
                fullReductionMoney = BigDecimal.valueOf(cycles).multiply(firstReduce)
                        .setScale(4, RoundingMode.DOWN);
            }
        }

        if (fullReductionMoney.compareTo(goodsPayMoney) > 0) {
            fullReductionMoney = goodsPayMoney;
        }
        return fullReductionMoney.max(BigDecimal.ZERO);
    }

    private Map<String, Object> convertMemberToMap(Member member) {
        Map<String, Object> map = new HashMap<>();
        map.put("member_id", member.getMemberId());
        map.put("level_id", member.getMemberLevel());
        map.put("member_level", member.getMemberLevel());
        map.put("member_label_ids", member.getMemberLabel());
        map.put("member_label", member.getMemberLabel());
        return map;
    }

    //================================通用查询


    @Override
    public FullReductionInfoVo info(Integer id) {
        MallFullReduction info = fullReductionMapper.selectById(id);
        if (ObjectUtil.isNull(info)) {
            throw new CommonException("满减活动不存在");
        }
//        if (info == null) {
//            return null;
//        }
        FullReductionInfoVo vo = new FullReductionInfoVo();
        BeanUtil.copyProperties(info, vo);
        // 查询关联商品
        // 额外查询 sku 和 goods 信息
        List<MallFullReductionGoods> relations = fullReductionGoodsMapper.selectList(
                new LambdaQueryWrapper<MallFullReductionGoods>()
                        .eq(MallFullReductionGoods::getFullReductionId, info.getId())
                        .orderByAsc(MallFullReductionGoods::getId));
        Map<String, Map<String, Object>> mallGoodsMap = selectGoodsAndSkuByFullReductionId(info.getId()).stream()
                .collect(Collectors.toMap(item -> item.get("goods_id") + "_" + item.get("sku_id"), item -> item,
                        (left, right) -> left));
        List<Map<String, Object>> goodsList = new ArrayList<>();

        var goodsDataMap = new HashMap<Integer, ArrayList<Integer>>();
        Set<Integer> displayedGoodsIds = new HashSet<>();
        for (MallFullReductionGoods relation : relations) {
            if (relation.getGoodsId() == null || relation.getSkuId() == null) {
                continue;
            }
            goodsDataMap.computeIfAbsent(relation.getGoodsId(), key -> new ArrayList<>()).add(relation.getSkuId());
            // Only one SKU is required to render each selected goods; all SKU ids are retained in goods_data.
            if (!displayedGoodsIds.add(relation.getGoodsId())) {
                continue;
            }
            Map<String, Object> item = getFullReductionGoodsInfo(info, relation, mallGoodsMap);
            if (item != null) {
                goodsList.add(item);
            }
        }
        vo.setGoods(goodsList);
        var goodsData = new ArrayList<Map<String, Object>>();
        for (var item : goodsDataMap.entrySet()) {
            var goodsId = item.getKey();
            var skuIds = item.getValue();
            goodsData.add(Map.of("goods_id", goodsId, "sku_ids", skuIds));
        }
        vo.setGoodsData(goodsData);
        return vo;
    }

    @Override
    public FullReductionInitVo init() {
        FullReductionInitVo initVo = new FullReductionInitVo();
        initVo.setStatusList(FullReductionStatusEnum.getMap());
        initVo.setTypeList(FullReductionTypeEnum.getMap());
        initVo.setGoodsTypeList(FullReductionGoodsTypeEnum.getMap());
        initVo.setRuleTypeList(FullReductionRuleTypeEnum.getMap());
        initVo.setMemberTypeList(FullReductionMemberTypeEnum.getMap());
        initVo.setPromotionTypeList(FullReductionPromotionTypeEnum.getMap());
        return initVo;
    }

    @Override
    public List<Map<String, Object>> selectGoodsAndSkuByFullReductionId(Integer fullReductionId) {
        MPJQueryWrapper<MallFullReductionGoods> wrapper = new MPJQueryWrapper<MallFullReductionGoods>()
                .select("fg.id", "fg.goods_id", "fg.sku_id", "g.goods_name", "g.goods_type", "g.goods_cover",
                        "sku.stock", "sku.sku_name", "sku.price", "sku.sku_image")
                .setAlias("fg")
                .leftJoin("?_mall_goods g ON fg.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_mall_goods_sku sku ON fg.sku_id = sku.sku_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("fg.full_reduction_id", fullReductionId);
        return fullReductionGoodsMapper.selectMaps(wrapper);
    }

    /**
     * Reads the selected SKU from its business module. Mall joins remain as the
     * compatibility fallback for historic mall activities.
     */
    private Map<String, Object> getFullReductionGoodsInfo(MallFullReduction activity,
                                                            MallFullReductionGoods relation,
                                                            Map<String, Map<String, Object>> mallGoodsMap) {
        String tradeType = relation.getTradeType();
        if (!StringUtils.hasText(tradeType)) {
            tradeType = activity.getAddon();
        }
        if (!StringUtils.hasText(tradeType)
                && coreSiteService.getAddonKeysBySiteId(activity.getSiteId()).contains("front_warehouse")) {
            // Activities created by older Java versions did not persist addon/trade_type.
            tradeType = "front_warehouse";
        }

        if (StringUtils.hasText(tradeType) && !"mall".equals(tradeType)) {
            FullReductionGoodsEventDefiner.FullReductionGoodsEvent event =
                    new FullReductionGoodsEventDefiner.FullReductionGoodsEvent();
            event.setSiteId(activity.getSiteId());
            event.setTradeType(tradeType);
            event.setSkuId(relation.getSkuId());
            event.setAuthority(true);
            event.addAppSign(tradeType);
            List<FullReductionGoodsEventDefiner.FullReductionGoodsResult> results =
                    CallbackPublisher.publishEventReturnList(event);
            if (!results.isEmpty() && results.get(0) != null) {
                FullReductionGoodsEventDefiner.FullReductionGoodsResult result = results.get(0);
                Map<String, Object> item = new HashMap<>();
                item.put("goods_id", result.getGoodsId());
                item.put("sku_id", result.getSkuId());
                item.put("sku_name", result.getSkuName());
                item.put("sku_image", result.getSkuImage());
                item.put("stock", result.getStock());
                item.put("price", result.getPrice());
                if (result.getGoodsInfo() != null) {
                    item.putAll(result.getGoodsInfo());
                }
                return item;
            }
        }

        Map<String, Object> item = mallGoodsMap.get(relation.getGoodsId() + "_" + relation.getSkuId());
        if (item == null) {
            return null;
        }
        item = new HashMap<>(item);
        item.put("goods_type_name", GoodsTypeEnum.getNameByType(String.valueOf(item.get("goods_type"))));
        return item;
    }

    @Override
    public List<Map<String, Object>> selectGoodsAndSkuBySkuList(List<Integer> idList) {
        MPJQueryWrapper<MallGoodsSku> wrapper = new MPJQueryWrapper<MallGoodsSku>()
                .select("sku.goods_id", "sku.sku_id", "sku.sku_name", "sku.sku_image", "sku.price",
                        "sku.stock", "g.goods_name", "g.goods_type")
                .setAlias("sku")
                .leftJoin("?_mall_goods g ON sku.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .in("sku.sku_id", idList);
        return skuMapper.selectMaps(wrapper);
    }

}
