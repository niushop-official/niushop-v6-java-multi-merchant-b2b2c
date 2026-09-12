package com.niu.core.service.core.vip_card.card.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.vip_card.*;
import com.niu.core.enums.vip_card.VipCardExpireStatusEnum;
import com.niu.core.enums.vip_card.VipCardTimeTypeEnum;
import com.niu.core.enums.vip_card.VipCardTrialStatusEnum;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.vip_card.VipCardBenefitsMapper;
import com.niu.core.mapper.vip_card.VipCardBenefitsSendLogMapper;
import com.niu.core.mapper.vip_card.VipCardBuyMapper;
import com.niu.core.mapper.vip_card.VipCardMapper;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import com.niu.core.service.core.notice.ICoreNoticeService;
import com.niu.core.service.core.vip_card.card.ICoreVipCardBenefitsSendLogService;
import com.niu.core.service.core.vip_card.card.ICoreVipCardBuyService;
import com.niu.core.service.core.vip_card.card.ICoreVipCardSaleNumService;
import com.niu.core.service.core.vip_card.card.ICoreVipCardService;
import com.niu.core.service.core.member.ICoreMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoreVipCardBuyServiceImpl implements ICoreVipCardBuyService {

    private static final Logger log = LoggerFactory.getLogger(CoreVipCardBuyServiceImpl.class);

    @Autowired
    private VipCardBuyMapper vipCardBuyMapper;

    @Autowired
    private VipCardBenefitsSendLogMapper sendLogMapper;

    @Autowired
    private VipCardBenefitsMapper benefitsMapper;

    @Autowired
    private VipCardMapper vipCardMapper;

    @Autowired
    private VipCardOrderMapper vipCardOrderMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private ICoreVipCardService coreVipCardService;

    @Autowired
    private ICoreVipCardSaleNumService coreSaleNumService;

    @Autowired
    private ICoreVipCardBenefitsSendLogService sendLogService;

    @Autowired
    private ICoreNoticeService coreNoticeService;

    @Autowired
    private ICoreMemberService coreMemberService;

    // ========== 查询方法 ==========

    @Override
    public MemberVipCardBuy getCardBuy(Integer cardId, Integer memberId) {
        return vipCardBuyMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardBuy>()
                        .eq(MemberVipCardBuy::getCardId, cardId)
                        .eq(MemberVipCardBuy::getMemberId, memberId)
                        .eq(MemberVipCardBuy::getIsExpire, VipCardExpireStatusEnum.EXPIRE_NO.getCode())
        );
    }

    @Override
    public MemberVipCardBuy getMemberBuy(Integer memberId) {
        return vipCardBuyMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardBuy>()
                        .eq(MemberVipCardBuy::getMemberId, memberId)
                        .eq(MemberVipCardBuy::getIsExpire, VipCardExpireStatusEnum.EXPIRE_NO.getCode())
        );
    }

    @Override
    public MemberVipCardBuy getCardBuyTrial(Integer cardId, Integer memberId) {
        return vipCardBuyMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardBuy>()
                        .eq(MemberVipCardBuy::getCardId, cardId)
                        .eq(MemberVipCardBuy::getMemberId, memberId)
                        .eq(MemberVipCardBuy::getIsTrial, VipCardTrialStatusEnum.TRIAL_YES.getCode())
        );
    }

    @Override
    public Integer getCardBuyNum(Integer cardId) {
        return Math.toIntExact(
                vipCardBuyMapper.selectCount(
                        new LambdaQueryWrapper<MemberVipCardBuy>()
                                .eq(MemberVipCardBuy::getCardId, cardId)
                                .eq(MemberVipCardBuy::getIsExpire, VipCardExpireStatusEnum.EXPIRE_NO.getCode())
                )
        );
    }

    // ========== 权益发放 ==========

    @Override
    @Transactional
    public void sendBenefits(MemberVipCardOrderItem orderItem) {
        Integer orderId = orderItem.getOrderId();
        Integer memberId = orderItem.getMemberId();
        Integer cardId = orderItem.getCardId();
        Integer itemId = orderItem.getItemId();
        Integer isRenew = orderItem.getIsRenew();

        // 计算过期时间
        Long expireTime = coreVipCardService.getCardExpireTime(orderItem);

        long now = System.currentTimeMillis() / 1000;
        JSONObject itemConfig = JSONUtil.parseObj(orderItem.getItemConfig());

        // 插入购买记录
        MemberVipCardBuy buy = new MemberVipCardBuy();
        buy.setSiteId(orderItem.getSiteId());
        buy.setOrderId(orderId);
        buy.setMemberId(memberId);
        buy.setCardId(cardId);
        buy.setItemId(itemId);
        buy.setIsTrial(itemConfig.getInt("is_trial", 0));
        buy.setTrialDay(itemConfig.getInt("time_limit", 0));
        buy.setExpireTime(expireTime);
        buy.setBenefitsConfig(orderItem.getBenefitsConfig());
        buy.setItemConfig(orderItem.getItemConfig());
        buy.setCreateTime(now);
        vipCardBuyMapper.insert(buy);
        Integer buyId = Math.toIntExact(buy.getId());

        // 续期：旧卡过期
        if (isRenew == 1) {
            LambdaUpdateWrapper<MemberVipCardBuy> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(MemberVipCardBuy::getCardId, cardId)
                    .eq(MemberVipCardBuy::getMemberId, memberId)
                    .ne(MemberVipCardBuy::getId, buyId)
                    .eq(MemberVipCardBuy::getIsExpire, VipCardExpireStatusEnum.EXPIRE_NO.getCode())
                    .set(MemberVipCardBuy::getIsExpire, VipCardExpireStatusEnum.EXPIRE_YES.getCode())
                    .set(MemberVipCardBuy::getExpireTime, now);
            vipCardBuyMapper.update(null, wrapper);
        }

        // 增加销量
        coreSaleNumService.incSaleNum(cardId, itemId, 1);
    }

    // ========== 批量权益奖励发放（定时任务调用） ==========

    @Override
    public void sendBenefitsGifts() {
        List<MemberVipCardBuy> buyList = vipCardBuyMapper.selectList(
                new LambdaQueryWrapper<MemberVipCardBuy>()
                        .eq(MemberVipCardBuy::getIsExpire, VipCardExpireStatusEnum.EXPIRE_NO.getCode())
                        .eq(MemberVipCardBuy::getIsTrial, VipCardTrialStatusEnum.TRIAL_NO.getCode())
        );

        for (MemberVipCardBuy buy : buyList) {
            Integer siteId = resolveSiteId(buy);
            Integer memberId = buy.getMemberId();
            Integer cardId = buy.getCardId();
            JSONObject benefitsConfig = JSONUtil.parseObj(buy.getBenefitsConfig());

            try {
                for (String key : benefitsConfig.keySet()) {
                    JSONObject benefits = benefitsConfig.getJSONObject(key);
                    if (benefits == null || !benefits.containsKey("gifts")) {
                        continue;
                    }
                    Object gifts = benefits.get("gifts");
                    try {
                        switch (key) {
                            case "stage_reward":
                                handleStageReward(siteId, memberId, cardId, key, gifts, buy, benefits);
                                break;
                            case "cycle_reward":
                                handleCycleReward(siteId, memberId, cardId, key, gifts, buy, benefits);
                                break;
                            case "birthday_reward":
                                handleBirthdayReward(siteId, memberId, cardId, key, gifts);
                                break;
                            default:
                                break;
                        }
                    } catch (Exception e) {
                        log.error("付费会员卡权益奖励发放失败，会员ID: {}, 权益类型: {}, 错误: {}", memberId, key, e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                log.error("付费会员卡权益奖励发放失败，会员ID: {}, 错误: {}", memberId, e.getMessage(), e);
            }
        }
    }

    private void handleStageReward(Integer siteId, Integer memberId, Integer cardId, String type, Object gifts, MemberVipCardBuy buy, JSONObject benefits) {
        Long createTime = buy.getCreateTime();
        Long stageSendTime = benefits.getInt("time", 0) * 86400L;

        MemberVipCardBenefitsSendLog log = sendLogMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardBenefitsSendLog>()
                        .eq(MemberVipCardBenefitsSendLog::getMemberId, memberId)
                        .eq(MemberVipCardBenefitsSendLog::getCardId, cardId)
                        .eq(MemberVipCardBenefitsSendLog::getBenefitsType, type)
        );
        if (log == null && System.currentTimeMillis() / 1000 >= createTime + stageSendTime) {
            sendGifts(siteId, memberId, cardId, type, gifts, "付费会员权益(阶段返利)");
        }
    }

    private void handleCycleReward(Integer siteId, Integer memberId, Integer cardId, String type, Object gifts, MemberVipCardBuy buy, JSONObject benefits) {
        String timeType = benefits.getStr("time_type");
        MemberVipCardBenefitsSendLog lastLog = sendLogMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardBenefitsSendLog>()
                        .eq(MemberVipCardBenefitsSendLog::getMemberId, memberId)
                        .eq(MemberVipCardBenefitsSendLog::getCardId, cardId)
                        .eq(MemberVipCardBenefitsSendLog::getBenefitsType, type)
                        .orderByDesc(MemberVipCardBenefitsSendLog::getCreateTime)
                        .last("limit 1")
        );

        long now = System.currentTimeMillis() / 1000;
        if (VipCardTimeTypeEnum.TIME_TYPE_DAY.getCode().equals(timeType)) {
            int days = benefits.getInt("time_day", 0);
            if (lastLog == null) {
                sendGifts(siteId, memberId, cardId, type, gifts, "付费会员权益(周期返利)");
            } else if (now >= lastLog.getCreateTime() + days * 86400L) {
                sendGifts(siteId, memberId, cardId, type, gifts, "付费会员权益(周期返利)");
            }
        } else if (VipCardTimeTypeEnum.TIME_TYPE_WEEK.getCode().equals(timeType)) {
            int sendWeekDay = benefits.getInt("time_week", 1);
            int todayWeekDay = LocalDate.now().getDayOfWeek().getValue(); // 1=周一，7=周日
            if (todayWeekDay == sendWeekDay) {
                if (lastLog == null) {
                    sendGifts(siteId, memberId, cardId, type, gifts, "付费会员权益(周期返利)");
                } else {
                    String lastWeek = formatYearWeek(lastLog.getCreateTime());
                    String currentWeek = formatYearWeek(System.currentTimeMillis() / 1000);
                    if (!lastWeek.equals(currentWeek)) {
                        sendGifts(siteId, memberId, cardId, type, gifts, "付费会员权益(周期返利)");
                    }
                }
            }
        } else if (VipCardTimeTypeEnum.TIME_TYPE_MONTH.getCode().equals(timeType)) {
            int sendMonthDay = benefits.getInt("time_month", 1);
            LocalDate today = LocalDate.now();
            int maxDay = today.lengthOfMonth();
            int actualDay = Math.min(sendMonthDay, maxDay);
            if (today.getDayOfMonth() == actualDay) {
                if (lastLog == null) {
                    sendGifts(siteId, memberId, cardId, type, gifts, "付费会员权益(周期返利)");
                } else {
                    String lastMonth = LocalDateTime.ofInstant(Instant.ofEpochSecond(lastLog.getCreateTime()), ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("yyyyMM"));
                    String currentMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
                    if (!lastMonth.equals(currentMonth)) {
                        sendGifts(siteId, memberId, cardId, type, gifts, "付费会员权益(周期返利)");
                    }
                }
            }
        }
    }

    private void handleBirthdayReward(Integer siteId, Integer memberId, Integer cardId, String type, Object gifts) {
        Member member = memberMapper.selectById(memberId);
        if (member == null || member.getBirthday() == null || member.getBirthday().isEmpty()) {
            return;
        }
        String birthdayMd = DateUtil.format(DateUtil.parse(member.getBirthday()), "MMdd");
        String todayMd = DateUtil.format(DateUtil.date(), "MMdd");
        if (birthdayMd.equals(todayMd)) {
            MemberVipCardBenefitsSendLog lastLog = sendLogMapper.selectOne(
                    new LambdaQueryWrapper<MemberVipCardBenefitsSendLog>()
                            .eq(MemberVipCardBenefitsSendLog::getMemberId, memberId)
                            .eq(MemberVipCardBenefitsSendLog::getCardId, cardId)
                            .eq(MemberVipCardBenefitsSendLog::getBenefitsType, type)
                            .orderByDesc(MemberVipCardBenefitsSendLog::getCreateTime)
                            .last("limit 1")
            );
            if (lastLog == null) {
                sendGifts(siteId, memberId, cardId, type, gifts, "付费会员权益(生日特权)");
            } else {
                int lastYear = LocalDateTime.ofInstant(Instant.ofEpochSecond(lastLog.getCreateTime()), ZoneId.systemDefault()).getYear();
                int currentYear = LocalDateTime.now().getYear();
                if (lastYear != currentYear) {
                    sendGifts(siteId, memberId, cardId, type, gifts, "付费会员权益(生日特权)");
                }
            }
        }
    }

    /**
     * 格式化年周，对齐 PHP date('YW')
     */
    private String formatYearWeek(long epochSecond) {
        LocalDate date = Instant.ofEpochSecond(epochSecond).atZone(ZoneId.systemDefault()).toLocalDate();
        WeekFields iso = WeekFields.ISO;
        return String.format("%d%02d", date.getYear(), date.get(iso.weekOfWeekBasedYear()));
    }

    /**
     * 解析站点ID：优先购买记录，其次订单，最后会员
     */
    private Integer resolveSiteId(MemberVipCardBuy buy) {
        Integer siteId = buy.getSiteId();
        if (siteId != null && siteId > 0) {
            return siteId;
        }
        if (buy.getOrderId() != null && buy.getOrderId() > 0) {
            MemberVipCardOrder order = vipCardOrderMapper.selectById(buy.getOrderId());
            if (order != null && order.getSiteId() != null && order.getSiteId() > 0) {
                return order.getSiteId();
            }
        }
        if (buy.getMemberId() != null && buy.getMemberId() > 0) {
            Member member = memberMapper.selectById(buy.getMemberId());
            if (member != null && member.getSiteId() != null && member.getSiteId() > 0) {
                return member.getSiteId();
            }
        }
        return siteId != null ? siteId : 0;
    }

    // ========== 发放礼品（异步任务） ==========

    @Override
    public void sendGifts(Integer siteId, Integer memberId, Integer cardId, String type, Object gifts, String memo) {
        Map<String, Object> param = new HashMap<>();
        param.put("from_type", "vip_card_benefits");
        param.put("memo", memo);
        coreMemberService.asyncMemberGiftGrant(siteId, memberId, gifts, param);

        MemberVipCardBenefits benefits = benefitsMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardBenefits>().eq(MemberVipCardBenefits::getType, type)
        );
        Integer benefitsId = Math.toIntExact(benefits != null ? benefits.getBenefitsId() : 0);

        sendLogService.add(memberId, cardId, benefitsId, type);
    }

    // ========== 过期处理 ==========

    @Override
    @Transactional
    public void vipCardBuyExpire() {
        long now = System.currentTimeMillis() / 1000;
        LambdaUpdateWrapper<MemberVipCardBuy> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberVipCardBuy::getIsExpire, VipCardExpireStatusEnum.EXPIRE_NO.getCode())
                .le(MemberVipCardBuy::getExpireTime, now)
                .set(MemberVipCardBuy::getIsExpire, VipCardExpireStatusEnum.EXPIRE_YES.getCode());
        vipCardBuyMapper.update(null, wrapper);
    }

    @Override
    public void vipCardBuyExpireRemind() {
        long now = System.currentTimeMillis() / 1000;
        long threeDaysLater = now + 3 * 24 * 3600L;
        List<MemberVipCardBuy> list = vipCardBuyMapper.selectList(
                new LambdaQueryWrapper<MemberVipCardBuy>()
                        .eq(MemberVipCardBuy::getIsExpire, VipCardExpireStatusEnum.EXPIRE_NO.getCode())
                        .le(MemberVipCardBuy::getExpireTime, threeDaysLater)
        );
        for (MemberVipCardBuy buy : list) {
            try {
                log.info("发送会员卡到期提醒通知 - id: {}, member_id: {}", buy.getId(), buy.getMemberId());
                MemberVipCard card = vipCardMapper.selectById(buy.getCardId());
                Map<String, Object> params = new HashMap<>();
                params.put("member_id", buy.getMemberId());
                params.put("order_id", buy.getOrderId());
                params.put("card_name", card != null ? card.getCardName() : "");
                params.put("expire_time", buy.getExpireTime());
                params.put("create_time", buy.getCreateTime());
                coreNoticeService.send(resolveSiteId(buy), "vip_card_buy_expire_remind", params);
            } catch (Exception e) {
                log.error("发送会员卡到期提醒通知失败 - id: {}, member_id: {}, 错误: {}", buy.getId(), buy.getMemberId(), e.getMessage());
            }
        }
    }
}
