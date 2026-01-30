package com.niu.core.service.core.recharge.refund.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.entity.recharge.RechargeOrderItem;
import com.niu.core.entity.recharge.RechargeOrderItemRefund;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.enums.pay.OrderStatusEnum;
import com.niu.core.enums.recharge.RechargeRefundStatusEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.recharge.RechargeOrderItemMapper;
import com.niu.core.mapper.recharge.RechargeOrderItemRefundMapper;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.pay.ICoreRefundService;
import com.niu.core.service.core.pay.param.PayRefundCreateParam;
import com.niu.core.service.core.pay.param.PayRefundTransferParam;
import com.niu.core.service.core.recharge.order.ICoreOrderCloseService;
import com.niu.core.service.core.recharge.refund.ICoreRechargeRefundService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CoreRechargeRefundServiceImpl implements ICoreRechargeRefundService {

    @Resource
    RechargeOrderItemMapper rechargeOrderItemMapper;

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreRefundService coreRefundService;

    @Resource
    RechargeOrderItemRefundMapper rechargeOrderItemRefundMapper;

    @Resource
    ICoreOrderCloseService coreOrderCloseService;

    @Resource
    ICoreMemberAccountService coreMemberAccountService;

    /**
     *
     * @return
     */
    @Override
    @Transactional
    public void create(Integer orderId) {
        RechargeOrder order = rechargeOrderMapper.selectOne(new QueryWrapper<RechargeOrder>().eq("order_id", orderId));
        if (order == null) throw new CommonException("订单不存在");
        if (!order.getIsEnableRefund().equals(1)) throw new CommonException("该订单不允许退款");
        if (!order.getOrderStatus().equals(OrderStatusEnum.FINISH.getStatus())) throw new CommonException("订单尚未支付不能进行退款");

        RechargeOrderItem orderItem = rechargeOrderItemMapper.selectOne(new QueryWrapper<RechargeOrderItem>().eq("order_id", orderId));
        if (!orderItem.getRefundStatus().equals(0)) throw new CommonException("订单已申请退款");

        Member member = memberMapper.selectOne(new QueryWrapper<Member>().select("balance").eq("member_id", order.getMemberId()));
        if (member == null) throw new CommonException("会员不存在");
        if (member.equals(0)) throw new CommonException("会员账户金额为0不允许进行退款");

        try {
            BigDecimal money = member.getBalance().compareTo(order.getOrderMoney()) == 1 ? order.getOrderMoney() : member.getBalance();

            // 创建支付单据
            PayRefundCreateParam refundParam = new PayRefundCreateParam();
            refundParam.setOutTradeNo(order.getOutTradeNo());
            refundParam.setSiteId(order.getSiteId());
            refundParam.setTradeType("recharge");
            refundParam.setTradeId(orderId.toString());
            refundParam.setMoney(money);
            String refundNo = coreRefundService.create(refundParam);

            // 添加退款记录
            RechargeOrderItemRefund model = new RechargeOrderItemRefund();
            model.setRefundNo(refundNo);
            model.setOrderId(orderId);
            model.setOrderItemId(orderItem.getOrderItemId());
            model.setNum(orderItem.getNum());
            model.setMoney(money);
            model.setItemType(orderItem.getItemType());
            model.setSiteId(order.getSiteId());
            model.setMemberId(order.getMemberId());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            model.setStatus(RechargeRefundStatusEnum.REFUNDING.getStatus());
            rechargeOrderItemRefundMapper.insert(model);

            order.setRefundStatus(RechargeRefundStatusEnum.REFUNDING.getStatus());
            rechargeOrderMapper.updateById(order);

            orderItem.setRefundNo(refundNo);
            orderItem.setRefundStatus(RechargeRefundStatusEnum.REFUNDING.getStatus());
            rechargeOrderItemMapper.updateById(orderItem);

            this.refund(model);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Transactional
    public void refund(RechargeOrderItemRefund refund) {
        // 扣除账户
        coreMemberAccountService.addLog(refund.getSiteId(), refund.getMemberId(), AccountTypeEnum.BALANCE.getType(), refund.getMoney().negate().doubleValue(), "recharge_refund", "充值订单退款", refund.getOrderId().toString());

        PayRefundTransferParam param = new PayRefundTransferParam();
        param.setSiteId(refund.getSiteId());
        param.setRefundNo(refund.getRefundNo());
        coreRefundService.refund(param);
    }

    /**
     * 退款完成
     *
     * @param refundNo
     */
    @Transactional
    public void refundComplete(String refundNo) {
        RechargeOrderItemRefund refund = rechargeOrderItemRefundMapper.selectOne(new QueryWrapper<RechargeOrderItemRefund>().eq("refund_no", refundNo));
        if (refund == null) return;
        if (!refund.getStatus().equals(RechargeRefundStatusEnum.REFUNDING.getStatus())) return;

        refund.setStatus(RechargeRefundStatusEnum.REFUND_COMPLETED.getStatus());
        rechargeOrderItemRefundMapper.updateById(refund);

        RechargeOrder order = new RechargeOrder();
        order.setOrderId(refund.getOrderId());
        order.setRefundStatus(RechargeRefundStatusEnum.REFUND_COMPLETED.getStatus());
        rechargeOrderMapper.updateById(order);

        RechargeOrderItem orderItem = new RechargeOrderItem();
        orderItem.setOrderItemId(refund.getOrderItemId());
        orderItem.setRefundStatus(RechargeRefundStatusEnum.REFUND_COMPLETED.getStatus());
        rechargeOrderItemMapper.updateById(orderItem);

        coreOrderCloseService.close(refund.getOrderId());
    }
}
