package com.niu.core.service.core.vip_card.order.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.entity.vip_card.MemberVipCardOrderItem;
import com.niu.core.enums.o2o.PayTypeEnum;
import com.niu.core.enums.vip_card.VipCardOrderStatusEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.vip_card.VipCardOrderItemMapper;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappShippingEventService;
import com.niu.core.service.core.delivery_weapp.param.WeappShippingParam;
import com.niu.core.service.core.delivery_weapp.param.WeappUploadShippingParam;
import com.niu.core.service.core.vip_card.order.ICoreVipCardOrderWeappDeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoreVipCardOrderWeappDeliveryServiceImpl implements ICoreVipCardOrderWeappDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(CoreVipCardOrderWeappDeliveryServiceImpl.class);

    @Autowired
    private VipCardOrderMapper orderMapper;

    @Autowired
    private VipCardOrderItemMapper orderItemMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private PayMapper payMapper;

    @Autowired
    private ICoreDeliveryWeappShippingEventService deliveryWeappShippingEventService;

    @Override
    public void orderShippingUploadShippingInfo(Integer orderId) {
        try {
            // 1. 查询订单（必须为 vip_card 类型且已完成）
            MemberVipCardOrder order = orderMapper.selectOne(
                    new LambdaQueryWrapper<MemberVipCardOrder>()
                            .eq(MemberVipCardOrder::getOrderId, orderId)
                            .eq(MemberVipCardOrder::getOrderType, "vip_card")
                            .eq(MemberVipCardOrder::getStatus, VipCardOrderStatusEnum.FINISH.getCode())
            );
            if (order == null) {
                log.warn("订单不存在或状态不是已完成: orderId={}", orderId);
                return;
            }

            // 2. 查询订单项
            List<MemberVipCardOrderItem> orderItems = orderItemMapper.selectList(
                    new LambdaQueryWrapper<MemberVipCardOrderItem>()
                            .eq(MemberVipCardOrderItem::getOrderId, orderId)
            );
            if (CollUtil.isEmpty(orderItems)) {
                log.warn("订单项不存在: orderId={}", orderId);
                return;
            }

            // 3. 查询支付信息
            Pay pay = payMapper.selectOne(
                    new LambdaQueryWrapper<Pay>()
                            .eq(Pay::getOutTradeNo, order.getOutTradeNo())
            );
            if (pay == null) {
                log.warn("支付信息不存在: outTradeNo={}", order.getOutTradeNo());
                return;
            }

            // 4. 判断是否为微信支付
            String payType = pay.getType();
            if (!PayTypeEnum.WECHATPAY.getKey().equals(payType) && !"huifu_wechatpay".equals(payType)) {
                log.info("订单未使用微信支付，跳过发货信息录入: orderId={}", orderId);
                return;
            }

            // 5. 查询会员 weapp_openid
            Member member = memberMapper.selectById(order.getMemberId());
            String openid = member != null ? member.getWeappOpenid() : "";

            // 6. 构建 shipping_list（取第一项商品信息）
            MemberVipCardOrderItem firstItem = orderItems.get(0);

            List<WeappUploadShippingParam.UploadShippingParam> shippingList = new ArrayList<>();
            WeappUploadShippingParam.UploadShippingParam shipping = new WeappUploadShippingParam.UploadShippingParam();
            shipping.setTrackingNo("");
            shipping.setExpressCompany("");
            shipping.setItemDesc("付费会员卡*" + firstItem.getNum());
            WeappUploadShippingParam.ContactInfo contact = new WeappUploadShippingParam.ContactInfo();
            contact.setConsignorContact("");
            contact.setReceiverContact("");
            shipping.setContact(contact);
            shippingList.add(shipping);

            // 7. 组装请求参数
            WeappShippingParam param = new WeappShippingParam();
            param.setTransactionId(pay.getTradeNo());
            param.setLogisticsType(3);
            param.setDeliveryMode(1);
            param.setShippingList(shippingList);
            param.setWeappOpenid(openid);
            param.setIsAllDelivered(true);
            param.setOrderNumberType(2);
            // 扩展字段（用于回调或记录）
            param.setSiteId(RequestUtils.defaultSiteId());
            param.setTradeId(orderId);
            param.setTradeType("vip_card");
            param.setTradeNo(order.getOrderNo());
            param.setSplitTradeId(orderId);
            param.setSplitTradeType("vip_card");
            param.setSplitTradeNo(order.getOrderNo());

            // 8. 调用微信小程序发货服务
            deliveryWeappShippingEventService.weappShipping(param);

        } catch (Exception e) {
            log.error("付费会员卡小程序订单发货失败 - orderId: {}, error: {}", orderId, e.getMessage(), e);
        }
    }
}