package com.niu.core.service.admin.vip_card.card.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.entity.vip_card.MemberVipCard;
import com.niu.core.entity.vip_card.MemberVipCardBenefits;
import com.niu.core.entity.vip_card.MemberVipCardItem;
import com.niu.core.enums.vip_card.VipCardStatusEnum;
import com.niu.core.mapper.vip_card.VipCardBenefitsMapper;
import com.niu.core.mapper.vip_card.VipCardItemMapper;
import com.niu.core.mapper.vip_card.VipCardMapper;
import com.niu.core.service.admin.vip_card.card.IAdminVipCardService;
import com.niu.core.service.admin.vip_card.card.param.AdminModifyStatusParam;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardAddParam;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardEditParam;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardPageParam;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardInfoVo;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardListVo;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardPageVo;
import com.niu.core.service.core.vip_card.card.ICoreVipCardBuyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminVipCardServiceImpl implements IAdminVipCardService {

    @Autowired
    private VipCardMapper vipCardMapper;

    @Autowired
    private VipCardItemMapper vipCardItemMapper;

    @Autowired
    private ICoreVipCardBuyService coreVipCardBuyService;

    @Autowired
    private VipCardBenefitsMapper benefitsMapper;

    // ========== 分页列表 ==========
    @Override
    public PageResult<AdminVipCardPageVo> getPage(PageParam pageParam, AdminVipCardPageParam param) {
        LambdaQueryWrapper<MemberVipCard> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(param.getCardName())) {
            wrapper.like(MemberVipCard::getCardName, param.getCardName());
        }
        wrapper.orderByDesc(MemberVipCard::getCreateTime);

        IPage<MemberVipCard> page = vipCardMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()), wrapper);

        List<AdminVipCardPageVo> voList = new ArrayList<>();
        for (MemberVipCard card : page.getRecords()) {
            AdminVipCardPageVo vo = new AdminVipCardPageVo();
            BeanUtil.copyProperties(card, vo);
            vo.setStatus(normalizeCardStatus(card.getStatus()));
            vo.setStatusName(VipCardStatusEnum.getNameByCode(vo.getStatus()));
            // 查询购买人数
            vo.setBuyNum(coreVipCardBuyService.getCardBuyNum(Math.toIntExact(card.getCardId())));
            // 查询规格项
            List<MemberVipCardItem> items = vipCardItemMapper.selectList(
                    new LambdaQueryWrapper<MemberVipCardItem>().eq(MemberVipCardItem::getCardId, card.getCardId()));
            if (CollUtil.isNotEmpty(items)) {
                List<AdminVipCardPageVo.CardItemVo> itemVos = items.stream().map(item -> {
                    AdminVipCardPageVo.CardItemVo itemVo = new AdminVipCardPageVo.CardItemVo();
                    BeanUtil.copyProperties(item, itemVo);
                    return itemVo;
                }).collect(Collectors.toList());
                vo.setItem(itemVos);
            }
            List<Integer> benefitsIds = Arrays.stream(vo.getBenefitsId().split(",")).map(Integer::parseInt).toList();
            List<MemberVipCardBenefits> benefits = benefitsMapper.selectList(new LambdaQueryWrapper<MemberVipCardBenefits>()
                    .in(MemberVipCardBenefits::getBenefitsId, benefitsIds));
            List<AdminVipCardPageVo.BenefitsPageVo> benefitsPageVos = BeanUtil.copyToList(benefits, AdminVipCardPageVo.BenefitsPageVo.class);
            vo.setBenefitsList(benefitsPageVos);
            voList.add(vo);
        }
        return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal()).setData(voList);
    }

    // ========== 列表 ==========
    @Override
    public List<AdminVipCardListVo> getList(AdminVipCardPageParam param) {
        LambdaQueryWrapper<MemberVipCard> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(param.getCardName())) {
            wrapper.like(MemberVipCard::getCardName, param.getCardName());
        }
        wrapper.orderByDesc(MemberVipCard::getCreateTime);
        List<MemberVipCard> cards = vipCardMapper.selectList(wrapper);
        return cards.stream().map(card -> {
            AdminVipCardListVo vo = new AdminVipCardListVo();
            BeanUtil.copyProperties(card, vo);
            vo.setStatus(normalizeCardStatus(card.getStatus()));
            return vo;
        }).collect(Collectors.toList());
    }

    // ========== 详情 ==========
    @Override
    public AdminVipCardInfoVo getInfo(Integer cardId) {
        MemberVipCard card = vipCardMapper.selectById(cardId);
        Assert.notNull(card, "会员卡不存在");
        AdminVipCardInfoVo vo = new AdminVipCardInfoVo();
        BeanUtil.copyProperties(card, vo, "shareConfig");
        vo.setStatus(normalizeCardStatus(card.getStatus()));

        // 处理 share_config 默认值
        JSONObject shareConfig = JSONUtil.parseObj(card.getShareConfig());
        AdminVipCardInfoVo.ShareConfigVo share = new AdminVipCardInfoVo.ShareConfigVo();
        share.setShareTitle(shareConfig.getStr("shareTitle", "专享会员，为你而来！"));
        share.setShareDesc(shareConfig.getStr("shareDesc", "开通专享会员，解锁专属权益，多档套餐可选，低价尝鲜试用，即刻升级体验！"));
        share.setShareImageH5(shareConfig.getStr("shareImageH5", "/static/resource/images/member/vip_card/share/share_h5.jpg"));
        share.setShareImageWeapp(shareConfig.getStr("shareImageWeapp", "/static/resource/images/member/vip_card/share/share_weapp.jpg"));
        vo.setShareConfig(share);

        // 查询规格项
        List<MemberVipCardItem> items = vipCardItemMapper.selectList(
                new LambdaQueryWrapper<MemberVipCardItem>().eq(MemberVipCardItem::getCardId, cardId));
        if (CollUtil.isNotEmpty(items)) {
            List<AdminVipCardInfoVo.VipCardItemVo> itemVos = items.stream().map(item -> {
                AdminVipCardInfoVo.VipCardItemVo itemVo = new AdminVipCardInfoVo.VipCardItemVo();
                BeanUtil.copyProperties(item, itemVo);
                return itemVo;
            }).collect(Collectors.toList());
            vo.setItem(itemVos);
        }
        return vo;
    }

    // ========== 添加 ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(AdminVipCardAddParam param) {
        // 处理数据
        Map<String, Object> processed = handleCardData(param);
        MemberVipCard card = new MemberVipCard();
        BeanUtil.copyProperties(param, card);
        card.setBenefitsConfig(JSONUtil.toJsonStr(param.getBenefitsConfig()));
        card.setBenefitsId((String) processed.get("benefits_id"));
        card.setBenefitsItemId((String) processed.get("benefits_item_id"));
        card.setCardConfig(param.getCardConfig());
        if (param.getShareConfig() != null) {
            card.setShareConfig(JSONUtil.toJsonStr(param.getShareConfig()));
        }
        card.setCreateTime(System.currentTimeMillis() / 1000);
        card.setSiteId(0); // 平台
        vipCardMapper.insert(card);
        Integer cardId = Math.toIntExact(card.getCardId());

        // 插入规格项
        if (CollUtil.isNotEmpty(param.getItemList())) {
            List<MemberVipCardItem> items = param.getItemList().stream().map(itemParam -> {
                MemberVipCardItem item = new MemberVipCardItem();
                BeanUtil.copyProperties(itemParam, item);
                item.setCardId(cardId);
                return item;
            }).collect(Collectors.toList());
            vipCardItemMapper.insert(items);
        }
        return cardId;
    }

    // ========== 编辑 ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Integer cardId, AdminVipCardEditParam param) {
        MemberVipCard oldCard = vipCardMapper.selectById(cardId);
        Assert.notNull(oldCard, "会员卡不存在");

        // 处理数据
        Map<String, Object> processed = handleCardData(param);
        MemberVipCard updateCard = new MemberVipCard();
        BeanUtil.copyProperties(param, updateCard);
        updateCard.setBenefitsConfig(JSONUtil.toJsonStr(param.getBenefitsConfig()));
        updateCard.setBenefitsId((String) processed.get("benefits_id"));
        updateCard.setBenefitsItemId((String) processed.get("benefits_item_id"));
        updateCard.setCardConfig(param.getCardConfig());
        if (param.getShareConfig() != null) {
            updateCard.setShareConfig(JSONUtil.toJsonStr(param.getShareConfig()));
        }
        updateCard.setUpdateTime(System.currentTimeMillis() / 1000);
        updateCard.setCardId(Long.valueOf(cardId));

        // 更新主表
        vipCardMapper.updateById(updateCard);

        // 处理规格项
        List<MemberVipCardItem> existItems = vipCardItemMapper.selectList(
                new LambdaQueryWrapper<MemberVipCardItem>().eq(MemberVipCardItem::getCardId, cardId));
        List<Integer> existIds = existItems.stream().map(u -> Math.toIntExact(u.getItemId())).toList();
        List<Integer> inputIds = param.getItemList().stream()
                .map(AdminVipCardAddParam.CardItemParam::getItemId)
                .filter(Objects::nonNull)
                .toList();

        // 新增
        List<AdminVipCardAddParam.CardItemParam> toAdd = param.getItemList().stream()
                .filter(item -> item.getItemId() == null)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(toAdd)) {
            List<MemberVipCardItem> addItems = toAdd.stream().map(itemParam -> {
                MemberVipCardItem item = new MemberVipCardItem();
                BeanUtil.copyProperties(itemParam, item);
                item.setCardId(cardId);
                return item;
            }).collect(Collectors.toList());
            vipCardItemMapper.insert(addItems);
        }

        // 更新
        List<AdminVipCardAddParam.CardItemParam> toUpdate = param.getItemList().stream()
                .filter(item -> item.getItemId() != null)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(toUpdate)) {
            for (AdminVipCardAddParam.CardItemParam itemParam : toUpdate) {
                MemberVipCardItem item = new MemberVipCardItem();
                BeanUtil.copyProperties(itemParam, item);
                item.setCardId(cardId);
                vipCardItemMapper.updateById(item);
            }
        }

        // 删除
        List<Integer> delIds = existIds.stream()
                .filter(id -> !inputIds.contains(id))
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(delIds)) {
            vipCardItemMapper.deleteByIds(delIds);
        }
    }

    // ========== 删除 ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(Integer cardId) {
        MemberVipCard card = vipCardMapper.selectById(cardId);
        if (card == null) {
            throw new AdminException("会员卡不存在");
        }
        // 检查购买人数
        int buyCount = coreVipCardBuyService.getCardBuyNum(cardId);
        if (buyCount > 0) {
            throw new AdminException(String.format("该会员卡已被 %d 人购买，不允许删除", buyCount));
        }
        // 状态为开启时不允许删除
        if (Objects.equals(card.getStatus(), VipCardStatusEnum.ON.getCode())) {
            throw new AdminException("该会员卡已上架，不能删除");
        }
        // 软删除或物理删除？这里按PHP物理删除
        vipCardMapper.deleteById(cardId);
        // 删除规格项
        vipCardItemMapper.delete(new LambdaQueryWrapper<MemberVipCardItem>().eq(MemberVipCardItem::getCardId, cardId));
    }

    // ========== 修改状态 ==========
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyStatus(AdminModifyStatusParam param) {
        // 与 PHP 一致：仅将当前开启的卡设为关闭，再更新目标卡状态
        vipCardMapper.update(null,
                new LambdaUpdateWrapper<MemberVipCard>()
                        .eq(MemberVipCard::getStatus, VipCardStatusEnum.ON.getCode())
                        .set(MemberVipCard::getStatus, VipCardStatusEnum.OFF.getCode())
        );
        vipCardMapper.update(null,
                new LambdaUpdateWrapper<MemberVipCard>()
                        .eq(MemberVipCard::getCardId, param.getCardId())
                        .set(MemberVipCard::getStatus, param.getStatus())
        );
    }

    // ========== 辅助方法 ==========

    /**
     * 兼容历史错误数据：关闭状态应为 2，旧版 Java 曾写入 0
     */
    private Integer normalizeCardStatus(Integer status) {
        if (status == null || Objects.equals(status, 0)) {
            return VipCardStatusEnum.OFF.getCode();
        }
        return status;
    }

    /**
     * 处理权益配置数据，提取 benefits_id 和 benefits_item_id
     */
    private Map<String, Object> handleCardData(Object param) {
        List<AdminVipCardAddParam.BenefitsConfigItem> benefitsConfig;
        if (param instanceof AdminVipCardAddParam) {
            benefitsConfig = ((AdminVipCardAddParam) param).getBenefitsConfig();
        } else if (param instanceof AdminVipCardEditParam) {
            benefitsConfig = ((AdminVipCardEditParam) param).getBenefitsConfig();
        } else {
            benefitsConfig = new ArrayList<>();
        }

        List<String> benefitsIds = new ArrayList<>();
        List<String> benefitsItemIds = new ArrayList<>();
        if (CollUtil.isNotEmpty(benefitsConfig)) {
            for (AdminVipCardAddParam.BenefitsConfigItem item : benefitsConfig) {
                if (item.getBenefitsId() != null) {
                    benefitsIds.add(String.valueOf(item.getBenefitsId()));
                }
                if (item.getBenefitsItem() != null && item.getBenefitsItem().getBenefitsItemId() != null) {
                    benefitsItemIds.add(String.valueOf(item.getBenefitsItem().getBenefitsItemId()));
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("benefits_id", CollUtil.join(benefitsIds, ","));
        result.put("benefits_item_id", CollUtil.join(benefitsItemIds, ","));
        return result;
    }
}
