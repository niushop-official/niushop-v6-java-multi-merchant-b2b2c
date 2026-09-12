package com.niu.core.service.api.vip_card.card.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.entity.vip_card.*;
import com.niu.core.enums.vip_card.VipCardBenefitsStatusEnum;
import com.niu.core.enums.vip_card.VipCardExpireStatusEnum;
import com.niu.core.enums.vip_card.VipCardSaleStatusEnum;
import com.niu.core.enums.vip_card.VipCardStatusEnum;
import com.niu.core.event.vip_card.VipCardGoodsListEventDefiner;
import com.niu.core.mapper.vip_card.*;
import com.niu.core.service.api.vip_card.card.IApiVipCardService;
import com.niu.core.service.api.vip_card.card.param.ApiVipCardGoodsParam;
import com.niu.core.service.api.vip_card.card.vo.*;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.member.dto.MemberInfoDto;
import com.niu.core.service.core.vip_card.card.ICoreVipCardBuyService;
import com.niu.core.service.core.vip_card.card.ICoreVipCardService;
import com.niu.core.service.core.vip_card.card.vo.VipCardItemVo;
import com.niu.core.service.core.vip_card.config.ICoreVipCardConfigService;
import com.niu.core.service.core.vip_card.config.vo.VipCardConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApiVipCardServiceImpl implements IApiVipCardService {

    @Autowired
    private VipCardMapper vipCardMapper;

    @Autowired
    private VipCardItemMapper vipCardItemMapper;

    @Autowired
    private VipCardBenefitsMapper vipCardBenefitsMapper;

    @Autowired
    private VipCardBenefitsItemMapper vipCardBenefitsItemMapper;

    @Autowired
    private VipCardBuyMapper vipCardBuyMapper;

    @Autowired
    private ICoreVipCardConfigService coreVipCardConfigService;

    @Autowired
    private ICoreVipCardBuyService coreVipCardBuyService;

    @Autowired
    private ICoreVipCardService coreVipCardService;

    @Autowired
    private ICoreMemberService coreMemberService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;


    // ====== 辅助方法 ======
    private Integer getMemberId() {
        return RequestUtils.memberId();
    }

    private Integer getSiteId() {
        return RequestUtils.siteId();
    }

    private MemberInfoDto getMemberInfo() {
        return RequestUtils.getCurrentMember();
    }

    // ====== 1. 会员卡详情 ======
    @Override
    public ApiVipCardDetailVo getInfo(Integer cardId) {
        Integer memberId = getMemberId();
        Integer siteId = getSiteId();

        VipCardConfigVo vipCardConfig = coreVipCardConfigService.getVipCardConfig();
        boolean isUse = vipCardConfig.getIsUse() != null && vipCardConfig.getIsUse().equals(1);

        MemberVipCardBuy memberBuy = (memberId != null) ? coreVipCardBuyService.getMemberBuy(memberId) : null;

        LambdaQueryWrapper<MemberVipCard> wrapper = new LambdaQueryWrapper<>();
        if (cardId != null && cardId > 0) {
            wrapper.eq(MemberVipCard::getCardId, cardId);
        } else {
            if (memberBuy != null) {
                wrapper.eq(MemberVipCard::getCardId, memberBuy.getCardId());
            } else {
                wrapper.eq(MemberVipCard::getStatus, VipCardStatusEnum.ON.getCode());
            }
        }

        if (memberBuy == null && !isUse) {
            throw new ApiException("付费会员卡购买已关闭，请联系商家");
        }

        MemberVipCard vipCard = vipCardMapper.selectOne(wrapper);
        if (vipCard == null) {
            throw new ApiException("会员卡不存在或已下架");
        }

        // 权益列表：已开通按 memberBuy.benefits_config 的 type 查，未开通按 card.benefits_id 查
        List<ApiVipCardDetailVo.BenefitsInfo> benefitsList = new ArrayList<>();
        List<MemberVipCardBenefits> benefits = new ArrayList<>();
        if (memberBuy != null && StrUtil.isNotEmpty(memberBuy.getBenefitsConfig())) {
            JSONObject benefitsConfig = parseJsonObject(memberBuy.getBenefitsConfig());
            List<String> types = benefitsConfig != null ? new ArrayList<>(benefitsConfig.keySet()) : new ArrayList<>();
            if (CollectionUtil.isNotEmpty(types)) {
                benefits = vipCardBenefitsMapper.selectList(
                        new LambdaQueryWrapper<MemberVipCardBenefits>()
                                .in(MemberVipCardBenefits::getType, types)
                                .select(MemberVipCardBenefits::getBenefitsId, MemberVipCardBenefits::getIcon,
                                        MemberVipCardBenefits::getName, MemberVipCardBenefits::getDesc)
                );
            }
        } else if (StrUtil.isNotEmpty(vipCard.getBenefitsId())){
            List<Integer> benefitsIds = getBenefitsId(vipCard.getBenefitsId());
            if (CollectionUtil.isNotEmpty(benefitsIds)) {
                benefits = vipCardBenefitsMapper.selectList(
                        new LambdaQueryWrapper<MemberVipCardBenefits>()
                                .in(MemberVipCardBenefits::getBenefitsId, benefitsIds)
                                .select(MemberVipCardBenefits::getBenefitsId, MemberVipCardBenefits::getIcon,
                                        MemberVipCardBenefits::getName, MemberVipCardBenefits::getDesc)
                );
            }
        }
        for (MemberVipCardBenefits b : benefits) {
            ApiVipCardDetailVo.BenefitsInfo info = new ApiVipCardDetailVo.BenefitsInfo();
            BeanUtil.copyProperties(b, info);
            benefitsList.add(info);
        }

        // 规格项处理
        List<MemberVipCardItem> items = vipCardItemMapper.selectList(
                new LambdaQueryWrapper<MemberVipCardItem>()
                        .eq(MemberVipCardItem::getCardId, vipCard.getCardId())
                        .eq(MemberVipCardItem::getIsSale, VipCardSaleStatusEnum.ON.getCode())
        );
        List<VipCardItemVo> itemVos = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(items)) {
            for (MemberVipCardItem item : items) {
                VipCardItemVo processed = coreVipCardService.getCardTrialAndRenewInfo(vipCard, item, memberId);
                processed.setSubtractMoney(processed.getCostPrice().subtract(processed.getPrice()).max(BigDecimal.ZERO));
                itemVos.add(processed);
            }
        }

        // 是否已开通
        MemberVipCardBuy buyInfo = null;
        boolean isOpen = false;
        if (memberId != null) {
            buyInfo = vipCardBuyMapper.selectOne(
                    new LambdaQueryWrapper<MemberVipCardBuy>()
                            .eq(MemberVipCardBuy::getCardId, vipCard.getCardId())
                            .eq(MemberVipCardBuy::getMemberId, memberId)
                            .eq(MemberVipCardBuy::getIsExpire, VipCardExpireStatusEnum.EXPIRE_NO.getCode())
            );
            if (buyInfo != null) isOpen = true;
        }

        // 组装VO
        ApiVipCardDetailVo vo = new ApiVipCardDetailVo();
        BeanUtil.copyProperties(vipCard, vo);
        vo.setBenefitsList(benefitsList);
        vo.setItem(itemVos);
        vo.setIsOpen(isOpen);
        if (buyInfo != null) {
            ApiVipCardDetailVo.VipCardBuyInfo buy = new ApiVipCardDetailVo.VipCardBuyInfo();
            BeanUtil.copyProperties(buyInfo, buy);
            vo.setBuyInfo(buy);
        }
        MemberInfoDto memberInfoMap = getMemberInfo();
        if (memberInfoMap != null) {
            ApiVipCardDetailVo.MemberInfo mi = new ApiVipCardDetailVo.MemberInfo();
            mi.setMemberId(memberInfoMap.getMemberId());
            mi.setNickname(memberInfoMap.getNickname());
            mi.setHeadimg(memberInfoMap.getHeadimg());
            vo.setMemberInfo(mi);
        }
        return vo;
    }

    // ====== 2. 权益分组 ======
    @Override
    public ApiVipCardBenefitsGroupVo getVipCardBenefitsGroup() {
        Integer memberId = getMemberId();

        MemberVipCardBuy memberBuy = (memberId != null) ? coreVipCardBuyService.getMemberBuy(memberId) : null;
        List<ApiVipCardBenefitsGroupVo.BenefitsGroupItem> groupItems = new ArrayList<>();

        if (memberBuy != null) {
            // 已购买
            JSONObject benefitsConfigJson = parseJsonObject(memberBuy.getBenefitsConfig());
            if (benefitsConfigJson != null && !benefitsConfigJson.isEmpty()) {
                List<String> types = new ArrayList<>(benefitsConfigJson.keySet());
                MemberVipCard vipCard = vipCardMapper.selectById(memberBuy.getCardId());
                List<MemberVipCardBenefits> benefits = vipCardBenefitsMapper.selectList(
                        new LambdaQueryWrapper<MemberVipCardBenefits>()
                                .in(MemberVipCardBenefits::getType, types)
                                .orderByDesc(MemberVipCardBenefits::getSort)
                );
                for (MemberVipCardBenefits b : benefits) {
                    ApiVipCardBenefitsGroupVo.BenefitsGroupItem item = new ApiVipCardBenefitsGroupVo.BenefitsGroupItem();
                    BeanUtil.copyProperties(b, item);
                    ApiVipCardBenefitsConfigVo config = parseBenefitsConfigObject(benefitsConfigJson.get(b.getType()));
                    if (config != null && !config.isEmpty()) {
                        item.setItem(buildBenefitsGroupItemVo(config));
                    } else {
                        ApiVipCardBenefitsGroupItemVo itemVo = resolveBenefitsGroupItemVo(vipCard, b, Collections.emptyList());
                        if (itemVo != null) {
                            item.setItem(itemVo);
                        }
                    }
                    if (b.getType().equals(b.getAddon() + "_buy_goods_discount")) {
                        appendBenefitsGoodsList(item, b.getAddon());
                    }
                    groupItems.add(item);
                }
            }
        } else {
            // 未购买
            MemberVipCard vipCard = vipCardMapper.selectOne(
                    new LambdaQueryWrapper<MemberVipCard>()
                            .eq(MemberVipCard::getStatus, VipCardStatusEnum.ON.getCode())
                            .orderByAsc(MemberVipCard::getCreateTime)
                            .last("limit 1")
            );
            if (vipCard != null) {
                List<Integer> benefitsIds = getBenefitsId(vipCard.getBenefitsId());
                if (CollectionUtil.isNotEmpty(benefitsIds)) {
                    List<MemberVipCardBenefits> benefits = vipCardBenefitsMapper.selectList(
                            new LambdaQueryWrapper<MemberVipCardBenefits>()
                                    .in(MemberVipCardBenefits::getBenefitsId, benefitsIds)
                                    .orderByDesc(MemberVipCardBenefits::getSort)
                    );
                    // 查询权益项
                    List<Integer> benefitsItemIds = getBenefitsId(vipCard.getBenefitsItemId());
                    List<MemberVipCardBenefitsItem> benefitsItems = new ArrayList<>();
                    if (CollectionUtil.isNotEmpty(benefitsItemIds)) {
                        benefitsItems = vipCardBenefitsItemMapper.selectList(
                                new LambdaQueryWrapper<MemberVipCardBenefitsItem>()
                                        .in(MemberVipCardBenefitsItem::getBenefitsItemId, benefitsItemIds)
                        );
                    }
                    for (MemberVipCardBenefits b : benefits) {
                        ApiVipCardBenefitsGroupVo.BenefitsGroupItem item = new ApiVipCardBenefitsGroupVo.BenefitsGroupItem();
                        BeanUtil.copyProperties(b, item);
                        ApiVipCardBenefitsGroupItemVo itemVo = resolveBenefitsGroupItemVo(vipCard, b, benefitsItems);
                        if (itemVo != null) {
                            item.setItem(itemVo);
                        }
                        if (b.getType().equals(b.getAddon() + "_buy_goods_discount")) {
                            appendBenefitsGoodsList(item, b.getAddon());
                        }
                        groupItems.add(item);
                    }
                }
            }
        }

        ApiVipCardBenefitsGroupVo vo = new ApiVipCardBenefitsGroupVo();
        vo.setBenefitsList(groupItems);
        if (CollectionUtil.isNotEmpty(groupItems)) {
            long total = vipCardBenefitsMapper.selectCount(
                    new LambdaQueryWrapper<MemberVipCardBenefits>()
                            .eq(MemberVipCardBenefits::getStatus, VipCardBenefitsStatusEnum.ON.getCode())
            );
            vo.setTotal(total);
        }
        return vo;
    }

    // ====== 3. 专属商品 ======
    @Override
    public List<ApiVipCardGoodsVo> getVipCardGoods(ApiVipCardGoodsParam param) {
        Integer memberId = getMemberId();
        List<String> benefitTypes = new ArrayList<>();

        // 1. 获取会员当前购买记录或默认会员卡权益类型
        MemberVipCardBuy memberBuy = (memberId != null) ? coreVipCardBuyService.getMemberBuy(memberId) : null;
        if (memberBuy != null) {
            JSONObject benefitsConfig = parseJsonObject(memberBuy.getBenefitsConfig());
            if (benefitsConfig != null) {
                benefitTypes.addAll(benefitsConfig.keySet());
            }
        } else {
            MemberVipCard vipCard = vipCardMapper.selectOne(
                    new LambdaQueryWrapper<MemberVipCard>()
                            .eq(MemberVipCard::getStatus, VipCardStatusEnum.ON.getCode())
                            .orderByAsc(MemberVipCard::getCreateTime)
                            .last("limit 1")
            );
            if (vipCard != null) {
                List<Integer> benefitsIds = getBenefitsId(vipCard.getBenefitsId());
                if (CollectionUtil.isNotEmpty(benefitsIds)) {
                    List<MemberVipCardBenefits> benefits = vipCardBenefitsMapper.selectList(
                            new LambdaQueryWrapper<MemberVipCardBenefits>()
                                    .in(MemberVipCardBenefits::getBenefitsId, benefitsIds)
                                    .select(MemberVipCardBenefits::getType)
                    );
                    benefitTypes = benefits.stream().map(MemberVipCardBenefits::getType).collect(Collectors.toList());
                }
            }
        }

        // 2. 判断是否包含目标权益
        String targetType = param.getGroup() + "_buy_goods_discount";
        List<ApiVipCardGoodsVo> goodsList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(benefitTypes) && benefitTypes.contains(targetType)) {
            // 3. 发布事件，获取商品列表
            VipCardGoodsListEventDefiner.VipCardGoodsListEvent vipGoodsListEvent = new VipCardGoodsListEventDefiner.VipCardGoodsListEvent();
            vipGoodsListEvent.setNum(param.getNum());
            vipGoodsListEvent.setGroup(param.getGroup());
            List<VipCardGoodsListEventDefiner.VipCardGoodsListEventResult> resultList = EventAndSubscribeOfPublisher.publishAndCallback(vipGoodsListEvent);
            for (VipCardGoodsListEventDefiner.VipCardGoodsListEventResult result : resultList) {
                if (ObjectUtil.isNotEmpty(result.getResultData())) {
                    goodsList.addAll(result.getResultData());
                }
            }
        }

        return goodsList;
    }

    // ====== 4. 组件信息 ======
    @Override
    public ApiVipCardComponentsVo getVipCardComponents() {
        VipCardConfigVo config = coreVipCardConfigService.getVipCardConfig();
        if (config.getIsUse() == null || !config.getIsUse().equals(1)) {
            return new ApiVipCardComponentsVo();
        }
        Integer memberId = getMemberId();
        MemberVipCardBuy memberBuy = (memberId != null) ? coreVipCardBuyService.getMemberBuy(memberId) : null;

        ApiVipCardComponentsVo vo = new ApiVipCardComponentsVo();

        if (memberBuy != null) {
            // 已购买
            MemberVipCard vipCard = vipCardMapper.selectById(memberBuy.getCardId());
            if (vipCard != null && StringUtils.isNotEmpty(memberBuy.getBenefitsConfig())) {
                JSONObject benefitsConfig = parseJsonObject(memberBuy.getBenefitsConfig());
                List<String> openTypes = benefitsConfig != null ? new ArrayList<>(benefitsConfig.keySet()) : new ArrayList<>();
                List<ApiVipCardComponentsVo.BenefitsSimple> benefitsList = new ArrayList<>();
                if (CollectionUtil.isNotEmpty(openTypes)) {
                    List<MemberVipCardBenefits> benefits = vipCardBenefitsMapper.selectList(
                            new LambdaQueryWrapper<MemberVipCardBenefits>()
                                    .in(MemberVipCardBenefits::getType, openTypes)
                                    .orderByDesc(MemberVipCardBenefits::getSort)
                                    .select(MemberVipCardBenefits::getBenefitsId, MemberVipCardBenefits::getName,
                                            MemberVipCardBenefits::getIcon, MemberVipCardBenefits::getType,
                                            MemberVipCardBenefits::getDesc)
                    );
                    for (MemberVipCardBenefits b : benefits) {
                        ApiVipCardComponentsVo.BenefitsSimple simple = new ApiVipCardComponentsVo.BenefitsSimple();
                        BeanUtil.copyProperties(b, simple);
                        simple.setIcon("/static/resource/images/member/vip_card/diy/" + b.getType() + ".png");
                        benefitsList.add(simple);
                    }
                }
                List<Integer> benefitsIds = getBenefitsId(vipCard.getBenefitsId());
                vo.setCardId(Math.toIntExact(vipCard.getCardId()));
                vo.setCardName(vipCard.getCardName());
                vo.setBenefitsNum(benefitsIds != null ? benefitsIds.size() : 0);
                vo.setBenefitsOpenNum(openTypes.size());
                vo.setIsOpen(1);
                vo.setBenefitsList(benefitsList);
            }
        } else {
            // 未购买
            boolean isUse = config.getIsUse() != null && config.getIsUse().equals(1);
            if (isUse) {
                MemberVipCard vipCard = vipCardMapper.selectOne(
                        new LambdaQueryWrapper<MemberVipCard>()
                                .eq(MemberVipCard::getStatus, VipCardStatusEnum.ON.getCode())
                                .orderByAsc(MemberVipCard::getCreateTime)
                );
                // PHP only requires an enabled card. An empty benefits_id must not hide the member-card component.
                if (vipCard != null) {
                    List<Integer> benefitsIds = getBenefitsId(vipCard.getBenefitsId());
                    List<ApiVipCardComponentsVo.BenefitsSimple> benefitsList = new ArrayList<>();
                    if (CollectionUtil.isNotEmpty(benefitsIds)) {
                        List<MemberVipCardBenefits> benefits = vipCardBenefitsMapper.selectList(
                                new LambdaQueryWrapper<MemberVipCardBenefits>()
                                        .in(MemberVipCardBenefits::getBenefitsId, benefitsIds)
                                        .orderByDesc(MemberVipCardBenefits::getSort)
                                        .select(MemberVipCardBenefits::getBenefitsId, MemberVipCardBenefits::getName,
                                                MemberVipCardBenefits::getIcon, MemberVipCardBenefits::getType,
                                                MemberVipCardBenefits::getDesc)
                        );
                        for (MemberVipCardBenefits b : benefits) {
                            ApiVipCardComponentsVo.BenefitsSimple simple = new ApiVipCardComponentsVo.BenefitsSimple();
                            BeanUtil.copyProperties(b, simple);
                            simple.setIcon("/static/resource/images/member/vip_card/diy/" + b.getType() + ".png");
                            benefitsList.add(simple);
                        }
                    }
                    vo.setCardId(Math.toIntExact(vipCard.getCardId()));
                    vo.setCardName(vipCard.getCardName());
                    vo.setBenefitsNum(benefitsIds != null ? benefitsIds.size() : 0);
                    vo.setBenefitsOpenNum(0);
                    vo.setIsOpen(0);
                    vo.setBenefitsList(benefitsList);
                }
            }
        }
        return vo;
    }

    /**
     * 重新格式 id
     * @param benefitsIdStr 重新格式
     * @return List<Integer>
     */
    public List<Integer> getBenefitsId(String benefitsIdStr){
        if (StrUtil.isBlank(benefitsIdStr)) {
            return new ArrayList<>();
        }
        List<Integer> benefitsIds;
        if (benefitsIdStr.trim().startsWith("[")) {
            benefitsIds = JSONUtil.toList(benefitsIdStr, Integer.class);
        } else {
            benefitsIds = Arrays.stream(benefitsIdStr.split(","))
                    .map(String::trim)
                    .filter(StrUtil::isNotEmpty)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }
        return benefitsIds;
    }

    private ApiVipCardBenefitsGroupItemVo buildBenefitsGroupItemVo(MemberVipCardBenefitsItem benefitItem) {
        ApiVipCardBenefitsGroupItemVo itemVo = new ApiVipCardBenefitsGroupItemVo();
        itemVo.setBenefitsItemId(benefitItem.getBenefitsItemId());
        itemVo.setBenefitsId(benefitItem.getBenefitsId());
        itemVo.setName(benefitItem.getName());
        itemVo.setBenefitsConfig(parseAndEnrichBenefitsConfig(benefitItem.getBenefitsConfig()));
        return itemVo;
    }

    private ApiVipCardBenefitsGroupItemVo buildBenefitsGroupItemVo(ApiVipCardBenefitsConfigVo config) {
        ApiVipCardBenefitsGroupItemVo itemVo = new ApiVipCardBenefitsGroupItemVo();
        itemVo.setBenefitsConfig(enrichBenefitsConfig(config));
        return itemVo;
    }

    /**
     * 解析权益 item：优先已选权益项列表，其次会员卡 benefits_config，最后按 benefits_id 查默认规则。
     */
    private ApiVipCardBenefitsGroupItemVo resolveBenefitsGroupItemVo(
            MemberVipCard vipCard, MemberVipCardBenefits benefit, List<MemberVipCardBenefitsItem> benefitsItems) {
        MemberVipCardBenefitsItem benefitItem = findBenefitsItem(benefitsItems, benefit.getBenefitsId());
        if (benefitItem == null && vipCard != null) {
            Long itemId = resolveBenefitsItemIdFromCardConfig(vipCard.getBenefitsConfig(), benefit.getBenefitsId());
            if (itemId != null) {
                benefitItem = vipCardBenefitsItemMapper.selectById(itemId);
            }
        }
        if (benefitItem == null) {
            benefitItem = vipCardBenefitsItemMapper.selectOne(
                    new LambdaQueryWrapper<MemberVipCardBenefitsItem>()
                            .eq(MemberVipCardBenefitsItem::getBenefitsId, benefit.getBenefitsId())
                            .last("limit 1")
            );
        }
        if (benefitItem == null) {
            return null;
        }
        return buildBenefitsGroupItemVo(benefitItem);
    }

    private MemberVipCardBenefitsItem findBenefitsItem(List<MemberVipCardBenefitsItem> benefitsItems, Long benefitsId) {
        if (CollectionUtil.isEmpty(benefitsItems) || benefitsId == null) {
            return null;
        }
        for (MemberVipCardBenefitsItem item : benefitsItems) {
            if (benefitsId.equals(item.getBenefitsId())) {
                return item;
            }
        }
        return null;
    }

    private Long resolveBenefitsItemIdFromCardConfig(String benefitsConfigJson, Long benefitsId) {
        if (StrUtil.isBlank(benefitsConfigJson) || benefitsId == null || !benefitsConfigJson.trim().startsWith("[")) {
            return null;
        }
        try {
            for (Object obj : JSONUtil.parseArray(benefitsConfigJson)) {
                JSONObject item = JSONUtil.parseObj(obj);
                Long currentBenefitsId = firstNonNullLong(item.getLong("benefits_id"), item.getLong("benefitsId"));
                if (!benefitsId.equals(currentBenefitsId)) {
                    continue;
                }
                JSONObject benefitsItem = item.getJSONObject("benefits_item");
                if (benefitsItem == null) {
                    benefitsItem = item.getJSONObject("benefitsItem");
                }
                if (benefitsItem == null) {
                    return null;
                }
                return firstNonNullLong(benefitsItem.getLong("benefits_item_id"), benefitsItem.getLong("benefitsItemId"));
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }

    private Long firstNonNullLong(Long first, Long second) {
        return first != null ? first : second;
    }

    private void appendBenefitsGoodsList(ApiVipCardBenefitsGroupVo.BenefitsGroupItem item, String addon) {
        ApiVipCardGoodsParam goodsParam = new ApiVipCardGoodsParam();
        goodsParam.setNum(6);
        goodsParam.setGroup(addon);
        ApiVipCardBenefitsGroupItemVo itemVo = item.getItem();
        if (itemVo == null) {
            itemVo = new ApiVipCardBenefitsGroupItemVo();
            itemVo.setBenefitsConfig(enrichBenefitsConfig(new ApiVipCardBenefitsConfigVo()));
            item.setItem(itemVo);
        }
        List<ApiVipCardGoodsVo> goodsList = getVipCardGoods(goodsParam);
        // 对齐 PHP：商城权益页用 item.cost_price 展示原价划线
        for (ApiVipCardGoodsVo goods : goodsList) {
            if (goods.getCostPrice() == null && goods.getGoodsSku() != null) {
                goods.setCostPrice(goods.getGoodsSku().getCostPrice());
            }
        }
        itemVo.setGoodsList(goodsList);
    }

    private ApiVipCardBenefitsConfigVo parseAndEnrichBenefitsConfig(String benefitsConfigJson) {
        ApiVipCardBenefitsConfigVo config = parseBenefitsConfigObject(benefitsConfigJson);
        if (config == null) {
            config = new ApiVipCardBenefitsConfigVo();
        }
        return enrichBenefitsConfig(config);
    }

    private ApiVipCardBenefitsConfigVo parseBenefitsConfigObject(Object configObj) {
        if (configObj == null) {
            return null;
        }
        try {
            if (configObj instanceof ApiVipCardBenefitsConfigVo vo) {
                return vo;
            }
            String json;
            if (configObj instanceof String str) {
                if (StrUtil.isBlank(str)) {
                    return new ApiVipCardBenefitsConfigVo();
                }
                json = str;
            } else {
                json = JSONUtil.toJsonStr(configObj);
            }
            // DB/配置为 snake_case，忽略未知字段保证解析稳健
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ApiVipCardBenefitsConfigVo config = mapper.readValue(json, ApiVipCardBenefitsConfigVo.class);
            return config != null ? config : new ApiVipCardBenefitsConfigVo();
        } catch (Exception ignored) {
            return new ApiVipCardBenefitsConfigVo();
        }
    }

    private ApiVipCardBenefitsConfigVo enrichBenefitsConfig(ApiVipCardBenefitsConfigVo config) {
        if (config == null) {
            config = new ApiVipCardBenefitsConfigVo();
        }
        ApiVipCardBenefitsConfigVo.ApiVipCardGiftsVo gifts = config.getGifts();
        if (gifts == null) {
            gifts = defaultGifts();
            config.setGifts(gifts);
        }
        JSONObject giftsJson = JSONUtil.parseObj(JacksonUtils.toSnakeCaseJSONString(gifts));
        JSONObject giftsContentJson = coreMemberService.getGiftContent(getSiteId(), giftsJson, "wap");
        if (giftsContentJson == null) {
            giftsContentJson = giftsJson;
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            mapper.setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ApiVipCardBenefitsConfigVo.ApiVipCardGiftsVo giftsContent = mapper.readValue(
                    giftsContentJson.toString(), ApiVipCardBenefitsConfigVo.ApiVipCardGiftsVo.class);
            config.setGiftsContent(giftsContent != null ? giftsContent : gifts);
        } catch (Exception e) {
            config.setGiftsContent(gifts);
        }
        return config;
    }

    private ApiVipCardBenefitsConfigVo.ApiVipCardGiftsVo defaultGifts() {
        ApiVipCardBenefitsConfigVo.ApiVipCardGiftsVo gifts = new ApiVipCardBenefitsConfigVo.ApiVipCardGiftsVo();

        ApiVipCardBenefitsConfigVo.GiftBalanceVo balance = new ApiVipCardBenefitsConfigVo.GiftBalanceVo();
        balance.setIsUse(0);
        balance.setMoney("");
        gifts.setBalance(balance);

        ApiVipCardBenefitsConfigVo.GiftPointVo point = new ApiVipCardBenefitsConfigVo.GiftPointVo();
        point.setIsUse(0);
        point.setNum("");
        gifts.setPoint(point);

        ApiVipCardBenefitsConfigVo.GiftShopCouponVo shopCoupon = new ApiVipCardBenefitsConfigVo.GiftShopCouponVo();
        shopCoupon.setIsUse(0);
        shopCoupon.setCouponId(new ArrayList<>());
        shopCoupon.setContent(new ArrayList<>());
        gifts.setShopCoupon(shopCoupon);

        return gifts;
    }

    private JSONObject parseJsonObject(String json) {
        if (StrUtil.isBlank(json)) {
            return null;
        }
        try {
            return JSONUtil.parseObj(json);
        } catch (Exception ignored) {
            return null;
        }
    }
}
