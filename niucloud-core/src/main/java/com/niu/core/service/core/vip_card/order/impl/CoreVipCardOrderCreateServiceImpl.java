package com.niu.core.service.core.vip_card.order.impl;

import cn.hutool.json.JSONUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.language.LanguageUtils;
import com.niu.core.entity.vip_card.MemberVipCard;
import com.niu.core.entity.vip_card.MemberVipCardItem;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.entity.vip_card.MemberVipCardOrderItem;
import com.niu.core.enums.vip_card.VipCardOrderLogEnum;
import com.niu.core.enums.vip_card.VipCardOrderStatusEnum;
import com.niu.core.enums.vip_card.VipCardSaleStatusEnum;
import com.niu.core.enums.vip_card.VipCardStatusEnum;
import com.niu.core.event.vip_card.order.AfterVipCardOrderCreateEvent;
import com.niu.core.mapper.vip_card.VipCardOrderItemMapper;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.vip_card.card.ICoreVipCardItemService;
import com.niu.core.service.core.vip_card.card.ICoreVipCardService;
import com.niu.core.service.core.vip_card.card.vo.VipCardItemVo;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderCreateService;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderEventService;
import com.niu.core.service.core.vip_card.order.param.CoreVipCardOrderCreateParam;
import com.niu.core.service.core.vip_card.order.vo.CoreVipCardOrderCreateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class CoreVipCardOrderCreateServiceImpl implements ICoreVipCardOrderCreateService {

    @Autowired
    private ICoreVipCardService coreVipCardService;

    @Autowired
    private ICoreVipCardItemService coreVipCardItemService;

    @Autowired
    private ICorePayService corePayService;

    @Autowired
    private ICoreVipCardOrderEventService orderEventService;

    @Autowired
    private VipCardOrderMapper orderMapper;

    @Autowired
    private VipCardOrderItemMapper orderItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoreVipCardOrderCreateResult create(CoreVipCardOrderCreateParam param) {
        // 1. 校验会员卡
        MemberVipCard vipCard = coreVipCardService.getInfo(param.getCardId());
        if (vipCard == null) {
            throw new CommonException("VIP_CARD_NOT_EXIST");
        }
        if (!VipCardStatusEnum.ON.getCode().equals(vipCard.getStatus())) {
            throw new CommonException("VIP_CARD_STATUS_ERROR");
        }

        // 2. 校验规格
        MemberVipCardItem vipCardItem = coreVipCardItemService.getInfo(param.getItemId());
        if (vipCardItem == null) {
            throw new CommonException("VIP_CARD_ITEM_NOT_EXIST");
        }
        if (!VipCardSaleStatusEnum.ON.getCode().equals(vipCardItem.getIsSale())) {
            throw new CommonException("VIP_CARD_ITEM_STATUS_ERROR");
        }

        // 3. 处理试用/续期信息
        VipCardItemVo processedItem = coreVipCardService.getCardTrialAndRenewInfo(vipCard, vipCardItem, param.getMemberId());

        // 4. 获取会员卡权益
        Map<String, Object> cardBenefits = coreVipCardService.getCardBenefits(vipCard);

        // 5. 构建订单项配置
        Map<String, Object> itemConfig = new HashMap<>();
        itemConfig.put("item_name", processedItem.getItemName());
        itemConfig.put("time_limit", processedItem.getTimeLimit());
        itemConfig.put("time_type", processedItem.getTimeType());
        itemConfig.put("price", processedItem.getPrice());
        itemConfig.put("cost_price", processedItem.getCostPrice());
        itemConfig.put("renew_price", processedItem.getRenewPrice());
        itemConfig.put("is_trial", processedItem.getIsTrial());

        // 6. 计算订单金额
        BigDecimal orderMoney = processedItem.getPrice();

        // 8. 构建订单主表数据
        MemberVipCardOrder order = new MemberVipCardOrder();
        order.setOrderNo(CommonUtils.createNo());
        order.setBody(vipCard.getCardName() + " " + processedItem.getItemName());
        order.setOrderFrom(param.getOrderFrom());
        order.setOrderType("vip_card");
        order.setStatus(VipCardOrderStatusEnum.WAIT_PAY.getCode());
        order.setMemberId(param.getMemberId());
        order.setIp(RequestUtils.ip()); // 获取IP
        order.setOrderDiscountMoney(BigDecimal.ZERO);
        order.setOrderMoney(orderMoney);
        order.setSiteId(param.getSiteId());
        order.setWeappVersion(param.getWeappVersion());
        order.setCreateTime(System.currentTimeMillis() / 1000);
        // 9. 保存订单
        orderMapper.insert(order);
        Integer orderId = Math.toIntExact(order.getOrderId());

        // 10. 创建支付记录
        corePayService.create(
                param.getSiteId(),
                VipCardOrderLogEnum.MEMBER.getCode(),    // PayDict.MEMBER
                param.getMemberId(),
                orderMoney,
                "vip_card",
                orderId,
                LanguageUtils.get("dict_order.trade_type_vip_card")
        );

        // 11. 构建订单项
        MemberVipCardOrderItem orderItem = new MemberVipCardOrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setMemberId(param.getMemberId());
        orderItem.setCardId(param.getCardId());
        orderItem.setItemId(param.getItemId());
        orderItem.setCardName(vipCard.getCardName());
        orderItem.setItemName(processedItem.getItemName());
//        orderItem.setItemType("vip_card");
        orderItem.setPrice(orderMoney);
        orderItem.setNum(1);
        orderItem.setItemMoney(orderMoney);
        orderItem.setIsRefund(0);
        orderItem.setBenefitsConfig(JSONUtil.toJsonStr(cardBenefits));   // 直接存 Map，mybatis-plus 自动转 JSON
        orderItem.setItemConfig(JSONUtil.toJsonStr(itemConfig));
        orderItem.setIsRenew(processedItem.getIsRenew());
        orderItem.setIsTrial(processedItem.getIsTrial());
        orderItemMapper.insert(orderItem);

        // 12. 触发订单创建后事件（异步设置超时等）
        AfterVipCardOrderCreateEvent event = new AfterVipCardOrderCreateEvent(
                this, order, orderId, System.currentTimeMillis() / 1000,
                param.getMainType() != null ? param.getMainType() : VipCardOrderLogEnum.MEMBER.getCode(),
                param.getMainId() != null ? param.getMainId() : param.getMemberId()
        );
        orderEventService.orderCreateAfter(event);

        // 13. 返回结果
        CoreVipCardOrderCreateResult result = new CoreVipCardOrderCreateResult();
        result.setTradeType("vip_card");
        result.setTradeId(orderId);
        result.setOrderNo(order.getOrderNo());
        return result;
    }
}