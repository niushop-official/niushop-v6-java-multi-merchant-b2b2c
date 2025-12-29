package com.niu.mall.service.core.refund.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.enums.pay.RefundTypeEnum;
import com.niu.core.event.common.CommonEvent;
import com.niu.core.event.refund.RefundSuccessEvent;
import com.niu.core.mapper.pay.PayRefundMapper;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.finance.ICoreFinanceService;
import com.niu.core.service.core.pay.param.PayRefundCreateParam;
import com.niu.core.service.core.pay.param.PayRefundTransferParam;
import com.niu.core.service.core.sys.ICoreSysStatService;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.entity.order.MallOrderRefundLog;
import com.niu.mall.enums.order.*;
import com.niu.mall.event.goods.MallSupplyGoodsDataInfoEventDefiner;
import com.niu.mall.event.order.OrderRefundFinishAfterEvent;
import com.niu.mall.event.refund.SyncSupplyOrderRefundEventDefiner;
import com.niu.mall.event.refund.SyncSupplyRefundNoEvent;
import com.niu.mall.event.refund.SyncSupplyRefundSuccessEvent;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundLogMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.impl.CoreGoodsStatServiceImpl;
import com.niu.mall.service.core.order.ICoreOrderCloseService;
import com.niu.mall.service.core.order.ICoreOrderDeliveryService;
import com.niu.mall.service.core.order.ICoreOrderService;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;
import com.niu.mall.service.core.refund.ICoreRefundService;
import com.niu.mall.service.core.refund.param.AddRefundLogParam;
import com.niu.mall.service.core.refund.param.RefundFinishParam;
import com.niu.mall.service.core.refund.vo.OrderRefundMoneyVo;
import com.niu.mall.service.core.shop.ICoreShopSettlementService;
import com.niu.mall.service.core.shop.ICoreStatService;
import com.niu.mall.service.core.shop.impl.CoreShopSettlementServiceImpl;
import com.niu.mall.service.core.shop.param.AddStatParam;
import com.niu.mall.service.core.shop.param.OrderRefundParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class CoreRefundServiceImpl implements ICoreRefundService {

    @Resource
    MallOrderRefundLogMapper mallOrderRefundLogMapper;

    @Resource
    MallOrderRefundMapper mallOrderRefundMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    ICoreStatService coreStatService;

    @Resource
    ICoreOrderDeliveryService coreOrderDeliveryService;

    @Resource
    ICoreOrderService coreOrderService;

    @Resource
    ICoreOrderCloseService coreOrderCloseService;

    @Resource
    com.niu.core.service.core.pay.ICoreRefundService corePayRefundService;

    @Resource
    ICoreAddonService coreAddonService;
    @Resource
    private ICoreGoodsStatService coreGoodsStatService;
    @Resource
    private PayRefundMapper payRefundMapper;
    @Resource
    private ICoreShopSettlementService coreShopSettlementService;
    @Resource
    private ICoreFinanceService coreFinanceService;
    @Resource
    private ICoreSysStatService coreSysStatService;

    /**
     * 添加维权日志
     *
     * @param data
     */
    @Override
    public void addLog(AddRefundLogParam data) {
        MallOrderRefundLog model = new MallOrderRefundLog();
        BeanUtil.copyProperties(data, model);
        model.setCreateTime(System.currentTimeMillis() / 1000);
        mallOrderRefundLogMapper.insert(model);
    }

    /**
     * 退款完成
     *
     * @param param
     */
    @Override
    public void finish(RefundFinishParam param) {
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (refund.getStatus().equals(OrderRefundStatusEnum.FINISH.getStatus().toString())) {
            throw new CommonException("退款已完成");
        }
        //查询商城订单
        MallOrder order = mallOrderMapper.selectById(refund.getOrderId());
        MallOrderRefund model = new MallOrderRefund();
        model.setRefundId(refund.getRefundId());
        model.setStatus(OrderRefundStatusEnum.FINISH.getStatus().toString());
        mallOrderRefundMapper.updateById(model);
        //对应的要将订单项设置为已完成
        MallOrderGoods orderGoods = new MallOrderGoods();
        orderGoods.setOrderGoodsId(refund.getOrderGoodsId());
        orderGoods.setStatus(OrderGoodsRefundEnum.REFUND_FINISH.getStatus());
        orderGoods.setIsEnableRefund(0);
        mallOrderGoodsMapper.updateById(orderGoods);

        //检查是否已退还运费
        BigDecimal refundDeliveryMoney = BigDecimal.ZERO;
        Long refundDeliveryCount = mallOrderRefundMapper.selectCount(new QueryWrapper<MallOrderRefund>()
                .eq("order_id", refund.getOrderId())
                .eq("is_refund_delivery",1)
                .ne("status", OrderRefundStatusEnum.CLOSE.getStatus())
        );

        if (refundDeliveryCount.intValue() > 0){
            refundDeliveryMoney = order.getDeliveryMoney();
        }
        //订单退款总额/退还运费累增
        order.setRefundMoney(order.getRefundMoney().add(refund.getMoney()));
        order.setRefundDeliveryMoney(refundDeliveryMoney);
        mallOrderMapper.updateById( order);
        //调用订单退款的负账结算
        OrderRefundParam refundParam = new OrderRefundParam();
        refundParam.setOrderId(Long.parseLong(order.getOrderId().toString()));
        refundParam.setRefundId(Long.parseLong(refund.getRefundId().toString()));
        refundParam.setSiteId(Long.parseLong(refund.getSiteId().toString()));
        refundParam.setRefundMoney(order.getRefundMoney());
        coreShopSettlementService.orderRefund(refundParam);

        refundFinishAfter(refund);
        OrderRefundFinishAfterEvent event = new OrderRefundFinishAfterEvent();
        event.setSiteId(refund.getSiteId());
        event.addAppSign("mall");
        event.setName("OrderRefundFinishAfterEvent");
        event.setRefund(refund);
        EventAndSubscribeOfPublisher.publishAll(event);

        //订单退款 资金流水统计/账单统计
        MallOrderRefund refundData = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
        );
        BigDecimal supplyRefundMoney = BigDecimal.ZERO;
        if (checkAddon("supply")){
            if(refundData.getSupplyRefundId() > 0 ){
                SyncSupplyOrderRefundEventDefiner.SyncSupplyOrderRefundEvent eventSupply = new SyncSupplyOrderRefundEventDefiner.SyncSupplyOrderRefundEvent();
                eventSupply.setRefundId(refundData.getSupplyRefundId());
                eventSupply.addAppSign("supply");
                List<SyncSupplyOrderRefundEventDefiner.SyncSupplyOrderRefundResult> supplyOrderRefundList = EventAndSubscribeOfPublisher.publishAndCallback(eventSupply);
                if(!supplyOrderRefundList.isEmpty()){
                    for (SyncSupplyOrderRefundEventDefiner.SyncSupplyOrderRefundResult item : supplyOrderRefundList){
                        if(ObjectUtil.isNotEmpty(item.getMoney())){
                            supplyRefundMoney = item.getMoney();
                        }
                    }
                }
            }
        }
        com.niu.core.service.core.finance.param.OrderRefundParam refundOrderParam = new com.niu.core.service.core.finance.param.OrderRefundParam();
        refundOrderParam.setSiteId(refund.getSiteId());
        refundOrderParam.setOrderId(order.getOrderId());
        refundOrderParam.setSupplyId(refund.getSupplyId());
        refundOrderParam.setRefundNo(refund.getRefundNo());
        refundOrderParam.setOrderRefundNo(refund.getOrderRefundNo());
        refundOrderParam.setSettlementStatus(order.getSettlementStatus());
        refundOrderParam.setMemberId(refund.getMemberId());
        refundOrderParam.setMoney(refund.getMoney());
        refundOrderParam.setRefundMoney(refund.getMoney());
        refundOrderParam.setShopRefundMoney(order.getShopRefundMoney());
        refundOrderParam.setMallRefundMoney(order.getMallRefundMoney());
        refundOrderParam.setSupplyRefundMoney(supplyRefundMoney);
        refundOrderParam.setRefundCommission(refund.getRefundCommission());
        refundOrderParam.setMallRate(order.getMallRate());
        refundOrderParam.setOrderMoney(order.getOrderMoney());
        refundOrderParam.setRefundDeliveryMoney(order.getRefundDeliveryMoney());
        refundOrderParam.setMallCouponMoney(order.getMallCouponMoney());
        refundOrderParam.setMallRefundCouponMoney(order.getMallRefundCouponMoney());
        coreFinanceService.orderRefund(refundOrderParam);
    }

    @Override
    public void refundFinishAfter(MallOrderRefund refund) {
        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>()
                .eq("order_id", refund.getOrderId())
                .select("order_id,site_id,status")
        );
        if (order == null) return;

        // 校验订单项是否全部发货
        if (order.getStatus().equals(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString())) {
            OrderDeliveryParam deliveryParam = new OrderDeliveryParam();
            deliveryParam.setOrderId(order.getOrderId());
            deliveryParam.setSiteId(order.getSiteId());
            deliveryParam.setDeliveryType(order.getDeliveryType());
            coreOrderDeliveryService.checkFinish(deliveryParam);
        }
        // 校验订单项是否全部退款
        if (!order.getStatus().equals(OrderStatusEnum.CLOSE.getStatus().toString())) {
            coreOrderCloseService.checkAllClose(order.getOrderId());
        }


        // 操作日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
        logParam.setMainId(0);
        logParam.setType(OrderRefundActionTypeEnum.FINISH_ACTION.getType());
        logParam.setStatus(Integer.valueOf(refund.getStatus()));
        this.addLog(logParam);

        // 商城统计
        log.info("商城统计:退款金额"+refund.getMoney());
        SysDataAddParam sysDataAddParam = new SysDataAddParam();
        sysDataAddParam.setRefundMoney(refund.getMoney());
        sysDataAddParam.setSiteId(order.getSiteId());
        coreSysStatService.addStat(sysDataAddParam);

        //商品退款数量 金额统计数据
        coreGoodsStatService.saveGoodsRefundNumAndMoneyByOrderId(refund);
    }


    /**
     * 转账
     * @param refund
     */
    @Override
    public void toTransfer(MallOrderRefund refund) {
        try {
            if (refund.getMoney().compareTo(BigDecimal.ZERO) == 0) {
                RefundFinishParam param = new RefundFinishParam();
                param.setOrderRefundNo(refund.getOrderRefundNo());
                this.finish(param);
            } else {
                MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>()
                        .select("order_id,order_type,out_trade_no")
                        .eq("order_id", refund.getOrderId()));
                if (order == null) throw new CommonException("订单已失效");

                // 创建退款单据
                PayRefundCreateParam param = new PayRefundCreateParam();
                param.setSiteId(refund.getSiteId());
                param.setMoney(refund.getMoney());
                param.setTradeId(order.getOrderId().toString());
                param.setTradeType(order.getOrderType());
                param.setOutTradeNo(order.getOutTradeNo());
                param.setReason("买家申请退款");
                String refunNo = corePayRefundService.create(param);

                MallOrderRefund model = new MallOrderRefund();
                model.setRefundId(refund.getRefundId());
                model.setRefundNo(refunNo);
                model.setStatus(OrderRefundStatusEnum.STORE_REFUND_TRANSFERING.getStatus().toString());
                mallOrderRefundMapper.updateById(model);

                //todo  supply退款同步供货商的退款单据号
                if (checkAddon("supply")){
                    if(refund.getSupplyId() != null && refund.getSupplyRefundId() != null && refund.getSupplyId() > 0 && refund.getSupplyRefundId() > 0){
                        // 触发事件 退款同步供货商的退款单据号
                        SyncSupplyRefundNoEvent event = new SyncSupplyRefundNoEvent();
                        event.setRefundId(refund.getSupplyRefundId());
                        event.addAppSign("supply");
                        event.setRefundNo(refunNo);
                        event.setSiteId(RequestUtils.siteId());
                        EventAndSubscribeOfPublisher.publishAll(event);
                    }
                }
                // 发起退款
                PayRefundTransferParam refundParam = new PayRefundTransferParam();
                refundParam.setRefundNo(refunNo);
                refundParam.setSiteId(refund.getSiteId());
                refundParam.setRefundType(RefundTypeEnum.BACK.getKey());
                refundParam.setVoucher("");
                corePayRefundService.refund(refundParam);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("退款转账失败");
        }
    }

    /**
     * 检查插件是否安装
     * @param key
     */
    private Boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap= coreAddonService.getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }

    /**
     * 退款转账完成
     *
     * @param refund
     */
    @Override
    public void refundTransferSuccess(PayRefund refund) {
        MallOrderRefund orderRefund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("refund_no", refund.getRefundNo())
        );
        if (orderRefund == null) return;

        if (checkAddon("supply")){
            if(orderRefund.getSupplyRefundId() > 0){
                SyncSupplyRefundSuccessEvent event = new SyncSupplyRefundSuccessEvent();
                event.setTradeType(refund.getTradeType());
                event.setTradeId(orderRefund.getOrderRefundNo());
                event.setRefund(refund);
                EventAndSubscribeOfPublisher.publishAll(event);
            }
        }

        MallOrderRefund model = new MallOrderRefund();
        model.setRefundId(orderRefund.getRefundId());
        model.setTransferTime(System.currentTimeMillis() / 1000);
        model.setRefundNo(refund.getRefundNo());
        mallOrderRefundMapper.updateById(model);

        RefundFinishParam param = new RefundFinishParam();
        param.setOrderRefundNo(orderRefund.getOrderRefundNo());
        this.finish(param);
    }

    /**
     * 退款校验
     * @param orderGoodsIds
     * @param applyRefundMoney
     */
    @Override
    public OrderRefundMoneyVo refundCheck(Integer[] orderGoodsIds, BigDecimal applyRefundMoney) {
//        if (applyRefundMoney.compareTo(BigDecimal.ZERO) <= 0) throw new CommonException("退款金额不能小于0");

        OrderRefundMoneyVo vo = this.getOrderRefundMoney(orderGoodsIds);
//        if (applyRefundMoney.compareTo(vo.getRefundMoney()) > 0) throw new CommonException("退款金额不能大于可退款总额");

        return vo;
    }

    /**
     * 获取订单最大可退金额
     * @param orderGoodsIds
     * @return
     */
    private OrderRefundMoneyVo getOrderRefundMoney(Integer[] orderGoodsIds) {
        BigDecimal refundMoney = new BigDecimal(0);

        List<OrderRefundMoneyVo.MallOrderGoods> orderGoodsList = CollectUtils.convert(mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().in("order_goods_id", orderGoodsIds)), OrderRefundMoneyVo.MallOrderGoods.class);
        if (ObjectUtil.isEmpty(orderGoodsList)) throw new CommonException("订单已失效");

        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().eq("order_id", orderGoodsList.get(0).getOrderId()));
        if (ObjectUtil.isEmpty(order)) throw new CommonException("订单已失效");

        String[] statusList = new String[]{ OrderStatusEnum.WAIT_DELIVERY.getStatus().toString(), OrderStatusEnum.WAIT_TAKE.getStatus().toString(), OrderStatusEnum.FINISH.getStatus().toString() };
        if (!Arrays.asList(statusList).contains(order.getStatus())) {
            throw new CommonException("退款已失效(退款未支付或已关闭)");
        }

        // 查询是否是最后一笔退款且还没有退运费
        Long orderGoodsCount = mallOrderGoodsMapper.selectCount(new QueryWrapper<MallOrderGoods>()
                .eq("order_id", order.getOrderId())
                //ns_mall_order_goods 表没有该字段
                //.eq("is_gift", 0)
        );
        Long refundCount = mallOrderRefundMapper.selectCount(new QueryWrapper<MallOrderRefund>()
                .eq("order_id", order.getOrderId())
                .ne("status", OrderRefundStatusEnum.CLOSE.getStatus())
        );
        Boolean isRefundDelivery = false;
        if (refundCount + orderGoodsIds.length >= orderGoodsCount) {
            Long refundDeliveryCount = mallOrderRefundMapper.selectCount(new QueryWrapper<MallOrderRefund>()
                    .eq("order_id", order.getOrderId())
                    .eq("is_refund_delivery", 1)
                    .ne("status", OrderRefundStatusEnum.CLOSE.getStatus())
            );
            if (refundDeliveryCount == 0) isRefundDelivery = true;
        }

        for (int i = 0; i < orderGoodsList.size(); i++) {
            OrderRefundMoneyVo.MallOrderGoods orderGoods = orderGoodsList.get(i);
            if (orderGoods.getIsEnableRefund().equals(0)) throw new CommonException("订单不允许退款");
            if (!orderGoods.getStatus().equals(OrderGoodsRefundEnum.NORMAL.getStatus())) throw new CommonException("订单已退款或存在未完成的退款");

            BigDecimal itemRefundMoney = orderGoods.getGoodsMoney().subtract(orderGoods.getDiscountMoney()).subtract(orderGoods.getMallDiscountMoney()).subtract(orderGoods.getShopActiveRefundMoney());

            if (isRefundDelivery && i == 0) {
                itemRefundMoney = itemRefundMoney.add(order.getDeliveryMoney());
                orderGoods.setIsRefundDelivery(1);
            } else {
                orderGoods.setIsRefundDelivery(0);
            }
            orderGoods.setItemRefundMoney(itemRefundMoney);
            refundMoney = refundMoney.add(itemRefundMoney);
        }

        OrderRefundMoneyVo vo = new OrderRefundMoneyVo();
        vo.setRefundMoney(refundMoney);
        vo.setOrderGoodsList(orderGoodsList);
        return vo;
    }
}
