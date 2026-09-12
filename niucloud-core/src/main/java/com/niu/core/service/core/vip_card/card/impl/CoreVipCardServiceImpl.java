package com.niu.core.service.core.vip_card.card.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.vip_card.*;
import com.niu.core.enums.vip_card.*;
import com.niu.core.mapper.vip_card.*;
import com.niu.core.service.core.vip_card.card.ICoreVipCardService;
import com.niu.core.service.core.vip_card.card.vo.VipCardItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class CoreVipCardServiceImpl implements ICoreVipCardService {

    @Autowired
    private VipCardMapper vipCardMapper;

    @Autowired
    private VipCardBenefitsMapper benefitsMapper;

    @Autowired
    private VipCardBenefitsItemMapper benefitsItemMapper;

    @Autowired
    private VipCardBuyMapper vipCardBuyMapper;

    @Autowired
    private VipCardOrderItemMapper vipCardOrderItemMapper;

    @Override
    public MemberVipCard getInfo(Integer cardId) {
        return vipCardMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCard>().eq(MemberVipCard::getCardId, cardId)
        );
    }

    @Override
    public Map<String, Object> getCardBenefits(MemberVipCard vipCard) {
        Map<String, Object> benefitsMap = new HashMap<>();
        String benefitsConfigJson = vipCard.getBenefitsConfig();
        if (benefitsConfigJson == null || benefitsConfigJson.isEmpty()) {
            return benefitsMap;
        }
        JSONArray benefitsArray = JSONUtil.parseArray(benefitsConfigJson);
        for (Object obj : benefitsArray) {
            JSONObject item = (JSONObject) obj;
            Integer benefitsId = firstNonNullInt(item.getInt("benefits_id"), item.getInt("benefitsId"));
            MemberVipCardBenefits benefits = benefitsMapper.selectOne(
                    new LambdaQueryWrapper<MemberVipCardBenefits>().eq(MemberVipCardBenefits::getBenefitsId, benefitsId)
            );
            if (benefits == null) {
                throw new CommonException("VIP_CARD_BENEFITS_NOT_EXIST");
            }
            JSONObject benefitsConfig = new JSONObject();
            Object benefitsItemObj = item.get("benefits_item");
            if (benefitsItemObj == null) {
                benefitsItemObj = item.get("benefitsItem");
            }
            if (benefitsItemObj != null) {
                JSONObject benefitsItemJson = (JSONObject) benefitsItemObj;
                Integer benefitsItemId = firstNonNullInt(
                        benefitsItemJson.getInt("benefits_item_id"),
                        benefitsItemJson.getInt("benefitsItemId")
                );
                MemberVipCardBenefitsItem benefitsItem = benefitsItemMapper.selectOne(
                        new LambdaQueryWrapper<MemberVipCardBenefitsItem>().eq(MemberVipCardBenefitsItem::getBenefitsItemId, benefitsItemId)
                );
                if (benefitsItem == null) {
                    throw new CommonException("VIP_CARD_BENEFITS_ITEM_NOT_EXIST");
                }
                String config = benefitsItem.getBenefitsConfig();
                if (config != null && !config.isEmpty()) {
                    benefitsConfig = JSONUtil.parseObj(config);
                }
            }
            benefitsMap.put(benefits.getType(), benefitsConfig);
        }
        return benefitsMap;
    }

    private Integer firstNonNullInt(Integer first, Integer second) {
        return first != null ? first : second;
    }

    /**
     * 获取会员卡过期时间
     * @param orderItem 订单项目参数
     * @return 返回结果
     */
    @Override
    public Long getCardExpireTime(MemberVipCardOrderItem orderItem) {
        boolean isRenew = VipCardRenewStatusEnum.RENEW_YES.getCode().equals(orderItem.getIsRenew());
        String itemConfigJson = orderItem.getItemConfig();
        JSONObject itemConfig = JSONUtil.parseObj(itemConfigJson);
        Integer timeLimit = itemConfig.getInt("time_limit");
        VipCardTimeTypeEnum timeType = VipCardTimeTypeEnum.getEnumByCode(itemConfig.getStr("time_type"));

        // 计算开始时间
        long expireTime;
        if (isRenew) {
            MemberVipCardBuy cardBuy = getActiveCardBuy(orderItem.getCardId(), orderItem.getMemberId());
            // 会员卡续期
            if (cardBuy != null && cardBuy.getExpireTime() != null) {
                // 解析 expireTime（假设为时间戳字符串或直接 Long）
                //将旧卡的剩余时间转移到新卡上
                expireTime = cardBuy.getExpireTime(); // 若为字符串，转成时间戳
            } else {
                expireTime = System.currentTimeMillis() / 1000;
            }
        } else {
            expireTime = System.currentTimeMillis() / 1000;
        }

        // 根据时间类型增加时长
        switch (timeType) {
            case TIME_TYPE_DAY:
                expireTime += timeLimit * 24L * 60 * 60;
                break;
            case TIME_TYPE_MONTH:
                expireTime += timeLimit * 24L * 60 * 60 * 30;
                break;
            case TIME_TYPE_YEAR:
                expireTime += timeLimit * 24L * 60 * 60 * 365;
                break;
            default:
                break;
        }
        return expireTime;
    }

    @Override
    public VipCardItemVo getCardTrialAndRenewInfo(MemberVipCard vipCard, MemberVipCardItem vipCardItem, Integer memberId) {
        Integer isTrialOpen = vipCard.getIsTrialOpen();
        Integer isTrial = vipCardItem.getIsTrial();

        VipCardItemVo vo = new VipCardItemVo();
        BeanUtil.copyProperties(vipCardItem, vo);

        // 获取购买记录和试用记录
        Integer cardId = Math.toIntExact(vipCard.getCardId());
        MemberVipCardBuy buyInfo = getActiveCardBuy(cardId, memberId);
        MemberVipCardBuy buyTrialInfo = getTrialCardBuy(cardId, memberId);
        // 查询订单（试用且未关闭）
        String sql = "SELECT order_id FROM ?_member_vip_card_order WHERE status != ".replace("?_", GlobalConfig.tablePrefix);
        MemberVipCardOrderItem orderItem = vipCardOrderItemMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardOrderItem>()
                        .eq(MemberVipCardOrderItem::getCardId, vipCard.getCardId())
                        .eq(MemberVipCardOrderItem::getMemberId, memberId)
                        .eq(MemberVipCardOrderItem::getIsTrial, VipCardTrialStatusEnum.TRIAL_YES.getCode())
                        .inSql(MemberVipCardOrderItem::getOrderId, sql + VipCardOrderStatusEnum.CLOSE.getCode())
                        .last("limit 1")
        );

        // 重置续期标识
        vo.setIsRenew(VipCardRenewStatusEnum.RENEW_NO.getCode());

        // 判断是否可试用
        if (buyInfo == null && buyTrialInfo == null && orderItem == null) {
            if (VipCardTrialOpenStatusEnum.TRIAL_OPEN.getCode().equals(isTrialOpen) && VipCardTrialStatusEnum.TRIAL_YES.getCode().equals(isTrial)) {
                vo.setPrice(vipCard.getTrialPrice());
                vo.setTimeLimit(vipCard.getTrialDay());
                vo.setTimeType(VipCardTimeTypeEnum.TIME_TYPE_DAY.getCode());
            }
        } else {
            // 已有购买或试用记录，禁止试用
            vo.setIsTrial(VipCardTrialStatusEnum.TRIAL_NO.getCode());
            // 检查续期
            if (buyInfo != null) {
                vo.setIsRenew(VipCardRenewStatusEnum.RENEW_YES.getCode());
                BigDecimal renewPrice = vo.getRenewPrice();
                if (renewPrice != null && renewPrice.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal originalPrice = vo.getPrice();
                    BigDecimal subtractMoney = originalPrice.subtract(renewPrice).setScale(2, RoundingMode.HALF_UP).max(BigDecimal.ZERO);
                    vo.setRenewSubtractMoney(subtractMoney);
                    vo.setPrice(renewPrice);
                }
            }
        }
        return vo;
    }

    private MemberVipCardBuy getActiveCardBuy(Integer cardId, Integer memberId) {
        return vipCardBuyMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardBuy>()
                        .eq(MemberVipCardBuy::getCardId, cardId)
                        .eq(MemberVipCardBuy::getMemberId, memberId)
                        .eq(MemberVipCardBuy::getIsExpire, VipCardExpireStatusEnum.EXPIRE_NO.getCode())
        );
    }

    private MemberVipCardBuy getTrialCardBuy(Integer cardId, Integer memberId) {
        return vipCardBuyMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardBuy>()
                        .eq(MemberVipCardBuy::getCardId, cardId)
                        .eq(MemberVipCardBuy::getMemberId, memberId)
                        .eq(MemberVipCardBuy::getIsTrial, VipCardTrialStatusEnum.TRIAL_YES.getCode())
        );
    }
}
