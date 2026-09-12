package com.niu.mall.service.api.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.DistanceCalculateUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.delivery.DeliveryCompany;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrderLog;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.shop.SiteShopCashOut;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderLogMapper;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.shop.SiteShopCashOutMapper;
import com.niu.core.mapper.site.DeliveryCompanyMapper;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.delivery_weapp.ICoreDeliveryWeappService;
import com.niu.core.service.core.delivery_weapp.vo.IsTradeManagedVo;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.mall.entity.order.*;
import com.niu.mall.enums.common.CommonActiveEnum;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.*;
import com.niu.mall.event.pintuan.CheckInvoiceStatusEventDefiner;
import com.niu.mall.mapper.order.*;
import com.niu.mall.service.api.order.IOrderService;
import com.niu.mall.service.api.order.param.DeliveryPackageSearchParam;
import com.niu.mall.service.api.order.param.MallOrderSearchParam;
import com.niu.mall.service.api.order.vo.*;
import com.niu.mall.service.core.order.*;
import com.niu.mall.service.core.order.param.DeliverySearchParam;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import com.niu.mall.service.core.order.param.OrderFinishParam;
import com.niu.mall.service.core.order.vo.MallOrderGroupCloseVo;
import com.niu.mall.service.core.order.vo.OrderConfigVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 订单实现
 */
@Service
public class OrderServiceImpl implements IOrderService {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGroupMapper mallOrderGroupMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    MallOrderDiscountMapper mallOrderDiscountMapper;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    @Resource
    PayMapper payMapper;

    @Resource
    DeliveryStoreMapper deliveryStoreMapper;

    @Resource
    MallOrderDeliveryMapper mallOrderDeliveryMapper;

    @Resource
    MallOrderRefundMapper mallOrderRefundMapper;

    @Resource
    ICoreOrderService coreOrderService;

    @Resource
    ICoreOrderCloseService coreOrderCloseService;

    @Resource
    ICoreOrderFinishService coreOrderFinishService;

    @Resource
    DeliveryCompanyMapper deliveryCompanyMapper;

    @Resource
    SiteShopCashOutMapper siteShopCashOutMapper;

    @Resource
    ISiteService siteService;

    @Resource
    DeliveryLocalDeliveryOrderLogMapper deliveryLocalDeliveryOrderLogMapper;

    @Resource
    DeliveryLocalDeliveryOrderMapper deliveryLocalDeliveryOrderMapper;
    @Resource
    private ICoreOrderGroupService coreOrderGroupService;
    @Resource
    private ICoreDeliveryWeappService coreDeliveryWeappService;
    @Resource
    private ICorePayService corePayService;
    @Resource
    private MallOrderGiftGoodsMapper mallOrderGiftGoodsMapper;
    @Resource
    private MallOrderDeliveryContentMapper orderDeliveryContentMapper;

    /**
     * 订单列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderListVo>
     */
    @Override
    public MallOrderListPageVo page(PageParam pageParam, MallOrderSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("site_id,order_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,refund_money,invoice_id,create_time,pay_time,delivery_type,taker_name,taker_mobile,taker_full_address,take_store_id,is_enable_refund,member_remark,mall_remark,close_remark,is_evaluate, group_id,extend_field_1,extend_field_2,use_point,point_deduct_money");
        queryWrapper.eq("member_id", RequestUtils.memberId());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderNo())) queryWrapper.like("order_no", searchParam.getOrderNo());
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(wrapper -> {
                wrapper.like("order_no", searchParam.getKeyword())
                        .or()
                        .like("body", searchParam.getKeyword());
            });
        }
        queryWrapper.orderByDesc("create_time");

        OrderConfigVo orderConfig = coreOrderConfigService.getConfig();

        IPage<MallOrder> iPage = mallOrderMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<Integer> orderIds = iPage.getRecords().stream().map(MallOrder::getOrderId).toList();
        List<Integer> groupIds = iPage.getRecords().stream().map(MallOrder::getGroupId).toList();
        List<MallOrderGoods> orderGoodsAll = new ArrayList<>();
        if (!orderIds.isEmpty()) {
            orderGoodsAll = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().in("order_id", orderIds));
        }
        List<MallOrderGroup> orderGroups = new ArrayList<>();
        if (!groupIds.isEmpty()) {
            orderGroups = mallOrderGroupMapper.selectByIds(groupIds);
        }
        List<MallOrder> orderGroupsList = new ArrayList<>();
        if (!orderGroups.isEmpty()) {
            orderGroupsList = mallOrderMapper.selectList(new QueryWrapper<MallOrder>().in("group_id", orderGroups.stream().map(MallOrderGroup::getGroupId).toList()));
        }

        List<MallOrderGiftGoods> allGiftRows = new ArrayList<>();
        if (!orderIds.isEmpty()) {
            allGiftRows = mallOrderGiftGoodsMapper.selectList(new LambdaQueryWrapper<MallOrderGiftGoods>()
                    .in(MallOrderGiftGoods::getOrderId, orderIds));
        }
        Map<Integer, List<MallOrderGiftGoods>> giftsByOrderId = allGiftRows.stream()
                .collect(Collectors.groupingBy(MallOrderGiftGoods::getOrderId));

        List<MallOrderListVo> list = new LinkedList<>();
        for (MallOrder item : iPage.getRecords()) {
            MallOrderListVo vo = new MallOrderListVo();
            BeanUtils.copyProperties(item, vo);

            List<MallOrderListVo.OrderGoods> orderGoods = CollectUtils.convert(orderGoodsAll.stream().filter(it -> it.getOrderId().equals(item.getOrderId())).toList(), MallOrderListVo.OrderGoods.class);
            vo.setOrderGoods(orderGoods);

            Map<String, Object> statusBlock = OrderStatusEnum.getDeliveryTypeMapByStatus(Integer.parseInt(item.getStatus()), item.getDeliveryType(), item.getOrderType());
            vo.setOrderStatusData(new LinkedHashMap<>(statusBlock));

            List<MallOrderGiftGoods> giftRows = giftsByOrderId.getOrDefault(item.getOrderId(), List.of());
            vo.setOrderGiftGoods(CollectUtils.convert(giftRows, MallOrderListVo.OrderGiftGoods.class));
            vo.setGiftOrderGoods(buildGiftOrderGoodsList(giftRows));

            vo.setExpireTime(vo.getCreateTime() + orderConfig.getCloseOrderInfo().getCloseLength() * 60);

            if (!item.getOutTradeNo().isEmpty()) {
                Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", item.getOutTradeNo()));
                if (pay != null) {
                    MallOrderListVo.Pay payVo = new MallOrderListVo.Pay();
                    BeanUtil.copyProperties(pay, payVo);
                    vo.setPay(payVo);
                }

            }
            //查询订单组
            List<MallOrderGroup> mallOrderGroupThis = orderGroups.stream().filter(it -> it.getGroupId().equals(item.getGroupId())).toList();
            MallOrderListVo.OrderGroup orderGroup = new MallOrderListVo.OrderGroup();
            if (!mallOrderGroupThis.isEmpty()) {
                BeanUtils.copyProperties(mallOrderGroupThis.get(0), orderGroup);
            }
            //查询订单
            List<MallGroupOrderInfoVo> mallOrder = CollectUtils.convert(orderGroupsList.stream().filter(it -> it.getGroupId().equals(item.getGroupId())).toList(), MallGroupOrderInfoVo.class);
            orderGroup.setMallOrder(mallOrder);
            vo.setOrderGroup(orderGroup);
            SiteInfoVo siteInfoVo = siteService.info(vo.getSiteId());
            MallOrderListVo.Site site = new MallOrderListVo.Site();
            site.setSiteID(siteInfoVo.getSiteId());
            site.setSiteName(siteInfoVo.getSiteName());
            vo.setSite(site);
            //校验发票
            vo.setCanInvoice(checkIfCanInvoice(item.getInvoiceId(), item.getOrderType(), item.getStatus()));
            vo.setInvoiceMoney(calcInvoiceMoney(item.getOrderMoney(), item.getRefundMoney()));
            list.add(vo);
        }

        //查询小程序是否已开通发货信息管理服务
        IsTradeManagedVo verifyRankIds = coreDeliveryWeappService.getIsTradeManaged();
        return new MallOrderListPageVo(PageResult.build(page, limit, iPage.getTotal()).setData(list), verifyRankIds.getMchId(), verifyRankIds.getIsTradeManaged());
    }

    /**
     * 订单详情
     *
     * @param orderId 主键
     * @return ShopOrderInfoVo
     */
    @Override
    public MallOrderInfoVo detail(Integer orderId, Map<String, String> allParams) {
        // 1. 查询订单主表（指定字段）
        MallOrder model = mallOrderMapper.selectOne(
                new QueryWrapper<MallOrder>()
                        .eq("order_id", orderId)
                        .eq("member_id", RequestUtils.memberId())
                        .select("*"));

        Assert.notNull(model, "订单不存在");

        MallOrderInfoVo vo = new MallOrderInfoVo();
        BeanUtils.copyProperties(model, vo);

        // 查询订单项
        // 2. 查询订单商品项
        List<MallOrderInfoVo.OrderGoods> orderGoods = CollectUtils.convert(mallOrderGoodsMapper.selectList(
                new QueryWrapper<MallOrderGoods>().eq("order_id", vo.getOrderId())), MallOrderInfoVo.OrderGoods.class);

        vo.setOrderGoods(orderGoods);

        // ========== 3. 虚拟商品退款逻辑（补齐） ==========
        long now = System.currentTimeMillis() / 1000;
        for (MallOrderInfoVo.OrderGoods goods : orderGoods) {
            if (GoodsTypeEnum.VIRTUAL.getType().equals(goods.getGoodsType())) {
                if (StringUtils.isEmpty(goods.getGoodsConfig())) {
                    continue;
                }
                Map<String, Object> goodsConfig = JSON.parseObject(goods.getGoodsConfig());
                if (goodsConfig == null) continue;
                String virtualDeliveryType = String.valueOf(goodsConfig.getOrDefault("virtual_delivery_type", ""));
                if ("verify".equals(virtualDeliveryType)) {
                    repairVirtualVerifyExpireTime(vo, goods, goodsConfig);
                    int virtualRefund = parseIntValue(goodsConfig.get("virtual_refund"), 0);
                    long refundExpireTime = parseLongValue(goodsConfig.get("refund_expire_time"), 0L);

                    if (virtualRefund == 1 && goods.getIsEnableRefund() == 1) {
                        // 手动退款且允许退款，判断是否过期
                        goods.setIsEnableRefund(refundExpireTime > now ? 1 : 0);
                    } else if (virtualRefund == 2) {
                        // 自动退款时前台不允许退款
                        goods.setIsEnableRefund(0);
                    }
                }
            }
        }

        // 4. 查询赠品项（并转换为 gift_order_goods 格式）
        List<MallOrderGiftGoods> giftGoodsList = mallOrderGiftGoodsMapper.selectList(
                new LambdaQueryWrapper<MallOrderGiftGoods>()
                        .eq(MallOrderGiftGoods::getOrderId, orderId)
                        .select(MallOrderGiftGoods::getOrderGiftId, MallOrderGiftGoods::getSiteId,
                                MallOrderGiftGoods::getOrderId, MallOrderGiftGoods::getMemberId,
                                MallOrderGiftGoods::getGoodsId, MallOrderGiftGoods::getSkuId,
                                MallOrderGiftGoods::getGoodsName, MallOrderGiftGoods::getSkuName,
                                MallOrderGiftGoods::getGoodsImage, MallOrderGiftGoods::getSkuImage,
                                MallOrderGiftGoods::getPrice, MallOrderGiftGoods::getNum,
                                MallOrderGiftGoods::getGoodsMoney)
        );
        vo.setOrderGiftGoods(CollectUtils.convert(giftGoodsList, MallOrderInfoVo.OrderGiftGoods.class));
        vo.setGiftOrderGoods(buildGiftOrderGoodsList(giftGoodsList));

        // 5. 查询订单组
        MallOrderGroup mallOrderGroup = mallOrderGroupMapper.selectOne(new QueryWrapper<MallOrderGroup>().eq("group_id", vo.getGroupId()));
        if (ObjectUtil.isNotEmpty(mallOrderGroup)) {
            MallOrderInfoVo.OrderGroup orderGroup = new MallOrderInfoVo.OrderGroup();
            BeanUtils.copyProperties(mallOrderGroup, orderGroup);
            
            // 查询订单组下的其他订单（排除当前订单）
            List<MallOrder> groupOrders = mallOrderMapper.selectList(
                new QueryWrapper<MallOrder>()
                    .eq("group_id", vo.getGroupId())
                    .ne("order_id", vo.getOrderId())
                    .select("*")
            );
            groupOrders.add(model);
            // 转换为MallOrderInfoVo列表
            List<MallOrderInfoVo> orderGroupOrders = new ArrayList<>();
            for (MallOrder groupOrder : groupOrders) {
                MallOrderInfoVo groupOrderVo = new MallOrderInfoVo();
                BeanUtils.copyProperties(groupOrder, groupOrderVo);
                
                // 查询订单商品
                List<MallOrderInfoVo.OrderGoods> groupOrderGoods = CollectUtils.convert(
                    mallOrderGoodsMapper.selectList(
                        new QueryWrapper<MallOrderGoods>().eq("order_id", groupOrder.getOrderId())
                    ),
                    MallOrderInfoVo.OrderGoods.class
                );
                groupOrderVo.setOrderGoods(groupOrderGoods);
                
                orderGroupOrders.add(groupOrderVo);
            }
            orderGroup.setMallOrders(orderGroupOrders);
            
            vo.setOrderGroup(orderGroup);
        }

        // 查询订单关闭配置
        OrderConfigVo orderConfig = coreOrderConfigService.getConfig();
        vo.setExpireTime(vo.getCreateTime() + orderConfig.getCloseOrderInfo().getCloseLength() * 60);

        // 查询订单支付信息
        if (!vo.getOutTradeNo().isEmpty()) {
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", vo.getOutTradeNo()));
            if (pay != null) {
                MallOrderListVo.Pay payVo = new MallOrderListVo.Pay();
                BeanUtil.copyProperties(pay, payVo);
                vo.setPay(payVo);
            }
        }
        vo.setShowLocalDeliveryInfo(false);
        // 查询订单包裹信息
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType()) || vo.getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
            List<MallOrderDelivery> deliveries = mallOrderDeliveryMapper.selectList(
                    new QueryWrapper<MallOrderDelivery>().eq("order_id", vo.getOrderId())
            );
            List<MallOrderInfoVo.OrderDelivery> orderDelivery = new ArrayList<>();
            for (MallOrderDelivery deliveryItem : deliveries) {
                MallOrderInfoVo.OrderDelivery orderDeliveryVo = new MallOrderInfoVo.OrderDelivery();
                BeanUtils.copyProperties(deliveryItem, orderDeliveryVo);
                if (ObjectUtil.isNotEmpty(deliveryItem.getLocalDeliveryOrderId())) {
                    DeliveryLocalDeliveryOrder localOrderDelivery = deliveryLocalDeliveryOrderMapper.selectOne(
                            new QueryWrapper<DeliveryLocalDeliveryOrder>()
                                    .eq("delivery_no", deliveryItem.getLocalDeliveryOrderId())
                                    .last("LIMIT 1"));
                    if (ObjectUtil.isNotEmpty(localOrderDelivery)) {
                        orderDeliveryVo.setLocalDeliveryStatus(Integer.parseInt(localOrderDelivery.getStatus()));
                        orderDeliveryVo.setLocalDeliveryStatusName(LocalDeliveryStatusEnum.getNameByStatus(Integer.parseInt(localOrderDelivery.getStatus())));
                        orderDeliveryVo.setRiderMobile(localOrderDelivery.getRiderMobile());
                        orderDeliveryVo.setRiderName(localOrderDelivery.getRiderName());
                        orderDeliveryVo.setDeliveryService(localOrderDelivery.getDeliveryService());
                        orderDeliveryVo.setDeliveryServiceName(DeliveryStoreTypeEnum.getNameByKey(localOrderDelivery.getDeliveryService()));
                    }
                }
                orderDelivery.add(orderDeliveryVo);
            }
            vo.setOrderDelivery(orderDelivery);
            if (!orderDelivery.isEmpty()) {
                String localDeliveryId = orderDelivery.get(0).getLocalDeliveryOrderId();
                if (ObjectUtil.isNotEmpty(localDeliveryId)) {
                    DeliveryLocalDeliveryOrder orderLog = deliveryLocalDeliveryOrderMapper.selectOne(new QueryWrapper<DeliveryLocalDeliveryOrder>().eq("delivery_no", localDeliveryId).last("limit 1"));
                    if (ObjectUtil.isNotEmpty(orderLog)) {
                        vo.setLocalDeliveryStatus(Integer.parseInt(orderLog.getStatus()));
                        List<String> statusList = List.of(LocalDeliveryStatusEnum.ORDER_ACCEPTED.getStatus().toString(),
                                LocalDeliveryStatusEnum.PENDING_ACCEPTANCE.getStatus().toString(),
                                LocalDeliveryStatusEnum.PENDING_PICKUP.getStatus().toString(),
                                LocalDeliveryStatusEnum.RIDER_ARRIVED.getStatus().toString(),
                                LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().toString());
                        if (!orderLog.getDeliveryService().equals(DeliveryStoreTypeEnum.MERCHANT.getKey()) && statusList.contains(orderLog.getStatus())) {
                            vo.setShowLocalDeliveryInfo(true);
                        }
                        List<MallOrderInfoVo.LocalDeliveryOrderLogInfo> list = CollectUtils.convert(deliveryLocalDeliveryOrderLogMapper.selectList(new QueryWrapper<DeliveryLocalDeliveryOrderLog>().eq("order_id", orderLog.getId())), MallOrderInfoVo.LocalDeliveryOrderLogInfo.class);
                        if (!list.isEmpty()) {
                            for (MallOrderInfoVo.LocalDeliveryOrderLogInfo logo : list) {
                                MallOrderInfoVo.LocalDeliveryOrderInfo localDeliveryOrderInfo = new MallOrderInfoVo.LocalDeliveryOrderInfo();
                                BeanUtils.copyProperties(orderLog, localDeliveryOrderInfo);
                                logo.setLocalDeliveryOrder(localDeliveryOrderInfo);
                            }
                            vo.setLocalDeliveryOrderLog(list);
                        }
                    }
                }
            }
        }
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType())) {
            String latitude = firstNonEmpty(allParams.get("position[latitude]"), allParams.get("position.latitude"), allParams.get("latitude"));
            String longitude = firstNonEmpty(allParams.get("position[longitude]"), allParams.get("position.longitude"), allParams.get("longitude"));

            //查信息门店信息
            DeliveryStore storeInfoVo = deliveryStoreMapper.selectById(vo.getTakeStoreId());
            if (ObjectUtil.isNotEmpty(storeInfoVo)) {
                MallOrderInfoVo.Store store = new MallOrderInfoVo.Store();
                BeanUtils.copyProperties(storeInfoVo, store);
                vo.setStore(store);
                if (StringUtils.isEmpty(latitude) || StringUtils.isEmpty(longitude)) {
                    vo.getStore().setDistance(0D);
                } else {
                    Double distance = DistanceCalculateUtils.calculateDistance(
                            Double.parseDouble(latitude),
                            Double.parseDouble(longitude),
                            Double.parseDouble(storeInfoVo.getLatitude()),
                            Double.parseDouble(storeInfoVo.getLongitude())
                    );
                    vo.getStore().setDistance(BigDecimal.valueOf(distance).divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP).doubleValue());
                }
            }
        }

        //查询小程序是否开通发货信息管理服务
        IsTradeManagedVo isTradeManagedVo = coreDeliveryWeappService.getIsTradeManaged();
        if (ObjectUtil.isNotEmpty(isTradeManagedVo)) {
            vo.setIsTradeManaged(isTradeManagedVo.getIsTradeManaged());
            vo.setMchId(isTradeManagedVo.getMchId());
        }
        //查询订单优惠信息
        List<MallOrderDiscount> mallOrderDiscountList = mallOrderDiscountMapper.selectList(new QueryWrapper<MallOrderDiscount>()
                .eq("order_id", vo.getOrderId()));

        if (ObjectUtil.isNotEmpty(mallOrderDiscountList)) {
            List<MallOrderDiscount> shopDiscounts = new ArrayList<>();
            List<MallOrderDiscount> platformDiscounts = new ArrayList<>();
            BigDecimal pointDeductMoney = BigDecimal.ZERO;
            boolean hasCouponDiscount = false;
            for (MallOrderDiscount mallOrderDiscount : mallOrderDiscountList) {
                if ("coupon".equals(mallOrderDiscount.getDiscountType())) {
                    hasCouponDiscount = true;
                }
                if ("coupon".equals(mallOrderDiscount.getDiscountType()) || "full_reduction".equals(mallOrderDiscount.getDiscountType())) {
                    shopDiscounts.add(mallOrderDiscount);
                }
                if ("platform_coupon".equals(mallOrderDiscount.getDiscountType()) || "point_deduct".equals(mallOrderDiscount.getDiscountType())) {
                    platformDiscounts.add(mallOrderDiscount);
                    if (mallOrderDiscount.getDiscountType().equals(CommonActiveEnum.POINT_DEDUCT.getType())) {
                        pointDeductMoney = pointDeductMoney.add(mallOrderDiscount.getMoney());
                    }
                }
            }
            vo.setDiscountRecords(shopDiscounts);
            vo.setPlatformDiscountRecords(platformDiscounts);
            vo.setOrderDiscount(mallOrderDiscountList);
            vo.setPointDeductMoney(pointDeductMoney);
            // 对齐 PHP OrderService::getDetail：存在 coupon 类型优惠记录时 coupon_money = discount_money - full_reduction_money
            if (hasCouponDiscount) {
                BigDecimal dm = vo.getDiscountMoney() != null ? vo.getDiscountMoney() : BigDecimal.ZERO;
                BigDecimal fr = vo.getFullReductionMoney() != null ? vo.getFullReductionMoney() : BigDecimal.ZERO;
                vo.setCouponMoney(dm.subtract(fr).setScale(2, RoundingMode.HALF_UP));
            }
        }
        SiteInfoVo siteInfoVo = siteService.info(vo.getSiteId());
        MallOrderInfoVo.Site site = new MallOrderInfoVo.Site();
        site.setSiteID(siteInfoVo.getSiteId());
        site.setSiteName(siteInfoVo.getSiteName());
        site.setFrontEndLogo(siteInfoVo.getFrontEndLogo());
        vo.setSite(site);

        vo.setCanInvoice(checkIfCanInvoice(vo.getInvoiceId(), vo.getOrderType(), vo.getStatus()));
        vo.setInvoiceMoney(calcInvoiceMoney(vo.getOrderMoney(), vo.getRefundMoney()));

        // 16. 虚拟商品发货内容
        MallOrderDeliveryContent deliveryContent = orderDeliveryContentMapper.selectOne(
                new LambdaQueryWrapper<MallOrderDeliveryContent>()
                        .eq(MallOrderDeliveryContent::getTradeId, vo.getOrderId())
                        .select(MallOrderDeliveryContent::getContent)
        );
        if (deliveryContent != null)
            vo.setOrderDeliveryContent(deliveryContent.getContent());

        vo.setOrderStatusData(new LinkedHashMap<>(vo.getStatusName()));

        return vo;
    }

    /**
     * 获取订单数量
     *
     * @return
     */
    @Override
    public OrderNumVo getNum() {
        Integer memberId = RequestUtils.memberId();

        OrderNumVo vo = new OrderNumVo();

        vo.setWaitPay(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                .eq("member_id", memberId)
                .eq("status", OrderStatusEnum.WAIT_PAY.getStatus())
        ));

        vo.setWaitShipping(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                .eq("member_id", memberId)
                .eq("status", OrderStatusEnum.WAIT_DELIVERY.getStatus())
        ));

        vo.setWaitTake(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                .eq("member_id", memberId)
                .eq("status", OrderStatusEnum.WAIT_TAKE.getStatus())
        ));

        vo.setEvaluate(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                .eq("member_id", memberId)
                .eq("status", OrderStatusEnum.FINISH.getStatus())
                .eq("is_evaluate", 0)
        ));
        Integer[] refundStatus = new Integer[]{
                OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus(),
                OrderRefundStatusEnum.STORE_AGREE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus(),
                OrderRefundStatusEnum.STORE_REFUSE_REFUND_GOODS_APPLY_WAIT_BUYER.getStatus(),
                OrderRefundStatusEnum.BUYER_REFUND_GOODS_WAIT_STORE.getStatus(),
                OrderRefundStatusEnum.STORE_REFUSE_TAKE_REFUND_GOODS_WAIT_BUYER.getStatus(),
                OrderRefundStatusEnum.STORE_AGREE_REFUND_WAIT_TRANSFER.getStatus(),
                OrderRefundStatusEnum.STORE_REFUND_TRANSFERING.getStatus()
        };
        vo.setRefund(mallOrderRefundMapper.selectCount(new QueryWrapper<MallOrderRefund>()
                .eq("member_id", memberId)
                .in("status", refundStatus)
        ));

        return vo;
    }

    /**
     * 关闭订单
     *
     * @param id
     */
    public Object close(Integer id) {

        MallOrderGroupCloseVo vo = coreOrderGroupService.checkCloseByOrderId(id);
        if (!vo.getIsAllowClose()) {
            return vo;
        }
        OrderCloseParam closeParam = new OrderCloseParam();
        closeParam.setOrderId(id);
        closeParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        closeParam.setMainId(RequestUtils.memberId());
        closeParam.setCloseType(OrderCloseTypeEnum.BUYER_CLOSE.getType());
        coreOrderCloseService.close(closeParam);
        return true;
    }

    /**
     * 订单完成
     *
     * @param id
     */
    @Override
    public void orderFinish(Integer id) {
        MallOrder order = mallOrderMapper.selectOne(
                new QueryWrapper<MallOrder>()
                        .eq("order_id", id)
                        .eq("member_id", RequestUtils.memberId())
                        .select("status", "order_from"));

        Assert.notNull(order, "订单不存在");
        if (StringUtils.isNotEmpty(order.getOutTradeNo())) {
            Pay pay = corePayService.findPayInfoByOutTradeNo(order.getOutTradeNo());
            if (ObjectUtil.isEmpty(pay)) throw new CommonException("支付信息不存在");
            if (pay.getChannel().equals(ChannelEnum.WEAPP.getCode()) && pay.getType().equals(TransferTypeEnum.WECHATPAY.getKey())) {
                throw new CommonException("微信小程序订单需在小程序端收货后才能操作完成");
            }
        }
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_TAKE.getStatus().toString()))
            throw new CommonException("只有待收货的订单才可以收货");
        OrderFinishParam finishParam = new OrderFinishParam();
        finishParam.setOrderId(id);
        finishParam.setMainType(OrderLogMainTypeEnum.MEMBER.getType());
        finishParam.setMainId(RequestUtils.memberId());
        coreOrderFinishService.finish(finishParam);
    }

    /**
     * 物流信息
     *
     * @param searchParam
     */
    @Override
    public DeliveryPackageVo getPackage(DeliveryPackageSearchParam searchParam) {
        MallOrderDelivery delivery = mallOrderDeliveryMapper.selectOne(new QueryWrapper<MallOrderDelivery>()
                .eq("id", searchParam.getId())
        );
        if (delivery == null) return null;

        DeliveryPackageVo vo = new DeliveryPackageVo();
        BeanUtil.copyProperties(delivery, vo);

        DeliveryPackageVo.Company companyVo = new DeliveryPackageVo.Company();
        DeliveryCompany company = deliveryCompanyMapper.selectById(delivery.getExpressCompanyId());
        BeanUtil.copyProperties(company, companyVo);
        vo.setCompany(companyVo);

        List<DeliveryPackageVo.OrderGoods> orderGoods = CollectUtils.convert(mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("delivery_id", delivery.getId())), DeliveryPackageVo.OrderGoods.class);
        vo.setOrderGoods(orderGoods);

        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType()) && !vo.getSubDeliveryType().equals(OrderDeliveryTypeEnum.NONE_EXPRESS.getType())) {
            DeliverySearchParam deliverySearchParam = new DeliverySearchParam();
            deliverySearchParam.setExpressNo(companyVo.getExpressNo());
            deliverySearchParam.setLogisticNo(delivery.getExpressNumber());
            deliverySearchParam.setMobile(searchParam.getMobile());
            vo.setTraces(coreOrderService.deliverySearch(deliverySearchParam));
        }

        return vo;
    }

    private void repairVirtualVerifyExpireTime(MallOrderInfoVo order, MallOrderInfoVo.OrderGoods goods, Map<String, Object> goodsConfig) {
        if (ObjectUtil.defaultIfNull(goods.getVerifyExpireTime(), 0L) > 0) {
            return;
        }
        int virtualVerifyType = parseIntValue(goodsConfig.get("virtual_verify_type"), 0);
        if (virtualVerifyType == 0) {
            return;
        }

        long virtualIndate = parseLongValue(goodsConfig.get("virtual_indate"), 0L);
        if (virtualIndate <= 0) {
            return;
        }

        long expireTime = virtualVerifyType == 1
                ? resolveVirtualVerifyBaseTime(order) + virtualIndate * 86400L
                : virtualIndate;
        if (expireTime <= 0) {
            return;
        }

        MallOrderGoods update = new MallOrderGoods();
        update.setOrderGoodsId(goods.getOrderGoodsId());
        update.setVerifyExpireTime(expireTime);
        if (ObjectUtil.defaultIfNull(goods.getIsVerify(), 0) == 0) {
            update.setIsVerify(1);
            goods.setIsVerify(1);
        }
        mallOrderGoodsMapper.updateById(update);
        goods.setVerifyExpireTime(expireTime);
    }

    private static long resolveVirtualVerifyBaseTime(MallOrderInfoVo order) {
        if (ObjectUtil.defaultIfNull(order.getPayTime(), 0L) > 0) {
            return order.getPayTime();
        }
        if (ObjectUtil.defaultIfNull(order.getDeliveryTime(), 0L) > 0) {
            return order.getDeliveryTime();
        }
        if (ObjectUtil.defaultIfNull(order.getCreateTime(), 0L) > 0) {
            return order.getCreateTime();
        }
        return System.currentTimeMillis() / 1000;
    }

    private static int parseIntValue(Object value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static long parseLongValue(Object value, long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static List<Map<String, Object>> buildGiftOrderGoodsList(List<MallOrderGiftGoods> giftGoodsList) {
        List<Map<String, Object>> giftOrderGoods = new ArrayList<>();
        if (ObjectUtil.isEmpty(giftGoodsList)) {
            return giftOrderGoods;
        }
        for (MallOrderGiftGoods gift : giftGoodsList) {
            Map<String, Object> giftMap = new HashMap<>();
            giftMap.put("goods", Map.of("goods_name", gift.getGoodsName() != null ? gift.getGoodsName() : ""));
            giftMap.put("goods_id", gift.getGoodsId());
            giftMap.put("sku_id", gift.getSkuId());
            giftMap.put("sku_name", StringUtils.isNotEmpty(gift.getSkuName()) ? gift.getSkuName() : "");
            giftMap.put("sku_image", gift.getSkuImage());
            giftMap.put("num", gift.getNum());
            giftOrderGoods.add(giftMap);
        }
        return giftOrderGoods;
    }

    private static String firstNonEmpty(String... values) {
        if (values == null) {
            return "";
        }
        for (String v : values) {
            if (StringUtils.isNotEmpty(v)) {
                return v;
            }
        }
        return "";
    }

    public Boolean checkIfCanInvoice(Integer invoiceId, String orderType, String status) {
        Boolean canInvoice;
        if (ObjectUtil.isNotEmpty(invoiceId) && invoiceId > 0) {
            canInvoice = false;
        } else {
            canInvoice = true;
            if (orderType.equals("mall")) {
                if (OrderStatusEnum.WAIT_PAY.getStatus().toString().equals(status) || OrderStatusEnum.CLOSE.getStatus().toString().equals(status)) {
                    canInvoice = false;
                }
            } else {
                //校验发票生效状态
                CheckInvoiceStatusEventDefiner.CheckInvoiceStatusEvent event = new CheckInvoiceStatusEventDefiner.CheckInvoiceStatusEvent();
                event.setOrderType(orderType);
                event.setStatus(status);
                List<CheckInvoiceStatusEventDefiner.CheckInvoiceStatusResult> resultList = EventAndSubscribeOfPublisher.publishAndCallback(event);
                Boolean er = false;
                if (!resultList.isEmpty()) {
                    for (CheckInvoiceStatusEventDefiner.CheckInvoiceStatusResult item : resultList) {
                        if (item == null) continue;
                        if (ObjectUtil.isNotEmpty(item.getData())) {
                            er = item.getData();
                        }
                    }
                }
                if (!er) {
                    canInvoice = false;
                }
            }
        }
        return canInvoice;
    }

    /**
     * 可开票金额 = order_money - refund_money，对齐 PHP
     */
    private BigDecimal calcInvoiceMoney(BigDecimal orderMoney, BigDecimal refundMoney) {
        BigDecimal order = orderMoney != null ? orderMoney : BigDecimal.ZERO;
        BigDecimal refund = refundMoney != null ? refundMoney : BigDecimal.ZERO;
        return order.subtract(refund).setScale(2, RoundingMode.HALF_UP);
    }
}
