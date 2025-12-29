package com.niu.mall.service.core.refund.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.NoticeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.event.common.CommonEvent;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.enums.order.*;
import com.niu.mall.event.order.ShopOrderRefundActiveCreateAfterEvent;
import com.niu.mall.event.refund.SyncSupplyRefundApplyEvent;
import com.niu.mall.event.refund.SyncSupplyRefundClassEvent;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.mall.service.api.refund.param.ApplyRefundParam;
import com.niu.mall.service.api.refund.param.CloseRefundParam;
import com.niu.mall.service.core.order.ICoreOrderDeliveryService;
import com.niu.mall.service.core.order.param.OrderDeliveryParam;
import com.niu.mall.service.core.refund.ICoreRefundActionService;
import com.niu.mall.service.core.refund.ICoreRefundService;
import com.niu.mall.service.core.refund.param.ActiveRefundParam;
import com.niu.mall.service.core.refund.param.AddRefundLogParam;
import com.niu.mall.service.core.refund.vo.OrderRefundMoneyVo;
import com.niu.mall.service.site.refund.param.AuditApplyParam;
import com.niu.mall.service.site.refund.param.AuditRefundGoodsParam;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoreRefundActionServiceImpl implements ICoreRefundActionService {

    @Resource
    ICoreRefundService coreRefundService;

    @Resource
    MallOrderRefundMapper mallOrderRefundMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    MallOrderMapper mallOrderMapper;
    @Resource
    SiteAddressMapper shopAddressMapper;
    @Resource
    ICoreOrderDeliveryService orderDeliveryService;
    @Resource
    ICoreAddonService coreAddonService;

    /**
     * 商家主动退款
     * @param param
     */
    @Override
    @Transactional
    public void shopActiveRefund(ActiveRefundParam param) {
        OrderRefundMoneyVo checkData = coreRefundService.refundCheck(param.getOrderGoodsIds(), param.getRefundMoney());

        List<OrderRefundMoneyVo.MallOrderGoods> orderGoodsList = checkData.getOrderGoodsList();

        for (OrderRefundMoneyVo.MallOrderGoods orderGoods : orderGoodsList) {
            MallOrderRefund model = new MallOrderRefund();
            BeanUtil.copyProperties(orderGoods, model);
            model.setSiteId(param.getSiteId());
            model.setOrderRefundNo(createRefundNo());
            model.setRefundType(OrderRefundTypeEnum.ONLY_REFUND.getType().toString());
            model.setReason(OrderRefundActionTypeEnum.ACTIVE_REFUND_ACTION.getName());
            model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            model.setApplyMoney(orderGoods.getItemRefundMoney());
            model.setMoney(orderGoods.getItemRefundMoney());
            model.setVoucher("");
            model.setRemark(param.getShopActiveRefundRemark());
            model.setSource(OrderRefundSourceEnum.ACTIVE_REFUND.getType().toString());
            model.setIsRefundDelivery(orderGoods.getIsRefundDelivery());
            model.setSupplyId(ObjectUtil.isNotEmpty(param.getSupplyId()) ? param.getSupplyId() : 0);
            model.setSupplyRefundId(ObjectUtil.isNotEmpty(param.getSupplyRefundId()) ? param.getSupplyRefundId() : 0);
            mallOrderRefundMapper.insert(model);

            MallOrderGoods orderGoodsModel = new MallOrderGoods();
            orderGoodsModel.setOrderGoodsId(orderGoods.getOrderGoodsId());
            orderGoodsModel.setOrderRefundNo(model.getOrderRefundNo());
            orderGoodsModel.setShopActiveRefund(1);
            orderGoodsModel.setShopActiveRefundMoney(orderGoods.getItemRefundMoney());
            orderGoodsModel.setStatus(OrderGoodsRefundEnum.REFUNDING.getStatus());
            mallOrderGoodsMapper.updateById(orderGoodsModel);

            ShopOrderRefundActiveCreateAfterEvent event = new ShopOrderRefundActiveCreateAfterEvent();
            event.setSiteId(model.getSiteId());
            event.addAppSign("mall");
            event.setName("ShopOrderRefundActiveCreateAfterEvent");
            event.setRefundParam(param);
            event.setRefund(model);
            event.setOrderGoods(orderGoods);
            EventAndSubscribeOfPublisher.publishAll(event);
        }
    }

    @Override
    @Transactional
    public void auditApply(AuditApplyParam param) {
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
                .eq("site_id", param.getSiteId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus().toString())) {
            throw new CommonException("退款已失效(只有申请中的退款才可以审核)");
        }

        MallOrderRefund model = new MallOrderRefund();
        model.setRefundId(refund.getRefundId());

        // 操作日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(param.getMainType());
        logParam.setMainId(param.getMainId());

        // 同意退款
        MallOrderGoods orderGoods = mallOrderGoodsMapper.selectOne(new QueryWrapper<MallOrderGoods>()
                .select("goods_money,discount_money,mall_discount_money")
                .eq("order_goods_id", refund.getOrderGoodsId()));
        if (orderGoods == null) throw new CommonException("订单已失效");

        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>()
                .select("order_no,delivery_money")
                .eq("order_id", refund.getOrderId()));
        if (order == null) throw new CommonException("订单已失效");

        if (param.getIsAgree().equals(1)) {
            BigDecimal maxRefundMoney = orderGoods.getGoodsMoney().subtract(orderGoods.getDiscountMoney()).subtract(orderGoods.getMallDiscountMoney());
            if (refund.getIsRefundDelivery().equals(1)) maxRefundMoney = maxRefundMoney.add(order.getDeliveryMoney());

            int comparison = (param.getMoney().subtract(maxRefundMoney)).compareTo(BigDecimal.ZERO);
            if (comparison > 0) throw new CommonException("退款金额不能大于可退款总额");

            model.setMoney(param.getMoney());
            // 如果是退款退货
            if (refund.getRefundType().equals(OrderRefundTypeEnum.ONLY_REFUND.getType().toString())) {
                model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString());
            } else {
                SiteAddress address = shopAddressMapper.selectOne(new QueryWrapper<SiteAddress>()
                        .eq("site_id", RequestUtils.siteId())
                        .eq("id", param.getRefundAddressId())
                );
                if (address == null) throw new CommonException("请选择退货地址");
                model.setRefundAddress(JSONUtil.parseObj(address).toString());
                model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus().toString());
            }

            logParam.setType(OrderRefundActionTypeEnum.AGREE_AUDIT_APPLY_ACTION.getType());
        } else {
            // 拒绝退款
            model.setShopReason(param.getShopReason());
            model.setStatus(OrderRefundStatusEnum.STORE_REFUSE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus().toString());
            logParam.setType(OrderRefundActionTypeEnum.REFUSE_AUDIT_APPLY_ACTION.getType());
        }
        String messageKey = OrderRefundStatusEnum.STORE_REFUSE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus().toString().equals(refund.getStatus()) ? "mall_refund_refuse" : "mall_refund_agree";
        mallOrderRefundMapper.updateById(model);

        logParam.setStatus(Integer.valueOf(model.getStatus()));
        coreRefundService.addLog(logParam);
        refund = mallOrderRefundMapper.selectById(refund.getRefundId());
        if (model.getStatus().equals(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString())) {
            coreRefundService.toTransfer(refund);
        }

        // 发送消息
        Map<String, Object> noticeParam = new HashMap<String, Object>();
        noticeParam.put("mallRefundId", refund.getRefundId());
        NoticeUtils.send(refund.getSiteId(), messageKey, noticeParam);
    }

    @Override
    @Transactional
    public void auditRefundGoods(AuditRefundGoodsParam param) {
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
                .eq("site_id", param.getSiteId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        if (!refund.getStatus().equals(OrderRefundStatusEnum.BUYER_REFUND_GOODS_WAIT_STORE.getStatus().toString())) {
            throw new CommonException("退款已失效(只有待确认收货的的退款才可以审核)");
        }

        MallOrderRefund model = new MallOrderRefund();
        model.setRefundId(refund.getRefundId());

        // 操作日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        logParam.setMainId(RequestUtils.uid());

        if (param.getIsAgree().equals(1)) {
            model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString());
            logParam.setType(OrderRefundActionTypeEnum.AGREE_AUDIT_REFUND_GOODS_ACTION.getType());
        } else {
            // 拒绝退货
            model.setShopReason(param.getShopReason());
            model.setStatus(OrderRefundStatusEnum.STORE_REFUSE_TAKE_REFUND_GOODS_WAIT_BUYER.getStatus().toString());
            logParam.setType(OrderRefundActionTypeEnum.REFUSE_AUDIT_REFUND_GOODS_ACTION.getType());
        }

        mallOrderRefundMapper.updateById(model);

        logParam.setStatus(Integer.valueOf(model.getStatus()));
        coreRefundService.addLog(logParam);

        if (model.getStatus().equals(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString())) {
            refund = mallOrderRefundMapper.selectById(refund.getRefundId());
            coreRefundService.toTransfer(refund);
        }
    }

    @Override
    public void close(CloseRefundParam param) {
        MallOrderRefund refund = mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                .eq("order_refund_no", param.getOrderRefundNo())
                .eq("member_id", RequestUtils.memberId())
        );
        if (refund == null) throw new CommonException("退款已失效");
        String[] status = {
                OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString(),
                OrderRefundStatusEnum.STORE_REFUND_TRANSFERING.getStatus().toString(),
                OrderRefundStatusEnum.FINISH.getStatus().toString(),
                OrderRefundStatusEnum.CLOSE.getStatus().toString()
        };
        if (Arrays.asList(status).contains(refund.getStatus())) {
            throw new CommonException("退款已失效(退款已完成或已关闭)");
        }

        MallOrderRefund model = new MallOrderRefund();
        model.setRefundId(refund.getRefundId());
        model.setStatus(OrderRefundStatusEnum.CLOSE.getStatus().toString());
        mallOrderRefundMapper.updateById(model);

        MallOrderGoods orderGoods = new MallOrderGoods();
        orderGoods.setStatus(OrderGoodsRefundEnum.NORMAL.getStatus());
        mallOrderGoodsMapper.update(orderGoods, new QueryWrapper<MallOrderGoods>().eq("order_refund_no", refund.getOrderRefundNo()));

        //校验一下订单项是否全部发货
        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().eq("order_id", refund.getOrderId()));
        if(order != null && order.getStatus().equals(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString())){
            OrderDeliveryParam orderDeliveryParam = new OrderDeliveryParam();
            orderDeliveryParam.setOrderId(order.getOrderId());
            orderDeliveryParam.setDeliveryType(order.getDeliveryType());
            orderDeliveryService.checkFinish(orderDeliveryParam);
        }

        // 添加日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(refund.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        logParam.setMainId(RequestUtils.memberId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setType(OrderRefundActionTypeEnum.CLOSE_ACTION.getType());
        coreRefundService.addLog(logParam);

        //商城退款关闭后，同步供货商退款关闭
        if(ObjectUtil.isNotEmpty(coreAddonService.getInfoByKey("supply")))
        {
            orderGoods = mallOrderGoodsMapper.selectOne(new QueryWrapper<MallOrderGoods>().eq("order_refund_no", refund.getOrderRefundNo()));
            if(orderGoods.getSupplyGoodsId() > 0)
            {
                // 触发事件 退款同步供货商的退款单据号
                SyncSupplyRefundClassEvent eventRefund = new SyncSupplyRefundClassEvent();
                eventRefund.setRefundData(refund);
                eventRefund.addAppSign("supply");
                eventRefund.setMainId(param.getMainId());
                eventRefund.setMainType(param.getMainType());
                eventRefund.setSiteId(RequestUtils.siteId());
                EventAndSubscribeOfPublisher.publishAll(eventRefund);
            }
        }
    }

    @Override
    @Transactional
    public void apply(ApplyRefundParam param) {
        MallOrderGoods orderGoods = mallOrderGoodsMapper.selectOne(new QueryWrapper<MallOrderGoods>()
                .eq("order_goods_id", param.getOrderGoodsId())
                .eq("member_id", RequestUtils.memberId()));
        if (orderGoods == null) throw new CommonException("订单已失效");
        if (orderGoods.getIsEnableRefund().equals(0)) throw new CommonException("订单不允许退款");
        if (!orderGoods.getStatus().equals(OrderGoodsRefundEnum.NORMAL.getStatus()))
            throw new CommonException("订单已退款或存在未完成的退款");

        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>()
                .select("delivery_money")
                .eq("order_id", orderGoods.getOrderId()));
        if (order == null) throw new CommonException("订单已失效");
        String orderRefundNo = createRefundNo();

            if (OrderRefundTypeEnum.RETURN_AND_REFUND_GOODS.getType().toString().equals(param.getRefundType()) && OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus().equals(orderGoods.getDeliveryStatus()))
            throw new CommonException("只有已发货的商品才能申请退货退款");

        Integer isRefundDelivery = 0;
        // 订单项数量
        Long orderGoodsCount = mallOrderGoodsMapper.selectCount(new QueryWrapper<MallOrderGoods>().eq("order_id", orderGoods.getOrderId()));
        // 已申请退款的订单项数量
        Long refundCount = mallOrderRefundMapper.selectCount(new QueryWrapper<MallOrderRefund>().eq("order_id", orderGoods.getOrderId()).ne("status", OrderRefundStatusEnum.CLOSE.getStatus()));

        if ((refundCount.intValue() + 1) >= orderGoodsCount.intValue()) {
            Long refundDeliveryCount = mallOrderRefundMapper.selectCount(new QueryWrapper<MallOrderRefund>().eq("order_id", orderGoods.getOrderId()).eq("is_refund_delivery", 1).ne("status", OrderRefundStatusEnum.CLOSE.getStatus()));
            if (refundDeliveryCount == 0) isRefundDelivery = 1;
        }
        //当前订单项最大可退金额
        BigDecimal maxRefundMoney = orderGoods.getGoodsMoney().subtract(orderGoods.getDiscountMoney()).subtract(orderGoods.getMallDiscountMoney());
        if (isRefundDelivery.equals(1)) maxRefundMoney = maxRefundMoney.add(order.getDeliveryMoney());
        Integer compare = (param.getApplyMoney().subtract(maxRefundMoney)).compareTo(BigDecimal.ZERO);
        if (compare > 0)
            throw new CommonException("退款金额不能大于可退款总额");

        MallOrderRefund model = new MallOrderRefund();
        BeanUtil.copyProperties(param, model);
        model.setSiteId(orderGoods.getSiteId());
        model.setOrderId(orderGoods.getOrderId());
        model.setOrderGoodsId(orderGoods.getOrderGoodsId());
        model.setOrderRefundNo(orderRefundNo);
        model.setMemberId(RequestUtils.memberId());
        model.setStatus(OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus().toString());
        model.setSource(OrderRefundSourceEnum.APPLY.getType().toString());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setVoucher(JSONUtil.parseArray(param.getVoucher()).toString());
        model.setIsRefundDelivery(isRefundDelivery);
        mallOrderRefundMapper.insert(model);

        MallOrderGoods orderGoodsUpdate = new MallOrderGoods();
        orderGoodsUpdate.setOrderGoodsId(orderGoods.getOrderGoodsId());
        orderGoodsUpdate.setOrderRefundNo(orderRefundNo);
        orderGoodsUpdate.setStatus(OrderGoodsRefundEnum.REFUNDING.getStatus());
        mallOrderGoodsMapper.updateById(orderGoodsUpdate);

        // 添加日志
        AddRefundLogParam logParam = new AddRefundLogParam();
        logParam.setOrderRefundNo(model.getOrderRefundNo());
        logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        logParam.setMainId(RequestUtils.memberId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setType(OrderRefundActionTypeEnum.APPLY_ACTION.getType());
        coreRefundService.addLog(logParam);
        //监听商城退款申请，同步供货商退款申请
        if(ObjectUtil.isNotEmpty(coreAddonService.getInfoByKey("supply")))
        {
            if(orderGoods.getSupplyGoodsId() > 0)
            {
                orderGoods.setOrderRefundNo(orderGoodsUpdate.getOrderRefundNo());
                orderGoods.setStatus(orderGoodsUpdate.getStatus());
                // 触发事件 退款同步供货商的退款单据号
                SyncSupplyRefundApplyEvent eventRefund = new SyncSupplyRefundApplyEvent();
                eventRefund.setRefundData( model);
                eventRefund.setOrderGoodsData(orderGoods);
                eventRefund.addAppSign("supply");
                eventRefund.setSiteId(RequestUtils.siteId());
                EventAndSubscribeOfPublisher.publishAll(eventRefund);
            }
        }
    }

    private String createRefundNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }
}
