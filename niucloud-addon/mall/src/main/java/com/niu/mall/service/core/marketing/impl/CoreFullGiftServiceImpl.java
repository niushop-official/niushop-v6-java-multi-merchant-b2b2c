package com.niu.mall.service.core.marketing.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLabel;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.mapper.member.MemberLabelMapper;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.mall.entity.fullactive.MallFullGift;
import com.niu.mall.entity.fullactive.MallFullGiftGoods;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.enums.fullactive.FullGiftGoodsTypeEnum;
import com.niu.mall.enums.fullactive.FullGiftMemberTypeEnum;
import com.niu.mall.enums.fullactive.FullGiftRuleTypeEnum;
import com.niu.mall.enums.fullactive.FullGiftStatusEnum;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.mapper.fullactive.FullGiftGoodsMapper;
import com.niu.mall.mapper.fullactive.FullGiftMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.service.admin.marketing.param.FullGiftParam;
import com.niu.mall.service.admin.marketing.vo.FullGiftInfoVo;
import com.niu.mall.service.admin.marketing.vo.FullGiftInitVo;
import com.niu.mall.service.core.marketing.ICoreFullGiftService;
import com.niu.mall.service.core.marketing.param.FullGiftCalculateParam;
import com.niu.mall.service.core.marketing.vo.FullGiftCalculateVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CoreFullGiftServiceImpl implements ICoreFullGiftService {

    @Resource
    private FullGiftMapper fullGiftMapper;

    @Resource
    private FullGiftGoodsMapper fullGiftGoodsMapper;

    @Resource
    private MallGoodsSkuMapper skuMapper;

    @Resource
    private MallGoodsMapper goodsMapper;

    @Resource
    private MemberLabelMapper memberLabelMapper;

    @Resource
    private MemberLevelMapper memberLevelMapper;

    @Override
    public List<Map<String, Object>> getActiveFullGift(Integer siteId, List<Integer> goodsIds, Integer memberLevelId, BigDecimal orderTotal) {
        Long currentTime = System.currentTimeMillis() / 1000;

        MPJLambdaWrapper<MallFullGift> queryWrapper = new MPJLambdaWrapper<MallFullGift>()
                .selectAll(MallFullGift.class)
                .eq(MallFullGift::getSiteId, siteId)
                .eq(MallFullGift::getStatus, FullGiftStatusEnum.PROCESS.getStatus())
                .le(MallFullGift::getStartTime, currentTime)
                .ge(MallFullGift::getEndTime, currentTime)
                .orderByAsc(MallFullGift::getSort)
                .orderByDesc(MallFullGift::getCreateTime);

        List<MallFullGift> fullGiftList = fullGiftMapper.selectList(queryWrapper);

        if (ObjectUtil.isEmpty(fullGiftList)) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> activeFullGiftList = new ArrayList<>();

        for (MallFullGift fullGift : fullGiftList) {
            if (checkMemberCondition(fullGift, memberLevelId)) {
                List<Integer> activeGoodsIds = getActiveGoodsIds(fullGift.getId());

                if (checkGoodsCondition(fullGift, goodsIds, activeGoodsIds)) {
                    Map<String, Object> giftInfo = BeanUtil.beanToMap(fullGift);
                    giftInfo.put("goods_ids", activeGoodsIds);
                    activeFullGiftList.add(giftInfo);
                }
            }
        }

        return activeFullGiftList;
    }

    @Override
    public Map<String, Object> calculateFullGift(Integer siteId, List<Integer> goodsIds, Integer memberLevelId, BigDecimal orderTotal) {
        List<Map<String, Object>> activeFullGiftList = getActiveFullGift(siteId, goodsIds, memberLevelId, orderTotal);

        if (ObjectUtil.isEmpty(activeFullGiftList)) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> matchedGifts = new ArrayList<>();

        for (Map<String, Object> giftInfo : activeFullGiftList) {
            String ruleJson = (String) giftInfo.get("ruleJson");
            List<Map> rawRules = JSONUtil.toList(ruleJson, Map.class);
            List<Map<String, Object>> rules = rawRules.stream()
                    .map(m -> (Map<String, Object>) m)
                    .collect(Collectors.toList());

            for (Map<String, Object> rule : rules) {
                BigDecimal count = new BigDecimal(rule.get("count").toString());
                if (orderTotal.compareTo(count) >= 0) {
                    Map<String, Object> matchedGift = new HashMap<>();
                    matchedGift.put("full_gift_id", giftInfo.get("fullGiftId"));
                    matchedGift.put("title", giftInfo.get("title"));
                    matchedGift.put("rule_type", giftInfo.get("ruleType"));
                    matchedGift.put("rule", rule);
                    matchedGifts.add(matchedGift);
                }
            }
        }

        if (ObjectUtil.isNotEmpty(matchedGifts)) {
            result.put("full_gift_list", matchedGifts);
        }

        return result;
    }

    @Override
    public Map<String, Object> checkGoodsFullGift(Integer siteId, List<Integer> goodsIds) {
        Map<String, Object> result = new HashMap<>();
        List<Integer> conflictGoodsIds = new ArrayList<>();

        Long currentTime = System.currentTimeMillis() / 1000;

        MPJLambdaWrapper<MallFullGift> queryWrapper = new MPJLambdaWrapper<MallFullGift>()
                .selectAll(MallFullGift.class)
                .eq(MallFullGift::getSiteId, siteId)
                .eq(MallFullGift::getStatus, FullGiftStatusEnum.PROCESS.getStatus())
                .le(MallFullGift::getStartTime, currentTime)
                .ge(MallFullGift::getEndTime, currentTime);

        List<MallFullGift> fullGiftList = fullGiftMapper.selectList(queryWrapper);

        for (MallFullGift fullGift : fullGiftList) {
            if (FullGiftGoodsTypeEnum.SELECTED.getType().equals(fullGift.getGoodsType())) {
                List<Integer> activeGoodsIds = getActiveGoodsIds(fullGift.getId());

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

    private boolean checkMemberCondition(MallFullGift fullGift, Integer memberLevelId) {
        if (FullGiftMemberTypeEnum.LABEL.getType().equals(fullGift.getMemberType())) {
            return true;
        }

        if (FullGiftMemberTypeEnum.LEVEL.getType().equals(fullGift.getMemberType())) {
            String memberTypeJson = fullGift.getMemberTypeJson();
            if (ObjectUtil.isNotEmpty(memberTypeJson)) {
                List<Integer> memberLevelIds = JSONUtil.toList(memberTypeJson, Integer.class);
                return memberLevelIds.contains(memberLevelId);
            }
        }

        return false;
    }

    private boolean checkGoodsCondition(MallFullGift fullGift, List<Integer> goodsIds, List<Integer> activeGoodsIds) {
        if (FullGiftGoodsTypeEnum.ALL.getType().equals(fullGift.getGoodsType())) {
            return true;
        }

        if (FullGiftGoodsTypeEnum.SELECTED.getType().equals(fullGift.getGoodsType())) {
            return goodsIds.stream().anyMatch(activeGoodsIds::contains);
        }

        if (FullGiftGoodsTypeEnum.SELECTED_NOT.getType().equals(fullGift.getGoodsType())) {
            return goodsIds.stream().noneMatch(activeGoodsIds::contains);
        }

        return false;
    }

    private List<Integer> getActiveGoodsIds(Integer fullGiftId) {
        LambdaQueryWrapper<MallFullGiftGoods> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(MallFullGiftGoods::getFullGiftId, fullGiftId);
        List<MallFullGiftGoods> fullGiftGoodsList = fullGiftGoodsMapper.selectList(queryWrapper);

        return fullGiftGoodsList.stream()
                .map(MallFullGiftGoods::getGoodsId)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<FullGiftCalculateVo> getFullGiftInfo1(FullGiftCalculateParam calculateParam) {
        Integer siteId = calculateParam.getSiteId();
        List<FullGiftCalculateParam.GoodsSkuData> goodsSkuData = calculateParam.getGoodsSkuData();
        Integer memberId = calculateParam.getMemberId();
        boolean checkRule = calculateParam.isCheckRule();

        if (ObjectUtil.isEmpty(goodsSkuData)) {
            return new ArrayList<>();
        }

        List<Integer> goodsIds = goodsSkuData.stream()
                .map(FullGiftCalculateParam.GoodsSkuData::getGoodsId)
                .distinct()
                .collect(Collectors.toList());

        BigDecimal orderTotal = goodsSkuData.stream()
                .map(FullGiftCalculateParam.GoodsSkuData::getPayMoney)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long currentTime = System.currentTimeMillis() / 1000;

        MPJLambdaWrapper<MallFullGift> queryWrapper = new MPJLambdaWrapper<MallFullGift>()
                .selectAll(MallFullGift.class)
                .eq(MallFullGift::getSiteId, siteId)
                .eq(MallFullGift::getStatus, FullGiftStatusEnum.PROCESS.getStatus())
                .le(MallFullGift::getStartTime, currentTime)
                .ge(MallFullGift::getEndTime, currentTime)
                .orderByAsc(MallFullGift::getSort)
                .orderByDesc(MallFullGift::getCreateTime);

        List<MallFullGift> fullGiftList = fullGiftMapper.selectList(queryWrapper);

        if (ObjectUtil.isEmpty(fullGiftList)) {
            return new ArrayList<>();
        }

        List<FullGiftCalculateVo> result = new ArrayList<>();

        for (MallFullGift fullGift : fullGiftList) {
            List<Integer> activeGoodsIds = getActiveGoodsIds(fullGift.getId());

            if (!checkGoodsCondition(fullGift, goodsIds, activeGoodsIds)) {
                continue;
            }

            List<FullGiftCalculateParam.GoodsSkuData> matchedGoods = getMatchedGoods(goodsSkuData, activeGoodsIds, fullGift.getGoodsType());

            if (ObjectUtil.isEmpty(matchedGoods)) {
                continue;
            }

            BigDecimal matchedTotal = matchedGoods.stream()
                    .map(FullGiftCalculateParam.GoodsSkuData::getPayMoney)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String ruleJson = fullGift.getRuleJson();
            List<Map> rawRules = JSONUtil.toList(ruleJson, Map.class);
            List<Map<String, Object>> rules = rawRules.stream()
                    .map(m -> (Map<String, Object>) m)
                    .collect(Collectors.toList());

            for (Map<String, Object> rule : rules) {
                BigDecimal count = new BigDecimal(rule.get("count").toString());
                if (matchedTotal.compareTo(count) >= 0) {
                    FullGiftCalculateVo calculateVo = new FullGiftCalculateVo();
                    calculateVo.setFullGiftId(fullGift.getId());
                    calculateVo.setTitle(fullGift.getTitle());
                    calculateVo.setMatchedTotal(matchedTotal);

                    List<Map> rawGiftDataList = JSONUtil.toList(rule.get("gift_data").toString(), Map.class);
                    List<Map<String, Object>> giftDataList = rawGiftDataList.stream()
                            .map(m -> (Map<String, Object>) m)
                            .collect(Collectors.toList());
                    List<FullGiftCalculateVo.GiftSkuData> giftList = new ArrayList<>();

                    for (Map<String, Object> giftData : giftDataList) {
                        FullGiftCalculateVo.GiftSkuData giftSkuData = new FullGiftCalculateVo.GiftSkuData();
                        giftSkuData.setGoodsId(Integer.parseInt(giftData.get("goods_id").toString()));
                        giftSkuData.setSkuId(Integer.parseInt(giftData.get("sku_id").toString()));
                        giftSkuData.setNum(Integer.parseInt(giftData.get("num").toString()));
                        giftSkuData.setSkuName(giftData.get("sku_name") != null ? giftData.get("sku_name").toString() : "");
                        giftSkuData.setSkuImage(giftData.get("sku_image") != null ? giftData.get("sku_image").toString() : "");
                        giftList.add(giftSkuData);
                    }
                    calculateVo.setGiftList(giftList);

                    List<FullGiftCalculateVo.MatchedGoods> matchedGoodsList = new ArrayList<>();
                    for (FullGiftCalculateParam.GoodsSkuData skuData : matchedGoods) {
                        FullGiftCalculateVo.MatchedGoods matchedGoodsItem = new FullGiftCalculateVo.MatchedGoods();
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

    //===================================同步php

    private static final List<String> CHECK_PRIORITY = Arrays.asList(
            FullGiftGoodsTypeEnum.SELECTED.getType(),
            FullGiftGoodsTypeEnum.SELECTED_NOT.getType(),
            FullGiftGoodsTypeEnum.ALL.getType()
    );

    @Resource
    private MemberMapper memberMapper;

    // 线程局部变量存储请求上下文（避免成员变量冲突）
    private final ThreadLocal<Integer> currentSiteId = new ThreadLocal<>();
    private final ThreadLocal<Integer> currentMemberId = new ThreadLocal<>();
    private final ThreadLocal<Boolean> currentCheckRule = new ThreadLocal<>();
    private final ThreadLocal<List<FullGiftCalculateParam.GoodsSkuData>> currentGoodsList = new ThreadLocal<>();
    private final ThreadLocal<Set<String>> usedSkuKeys = ThreadLocal.withInitial(HashSet::new);

    @Override
    public List<FullGiftCalculateVo> getFullGiftInfo(FullGiftCalculateParam calculateParam) {
        // 初始化上下文
        currentSiteId.set(calculateParam.getSiteId());
        currentMemberId.set(calculateParam.getMemberId());
        currentCheckRule.set(calculateParam.isCheckRule());
        usedSkuKeys.get().clear();

        // 格式化商品数据（只保留有效商品）
        List<FullGiftCalculateParam.GoodsSkuData> formattedGoods = formatGoodsSkuData(calculateParam.getGoodsSkuData());
        if (ObjectUtil.isEmpty(formattedGoods)) {
            return Collections.emptyList();
        }
        currentGoodsList.set(formattedGoods);

        // 获取会员信息（用于权限校验）
        Map<String, Object> memberInfo = getMemberInfo(calculateParam.getMemberId());

        // 获取所有生效活动并按优先级匹配
        Map<String, List<FullGiftInfoVo>> groupedActiveList = getEffectiveFullGiftList();
        List<FullGiftCalculateVo> result = new ArrayList<>();

        for (String goodsType : CHECK_PRIORITY) {
            List<FullGiftInfoVo> actives = groupedActiveList.get(goodsType);
            if (ObjectUtil.isNotEmpty(actives)) {
                List<FullGiftCalculateVo> matched = checkActiveByType(actives, goodsType, memberInfo);
                if (ObjectUtil.isNotEmpty(matched)) {
                    result.addAll(matched);
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> getFullGiftInfoOnlyCheck(Integer goodsId, Integer skuId,
                                                        Integer memberId, Integer siteId) {
        long now = System.currentTimeMillis() / 1000;
        // 查询所有生效活动
        List<MallFullGift> activeList = fullGiftMapper.selectList(
                new LambdaQueryWrapper<MallFullGift>()
                        .eq(MallFullGift::getSiteId, siteId)
                        .eq(MallFullGift::getStatus, FullGiftStatusEnum.PROCESS.getStatus())
                        .le(MallFullGift::getStartTime, now)
                        .ge(MallFullGift::getEndTime, now)
        );
        if (ObjectUtil.isEmpty(activeList)) {
            return Collections.emptyMap();
        }

//        List<FullGiftInfoVo> infos = new ArrayList<>();
//        for (MallFullGift item : activeList) {
//            FullGiftInfoVo vo = BeanUtil.copyProperties(item, FullGiftInfoVo.class, "ruleJson");
//            infos.add(vo);
//        }

        Map<String, Object> memberInfo = getMemberInfo(memberId);
        String skuKey = goodsId + "_" + skuId;

        // 获取所有活动关联的SKU（批量）
        List<Integer> activeIds = activeList.stream().map(MallFullGift::getId).collect(Collectors.toList());
        List<MallFullGiftGoods> allRelation = fullGiftGoodsMapper.selectList(
                new LambdaQueryWrapper<MallFullGiftGoods>()
                        .eq(MallFullGiftGoods::getSiteId, siteId)
                        .in(MallFullGiftGoods::getFullGiftId, activeIds)
        );
        Map<Integer, Set<String>> relationSkuMap = new HashMap<>();
        for (MallFullGiftGoods goods : allRelation) {
            relationSkuMap.computeIfAbsent(goods.getFullGiftId(), k -> new HashSet<>())
                    .add(goods.getGoodsId() + "_" + goods.getSkuId());
        }

        for (MallFullGift item : activeList) {
            FullGiftInfoVo active = BeanUtil.copyProperties(item, FullGiftInfoVo.class, "ruleJson");
            // 会员权限校验
            if (!checkMemberPermission(active, memberInfo)) {
                continue;
            }
            Set<String> relationSkus = relationSkuMap.getOrDefault(active.getId(), Collections.emptySet());
            boolean match = false;
            switch (active.getGoodsType()) {
                case "selected" -> match = relationSkus.contains(skuKey);
                case "selected_not" -> match = !relationSkus.contains(skuKey);
                case "all" -> match = true;
                default -> {
                }
            }
            if (match) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", active.getId());
                result.put("title", active.getTitle());
                result.put("goods_type", active.getGoodsType());
                result.put("member_type", active.getMemberType());
                result.put("member_type_json", active.getMemberTypeJson());
                result.put("rule_type", item.getRuleType());
                result.put("rule_json", item.getRuleJson());
                result.put("start_time", item.getStartTime());
                result.put("end_time", item.getEndTime());
                return result;
            }
        }
        return Collections.emptyMap();
    }

    // ==================== 私有核心方法 ====================

    /**
     * 格式化商品SKU数据（过滤无效商品）
     */
    private List<FullGiftCalculateParam.GoodsSkuData> formatGoodsSkuData(List<FullGiftCalculateParam.GoodsSkuData> goodsSkuData) {
        if (ObjectUtil.isEmpty(goodsSkuData)) {
            return Collections.emptyList();
        }
        List<FullGiftCalculateParam.GoodsSkuData> result = new ArrayList<>();
        for (FullGiftCalculateParam.GoodsSkuData item : goodsSkuData) {
            if (item.getGoodsId() == null || item.getSkuId() == null ||
                    item.getPayMoney() == null || item.getPayMoney().compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }
            item.setSkuKey(item.getGoodsId() + "_" + item.getSkuId());
            result.add(item);
        }
        return result;
    }

    /**
     * 获取会员信息（转换为Map，便于取标签/等级）
     */
    private Map<String, Object> getMemberInfo(Integer memberId) {
        if (memberId == null || memberId <= 0) {
            return Collections.emptyMap();
        }
        // 假设 member 表有 member_label_ids, level_id 等字段
        // 这里简化：实际需根据数据库字段调整
        Map<String, Object> info = new HashMap<>();
        // 伪代码，实际需查询
        Member member = memberMapper.selectById(memberId);
        info.put("level_id", member.getMemberLevel());
        info.put("member_label_ids", member.getMemberLabel());
        return info;
    }

    /**
     * 获取当前生效的满赠活动列表（按商品类型分组，组内按 sort 排序）
     */
    private Map<String, List<FullGiftInfoVo>> getEffectiveFullGiftList() {
        long now = System.currentTimeMillis() / 1000;
        Integer siteId = currentSiteId.get();

        List<MallFullGift> list = fullGiftMapper.selectList(
                new LambdaQueryWrapper<MallFullGift>()
                        .eq(MallFullGift::getSiteId, siteId)
                        .eq(MallFullGift::getStatus, FullGiftStatusEnum.PROCESS.getStatus())
                        .le(MallFullGift::getStartTime, now)
                        .ge(MallFullGift::getEndTime, now)
        );
        if (ObjectUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }

        List<FullGiftInfoVo> infos = new ArrayList<>();
        for (MallFullGift item : list) {
            FullGiftInfoVo vo = BeanUtil.copyProperties(item, FullGiftInfoVo.class, "ruleJson");
            // 补充最小门槛金额 minThreshold
            List<FullGiftParam.Rule> ruleJson = JSONUtil.toList(item.getRuleJson(), FullGiftParam.Rule.class);
            vo.setRuleJson(ruleJson);
            vo.setMinThreshold(getMinThreshold(item.getRuleJson()));
            infos.add(vo);
        }

        // 按商品类型分组
        Map<String, List<FullGiftInfoVo>> grouped = infos.stream()
                .collect(Collectors.groupingBy(FullGiftInfoVo::getGoodsType));
        // 组内按 sort 升序排序
        for (List<FullGiftInfoVo> group : grouped.values()) {
            group.sort(Comparator.comparing(FullGiftInfoVo::getSort, Comparator.nullsLast(Integer::compareTo)));
        }
        return grouped;
    }

    /**
     * 解析规则 JSON，获取最小门槛金额
     */
    private BigDecimal getMinThreshold(String ruleJson) {
        if (StrUtil.isBlank(ruleJson)) {
            return BigDecimal.ZERO;
        }
        try {
            JSONArray rules = JSONUtil.parseArray(ruleJson);
            BigDecimal min = null;
            for (Object obj : rules) {
                JSONObject rule = (JSONObject) obj;
                BigDecimal amount = rule.getBigDecimal("amount");
                if (amount != null && amount.compareTo(BigDecimal.ZERO) > 0) {
                    if (min == null || amount.compareTo(min) < 0) {
                        min = amount;
                    }
                }
            }
            return min == null ? BigDecimal.ZERO : min;
        } catch (Exception e) {
            log.error("解析 rule_json 失败: {}", ruleJson, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 按商品类型校验活动（核心）
     */
    private List<FullGiftCalculateVo> checkActiveByType(List<FullGiftInfoVo> actives,
                                                        String goodsType,
                                                        Map<String, Object> memberInfo) {
        List<FullGiftCalculateVo> result = new ArrayList<>();
        List<FullGiftCalculateParam.GoodsSkuData> allGoods = currentGoodsList.get();
        Set<String> usedKeys = usedSkuKeys.get();

        for (FullGiftInfoVo active : actives) {
            // 1. 会员权限校验
            if (!checkMemberPermission(active, memberInfo)) {
                continue;
            }
            // 2. 获取该活动允许参与的商品（排除已使用）
            List<FullGiftCalculateParam.GoodsSkuData> matchedGoods = getMatchedGoodsForActive(active.getId(), goodsType, allGoods, usedKeys);
            if (ObjectUtil.isEmpty(matchedGoods)) {
                continue;
            }
            // 3. 累计匹配商品的实付金额
            BigDecimal matchedTotal = matchedGoods.stream()
                    .map(FullGiftCalculateParam.GoodsSkuData::getPayMoney)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // 若设置了不校验规则，则直接认为达标（matchedTotal = 最小门槛）
            if (!currentCheckRule.get()) {
                matchedTotal = active.getMinThreshold();
            }
            if (matchedTotal.compareTo(active.getMinThreshold()) < 0) {
                continue;
            }
            // 4. 计算赠品（阶梯/循环）
            GiftCalcResult giftResult = calcGiftByRuleType(active, matchedTotal);
            if (!giftResult.isPass() || ObjectUtil.isEmpty(giftResult.getGiftList())) {
                continue;
            }
            // 5. 锁定商品（标记为已使用）
            for (FullGiftCalculateParam.GoodsSkuData goods : matchedGoods) {
                usedKeys.add(goods.getSkuKey());
            }
            // 6. 组装返回VO
            FullGiftCalculateVo vo = new FullGiftCalculateVo();
            BeanUtil.copyProperties(active, vo);
            vo.setMatchedTotal(matchedTotal);
            vo.setMatchedGoods(convertToMatchedGoods(matchedGoods));
            List<FullGiftCalculateVo.GiftSkuData> skuDatas = BeanUtil.copyToList(giftResult.getGiftList(), FullGiftCalculateVo.GiftSkuData.class);
            vo.setGiftList(skuDatas);
            result.add(vo);
        }
        return result;
    }

    /**
     * 获取单个活动允许参与的商品列表（排除已归属商品）
     */
    private List<FullGiftCalculateParam.GoodsSkuData> getMatchedGoodsForActive(Integer giftId,
                                                                               String goodsType,
                                                                               List<FullGiftCalculateParam.GoodsSkuData> allGoods,
                                                                               Set<String> usedKeys) {
        // 查询活动关联的SKU KEY集合
        Set<String> relationSkus = new HashSet<>();
        if (!FullGiftGoodsTypeEnum.ALL.getType().equals(goodsType)) {
            List<MallFullGiftGoods> goodsList = fullGiftGoodsMapper.selectList(
                    new LambdaQueryWrapper<MallFullGiftGoods>()
                            .eq(MallFullGiftGoods::getFullGiftId, giftId)
                            .eq(MallFullGiftGoods::getSiteId, currentSiteId.get())
            );
            for (MallFullGiftGoods g : goodsList) {
                relationSkus.add(g.getGoodsId() + "_" + g.getSkuId());
            }
        }

        List<FullGiftCalculateParam.GoodsSkuData> matched = new ArrayList<>();
        for (FullGiftCalculateParam.GoodsSkuData item : allGoods) {
            if (usedKeys.contains(item.getSkuKey())) {
                continue;
            }
            boolean match = false;
            switch (goodsType) {
                case "selected" -> match = relationSkus.contains(item.getSkuKey());
                case "selected_not" -> match = !relationSkus.contains(item.getSkuKey());
                case "all" -> match = true;
                default -> {
                }
            }
            if (match) {
                matched.add(item);
            }
        }
        return matched;
    }

    /**
     * 根据规则类型计算赠品（阶梯/循环）
     */
    private GiftCalcResult calcGiftByRuleType(FullGiftInfoVo active, BigDecimal matchedTotal) {
        List<FullGiftParam.Rule> ruleJson = active.getRuleJson();
        if (ruleJson == null || ruleJson.size() == 0) {
            return new GiftCalcResult(false, Collections.emptyList());
        }

        String ruleType = active.getRuleType();
        List<FullGiftParam.GiftSkuData> giftList = new ArrayList<>();

        // 阶梯赠：取最高达标档位
        if (FullGiftRuleTypeEnum.LADDER.getType().equals(ruleType)) {
            int maxLevel = -1;
            FullGiftParam.Rule bestRule = null;
            for (FullGiftParam.Rule rule : ruleJson) {

                BigDecimal threshold = rule.getAmount();
                int level = rule.getLevel();
                if (matchedTotal.compareTo(threshold) >= 0 && level > maxLevel) {
                    maxLevel = level;
                    bestRule = rule;
                }
            }
            if (bestRule != null) {
                giftList = parseGiftSkuData(bestRule.getGiftSkuData());
            }
        }
        // 循环赠：每满一次叠加数量
        else if (FullGiftRuleTypeEnum.CYCLE.getType().equals(ruleType)) {
            FullGiftParam.Rule cycleRule = ruleJson.get(0);
            BigDecimal threshold = cycleRule.getAmount();
            if (threshold.compareTo(BigDecimal.ZERO) > 0 && matchedTotal.compareTo(threshold) >= 0) {
                long giftTimes = matchedTotal.divide(threshold, 0, RoundingMode.DOWN).longValue();
                List<FullGiftParam.GiftSkuData> baseGift = parseGiftSkuData(cycleRule.getGiftSkuData());
                for (FullGiftParam.GiftSkuData gift : baseGift) {
                    gift.setNum(gift.getNum() * (int) giftTimes);
                }
                giftList = baseGift;
            }
        }

        if (ObjectUtil.isEmpty(giftList)) {
            return new GiftCalcResult(false, Collections.emptyList());
        }
        return new GiftCalcResult(true, giftList);
    }

    /**
     * 解析赠品SKU数据，并过滤下架商品
     */
    private List<FullGiftParam.GiftSkuData> parseGiftSkuData(List<Map<String, Object>> giftSkuDataArray) {
        if (ObjectUtil.isEmpty(giftSkuDataArray)) {
            return Collections.emptyList();
        }
        // 收集所有赠品项（先展开）
        List<FullGiftParam.GiftSkuData> rawGifts = new ArrayList<>();
        for (Object obj : giftSkuDataArray) {
            JSONObject goodsItem = JSONUtil.parseObj(obj);
            Integer goodsId = goodsItem.getInt("goods_id");
            JSONArray skuList = goodsItem.getJSONArray("sku_list");
            if (skuList == null) continue;
            for (Object skuObj : skuList) {
                JSONObject skuItem = JSONUtil.parseObj(skuObj);
                var gsd = new FullGiftParam.GiftSkuData();
                gsd.setGoodsId(goodsId);
                gsd.setSkuId(skuItem.getInt("sku_id"));
                gsd.setNum(skuItem.getInt("num", 1));
                rawGifts.add(gsd);
            }
        }
        if (rawGifts.isEmpty()) {
            return Collections.emptyList();
        }

        // 批量查询SKU信息
        List<Integer> skuIds = rawGifts.stream().map(FullGiftParam.GiftSkuData::getSkuId).toList();
        List<Integer> goodsIds = rawGifts.stream().map(FullGiftParam.GiftSkuData::getGoodsId).toList();
        List<MallGoodsSku> skuList = skuMapper.selectList(
                new LambdaQueryWrapper<MallGoodsSku>().in(MallGoodsSku::getSkuId, skuIds)
        );
        Map<Integer, MallGoods> goodsMap = goodsMapper.selectList(
                new LambdaQueryWrapper<MallGoods>().in(MallGoods::getGoodsId, goodsIds)
        ).stream().collect(Collectors.toMap(MallGoods::getGoodsId, g -> g));
        Map<Integer, MallGoodsSku> skuMap = skuList.stream()
                .collect(Collectors.toMap(MallGoodsSku::getSkuId, s -> s));

        // 过滤下架商品（status != ONLINE）
        List<FullGiftParam.GiftSkuData> result = new ArrayList<>();
        for (FullGiftParam.GiftSkuData gift : rawGifts) {
            MallGoodsSku sku = skuMap.get(gift.getSkuId());
            MallGoods goods = goodsMap.get(gift.getGoodsId());
            if (sku == null) continue;
            if (goods == null || !GoodsStatusEnum.ONLINE.getStatus().equals(goods.getStatus())) {
                // 过滤下架赠品
                continue;
            }
            gift.setSkuName(sku.getSkuName());
            gift.setSkuImage(sku.getSkuImage());
            gift.setPrice(sku.getPrice());
            gift.setStock(sku.getStock());
            gift.setGoodsName(goods.getGoodsName());
            gift.setGoodsTypeName(GoodsTypeEnum.getNameByType(goods.getGoodsType()));
            result.add(gift);
        }
        return result;
    }

    /**
     * 会员权限校验（支持 all / label / level）
     */
    private boolean checkMemberPermission(FullGiftInfoVo active, Map<String, Object> memberInfo) {
        String memberType = active.getMemberType();
        // 全部会员或未登录用户
        if (FullGiftMemberTypeEnum.ALL.getType().equals(memberType)) {
            return true;
        }
        if (memberInfo == null || memberInfo.isEmpty()) {
            return false;
        }
        String memberTypeJson = active.getMemberTypeJson();
        if (StrUtil.isBlank(memberTypeJson)) {
            return false;
        }
        if (FullGiftMemberTypeEnum.LABEL.getType().equals(memberType)) {
            Set<Integer> labelIds = parseIds(memberTypeJson);
            Set<Integer> memberLabelIds = getMemberLabelIds(memberInfo);
            return !Collections.disjoint(labelIds, memberLabelIds);
        }
        if (FullGiftMemberTypeEnum.LEVEL.getType().equals(memberType)) {
            Set<Integer> levelIds = parseIds(memberTypeJson);
            Integer memberLevelId = getMemberLevelId(memberInfo);
            return memberLevelId != null && levelIds.contains(memberLevelId);
        }
        return false;
    }

    private Set<Integer> parseIds(String str) {
        if (StrUtil.isBlank(str)) return Collections.emptySet();
        JSONArray arr = JSONUtil.parseArray(str);
        Set<Integer> ids = new HashSet<>();
        for (Object obj : arr) {
            if (obj instanceof Number) {
                ids.add(((Number) obj).intValue());
            } else {
                ids.add(Integer.parseInt(obj.toString()));
            }
        }
        return ids;
    }

    private Set<Integer> getMemberLabelIds(Map<String, Object> memberInfo) {
        // 根据实际数据库字段获取会员标签ID集合
        Object labels = memberInfo.get("member_label_ids");
        if (labels == null) return Collections.emptySet();
        if (labels instanceof Collection) {
            return new HashSet<>((Collection<Integer>) labels);
        }
        return parseIds(labels.toString());
    }

    private Integer getMemberLevelId(Map<String, Object> memberInfo) {
        Object level = memberInfo.get("level_id");
        if (level == null) return null;
        return Integer.parseInt(level.toString());
    }

    private List<FullGiftCalculateVo.MatchedGoods> convertToMatchedGoods(List<FullGiftCalculateParam.GoodsSkuData> goodsList) {
        List<FullGiftCalculateVo.MatchedGoods> result = new ArrayList<>();
        for (FullGiftCalculateParam.GoodsSkuData g : goodsList) {
            FullGiftCalculateVo.MatchedGoods mg = new FullGiftCalculateVo.MatchedGoods();
            mg.setGoodsId(g.getGoodsId());
            mg.setSkuId(g.getSkuId());
            mg.setPayMoney(g.getPayMoney());
            mg.setSkuKey(g.getSkuKey());
            result.add(mg);
        }
        return result;
    }

    // 内部结果类
    private static class GiftCalcResult {
        private final boolean pass;
        private final List<FullGiftParam.GiftSkuData> giftList;

        public GiftCalcResult(boolean pass, List<FullGiftParam.GiftSkuData> giftList) {
            this.pass = pass;
            this.giftList = giftList;
        }

        public boolean isPass() {
            return pass;
        }

        public List<FullGiftParam.GiftSkuData> getGiftList() {
            return giftList;
        }
    }

    //================================通用查询

    @Override
    public FullGiftInitVo init() {
        FullGiftInitVo initVo = new FullGiftInitVo();
        initVo.setStatusList(FullGiftStatusEnum.getMap());
        initVo.setGoodsTypeList(FullGiftGoodsTypeEnum.getMap());
        initVo.setRuleTypeList(FullGiftRuleTypeEnum.getMap());
        initVo.setMemberTypeList(FullGiftMemberTypeEnum.getMap());
        return initVo;
    }

    @Override
    public FullGiftInfoVo info(Integer id) {
        MallFullGift fullGift = fullGiftMapper.selectById(id);
        if (ObjectUtil.isNull(fullGift)) {
            throw new CommonException("满赠活动不存在");
        }

        // 1. 复制值 ruleJson需要另外处理
        FullGiftInfoVo vo = BeanUtil.copyProperties(fullGift, FullGiftInfoVo.class, "ruleJson");
        // 2. 查询关联商品 额外查询 sku 和 goods 信息
        List<Map<String, Object>> goodsList = this.selectGoodsAndSkuByFullGiftId(fullGift.getId());
        var goodsDataMap = new HashMap<Integer, ArrayList<Integer>>();
        for (var item : goodsList) {
            item.put("goods_type_name", GoodsTypeEnum.getNameByType(String.valueOf(item.get("goods_type"))));
            goodsDataMap.computeIfAbsent(Integer.parseInt(String.valueOf(item.get("goods_id"))), k -> new ArrayList<>())
                    .add(Integer.parseInt(String.valueOf(item.get("sku_id"))));
        }
        vo.setGoods(goodsList);
        var goodsData = new ArrayList<Map<String, Object>>();
        for (var item : goodsDataMap.entrySet()) {
            var goodsId = item.getKey();
            var skuIds = item.getValue();
            goodsData.add(Map.of("goods_id", goodsId, "sku_ids", skuIds));
        }
        vo.setGoodsData(goodsData);

        // 3. 查询赠品 sku和 goods 信息, 组装ruleJson字段
        List<FullGiftParam.Rule> ruleJson = JSONUtil.toList(fullGift.getRuleJson(), FullGiftParam.Rule.class); // 假设工具类
        for (var rule : ruleJson) {
            List<Map<String, Object>> giftSkuData = rule.getGiftSkuData();
            List<Map<String, Object>> returnGiftSkuList = new ArrayList<>();
            for (Map<String, Object> gift : giftSkuData) {
                List<Map<String, Object>> skuList = (List<Map<String, Object>>) gift.get("sku_list");
                Map<Integer, Integer> skuNumMap = skuList.stream()
                        .collect(Collectors.toMap(s -> (Integer) s.get("sku_id"), s -> (Integer) s.get("num")));
                List<Integer> skuIds = new ArrayList<>(skuNumMap.keySet());
                List<Map<String, Object>> skus = this.selectGoodsAndSkuBySkuList(skuIds);
                for (var sku : skus) {
                    sku.put("goods_type_name", GoodsTypeEnum.getNameByType(String.valueOf(sku.get("goods_type"))));
                    sku.put("num", skuNumMap.get(Integer.parseInt(String.valueOf(sku.get("sku_id")))));
                    returnGiftSkuList.add(sku);
                }
            }
            rule.setGiftSkuData(returnGiftSkuList);
        }
        vo.setRuleJson(ruleJson);
        fillMemberTypeInfo(fullGift, vo);
        return vo;
    }

    /**
     * 与 PHP FullGift 模型 append member_type_info 一致
     */
    private void fillMemberTypeInfo(MallFullGift fullGift, FullGiftInfoVo vo) {
        List<Map<String, Object>> list = new ArrayList<>();
        String memberType = fullGift.getMemberType();
        String raw = fullGift.getMemberTypeJson();
        if (!FullGiftMemberTypeEnum.LABEL.getType().equals(memberType)
                && !FullGiftMemberTypeEnum.LEVEL.getType().equals(memberType)) {
            vo.setMemberTypeInfo(list);
            return;
        }
        if (StrUtil.isBlank(raw) || "[]".equals(raw.trim())) {
            vo.setMemberTypeInfo(list);
            return;
        }
        JSONArray arr = JSONUtil.parseArray(raw);
        if (arr == null || arr.isEmpty()) {
            vo.setMemberTypeInfo(list);
            return;
        }
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            ids.add(arr.getInt(i));
        }
        if (ids.isEmpty()) {
            vo.setMemberTypeInfo(list);
            return;
        }
        Integer siteId = fullGift.getSiteId();
        if (FullGiftMemberTypeEnum.LABEL.getType().equals(memberType)) {
            List<MemberLabel> labels = memberLabelMapper.selectList(
                    new LambdaQueryWrapper<MemberLabel>()
                            .eq(MemberLabel::getSiteId, siteId)
                            .in(MemberLabel::getLabelId, ids)
                            .select(MemberLabel::getLabelId, MemberLabel::getLabelName));
            for (MemberLabel l : labels) {
                Map<String, Object> row = new HashMap<>();
                row.put("label_id", l.getLabelId());
                row.put("label_name", l.getLabelName());
                list.add(row);
            }
        } else {
            List<MemberLevel> levels = memberLevelMapper.selectList(
                    new LambdaQueryWrapper<MemberLevel>()
                            .eq(MemberLevel::getSiteId, siteId)
                            .in(MemberLevel::getLevelId, ids)
                            .select(MemberLevel::getLevelId, MemberLevel::getLevelName));
            for (MemberLevel lvl : levels) {
                Map<String, Object> row = new HashMap<>();
                row.put("level_id", lvl.getLevelId());
                row.put("level_name", lvl.getLevelName());
                list.add(row);
            }
        }
        vo.setMemberTypeInfo(list);
    }

    @Override
    public List<Map<String, Object>> selectGoodsAndSkuByFullGiftId(Integer fullGiftId) {
        MPJQueryWrapper<MallFullGiftGoods> wrapper = new MPJQueryWrapper<MallFullGiftGoods>()
                .select("gg.id", "gg.goods_id", "gg.sku_id", "g.goods_name", "g.goods_type",
                        "sku.stock", "sku.sku_name", "sku.price", "sku.sku_image")
                .setAlias("gg")
                .leftJoin("?_mall_goods g ON gg.goods_id = g.goods_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_mall_goods_sku sku ON gg.sku_id = sku.sku_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("gg.full_gift_id", fullGiftId);
        return fullGiftGoodsMapper.selectMaps(wrapper);
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

    private List<FullGiftCalculateParam.GoodsSkuData> getMatchedGoods(
            List<FullGiftCalculateParam.GoodsSkuData> goodsSkuData,
            List<Integer> activeGoodsIds,
            String goodsType) {

        if (FullGiftGoodsTypeEnum.ALL.getType().equals(goodsType)) {
            return new ArrayList<>(goodsSkuData);
        }

        if (FullGiftGoodsTypeEnum.SELECTED.getType().equals(goodsType)) {
            return goodsSkuData.stream()
                    .filter(item -> activeGoodsIds.contains(item.getGoodsId()))
                    .collect(Collectors.toList());
        }

        if (FullGiftGoodsTypeEnum.SELECTED_NOT.getType().equals(goodsType)) {
            return goodsSkuData.stream()
                    .filter(item -> !activeGoodsIds.contains(item.getGoodsId()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

}