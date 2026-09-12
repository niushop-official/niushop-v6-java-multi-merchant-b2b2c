package com.niu.mall.service.core.refund.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.NoticeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.SiteAddress;
import com.niu.core.mapper.site.SiteAddressMapper;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.enums.order.*;
import com.niu.mall.event.order.ShopOrderRefundActiveCreateAfterEvent;
import com.niu.mall.event.refund.SyncSupplyRefundApplyEvent;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.mall.service.api.refund.param.ApplyRefundParam;
import com.niu.mall.service.api.refund.param.CloseRefundParam;
import com.niu.mall.service.core.order.ICoreOrderConfigService;
import com.niu.mall.service.core.refund.ICoreRefundActionService;
import com.niu.mall.service.core.refund.ICoreRefundService;
import com.niu.mall.service.core.refund.param.ActiveRefundParam;
import com.niu.mall.service.core.refund.param.AddRefundLogParam;
import com.niu.mall.service.core.refund.vo.OrderRefundMoneyVo;
import com.niu.mall.service.site.refund.param.AuditApplyParam;
import com.niu.mall.service.site.refund.param.AuditRefundGoodsParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单退款操作服务实现
 */
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
    ICoreAddonService coreAddonService;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    /**
     * 商家主动退款
     *
     * @param param 主动退款参数
     */
    @Override
    @Transactional
    public void shopActiveRefund(ActiveRefundParam param) {

        if (ObjectUtil.isNotEmpty(param.getOrderId())){
            //查询订单商品信息
            List<MallOrderGoods> orderGoods = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>()
                    .eq("order_id", param.getOrderId()).eq("status",OrderGoodsRefundEnum.NORMAL.getStatus()).select("order_goods_id"));
            param.setOrderGoodsIds(orderGoods.stream().map(MallOrderGoods::getOrderGoodsId).toArray(Integer[]::new));
        }

        OrderRefundMoneyVo checkData = coreRefundService.getOrderRefundMoney(param.getOrderGoodsIds());
        MallOrder orderData = checkData.getOrderData();
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
            model.setRemark(param.getRemark());
            model.setSource(param.getSource());
            model.setIsRefundDelivery(orderGoods.getIsRefundDelivery());
            model.setSupplyId(ObjectUtil.isNotEmpty(param.getSupplyId()) ? param.getSupplyId() : 0);
            model.setSupplyRefundId(ObjectUtil.isNotEmpty(param.getSupplyRefundId()) ? param.getSupplyRefundId() : 0);
            model.setCircleId(ObjectUtil.isNotEmpty(orderData.getCircleId()) ? orderData.getCircleId() : 0);
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

    /**
     * 审核退款申请
     *
     * @param param 审核参数
     */
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
            BigDecimal maxRefundMoney = coreRefundService.getItemRefundableMoney(refund.getOrderGoodsId());
            if (refund.getIsRefundDelivery().equals(1)) maxRefundMoney = maxRefundMoney.add(order.getDeliveryMoney());

            int comparison = (param.getMoney().subtract(maxRefundMoney)).compareTo(BigDecimal.ZERO);
            if (comparison > 0) throw new CommonException("退款金额不能大于可退款总额");

            model.setMoney(param.getMoney());
            // 如果是退款退货
            if (refund.getRefundType().equals(OrderRefundTypeEnum.ONLY_REFUND.getType().toString())) {
                model.setStatus(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString());
            } else {
                SiteAddress address = shopAddressMapper.selectOne(new QueryWrapper<SiteAddress>()
                        .eq("site_id", param.getSiteId())
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
        String messageKey = param.getIsAgree().equals(1) ? "mall_refund_agree" : "mall_refund_refuse";
        mallOrderRefundMapper.updateById(model);

        logParam.setStatus(Integer.valueOf(model.getStatus()));
        coreRefundService.addLog(logParam);
        refund = mallOrderRefundMapper.selectById(refund.getRefundId());
        if (model.getStatus().equals(OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus().toString())) {
            coreRefundService.toTransfer(refund);
        }

        // 发送消息
        Map<String, Object> noticeParam = new HashMap<String, Object>();
        noticeParam.put("order_refund_no", refund.getOrderRefundNo());
        NoticeUtils.send(refund.getSiteId(), messageKey, noticeParam);
    }

    /**
     * 审核退货商品
     *
     * @param param 审核参数
     */
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
        logParam.setMainType(param.getMainType());
        logParam.setMainId(param.getMainId());

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

    /**
     * 关闭退款申请
     *
     * @param param 关闭参数
     */
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
        //对应的要将订单项还原
        MallOrderGoods orderGoods = new MallOrderGoods();
        orderGoods.setStatus(OrderGoodsRefundEnum.NORMAL.getStatus());
        mallOrderGoodsMapper.update(orderGoods, new QueryWrapper<MallOrderGoods>().eq("order_refund_no", refund.getOrderRefundNo()));

        //查询退款信息 调用退款关闭后的事件 AfterMallOrderRefundClose
        MallOrderRefund mode = mallOrderRefundMapper.selectById(refund.getRefundId());
        coreRefundService.AfterMallOrderRefundClose(mode, param);
    }

    /**
     * 申请退款
     *
     * @param param 申请参数
     */
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
                .select("delivery_money,circle_id")
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
        BigDecimal maxRefundMoney = coreRefundService.getItemRefundableMoney(orderGoods.getOrderGoodsId());
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
        model.setWeappVersion(RequestUtils.weappVersion());
        model.setCircleId(ObjectUtil.isNotEmpty(order.getCircleId()) ? order.getCircleId() : 0);
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
        // 退款申请通知商家
        String siteMessageKey = OrderRefundTypeEnum.ONLY_REFUND.getType().toString().equals(model.getRefundType())
                ? "mall_order_refund_remind_site"
                : "mall_order_refund_goods_remind_site";
        Map<String, Object> siteNoticeParam = new HashMap<>();
        siteNoticeParam.put("refund", model);
        siteNoticeParam.put("order_refund_no", model.getOrderRefundNo());
        NoticeUtils.send(model.getSiteId(), siteMessageKey, siteNoticeParam);
        //监听商城退款申请，同步供货商退款申请
        if(ObjectUtil.isNotEmpty(coreAddonService.getInfoByKey("supply")))
        {
            if(orderGoods.getSupplyOrderGoodsId() != null && orderGoods.getSupplyOrderGoodsId() > 0)
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

        //如果开启未发货的快捷退款（与 PHP RefundActionService.apply 一致）
        if (OrderRefundTypeEnum.ONLY_REFUND.getType().toString().equals(param.getRefundType())
                && OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus().equals(orderGoods.getDeliveryStatus())) {
            Integer refundAuditBeforeDelivery = coreOrderConfigService.refundAudit().getRefundAuditBeforeDelivery();
            if (refundAuditBeforeDelivery != null && refundAuditBeforeDelivery.equals(0)) {
                AuditApplyParam auditParam = new AuditApplyParam();
                auditParam.setShopReason("");
                auditParam.setIsAgree(1);
                auditParam.setMoney(param.getApplyMoney());
                auditParam.setRefundAddressId(0);
                auditParam.setOrderRefundNo(orderRefundNo);
                auditParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                auditParam.setMainId(0);
                auditParam.setSiteId(orderGoods.getSiteId());
                auditApply(auditParam);
            }
        }
    }

    /**
     * 创建退款单号
     *
     * @return 退款单号
     */
    private String createRefundNo() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long id = snowflake.nextId();
        return DateUtil.format(DateUtil.date(System.currentTimeMillis()), "yyyyMMdd") + id;
    }
}
