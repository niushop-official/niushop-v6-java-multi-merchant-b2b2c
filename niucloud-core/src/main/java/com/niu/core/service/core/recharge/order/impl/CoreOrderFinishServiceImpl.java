package com.niu.core.service.core.recharge.order.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.shop.request.shipping.*;
import cn.binarywang.wx.miniapp.bean.shop.response.WxMaOrderShippingInfoBaseResponse;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.entity.recharge.RechargeOrderItem;
import com.niu.core.entity.recharge.RechargePackage;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.enums.member.MemberAccountChangeTypeEnum;
import com.niu.core.enums.recharge.RechargeOrderLogEnum;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.mapper.recharge.RechargeOrderItemMapper;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.mapper.recharge.RechargePackageMapper;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.notice.ICoreNoticeService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.recharge.finance.ICoreFinanceService;
import com.niu.core.service.core.recharge.order.*;
import com.niu.core.service.core.recharge.order.param.AfterOrderCloseParam;
import com.niu.core.service.core.weapp.ICoreWeappDeliveryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CoreOrderFinishServiceImpl implements ICoreOrderFinishService {

    @Resource
    private RechargeOrderMapper rechargeOrderMapper;
    @Resource
    private RechargePackageMapper rechargePackageMapper;
    @Resource
    private RechargeOrderItemMapper rechargeOrderItemMapper;
    @Resource
    private ICoreMemberAccountService coreMemberAccountService;
    @Resource
    private ICoreFinanceService coreFinanceService;
    @Resource
    private ICoreNoticeService coreNoticeService;
    @Resource
    private ICoreOrderWeappDeliveryService coreOrderWeappDeliveryService;
    @Resource
    private ICoreOrderSettlementService coreOrderSettlementService;

    /**
     * 完成
     * @param orderId 参数
     */
    @Override
    public void finish(Integer orderId) {
        RechargeOrder order = rechargeOrderMapper.selectById(orderId);
        if (ObjectUtil.isEmpty(order)){
            throw new CommonException("订单不存在");
        }
        //发放余额
        recharge(order);
        //发放奖励
        reward(order);

        //资金流水统计、账单统计
        coreFinanceService.orderPay(order);

        //消费发送
        coreNoticeService.send(order.getSiteId(), "recharge_success", Map.of("order_id", order.getOrderId()));

        //微信小程序 发货信息录入接口
        coreOrderWeappDeliveryService.orderShippingUploadShippingInfo(order.getOrderId());

        //结算
        coreOrderSettlementService.settlement(order.getOrderId());
    }

    /**
     * 充值余额
     */
    public void recharge(RechargeOrder order) {
        //发放余额
        log.info("会员充值发放余额开始{}", order.getOrderFaceMoney());
        if(order.getOrderFaceMoney().compareTo(BigDecimal.ZERO)>0){
            String memo = "会员充值到账" + order.getOrderFaceMoney() + "元余额";
            coreMemberAccountService.addLog(order.getSiteId(), order.getMemberId(), AccountTypeEnum.BALANCE.getType(), order.getOrderFaceMoney().doubleValue(), "recharge", memo,order.getOrderId().toString());
        }

        //增加销量
        RechargeOrderItem item = rechargeOrderItemMapper.selectOne(new QueryWrapper<RechargeOrderItem>().eq("order_id", order.getOrderId()).last("limit 1"));
        if(ObjectUtil.isNotEmpty(item)){
            rechargePackageMapper.update(new RechargePackage(), new UpdateWrapper<RechargePackage>()
                    .eq("package_id", item.getItemId()).setSql("sale_num = sale_num + 1"));
        }
    }

    /**
     * 发放奖励
     */
    public void reward(RechargeOrder order) {
        //发放积分
        log.info("会员充值发放积分开始{}", order.getOrderGivePoint());
        if(order.getOrderGivePoint() > 0){
            String memo = "会员充值赠送" + order.getOrderGivePoint() + "积分";
            coreMemberAccountService.addLog(order.getSiteId(), order.getMemberId(), AccountTypeEnum.POINT.getType(), order.getOrderGivePoint().doubleValue(), "recharge_give", memo,order.getOrderId().toString());
        }

        //发放成长值
        // PHP代码：'会员充值赠送' . $order_give_growth . '成长值'
        log.info("会员充值发放成长值开始{}", order.getOrderGiveGrowth());
        if(order.getOrderGiveGrowth() > 0){
            String memo = "会员充值赠送" + order.getOrderGiveGrowth() + "成长值";
            coreMemberAccountService.addLog(order.getSiteId(), order.getMemberId(), AccountTypeEnum.GROWTH.getType(), order.getOrderGiveGrowth().doubleValue(), "recharge", memo,order.getOrderId().toString());
        }
    }
}
