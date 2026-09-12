package com.niu.core.service.admin.vip_card.card.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.vip_card.MemberVipCard;
import com.niu.core.entity.vip_card.MemberVipCardBuy;
import com.niu.core.entity.vip_card.MemberVipCardItem;
import com.niu.core.enums.vip_card.VipCardExpireStatusEnum;
import com.niu.core.enums.vip_card.VipCardTrialStatusEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.vip_card.VipCardBuyMapper;
import com.niu.core.mapper.vip_card.VipCardItemMapper;
import com.niu.core.mapper.vip_card.VipCardMapper;
import com.niu.core.service.admin.vip_card.card.IAdminVipCardBuyService;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardBuyPageParam;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardBuyPageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AdminVipCardBuyServiceImpl implements IAdminVipCardBuyService {

    @Autowired
    private VipCardBuyMapper vipCardBuyMapper;

    @Autowired
    private VipCardItemMapper vipCardItemMapper;

    @Autowired
    private VipCardMapper vipCardMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public PageResult<AdminVipCardBuyPageVo> getPage(PageParam pageParam, AdminVipCardBuyPageParam param) {
        LambdaQueryWrapper<MemberVipCardBuy> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(MemberVipCardBuy::getCreateTime);

        if (StrUtil.isNotBlank(param.getCardName())) {
            List<Integer> cardIds = vipCardMapper.selectList(
                            new LambdaQueryWrapper<MemberVipCard>()
                                    .eq(MemberVipCard::getCardName, param.getCardName())
                                    .select(MemberVipCard::getCardId))
                    .stream()
                    .map(card -> Math.toIntExact(card.getCardId()))
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(cardIds)) {
                return PageResult.build(pageParam.getPage(), pageParam.getLimit(), 0L, Collections.emptyList());
            }
            wrapper.in(MemberVipCardBuy::getCardId, cardIds);
        }
        if (StrUtil.isNotBlank(param.getKeyword())) {
            List<Integer> memberIds = memberMapper.selectList(
                            new LambdaQueryWrapper<Member>()
                                    .and(w -> w.like(Member::getNickname, param.getKeyword())
                                            .or().like(Member::getMobile, param.getKeyword())
                                            .or().like(Member::getMemberNo, param.getKeyword()))
                                    .select(Member::getMemberId))
                    .stream()
                    .map(Member::getMemberId)
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(memberIds)) {
                return PageResult.build(pageParam.getPage(), pageParam.getLimit(), 0L, Collections.emptyList());
            }
            wrapper.in(MemberVipCardBuy::getMemberId, memberIds);
        }
        if (param.getCardId() != null) {
            wrapper.eq(MemberVipCardBuy::getCardId, param.getCardId());
        }
        if (param.getCreateTimeStart() != null && param.getCreateTimeEnd() != null) {
            wrapper.between(MemberVipCardBuy::getCreateTime, param.getCreateTimeStart(), param.getCreateTimeEnd());
        } else if (param.getCreateTimeStart() != null) {
            wrapper.ge(MemberVipCardBuy::getCreateTime, param.getCreateTimeStart());
        } else if (param.getCreateTimeEnd() != null) {
            wrapper.le(MemberVipCardBuy::getCreateTime, param.getCreateTimeEnd());
        }

        IPage<MemberVipCardBuy> page = vipCardBuyMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()), wrapper);

        if (page.getRecords().isEmpty()) {
            return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal());
        }

        Map<Integer, Member> memberMap = loadMemberMap(page.getRecords());
        Map<Integer, MemberVipCard> cardMap = loadCardMap(page.getRecords());
        Map<Integer, MemberVipCardItem> itemMap = loadItemMap(page.getRecords());

        List<AdminVipCardBuyPageVo> records = page.getRecords().stream()
                .map(buy -> buildVo(buy, memberMap, cardMap, itemMap))
                .collect(Collectors.toList());

        return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal(), records);
    }

    @Override
    public AdminVipCardBuyPageVo getInfo(Integer id) {
        MemberVipCardBuy buy = vipCardBuyMapper.selectById(id);
        if (buy == null) {
            return null;
        }

        Map<Integer, Member> memberMap = loadMemberMap(Collections.singletonList(buy));
        Map<Integer, MemberVipCard> cardMap = loadCardMap(Collections.singletonList(buy));
        Map<Integer, MemberVipCardItem> itemMap = loadItemMap(Collections.singletonList(buy));

        return buildVo(buy, memberMap, cardMap, itemMap);
    }

    private AdminVipCardBuyPageVo buildVo(MemberVipCardBuy buy,
                                          Map<Integer, Member> memberMap,
                                          Map<Integer, MemberVipCard> cardMap,
                                          Map<Integer, MemberVipCardItem> itemMap) {
        AdminVipCardBuyPageVo vo = new AdminVipCardBuyPageVo();
        if (buy.getId() != null) {
            vo.setId(Math.toIntExact(buy.getId()));
        }
        vo.setSiteId(buy.getSiteId());
        vo.setOrderId(buy.getOrderId());
        vo.setMemberId(buy.getMemberId());
        vo.setCardId(buy.getCardId());
        vo.setItemId(buy.getItemId());
        vo.setIsExpire(buy.getIsExpire());
        vo.setIsTrial(buy.getIsTrial());
        vo.setExpireTime(buy.getExpireTime());
        vo.setCreateTime(buy.getCreateTime());
        vo.setIsExpireName(VipCardExpireStatusEnum.getNameByCode(buy.getIsExpire()));
        vo.setIsTrialName(VipCardTrialStatusEnum.getNameByCode(buy.getIsTrial()));

        if (Objects.nonNull(buy.getMemberId())) {
            vo.setMember(memberMap.get(buy.getMemberId()));
        }
        if (Objects.nonNull(buy.getCardId())) {
            vo.setVipCard(cardMap.get(buy.getCardId()));
        }
        if (Objects.nonNull(buy.getItemId())) {
            vo.setVipCardItem(itemMap.get(buy.getItemId()));
        }
        return vo;
    }

    private Map<Integer, Member> loadMemberMap(List<MemberVipCardBuy> buyList) {
        List<Integer> memberIds = buyList.stream()
                .map(MemberVipCardBuy::getMemberId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(memberIds)) {
            return Collections.emptyMap();
        }
        return memberMapper.selectList(
                        new LambdaQueryWrapper<Member>()
                                .in(Member::getMemberId, memberIds)
                                .select(Member::getMemberId, Member::getHeadimg, Member::getNickname,
                                        Member::getMobile, Member::getMemberNo))
                .stream()
                .collect(Collectors.toMap(Member::getMemberId, member -> member, (a, b) -> a));
    }

    private Map<Integer, MemberVipCard> loadCardMap(List<MemberVipCardBuy> buyList) {
        List<Integer> cardIds = buyList.stream()
                .map(MemberVipCardBuy::getCardId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(cardIds)) {
            return Collections.emptyMap();
        }
        Map<Integer, MemberVipCard> cardMap = new HashMap<>();
        vipCardMapper.selectList(
                        new LambdaQueryWrapper<MemberVipCard>()
                                .in(MemberVipCard::getCardId, cardIds)
                                .select(MemberVipCard::getCardId, MemberVipCard::getCardName))
                .forEach(card -> cardMap.put(Math.toIntExact(card.getCardId()), card));
        return cardMap;
    }

    private Map<Integer, MemberVipCardItem> loadItemMap(List<MemberVipCardBuy> buyList) {
        List<Integer> itemIds = buyList.stream()
                .map(MemberVipCardBuy::getItemId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(itemIds)) {
            return Collections.emptyMap();
        }
        Map<Integer, MemberVipCardItem> itemMap = new HashMap<>();
        vipCardItemMapper.selectList(
                        new LambdaQueryWrapper<MemberVipCardItem>()
                                .in(MemberVipCardItem::getItemId, itemIds)
                                .select(MemberVipCardItem::getItemId, MemberVipCardItem::getItemName,
                                        MemberVipCardItem::getTimeLimit, MemberVipCardItem::getTimeType))
                .forEach(item -> itemMap.put(Math.toIntExact(item.getItemId()), item));
        return itemMap;
    }
}
