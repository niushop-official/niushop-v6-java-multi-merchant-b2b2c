package com.niu.mall.service.core.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.NoticeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.basic.Invoice;
import com.niu.core.enums.basic.InvoiceContentEnum;
import com.niu.core.enums.basic.InvoiceStatusEnum;
import com.niu.core.mapper.basic.InvoiceMapper;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.finance.ICoreFinanceService;
import com.niu.core.service.core.finance.param.OrderFinanceParam;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderCloseService;
import com.niu.core.service.core.local_delivery.param.LocalDeliveryOrderCloseParam;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.shop.ICoreShopMemberService;
import com.niu.core.service.core.sys.ICorePrinterService;
import com.niu.core.service.core.sys.ICoreSysStatService;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import com.niu.core.service.core.sys.param.SysPrinterPrintTicketParam;
import com.niu.mall.entity.cart.MallCart;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDiscount;
import com.niu.mall.entity.order.MallOrderGiftGoods;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.config.MallOrderDelaySpear;
import com.niu.mall.entity.order.config.MallOrderEvaluate;
import com.niu.mall.entity.order.config.MallOrderFinish;
import com.niu.mall.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.*;
import com.niu.mall.event.order.MallOrderSettlementEvent;
import com.niu.mall.event.order.MallOrderSettlementFinishEvent;
import com.niu.mall.event.order.OrderCloseStockEvent;
import com.niu.mall.event.order.SyncSupplyGoodsStockDecEvent;
import com.niu.mall.mapper.cart.MallCartMapper;
import com.niu.mall.mapper.order.MallOrderDiscountMapper;
import com.niu.mall.mapper.order.MallOrderGiftGoodsMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.goods.ICoreGoodsService;
import com.niu.mall.service.core.goods.ICoreGoodsStatNumService;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import com.niu.mall.service.core.marketing.ICoreCouponMemberService;
import com.niu.mall.service.core.order.*;
import com.niu.mall.service.core.order.helper.OrderPointDeductHelper;
import com.niu.mall.service.core.order.param.*;
import com.niu.mall.service.core.order.vo.*;
import com.niu.mall.service.site.marketing.IMallDiscountService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoreOrderEventServiceImpl implements ICoreOrderEventService {

    @Resource
    ICoreInvoiceService coreInvoiceService;

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    ICoreOrderLogService coreOrderLogService;

    @Resource
    ICoreGoodsService coreGoodsService;

    @Resource
    MallOrderDiscountMapper mallOrderDiscountMapper;

    @Resource
    ICoreCouponMemberService coreCouponMemberService;

    @Resource
    MallCartMapper cartMapper;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    @Resource
    ICoreSysStatService coreSysStatService;

    @Resource
    InvoiceMapper invoiceMapper;

    @Resource
    ICoreOrderDeliveryService coreOrderDeliveryService;

    @Resource
    ICoreLocalDeliveryOrderCloseService coreLocalDeliveryOrderCloseService;

    @Resource
    ICoreMemberService coreMemberService;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;
    @Resource
    ICoreAddonService coreAddonService;
    @Resource
    @Lazy
    ICoreFinanceService financeService;
    @Resource
    ICoreShopMemberService coreShopMemberService;
    @Resource
    IMallDiscountService mallDiscountService;
    @Resource
    ICoreGoodsStatNumService coreGoodsStatNumService;
    @Resource
    ICorePrinterService corePrinterService;
    @Resource
    @Lazy
    ICoreOrderCloseService coreOrderCloseService;
    @Resource
    ICoreOrderWeappDeliveryService coreOrderWeappDeliveryService;
    @Resource
    @Lazy
    ICoreOrderSettlementService coreOrderSettlementService;
    @Resource
    private MallOrderGiftGoodsMapper orderGiftGoodsMapper;
    @Resource
    private ICoreOrderGroupService coreOrderGroupService;
    @Resource
    private OrderPointDeductHelper orderPointDeductHelper;

    /**
     * 订单关闭之后
     *
     * @param order
     * @param param
     */
    @Override
    @Async
    public void orderCloseAfter(MallOrder order, OrderCloseParam param) {

        // 返还使用的优惠
        List<MallOrderDiscount> orderDiscount = mallOrderDiscountMapper.selectList(new QueryWrapper<MallOrderDiscount>().eq("order_id", order.getOrderId()));
        if (ObjectUtil.isNotEmpty(orderDiscount)) {
            List<CheckMallOrderDiscountVo> checkList = new ArrayList<>();
            for (MallOrderDiscount item : orderDiscount) {
                boolean isHas;
                switch (item.getDiscountType()) {
                    case "coupon":
                        isHas = false;
                        checkList = mallOrderDiscountMapper.selectJoinList(CheckMallOrderDiscountVo.class, new MPJQueryWrapper<MallOrderDiscount>()
                                .setAlias("nmod")
                                .innerJoin("?_mall_order nmo ON nmod.order_id = nmo.order_id".replace("?_", GlobalConfig.tablePrefix))
                                .eq("nmod.discount_type", "coupon")
                                .eq("nmod.discount_type_id", item.getDiscountTypeId())
                                .eq("nmo.group_id", order.getGroupId())
                                .ne("nmod.order_id", order.getOrderId())
                                .select("nmod.id, nmod.site_id, nmod.order_id, nmod.order_goods_ids, nmod.type, nmod.num, nmod.money, nmod.discount_type, nmod.discount_type_id, nmod.content, nmod.create_time, nmod.status, nmo.status AS order_status"));
                        if (ObjectUtil.isNotEmpty(checkList)) {
                            for (CheckMallOrderDiscountVo check : checkList) {
                                if (!check.getOrderStatus().equals(OrderStatusEnum.CLOSE.getStatus())) {
                                    isHas = true;
                                }
                            }
                        }
                        if (!isHas) {
                            coreCouponMemberService.recover(item.getDiscountTypeId());
                        }
                        break;
                    case "platform_coupon":
                        isHas = false;
                        checkList = mallOrderDiscountMapper.selectJoinList(CheckMallOrderDiscountVo.class, new MPJQueryWrapper<MallOrderDiscount>()
                                .setAlias("nmod")
                                .innerJoin("?_mall_order nmo ON nmod.order_id = nmo.order_id".replace("?_", GlobalConfig.tablePrefix))
                                .eq("nmod.discount_type", "platform_coupon")
                                .eq("nmod.discount_type_id", item.getDiscountTypeId())
                                .eq("nmo.group_id", order.getGroupId())
                                .ne("nmod.order_id", order.getOrderId())
                                .select("nmod.id, nmod.site_id, nmod.order_id, nmod.order_goods_ids, nmod.type, nmod.num, nmod.money, nmod.discount_type, nmod.discount_type_id, nmod.content, nmod.create_time, nmod.status, nmo.status AS order_status"));
                        if (ObjectUtil.isNotEmpty(checkList)) {
                            for (CheckMallOrderDiscountVo check : checkList) {
                                if (!check.getOrderStatus().equals(OrderStatusEnum.CLOSE.getStatus())) {
                                    isHas = true;
                                }
                            }
                        }
                        if (!isHas) {
                            coreCouponMemberService.recover(item.getDiscountTypeId());
                        }
                        break;


                }
            }
        }

        //返还商品库存
        List<MallOrderGoods> orderGoods = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("order_id", order.getOrderId()));

        // 非退款关单退还抵扣积分（对齐 PHP AfterMallOrderClose；退款关单由订单项处理）
        String closeType = param.getCloseType();
        orderPointDeductHelper.refundOnOrderClose(order, closeType, orderGoods);
        boolean shouldDecSaleNum = order.getPayTime() == 0
                || OrderCloseTypeEnum.REFUND_CLOSE.getType().equals(closeType);
        for (MallOrderGoods item : orderGoods) {
            // 扣除销量：未支付关单，或已支付全额退款关单
            if (shouldDecSaleNum) {
                coreGoodsService.decSaleNum(item.getSiteId(), item.getGoodsId(), item.getSkuId(), item.getNum());
                GoodsAddStatParam goodsAddStatParam = new GoodsAddStatParam();
                goodsAddStatParam.setSiteId(item.getSiteId());
                goodsAddStatParam.setGoodsId(item.getGoodsId());
                goodsAddStatParam.setSaleNum(item.getNum() * -1);
                coreGoodsStatService.addStat(goodsAddStatParam);
            }

            // 返还库存
            if (item.getSupplyGoodsId() == 0) {
                coreGoodsService.incStock(item.getSiteId(), item.getGoodsId(), item.getSkuId(), item.getNum());
            }

        }

        // ========== 补充2：赠品库存返还 ==========
        List<MallOrderGiftGoods> giftGoodsList = orderGiftGoodsMapper.selectList(
                new LambdaQueryWrapper<MallOrderGiftGoods>().eq(MallOrderGiftGoods::getOrderId, order.getOrderId())
        );
        for (MallOrderGiftGoods gift : giftGoodsList) {
            if (gift.getSupplyGoodsId() == 0) {
                coreGoodsService.incStock(gift.getSiteId(), gift.getGoodsId(), gift.getSkuId(), gift.getNum());
            }
        }

        //订单退款同步供应商库存
        if (checkAddon("supply")) {
            if (ObjectUtil.isNotEmpty(order.getSupplyId()) && order.getSupplyId() > 0) {
                OrderCloseStockEvent orderCloseStockEvent = new OrderCloseStockEvent();
                orderCloseStockEvent.setOrderId(order.getOrderId());
                orderCloseStockEvent.addAppSign("supply");
                EventAndSubscribeOfPublisher.publishAll(orderCloseStockEvent);
            }
        }

        // 发票失效
        if (order.getInvoiceId() > 0) coreInvoiceService.close(order.getInvoiceId());

        // ========== 补充3：订单组关闭 ==========
        coreOrderGroupService.closeOfAllOrderClose(order.getOrderId());

        //本地配送渠道关闭
        try {
            LocalDeliveryOrderCloseParam closeParam = new LocalDeliveryOrderCloseParam();
            closeParam.setTradeType("mall");
            closeParam.setTradeNo(order.getOrderNo());
            coreLocalDeliveryOrderCloseService.closeOrder(closeParam);
        } catch (Exception e) {
            log.error("本地配送渠道关闭失败: {}", e.getMessage());
        }

        // 添加订单日志
        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(order.getOrderId());
        logParam.setStatus(Integer.valueOf(order.getStatus()));
        logParam.setMainType(ObjectUtil.defaultIfNull(param.getMainType(), OrderLogMainTypeEnum.MEMBER.getType()));
        logParam.setMainId(ObjectUtil.defaultIfNull(param.getMainId(), order.getMemberId()));
        logParam.setType(OrderLogActionEnum.ORDER_CLOSE_ACTION.getAction());
        coreOrderLogService.add(logParam);

    }

    /**
     * 订单创建之后
     *
     * @param vo
     */
    @Override
    public void orderCreateAfter(OrderCreateDataVo vo) {
        log.info("订单AfterShopOrderCreate 订单id:" + vo.getOrderId());
        try {
            long time = System.currentTimeMillis() / 1000;
            // 创建发票
            this.createInvoice(vo);

            for (OrderCreateDataVo.Sku item : vo.getGoodsData().values()) {
                // 增加销量
                coreGoodsService.incSaleNum(item.getSiteId(), item.getGoodsId(), item.getSkuId(), item.getNum());
                // 减少库存
                coreGoodsService.decStock(item.getSiteId(), item.getGoodsId(), item.getSkuId(), item.getNum());

                GoodsAddStatParam goodsAddStatParam = new GoodsAddStatParam();
                goodsAddStatParam.setSiteId(item.getSiteId());
                goodsAddStatParam.setGoodsId(item.getGoodsId());
                goodsAddStatParam.setSaleNum(item.getNum());
                coreGoodsStatService.addStat(goodsAddStatParam);
            }

            //扣减赠品库存
            for (MallOrderGiftGoods giftGoods : vo.getGiftGoods()) {
                coreGoodsService.decStock(giftGoods.getSiteId(), giftGoods.getGoodsId(), giftGoods.getSkuId(), giftGoods.getNum());
            }

            // 清除购物车
            if (ObjectUtil.isNotEmpty(vo.getCartIds())) {
                cartMapper.delete(new QueryWrapper<MallCart>()
                        .in("id", vo.getCartIds())
                        .eq("site_id", vo.getSiteId())
                        .eq("member_id", vo.getMemberId()));
            }


            //同步供货库存和销量(减/增)
            if (ObjectUtil.isNotEmpty(vo.getSupplyId()) && vo.getSupplyId() > 0) {
                SyncSupplyGoodsStockDecEvent event = new SyncSupplyGoodsStockDecEvent();
                event.setSiteId(vo.getSiteId());
                event.setOrderId(vo.getOrderId());
                EventAndSubscribeOfPublisher.publishAll(event);
            }

            // 添加订单日志
            AddOrderLogParam logParam = new AddOrderLogParam();
            logParam.setOrderId(vo.getOrderId());
            logParam.setStatus(OrderStatusEnum.WAIT_PAY.getStatus());
            logParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
            logParam.setMainId(vo.getMemberId());
            logParam.setType(OrderLogActionEnum.ORDER_CREATE_ACTION.getAction());
            coreOrderLogService.add(logParam);

            //创建定时关闭任务
            coreOrderCloseService.setOrderCloseTimeout(vo.getOrderId(), time);

            // 添加统计
            SysDataAddParam statParam = new SysDataAddParam();
            statParam.setOrderNum(1);
            statParam.setSiteId(vo.getSiteId());
            coreSysStatService.addStat(statParam);
        } catch (Exception e) {
            log.error("订单AfterShopOrderCreate失败 - orderId: {}, 错误: {}", vo.getOrderId(), e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * 创建发票
     *
     * @param vo
     */
    private void createInvoice(OrderCreateDataVo vo) {
        InvoiceInfoVo invoice = coreInvoiceService.invoice(vo.getSiteId());
        if (ObjectUtil.isEmpty(invoice)) return;
        if (invoice.getIsInvoice().equals("1") && ObjectUtil.isNotEmpty(vo.getInvoice().getHeaderName())) {
            Invoice model = new Invoice();
            BeanUtil.copyProperties(vo.getInvoice(), model);
            model.setSiteId(vo.getSiteId());
            model.setMemberId(vo.getMemberId());
            model.setTradeId(vo.getOrderId());
            model.setTradeType("mall");
            model.setMoney(vo.getBasic().getOrderMoney());
            model.setStatus(InvoiceStatusEnum.WAIT_OPEN.getStatus());
            model.setCreateTime(System.currentTimeMillis() / 1000);
            model.setName(InvoiceContentEnum.getNameByType(vo.getInvoice().getContentType()));
            invoiceMapper.insert(model);

            MallOrder order = new MallOrder();
            order.setInvoiceId(model.getId());
            order.setOrderId(vo.getOrderId());
            mallOrderMapper.updateById(order);
        }
    }

    /**
     * 订单支付之后
     *
     * @param order
     * @param param
     */
    @Override
    @Async
    public void orderPayAfter(MallOrder order, OrderPayParam param) {

        // 发票改变状态
        if (order.getInvoiceId() > 0) coreInvoiceService.open(order.getInvoiceId());

        // 添加订单日志
        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(order.getOrderId());
        logParam.setStatus(Integer.valueOf(order.getStatus()));
        logParam.setMainType(ObjectUtil.defaultIfNull(param.getMainType(), OrderLogMainTypeEnum.MEMBER.getType()));
        logParam.setMainId(ObjectUtil.defaultIfNull(param.getMainId(), order.getMemberId()));
        logParam.setType(OrderLogActionEnum.ORDER_PAY_ACTION.getAction());
        coreOrderLogService.add(logParam);

        // 添加统计
        SysDataAddParam statParam = new SysDataAddParam();
        statParam.setSiteId(order.getSiteId());
        statParam.setSaleMoney(order.getOrderMoney());
        coreSysStatService.addStat(statParam);


        //订单支付 资金流水统计/账单统计
        OrderFinanceParam financeParam = new OrderFinanceParam();
        BeanUtils.copyProperties(order, financeParam);
        financeService.orderPay(financeParam);

        //修改店铺卖家消费时间
        coreShopMemberService.setConsum(order.getSiteId(), order.getMemberId(), 1, order.getOrderMoney());

        // 发送消息
        Map<String, Object> noticeParam = new HashMap<String, Object>();
        noticeParam.put("order", order);
        NoticeUtils.send(order.getSiteId(), "mall_order_pay", noticeParam);


        //商品支付数量 金额统计数据
        coreGoodsStatService.saveGoodsPayNumAndMoneyByOrderId(order.getOrderId());
        //商城商品销量统计
        coreGoodsStatNumService.updateMallSaleNum(order.getOrderId());

        // 虚拟商品自动发货（对照 PHP AfterMallOrderPay::afterOrderPayEvent）
        handleVirtualOrderDelivery(order);
        //限时折扣活动数据累计
        mallDiscountService.orderPayAfter(order);


        //小票打印，订单付款之后
        try {
            SysPrinterPrintTicketParam printTicketParam = new SysPrinterPrintTicketParam();
            printTicketParam.setSiteId(order.getSiteId());
            printTicketParam.setType("mallGoodsOrder");
            printTicketParam.setTrigger("pay_after");
            SysPrinterPrintTicketParam.Business business = new SysPrinterPrintTicketParam.Business();
            business.setOrderId(order.getOrderId());
            printTicketParam.setBusiness(business);
            corePrinterService.printTicket(printTicketParam);
        } catch (Exception e) {
            log.error(" 小票打印失败 - 堆栈信息:", e);
        }
    }

    @Override
    public void handleVirtualOrderDelivery(MallOrder order) {
        List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper.selectList(
                new QueryWrapper<MallOrderGoods>().eq("order_id", order.getOrderId())
        );
        if (checkAddon("supply")) {
            orderGoodsList = orderGoodsList.stream()
                    .filter(item -> item.getSupplyId() == null || item.getSupplyId() == 0)
                    .collect(Collectors.toList());
        }
        List<Integer> virtualOrderGoodsIds = new ArrayList<>();
        for (MallOrderGoods orderGoods : orderGoodsList) {
            if (!GoodsTypeEnum.VIRTUAL.getType().equals(orderGoods.getGoodsType())) {
                continue;
            }
            if (!org.springframework.util.StringUtils.hasText(orderGoods.getGoodsConfig())) {
                continue;
            }
            JSONObject goodsConfig = JSONUtil.parseObj(orderGoods.getGoodsConfig());
            String virtualDeliveryType = goodsConfig.getStr("virtual_delivery_type", "");
            switch (virtualDeliveryType) {
                case "auto":
                    virtualOrderGoodsIds.add(orderGoods.getOrderGoodsId());
                    break;
                case "verify":
                    int virtualVerifyType = goodsConfig.getInt("virtual_verify_type", 0);
                    long expireTime;
                    if (virtualVerifyType == 0) {
                        expireTime = 0L;
                    } else if (virtualVerifyType == 1) {
                        expireTime = System.currentTimeMillis() / 1000 + goodsConfig.getLong("virtual_indate", 0L) * 86400;
                    } else {
                        expireTime = goodsConfig.getLong("virtual_indate", 0L);
                    }
                    if (goodsConfig.getInt("virtual_refund", 0) == 1) {
                        int refundDay = goodsConfig.getInt("virtual_refund_day", 0);
                        long refundExpireTime = refundDay > 0 ? System.currentTimeMillis() / 1000 + refundDay * 86400L : 0L;
                        goodsConfig.set("refund_expire_time", refundExpireTime);
                    }
                    MallOrderGoods verifyUpdate = new MallOrderGoods();
                    verifyUpdate.setOrderGoodsId(orderGoods.getOrderGoodsId());
                    verifyUpdate.setGoodsConfig(goodsConfig.toString());
                    verifyUpdate.setVerifyExpireTime(expireTime);
                    verifyUpdate.setIsVerify(1);
                    mallOrderGoodsMapper.updateById(verifyUpdate);
                    virtualOrderGoodsIds.add(orderGoods.getOrderGoodsId());
                    break;
                default:
                    break;
            }
        }
        if (ObjectUtil.isNotEmpty(virtualOrderGoodsIds)) {
            OrderDeliveryParam deliveryParam = new OrderDeliveryParam();
            deliveryParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
            deliveryParam.setMainId(0);
            deliveryParam.setSiteId(order.getSiteId());
            deliveryParam.setOrderId(order.getOrderId());
            deliveryParam.setDeliveryType(OrderDeliveryTypeEnum.VIRTUAL.getType());
            deliveryParam.setOrderGoodsIds(virtualOrderGoodsIds.toArray(Integer[]::new));
            coreOrderDeliveryService.delivery(deliveryParam);
        }
    }

    /**
     * 检查插件是否安装
     *
     * @param key
     */
    private Boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap = coreAddonService.getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }

    /**
     * 订单发货完成之后
     *
     * @param order
     * @param orderDeliveryDataParam
     */
    @Override
    @Async
    public void orderDeliveryAfter(MallOrder order, OrderDeliveryParam orderDeliveryDataParam) {

        MallOrderFinish orderFinish = coreOrderConfigService.getConfig().getConfirm();
        if (orderFinish.getIsFinishEr()) {
            if (orderFinish.getFinishLength() > 0) {
                Long timeout = order.getDeliveryTime() + orderFinish.getFinishLength() * 86400;
                mallOrderMapper.update(new MallOrder(), new UpdateWrapper<MallOrder>().eq("order_id", order.getOrderId())
                        .set("timeout", timeout));
            }
        }
        // 订单定时收货任务
        Long timeout = 0L;
        List<MallOrderGoods> orderGoods = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("order_id", order.getOrderId()));
        if (orderGoods.size() == 1 && orderGoods.get(0).getIsVerify() == 1) {
            if (orderGoods.get(0).getVerifyExpireTime() > 0) {
                timeout = orderGoods.get(0).getVerifyExpireTime();
            }
        } else {
            OrderConfigVo orderConfig = coreOrderConfigService.getConfig();
            if (orderConfig.getConfirm().getIsFinishEr() && orderConfig.getConfirm().getFinishLength() > 0) {
                timeout = order.getDeliveryTime() + orderConfig.getConfirm().getFinishLength() * 86400;
            }
        }
        if (timeout > 0) {
            MallOrder orderUpdate = new MallOrder();
            orderUpdate.setOrderId(order.getOrderId());
            orderUpdate.setTimeout(timeout);
            mallOrderMapper.updateById(orderUpdate);
        }

        // 添加订单日志
        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(order.getOrderId());
        logParam.setStatus(OrderStatusEnum.WAIT_TAKE.getStatus());
        logParam.setMainType(ObjectUtil.defaultIfNull(orderDeliveryDataParam.getMainType(), OrderLogMainTypeEnum.SYSTEM.getType()));
        logParam.setMainId(ObjectUtil.defaultIfNull(orderDeliveryDataParam.getMainId(), 0));
        logParam.setType(OrderLogActionEnum.ORDER_DELIVERY_ACTION.getAction());
        coreOrderLogService.add(logParam);

        // 物流信息上传
        String str = coreOrderWeappDeliveryService.orderShippingUploadShippingInfo(order.getOrderId(), order.getSiteId());
        if (StringUtils.isNotEmpty(str)) {
            throw new CommonException(str);
        }
        // 发送消息
        Map<String, Object> noticeParam = new HashMap<String, Object>();
        noticeParam.put("order", order);
        NoticeUtils.send(order.getSiteId(), "mall_order_delivery", noticeParam);
    }


    /**
     * 订单完成之后
     *
     * @param order
     * @return
     */
    @Override
    @Async
    public void orderFinishAfter(MallOrder order, OrderFinishParam orderFinishParam) {


        // 添加订单日志
        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(order.getOrderId());
        logParam.setStatus(OrderStatusEnum.FINISH.getStatus());
        logParam.setMainType(ObjectUtil.defaultIfNull(orderFinishParam.getMainType(), OrderLogMainTypeEnum.SYSTEM.getType()));
        logParam.setMainId(ObjectUtil.defaultIfNull(orderFinishParam.getMainId(), 0));
        logParam.setType(OrderLogActionEnum.ORDER_FINISH_ACTION.getAction());
        coreOrderLogService.add(logParam);

        // 微信小程序 发送 确认收货提醒消息
        coreOrderWeappDeliveryService.orderShippingNotifyConfirmReceive(order.getOutTradeNo(), order.getDeliveryType());

        // 订单完成发放积分成长值
        Map<String, Object> data = new HashMap<>();
        data.put("related_id", order.getOrderId());
        data.put("from_type", "consume_reward");
        data.put("order_money", order.getOrderMoney());
        coreMemberService.sendGrowth(RequestUtils.defaultSiteId(), order.getMemberId(), "mall_buy_goods", data);
        coreMemberService.sendGrowth(RequestUtils.defaultSiteId(), order.getMemberId(), "mall_buy_order", new HashMap<>());
        coreMemberService.sendPoint(RequestUtils.defaultSiteId(), order.getMemberId(), "mall_buy_goods", data);

        //设置订单自动好评时间
        MallOrderEvaluate evaluateConfig = coreOrderConfigService.getEvaluateConfig();
        if (evaluateConfig.getIsAutoGoodEvaluate() == 1) {
            if (evaluateConfig.getGoodEvaluateDays() > 0) {
                mallOrderMapper.update(new MallOrder(), new UpdateWrapper<MallOrder>().eq("order_id", order.getOrderId())
                        .set("evaluate_timer", order.getFinishTime() + evaluateConfig.getGoodEvaluateDays() * 86400)
                );
            }
        }
        //设置自动结算时间
        MallOrderDelaySpear orderConfig = coreOrderConfigService.orderDelaySpear();
        if (orderConfig.getIsDelaySpear() == 1) {
            if (orderConfig.getDelayDays() > 0) {
                mallOrderMapper.update(new MallOrder(), new UpdateWrapper<MallOrder>().eq("order_id", order.getOrderId())
                        .set("settlement_timer", order.getFinishTime() + orderConfig.getDelayDays() * 86400)
                );
            }
        } else {
            coreOrderSettlementService.settlement(order);
        }


        OrderFinanceParam financeParam = new OrderFinanceParam();
        BeanUtils.copyProperties(order, financeParam);
        financeService.orderFinish(financeParam);

        try {
            SysPrinterPrintTicketParam printTicketParam = new SysPrinterPrintTicketParam();
            printTicketParam.setSiteId(order.getSiteId());
            printTicketParam.setType("mallGoodsOrder");
            printTicketParam.setTrigger("take_delivery");
            SysPrinterPrintTicketParam.Business business = new SysPrinterPrintTicketParam.Business();
            business.setOrderId(order.getOrderId());
            printTicketParam.setBusiness(business);
            corePrinterService.printTicket(printTicketParam);
        } catch (Exception e) {
            log.error(" 小票打印失败 - 堆栈信息:", e);
        }
    }

    /**
     * 订单结算
     *
     * @param order 订单信息
     */
    @Override
    public void orderSettlement(MallOrder order) {
        MallOrderSettlementEvent event = new MallOrderSettlementEvent();
        event.setSiteId(order.getSiteId());
        event.addAppSign("mall");
        event.setName("MallOrderSettlement");
        event.setOrder(order);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    /**
     * 订单结算完成
     *
     * @param order 订单信息
     */
    @Override
    public void orderSettlementFinish(MallOrder order) {
        MallOrderSettlementFinishEvent event = new MallOrderSettlementFinishEvent();
        event.setSiteId(order.getSiteId());
        event.addAppSign("mall");
        event.setName("MallOrderSettlementFinish");
        event.setOrder(order);
        EventAndSubscribeOfPublisher.publishAll(event);
    }
}
