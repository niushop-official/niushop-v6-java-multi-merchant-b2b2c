package com.niu.core.service.core.vip_card.order.impl;

import cn.hutool.json.JSONUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.vip_card.MemberVipCardOrderItem;
import com.niu.core.enums.vip_card.VipCardTimeTypeEnum;
import com.niu.core.service.admin.notice.INoticeService;
import com.niu.core.service.core.notice.ICoreNoticeService;
import com.niu.core.service.core.vip_card.card.ICoreVipCardBuyService;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderFinishService;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderWeappDeliveryService;
import com.niu.core.service.core.vip_card.order.param.VipCardOrderFinishParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class CoreVipCardOrderFinishServiceImpl implements ICoreVipCardOrderFinishService {

    private static final Logger log = LoggerFactory.getLogger(CoreVipCardOrderFinishServiceImpl.class);

    @Autowired
    private ICoreVipCardBuyService coreVipCardBuyService;

    @Autowired
    private ICoreNoticeService coreNoticeService;

    @Autowired
    private ICoreVipCardOrderWeappDeliveryService orderWeappDeliveryService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finish(VipCardOrderFinishParam param) {
        // 1. 发放会员卡权益
        sendBenefits(param);

        // 2. 发放购卡返利奖励
        sendGifts(param);

        // 3. 发送开通成功通知
        sendNotice(param);

        // 4. 小程序发货信息录入
        orderWeappDeliveryService.orderShippingUploadShippingInfo(param.getOrderId());

        // 5. 结算（已注释，暂不实现）
        // (new CoreOrderSettlementService())->settlement($data);
    }

    private void sendBenefits(VipCardOrderFinishParam param) {
        // 从 orderItem 中提取订单项信息
        MemberVipCardOrderItem orderItem = param.getOrderItem();
        if (orderItem == null) {
            throw new CommonException("订单项数据不存在");
        }
        coreVipCardBuyService.sendBenefits(orderItem);
    }

    private void sendGifts(VipCardOrderFinishParam param) {
        MemberVipCardOrderItem orderItem = param.getOrderItem();
        if (orderItem == null) {
            return;
        }
        Map<String, Object> itemConfig = JSONUtil.parseObj(orderItem.getItemConfig());
        Map<String, Object> benefitsConfig = JSONUtil.parseObj(orderItem.getBenefitsConfig());
        // 判断是否为试用（is_trial == 0）且存在 buy_card_reward
        Integer isTrial = (Integer) itemConfig.getOrDefault("is_trial", 1);
        if (isTrial == 0 && benefitsConfig.containsKey("buy_card_reward")) {
            Map<String, Object> buyCardReward = JSONUtil.parseObj(benefitsConfig.get("buy_card_reward"));
            Object gifts = buyCardReward.get("gifts");
            if (gifts != null) {
                Integer siteId = param.getSiteId();
                Integer memberId = param.getMemberId();
                Integer cardId = orderItem.getCardId();
                coreVipCardBuyService.sendGifts(siteId, memberId, cardId, "buy_card_reward", gifts, "付费会员权益(购卡返利)");
            }
        }
    }

    private void sendNotice(VipCardOrderFinishParam param) {
        MemberVipCardOrderItem orderItem = param.getOrderItem();
        Map<String, Object> orderItemConfig = JSONUtil.parseObj(orderItem.getItemConfig());
        String timeLimit = String.valueOf(orderItemConfig.getOrDefault("time_limit", ""));
        String timeType = orderItemConfig.getOrDefault("time_type", "").toString();

        String timeDesc = switchTimeType(timeType, timeLimit);

        Map<String, Object> params = new HashMap<>();
        params.put("order_id", param.getOrderId());
        params.put("card_name", orderItem.getCardName());
        params.put("order_money", param.getOrderMoney());
        params.put("time", timeDesc);
        params.put("item_name", orderItemConfig.getOrDefault("item_name", ""));
        params.put("create_time", param.getCreateTime());

        coreNoticeService.send(param.getSiteId(), "vip_card_open_success", params);
    }

    private String switchTimeType(String timeType, String timeLimit) {
        String time = "";
        if (timeType == null) return time;
        switch (VipCardTimeTypeEnum.getEnumByCode(timeType)) {
            case TIME_TYPE_DAY:
                time = timeLimit + "天";
                break;
            case TIME_TYPE_MONTH:
                time = timeLimit + "月";
                break;
            case TIME_TYPE_YEAR:
                time = timeLimit + "年";
                break;
            default:
                break;
        }
        return time;
    }
}
