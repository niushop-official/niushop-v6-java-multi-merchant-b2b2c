package com.niu.core.service.core.recharge.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CommonUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.entity.recharge.RechargeOrderItem;
import com.niu.core.entity.recharge.RechargePackage;
import com.niu.core.enums.recharge.RechargeOrderLogEnum;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.mapper.recharge.RechargeOrderItemMapper;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.mapper.recharge.RechargePackageMapper;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.recharge.order.ICoreOrderCreateService;
import com.niu.core.service.core.recharge.order.ICoreOrderEventService;
import com.niu.core.service.core.recharge.order.param.AfterOrderCreateParam;
import com.niu.core.service.core.recharge.order.param.RechargeOrderCreateParam;
import com.niu.core.service.core.recharge.order.vo.RechargeOrderCreateVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class CoreOrderCreateServiceImpl implements ICoreOrderCreateService {

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    RechargeOrderItemMapper rechargeOrderItemMapper;

    @Resource
    ICorePayService corePayService;

    @Resource
    ICoreOrderEventService coreOrderEventService;

    @Resource
    RechargePackageMapper rechargeMapper;

    /**
     * 创建订单
     * @param param 创建参数
     * @return 创建结果
     */
    @Override
    @Transactional
    public RechargeOrderCreateVo create(RechargeOrderCreateParam param) {
        BigDecimal orderMoney = param.getRechargeMoney();
        BigDecimal orderItemMoney = param.getRechargeMoney();
        BigDecimal orderFaceMoney = param.getRechargeMoney();
        BigDecimal orderGiveMoney = BigDecimal.ZERO;
        Integer orderGivePoint = 0;
        Integer orderGiveGrowth = 0;
        //查询充值套餐
        if(ObjectUtil.isNotEmpty(param.getPackageId())){
            RechargePackage rechargePackage = rechargeMapper.selectById(param.getPackageId());
            if (ObjectUtil.isEmpty(rechargePackage)){
                throw new CommonException("充值套餐不存在");
            }
            orderMoney = rechargePackage.getPrice();
            orderItemMoney = rechargePackage.getPrice();
            orderFaceMoney = rechargePackage.getPrice().add(rechargePackage.getGiveMoney());
            orderGiveMoney = rechargePackage.getGiveMoney();
            orderGivePoint = rechargePackage.getPoint();
            orderGiveGrowth = rechargePackage.getGrowth();
        }

        String mainType = ObjectUtil.isNotEmpty(param.getMainType()) ? param.getMainType() : RechargeOrderLogEnum.MEMBER.getType();
        Integer mainId = ObjectUtil.isNotEmpty(param.getMainId()) ? param.getMainId() : param.getMemberId();
        try {
            RechargeOrderCreateVo vo = new RechargeOrderCreateVo();
            vo.setTradeType("recharge");

            RechargeOrder order = new RechargeOrder();
            order.setOrderNo(CommonUtils.createNo());
            order.setOrderFrom(param.getOrderFrom());
            order.setOrderType("recharge");
            order.setOrderStatus(RechargeOrderStatusEnum.WAIT_PAY.getStatus());
            order.setMemberId(param.getMemberId());
            order.setIp(RequestUtils.ip());
            order.setMemberMessage(ObjectUtil.defaultIfNull(param.getMemberMessage(), ""));
            order.setOrderItemMoney(orderItemMoney);
            order.setOrderDiscountMoney(BigDecimal.ZERO);
            order.setOrderMoney(orderMoney);
            order.setSiteId(param.getSiteId());
            order.setOrderFaceMoney(orderFaceMoney);
            order.setOrderGiveMoney(orderGiveMoney);
            order.setOrderGivePoint(orderGivePoint);
            order.setOrderGiveGrowth(orderGiveGrowth);
            order.setCreateTime(System.currentTimeMillis() / 1000);
            this.rechargeOrderMapper.insert(order);

            //添加订单支付表
            corePayService.create(param.getSiteId(), RechargeOrderLogEnum.MEMBER.getType(), mainId, orderMoney, "recharge", order.getOrderId(), "订单充值");


            RechargeOrderItem orderItem = new RechargeOrderItem();
            orderItem.setOrderId(order.getOrderId());
            orderItem.setItemId(param.getPackageId());
            orderItem.setMemberId(param.getMemberId());
            orderItem.setItemType("recharge");
            orderItem.setItemName("会员充值");
            orderItem.setItemImage("/static/image/icon.png");
            orderItem.setPrice(orderItemMoney);
            orderItem.setNum(1);
            orderItem.setItemMoney(orderItemMoney);
            orderItem.setIsRefund(0);
            orderItem.setSiteId(param.getSiteId());
            orderItem.setCreateTime(System.currentTimeMillis() / 1000);
            this.rechargeOrderItemMapper.insert(orderItem);

            //订单创建成功后事件
            AfterOrderCreateParam paramAfter = new AfterOrderCreateParam();
            paramAfter.setOrderId(order.getOrderId());
            paramAfter.setSiteId(param.getSiteId());
            paramAfter.setTime(System.currentTimeMillis() / 1000);
            paramAfter.setOrderData(order);
            paramAfter.setMainId(mainId);
            paramAfter.setMainType(mainType);
            coreOrderEventService.AfterRechargeOrderCreate(paramAfter);
            vo.setTradeId(order.getOrderId());
            return vo;
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }


}
