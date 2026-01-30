package com.niu.core.service.core.recharge.order.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.entity.recharge.RechargeOrderItem;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.recharge.RechargeOrderItemMapper;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappShippingEventService;
import com.niu.core.service.core.delivery_weapp.param.WeappShippingParam;
import com.niu.core.service.core.delivery_weapp.param.WeappUploadShippingParam;
import com.niu.core.service.core.recharge.order.ICoreOrderWeappDeliveryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
public class CoreOrderWeappDeliveryServiceImpl implements ICoreOrderWeappDeliveryService {

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    RechargeOrderItemMapper rechargeOrderItemMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    PayMapper payMapper;

    @Resource
    ICoreDeliveryWeappShippingEventService coreDeliveryWeappShippingEventService;

    /**
     * 微信小程序 发货信息录入接口
     *
     * @param orderId 订单ID
     * @return 微信小程序 发货信息录入接口结果
     */
    @Override
    public String orderShippingUploadShippingInfo(Integer orderId) {
        //查询订单
        RechargeOrder order = rechargeOrderMapper.selectOne(new LambdaQueryWrapper<RechargeOrder>()
                .eq(RechargeOrder::getOrderId, orderId)
                .eq(RechargeOrder::getOrderType, "recharge")
                .eq(RechargeOrder::getOrderStatus, RechargeOrderStatusEnum.FINISH.getStatus())
        );
        if (ObjectUtil.isEmpty(order)) {
            return "";
        }
        //查询订单
        List<RechargeOrderItem> item = rechargeOrderItemMapper.selectList(new LambdaQueryWrapper<RechargeOrderItem>()
                .eq(RechargeOrderItem::getOrderId, orderId)
        );
        if (item.isEmpty()) {
            return "";
        }
        //查询会员信息
        Member member = memberMapper.selectById(order.getMemberId());

        //查询支付信息
        Pay pay = payMapper.selectOne(new LambdaQueryWrapper<Pay>()
                .eq(Pay::getOutTradeNo, order.getOutTradeNo()));
        if (ObjectUtil.isEmpty(order)) {
            return "";
        }

        try {
            log.info("rechargeOrder UploadShippingInfo:{}", JSONUtil.toJsonStr(pay));
            if (!TransferTypeEnum.WECHATPAY.getKey().equals(pay.getType()) || !"huifu_wechatpay".equals(pay.getType()))
                return "订单未使用微信支付";

            WeappShippingParam shippingData = new WeappShippingParam();
            shippingData.setTransactionId(pay.getTradeNo());
            // 物流模式，发货方式枚举值：1、实体物流配送采用快递公司进行实体物流配送形式 2、同城配送 3、虚拟商品，虚拟商品，例如话费充值，点卡等，无实体配送形式 4、用户自提
            shippingData.setLogisticsType(3);
            // 发货模式，发货模式枚举值：1、UNIFIED_DELIVERY（统一发货）2、SPLIT_DELIVERY（分拆发货）
            shippingData.setDeliveryMode(1);
            // 物流信息列表
            List<WeappUploadShippingParam.UploadShippingParam> shippingList = new LinkedList<>();
            WeappUploadShippingParam.UploadShippingParam shippingListBean = new WeappUploadShippingParam.UploadShippingParam();
            WeappUploadShippingParam.ContactInfo contact = new WeappUploadShippingParam.ContactInfo();
            contact.setReceiverContact("");
            contact.setConsignorContact("");
            shippingListBean.setItemDesc("会员充值*" + item.get(0).getNum());
            shippingListBean.setContact(contact);
            shippingListBean.setTrackingNo("");
            shippingListBean.setExpressCompany("");
            shippingList.add(shippingListBean);
            // 物流信息列表，发货物流单列表，支持统一发货（单个物流单）和分拆发货（多个物流单）两种模式，多重性: [1, 10]
            shippingData.setShippingList(shippingList);
            // 用户标识，用户在小程序appid下的唯一标识。 下单前需获取到用户的Openid 示例值: oUpF8uMuAJO_M2pxb1Q9zNjWeS6o 字符字节限制: [1, 128]
            shippingData.setWeappOpenid(member.getWeappOpenid());
            shippingData.setIsAllDelivered(true);
            shippingData.setOrderNumberType(2);

            shippingData.setSiteId(RequestUtils.defaultSiteId());
            shippingData.setTradeId(order.getOrderId());
            shippingData.setTradeType("recharge");
            shippingData.setTradeNo(order.getOrderNo());
            shippingData.setSplitTradeId(order.getOrderId());
            shippingData.setSplitTradeType("recharge");
            shippingData.setSplitTradeNo(order.getOrderNo());
            coreDeliveryWeappShippingEventService.weappShipping(shippingData);
        } catch (Exception e) {
            log.error("微信小程序发货信息录入接口异常:{}", e.getMessage());
        }
        return "";
    }
}
