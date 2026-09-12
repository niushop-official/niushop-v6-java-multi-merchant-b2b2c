package com.niu.mall.service.site.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.DistanceCalculateUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.diy_form.DiyFormRecordsFields;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.shop.SiteShopCashOut;
import com.niu.core.entity.verify.Verify;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.diy_form.DiyFormRecordsFieldsMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.shop.SiteShopCashOutMapper;
import com.niu.core.mapper.verify.VerifyMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysAreaService;
import com.niu.core.service.admin.sys.vo.SysAreaResolveVo;
import com.niu.core.service.core.delivery.vo.DeliveryConfigVo;
import com.niu.core.service.core.delivery.vo.LocalInfoVo;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.site.delivery.IDeliveryService;
import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.entity.order.*;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.*;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.core.mapper.goods.MallGoodsSkuMapper;
import com.niu.mall.mapper.order.*;
import com.niu.mall.service.admin.order.vo.MallOrderInfoVo;
import com.niu.mall.service.admin.order.vo.MallOrderLogListVo;
import com.niu.mall.service.api.order.vo.OrderNumVo;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GoodsDelStatParam;
import com.niu.mall.service.core.order.ICoreOrderCloseService;
import com.niu.mall.service.core.order.ICoreOrderDiscountService;
import com.niu.mall.service.core.order.ICoreOrderLogService;
import com.niu.mall.service.core.order.ICoreOrderPayService;
import com.niu.mall.service.core.order.param.AddOrderLogParam;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import com.niu.mall.service.core.order.param.OrderPayParam;
import com.niu.mall.service.core.shop.ICoreStatService;
import com.niu.mall.service.core.shop.param.DelStatParam;
import com.niu.mall.service.site.order.IMallOrderService;
import com.niu.mall.service.site.order.param.*;
import com.niu.mall.service.site.order.vo.EditDeliveryVo;
import com.niu.mall.service.site.order.vo.MallOrderListVo;
import com.niu.mall.service.site.order.vo.MallOrderMemberConsumeListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 订单实现
 */
@Service
public class MallOrderServiceImpl implements IMallOrderService {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    PayMapper payMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    MallOrderDeliveryMapper mallOrderDeliveryMapper;

    @Resource
    MallOrderLogMapper mallOrderLogMapper;

    @Resource
    ICoreOrderPayService coreOrderPayService;

    @Resource
    ICoreOrderCloseService coreOrderCloseService;

    @Resource
    ICoreOrderLogService coreOrderLogService;

    @Resource
    ICorePayService corePayService;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    ISiteService siteService;

    @Resource
    DiyFormRecordsFieldsMapper diyFormRecordsFieldsMapper;
    @Resource
    MallGoodsSkuMapper mallGoodsSkuMapper;

    @Resource
    MallOrderRefundMapper mallOrderRefundMapper;
    @Resource
    SiteShopCashOutMapper siteShopCashOutMapper;

    @Resource
    IDeliveryService deliveryService;

    @Resource
    DeliveryLocalDeliveryMapper deliveryLocalDeliveryMapper;
    @Resource
    ICoreStatService coreStatService;
    @Resource
    private ICoreGoodsStatService coreGoodsStatService;
    @Resource
    private DeliveryStoreMapper deliveryStoreMapper;
    @Resource
    private VerifyMapper verifyMapper;
    @Resource
    private DeliveryLocalDeliveryOrderMapper deliveryLocalDeliveryOrderMapper;
    @Resource
    private MallOrderGiftGoodsMapper mallOrderGiftGoodsMapper;
    @Resource
    private MallOrderDeliveryContentMapper orderDeliveryContentMapper;
    @Resource
    private ICoreOrderDiscountService coreOrderDiscountService;

    @Resource
    private ISysAreaService sysAreaService;

    /**
     * 订单列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderListVo>
     */
    @Override
    public PageResult<MallOrderListVo> page(PageParam pageParam, MallOrderSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallOrder> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("o")
                .leftJoin("?_member m ON o.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_mall_order_goods g ON o.order_id = g.order_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_pay p ON o.out_trade_no = p.out_trade_no".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("o.order_id,o.site_id,o.order_no,o.order_type,o.order_from,o.out_trade_no,o.status,o.member_id,o.ip,o.goods_money,o.delivery_money,o.order_money,o.create_time,o.pay_time,o.delivery_type,o.taker_name,o.taker_mobile,o.taker_full_address,o.take_store_id,o.is_enable_refund,o.member_remark,o.shop_remark,o.close_remark,o.mall_discount_money,o.mall_money,o.mall_refund_money,o.refund_money,o.shop_money,o.shop_refund_money,o.supply_id,o.supply_money,o.supply_refund_money,o.mall_coupon_money,o.mall_refund_coupon_money");
        queryWrapper.eq("o.site_id", RequestUtils.siteId());
        queryWrapper.groupBy("o.order_id");
        queryWrapper.orderByDesc("o.create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getSearchType()) && ObjectUtil.isNotEmpty(searchParam.getSearchName())) {
            if (searchParam.getSearchType().equals("goods_name")) {
                queryWrapper.like("g." + searchParam.getSearchType(), searchParam.getSearchName());
            } else {
                queryWrapper.like("o." + searchParam.getSearchType(), searchParam.getSearchName());
            }

        }
        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("o.status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderFrom()) && !"all".equals(searchParam.getOrderFrom()))
            queryWrapper.eq("o.order_from", searchParam.getOrderFrom());
        if (ObjectUtil.isNotEmpty(searchParam.getPayType()) && !"all".equals(searchParam.getPayType())) {
            queryWrapper.eq("p.type", searchParam.getPayType());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime()))
            QueryMapperUtils.buildByTime(queryWrapper, "o.create_time", searchParam.getCreateTime());
        if (ObjectUtil.isNotEmpty(searchParam.getPayTime()))
            QueryMapperUtils.buildByTime(queryWrapper, "p.pay_time", searchParam.getPayTime());
        if (ObjectUtil.isNotEmpty(searchParam.getExtendField1()))
            queryWrapper.eq("o.extend_field_1", searchParam.getExtendField1());
        if (ObjectUtil.isNotEmpty(searchParam.getExtendField2()))
            queryWrapper.eq("o.extend_field_2", searchParam.getExtendField2());
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.like("m.member_no", searchParam.getKeyword()).or()
                    .like("m.nickname", searchParam.getKeyword()).or()
                    .like("m.username", searchParam.getKeyword()).or()
                    .like("m.mobile", searchParam.getKeyword()));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getOrderKeyword())) {
            queryWrapper.and(i -> i.like("o.order_no", searchParam.getOrderKeyword()).or()
                    .like("o.body", searchParam.getOrderKeyword()));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getOrderNo())) {
            queryWrapper.like("o.order_no", searchParam.getOrderNo());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getActivityType()))
            queryWrapper.eq("o.activity_type", searchParam.getActivityType());
        if (ObjectUtil.isNotEmpty(searchParam.getDeliveryType()) && !"all".equals(searchParam.getDeliveryType()))
            queryWrapper.eq("o.delivery_type", searchParam.getDeliveryType());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsSource()) && !"all".equals(searchParam.getGoodsSource())) {
            if (searchParam.getGoodsSource().equals("self_goods") || searchParam.getGoodsSource().equals("0")) {
                queryWrapper.eq("o.supply_order_id", 0);
            } else {
                queryWrapper.ne("o.supply_order_id", 0);
            }
        }

        IPage<MallOrderListVo> iPage = mallOrderMapper.selectJoinPage(new Page<>(page, limit), MallOrderListVo.class, queryWrapper);
        for (MallOrderListVo vo : iPage.getRecords()) {
            MPJQueryWrapper<MallOrderGoods> orderGoodsWra = new MPJQueryWrapper<>();
            orderGoodsWra.setAlias("o")
                    .leftJoin("?_mall_order_refund r ON o.order_goods_id = r.order_goods_id ".replace("?_", GlobalConfig.tablePrefix))
                    .leftJoin("?_mall_order_delivery od ON o.delivery_id = od.id ".replace("?_", GlobalConfig.tablePrefix))
                    .leftJoin("?_delivery_company dc ON dc.company_id = od.express_company_id ".replace("?_", GlobalConfig.tablePrefix))
                    .leftJoin("?_delivery_local_delivery_order dldo ON od.local_delivery_order_id = dldo.delivery_no ".replace("?_", GlobalConfig.tablePrefix))
            ;
            orderGoodsWra.select("o.*");
            orderGoodsWra.select("r.refund_id,r.apply_money,r.money refund_money");
            orderGoodsWra.select("od.delivery_type,od.express_company_id,od.express_number,od.local_deliver_id,od.local_delivery_order_id,dc.company_name as express_company_name,dldo.status localDeliveryStatus,od.status orderDeliveryStatus");
            orderGoodsWra.eq("o.order_id", vo.getOrderId());
            if (StringUtils.isNotEmpty(searchParam.getSearchType()) && searchParam.getSearchType().equals("goods_name")) {
                orderGoodsWra.like("o.goods_name", searchParam.getSearchName());
            }

            List<MallOrderListVo.OrderGoods> orderGoods = mallOrderGoodsMapper.selectJoinList(MallOrderListVo.OrderGoods.class, orderGoodsWra);

            for (MallOrderListVo.OrderGoods orderGoodsItem : orderGoods) {
                MallOrderListVo.OrderDeliveryData orderDeliveryData = new MallOrderListVo.OrderDeliveryData();
                BeanUtil.copyProperties(orderGoodsItem, orderDeliveryData);
                orderGoodsItem.setOrderDelivery(orderDeliveryData);
                vo.setLocalDeliveryStatus(orderGoodsItem.getLocalDeliveryStatus());
            }


            //按照商品进行分组
            List<MallOrderListVo.OrderGoods> orderGoodsListAll = new ArrayList<>();
            Map<Integer, List<MallOrderListVo.OrderGoods>> orderGoodsMap = orderGoods.stream().collect(Collectors.groupingBy(MallOrderListVo.OrderGoods::getOrderGoodsId));
            for (Integer key : orderGoodsMap.keySet()) {
                orderGoodsListAll.add(orderGoodsMap.get(key).get(0));
            }

            vo.setOrderGoods(orderGoodsListAll);

            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", vo.getMemberId()));
            BeanUtil.copyProperties(member, memberVo);
            vo.setMember(memberVo);
            vo.setSupplyMoney((ObjectUtil.isNotEmpty(vo.getSupplyMoney()) ? vo.getSupplyMoney() : BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
            vo.setSupplyRefundMoney((ObjectUtil.isNotEmpty(vo.getSupplyRefundMoney()) ? vo.getSupplyRefundMoney() : BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP));
            if (!vo.getOutTradeNo().isEmpty()) {
                Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", vo.getOutTradeNo()));
                if (pay != null) {
                    MallOrderListVo.Pay payVo = new MallOrderListVo.Pay();
                    BeanUtil.copyProperties(pay, payVo);
                    vo.setPay(payVo);
                }
            }
            //获取店铺信息
            if (ObjectUtil.isNotEmpty(vo.getSupplyId()) && vo.getSupplyId() > 0) {
                SiteInfoVo siteInfoVo = siteService.info(vo.getSupplyId());
                MallOrderListVo.Supply supply = new MallOrderListVo.Supply();
                supply.setSiteId(siteInfoVo.getSiteId());
                supply.setSiteName(siteInfoVo.getSiteName());
                vo.setSupply(supply);
                vo.setSiteName(siteInfoVo.getSiteName());
            } else {
                vo.setSupplyMoney(BigDecimal.ZERO);
                vo.setSupplyRefundMoney(BigDecimal.ZERO);
            }
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    /**
     * 订单详情
     *
     * @param id 主键
     * @return ShopOrderInfoVo
     */
    @Override
    public MallOrderInfoVo detail(Integer id) {
        // ns_mall_order 无 relate_id  ,activity_type,  form_record_id 字段
        MallOrder model = mallOrderMapper.selectOne(
                new QueryWrapper<MallOrder>()
                        .eq("order_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .select("order_id, site_id, order_no, order_type, order_from, out_trade_no, status, member_id, ip, " +
                                "goods_money, delivery_money, order_money, invoice_id, create_time, pay_time, delivery_time, " +
                                "take_time, finish_time, close_time, delivery_type, sub_delivery_type, taker_name, taker_mobile, " +
                                "taker_province, taker_city, taker_district, taker_address, taker_full_address, " +
                                "taker_longitude, taker_latitude, take_store_id, is_enable_refund, member_remark, shop_remark, " +
                                "close_remark, discount_money,is_evaluate, mall_discount_money, refund_money, shop_money, mall_money, " +
                                "shop_refund_money, mall_refund_money, mall_coupon_money, mall_refund_coupon_money, " +
                                "shop_commission, shop_refund_commission, supply_id, supply_money, supply_refund_money, " +
                                "form_record_id, buyer_ask_delivery_time, refund_deduct_point, refund_point_deduct_money, " +
                                "point_deduct_money, use_point, settlement_status")
        );
        Assert.notNull(model, "订单不存在");

        MallOrderInfoVo vo = new MallOrderInfoVo();
        BeanUtils.copyProperties(model, vo);
        vo.setUsePoint(model.getUsePoint() != null ? new BigDecimal(String.valueOf(model.getUsePoint())) : BigDecimal.ZERO);
        vo.setRefundDeductPoint(model.getRefundDeductPoint() != null ? new BigDecimal(model.getRefundDeductPoint()) : BigDecimal.ZERO);

        // 2. 查询会员信息
        MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", vo.getMemberId()));
        BeanUtil.copyProperties(member, memberVo);
        vo.setMember(memberVo);

        // 查询订单项
        MPJQueryWrapper<MallOrderGoods> orderGoodsWra = new MPJQueryWrapper<>();
        orderGoodsWra.setAlias("o")
                .leftJoin("?_mall_order_refund r ON o.order_goods_id = r.order_goods_id ".replace("?_", GlobalConfig.tablePrefix));
        orderGoodsWra.select("o.*");
        orderGoodsWra.select("r.refund_id,r.apply_money,r.money refund_money");
        orderGoodsWra.eq("o.order_id", vo.getOrderId());
        List<MallOrderInfoVo.OrderGoods> orderGoods = mallOrderGoodsMapper.selectJoinList(MallOrderInfoVo.OrderGoods.class, orderGoodsWra);

        //按照商品进行分组
        List<MallOrderInfoVo.OrderGoods> orderGoodsListAll = new ArrayList<>();
        Map<Integer, List<MallOrderInfoVo.OrderGoods>> orderGoodsMap = orderGoods.stream().collect(Collectors.groupingBy(MallOrderInfoVo.OrderGoods::getOrderGoodsId));
        for (Integer key : orderGoodsMap.keySet()) {
            orderGoodsListAll.add(orderGoodsMap.get(key).get(0));
        }

        Boolean isRefundStatusShow = false;
        List<String> goodsTypes = new ArrayList<>();
        for (MallOrderInfoVo.OrderGoods orderGoodsItem : orderGoodsListAll) {
            MallGoods goodsInfo = mallGoodsMapper.selectById(orderGoodsItem.getGoodsId());
            goodsTypes.add(orderGoodsItem.getGoodsType());
            orderGoodsItem.setUnit(goodsInfo.getUnit().equals("") ? "件" : goodsInfo.getUnit());
            orderGoodsItem.setDeliveryTypeName(vo.getDeliveryTypeName());
            if (!orderGoodsItem.getStatus().equals(OrderGoodsRefundEnum.NORMAL.getStatus())) {
                isRefundStatusShow = true;
            }
        }
        vo.setIsRefundStatusShow(isRefundStatusShow);
        vo.setHasGoodsTypes(JSONUtil.toJsonStr(goodsTypes));
        vo.setOrderGoods(orderGoodsListAll);

        // 4. 虚拟商品核销手机号逻辑
        int virtualVerifyMobile = 1; // 默认1
        if (goodsTypes.contains(GoodsTypeEnum.VIRTUAL.getType()) && !orderGoodsListAll.isEmpty()) {
            virtualVerifyMobile = orderGoodsListAll.get(0).getVirtualVerifyMobile() != null ? orderGoodsListAll.get(0).getVirtualVerifyMobile() : 0;
        }
        vo.setVirtualVerifyMobile(virtualVerifyMobile);

        // 6. 查询赠品
        List<MallOrderGiftGoods> giftGoodsList = mallOrderGiftGoodsMapper.selectList(
                new LambdaQueryWrapper<MallOrderGiftGoods>().eq(MallOrderGiftGoods::getOrderId, id)
        );
        List<Map<String, Object>> giftOrderGoods = new ArrayList<>();
        for (MallOrderGiftGoods gift : giftGoodsList) {
            Map<String, Object> giftItem = new HashMap<>();
            Map<String, String> goods = new HashMap<>();
            goods.put("goods_name", gift.getGoodsName());
            giftItem.put("goods", goods);
            giftItem.put("goods_id", gift.getGoodsId());
            giftItem.put("sku_id", gift.getSkuId());
            giftItem.put("sku_name", StringUtils.isEmpty(gift.getSkuName()) ? gift.getGoodsName() : gift.getSkuName());
            giftItem.put("sku_image", gift.getSkuImage());
            giftItem.put("num", gift.getNum());
            giftOrderGoods.add(giftItem);
        }
        vo.setOrderGiftGoods(giftOrderGoods);


        //查询订单配送信息 mall_order_delivery
        List<MallOrderDelivery> orderDelivery = mallOrderDeliveryMapper.selectList(new QueryWrapper<MallOrderDelivery>().eq("order_id", vo.getOrderId()));

        if (!orderDelivery.isEmpty()) {
            List<MallOrderInfoVo.OrderDelivery> orderDeliveryVoList = new ArrayList<>();
            for (MallOrderDelivery orderDeliveryItem : orderDelivery) {
                //查询订单配送信息 mall_order_delivery
                MallOrderInfoVo.OrderDelivery orderDeliveryVo = new MallOrderInfoVo.OrderDelivery();
                BeanUtils.copyProperties(orderDeliveryItem, orderDeliveryVo);
                if (ObjectUtil.isNotEmpty(orderDeliveryItem.getLocalDeliveryOrderId())) {
                    DeliveryLocalDeliveryOrder localOrderDelivery = deliveryLocalDeliveryOrderMapper.selectOne(new QueryWrapper<DeliveryLocalDeliveryOrder>()
                            .eq("delivery_no", orderDeliveryItem.getLocalDeliveryOrderId()).last("LIMIT 1"));
                    orderDeliveryVo.setLocalDeliveryStatus(localOrderDelivery.getStatus());
                    orderDeliveryVo.setLocalDeliveryStatusName(LocalDeliveryStatusEnum.getNameByStatus(Integer.parseInt(localOrderDelivery.getStatus())));
                    orderDeliveryVo.setRiderMobile(localOrderDelivery.getRiderMobile());
                    orderDeliveryVo.setRiderName(localOrderDelivery.getRiderName());
                    orderDeliveryVo.setDeliveryService(localOrderDelivery.getDeliveryService());
                    orderDeliveryVo.setDeliveryServiceName(DeliveryStoreTypeEnum.getNameByKey(localOrderDelivery.getDeliveryService()));
                }
                orderDeliveryVoList.add(orderDeliveryVo);
            }
            vo.setOrderDelivery(orderDeliveryVoList);
        }

        // 查询订单支付信息
        if (!vo.getOutTradeNo().isEmpty()) {
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", vo.getOutTradeNo()));
            if (pay != null) {
                MallOrderInfoVo.Pay payVo = new MallOrderInfoVo.Pay();
                BeanUtil.copyProperties(pay, payVo);
                vo.setPay(payVo);
            }
        }

        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType())) {
            DeliveryStore store = deliveryStoreMapper.selectById(vo.getTakeStoreId());
            MallOrderInfoVo.Store storeVo = new MallOrderInfoVo.Store();
            BeanUtil.copyProperties(store, storeVo);
            vo.setStore(storeVo);
            //查询核销记录
            Verify verify = verifyMapper.selectOne(new QueryWrapper<Verify>()
                    .eq("site_id", vo.getSiteId()).eq("type", "shopPickUpOrder").eq("relate_tag", id));
            if (ObjectUtil.isNotEmpty(verify)) {
                //查询会员信息
                Member memberVerify = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", verify.getVerifierMemberId()));
                vo.setVerifyCode(verify.getCode());
                MallOrderInfoVo.VerifierMemberInfo memberVerifyObj = new MallOrderInfoVo.VerifierMemberInfo();
                if (ObjectUtil.isNotEmpty(memberVerify)) {
                    memberVerifyObj.setMemberId(memberVerify.getMemberId());
                    memberVerifyObj.setNickname(memberVerify.getNickname());
                }
                vo.setVerifierMember(memberVerifyObj);
            }
        }

        // 查询订单包裹信息
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
            DeliveryStore store = deliveryStoreMapper.selectById(vo.getTakeStoreId());
            MallOrderInfoVo.Store storeVo = new MallOrderInfoVo.Store();
            BeanUtil.copyProperties(store, storeVo);
            vo.setStore(storeVo);
        }

        // 查询订单包裹信息
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
            List<MallOrderInfoVo.OrderDelivery> orderDelivery1 = CollectUtils.convert(mallOrderDeliveryMapper.selectList(new QueryWrapper<MallOrderDelivery>().eq("order_id", vo.getOrderId())), MallOrderInfoVo.OrderDelivery.class);
            vo.setOrderDelivery(orderDelivery1);
        }

        //查询店铺信息
        SiteInfoVo siteInfoVo = siteService.info(vo.getSiteId());
        vo.setSiteName(siteInfoVo.getSiteName());
        if (ObjectUtil.isEmpty(vo.getShopCommission())) {
            vo.setShopCommission(BigDecimal.ZERO);
        }
        if (ObjectUtil.isEmpty(vo.getShopRefundCommission())) {
            vo.setShopRefundCommission(BigDecimal.ZERO);
        }
        //获取店铺信息
        if (ObjectUtil.isNotEmpty(vo.getSupplyId()) && vo.getSupplyId() > 0) {
            SiteInfoVo supplyInfo = siteService.info(vo.getSupplyId());
            MallOrderInfoVo.Supply supply = new MallOrderInfoVo.Supply();
            supply.setSiteId(supplyInfo.getSiteId());
            supply.setSiteName(supplyInfo.getSiteName());
            vo.setSupply(supply);
            if (ObjectUtil.isEmpty(vo.getSupplyRefundMoney())) {
                vo.setSupplyRefundMoney(BigDecimal.ZERO);
            }
        }

        List<MallOrderLogListVo> orderLog = CollectUtils.convert(mallOrderLogMapper.selectList(new QueryWrapper<MallOrderLog>().eq("order_id", vo.getOrderId()).orderByDesc("create_time")), MallOrderLogListVo.class);
        vo.setOrderLog(orderLog);

        if (vo.getFormRecordId() != null && vo.getFormRecordId() > 0) {
            Long fieldCount = diyFormRecordsFieldsMapper.selectCount(
                    new QueryWrapper<DiyFormRecordsFields>()
                            .eq("record_id", vo.getFormRecordId())
            );
            vo.setFormRecordShow(fieldCount > 0 ? 1 : 0);
        } else {
            vo.setFormRecordShow(0);
        }

        if (ObjectUtil.isNotEmpty(orderGoods)) {
            for (MallOrderInfoVo.OrderGoods item : orderGoods) {
                if (item.getFormRecordId() != null && item.getFormRecordId() > 0) {
                    Long fieldCount = diyFormRecordsFieldsMapper.selectCount(
                            new QueryWrapper<DiyFormRecordsFields>()
                                    .eq("record_id", item.getFormRecordId())
                    );
                    item.setFormRecordShow(fieldCount > 0 ? 1 : 0);
                } else {
                    item.setFormRecordShow(0);
                }
            }
        }

        boolean isInstallFenxiao = false;
        if (siteInfoVo != null && ObjectUtil.isNotEmpty(siteInfoVo.getAddons())) {
            isInstallFenxiao = siteInfoVo.getAddons().contains("mall_fenxiao");
        }
        vo.setIsInstallFenxiao(isInstallFenxiao);

        // 15. 优惠明细（店铺级 + 平台级）
        List<MallOrderDiscount> discountRecords = coreOrderDiscountService.getDiscountRecords(id, null, OrderDiscountTypeEnum.getShopDiscountType(), null, 1);
        vo.setDiscountRecords(discountRecords);
        List<MallOrderDiscount> platformDiscountRecords = coreOrderDiscountService.getDiscountRecords(id, null, OrderDiscountTypeEnum.getPlatFormDiscountType(), null, 1);
        vo.setPlatformDiscountRecords(platformDiscountRecords);
        backfillPointDeductFromRecords(vo, id, platformDiscountRecords);
        // 优惠详情
        vo.setDiscountDetails(coreOrderDiscountService.getOrderDiscountDetails(
                id, vo.getDiscountMoney(), vo.getMallCouponMoney(),
                vo.getPointDeductMoney(), vo.getUsePoint() != null ? vo.getUsePoint().intValue() : 0));

        // 16. 虚拟商品发货内容
        MallOrderDeliveryContent content = orderDeliveryContentMapper.selectOne(
                new LambdaQueryWrapper<MallOrderDeliveryContent>().eq(MallOrderDeliveryContent::getTradeId, id)
        );
        vo.setOrderDeliveryContent(content != null ? content.getContent() : "");

        return vo;
    }

    /**
     * 关闭订单
     *
     * @param id
     */
    @Override
    public void close(Integer id) {
        OrderCloseParam closeParam = new OrderCloseParam();
        closeParam.setOrderId(id);
        closeParam.setSiteId(RequestUtils.siteId());
        closeParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        closeParam.setMainId(RequestUtils.uid());
        closeParam.setCloseType(OrderCloseTypeEnum.SHOP_CLOSE.getType());
        coreOrderCloseService.close(closeParam);
    }

    /**
     * 设置商家备注
     *
     * @param param
     */
    @Override
    public void setShopRemark(SetShopRemarkParam param) {
        MallOrder model = mallOrderMapper.selectOne(
                new QueryWrapper<MallOrder>()
                        .select("order_id,status")
                        .eq("order_id", param.getOrderId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "订单不存在");

        model.setShopRemark(param.getShopRemark());
        mallOrderMapper.updateById(model);

        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(model.getOrderId());
        logParam.setStatus(Integer.valueOf(model.getStatus()));
        logParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        logParam.setMainId(RequestUtils.uid());
        logParam.setType(OrderLogActionEnum.ORDER_REMARK_ACTION.getAction());
        coreOrderLogService.add(logParam);
    }

    /**
     * 订单调价
     *
     * @param param
     */
    @Override
    @Transactional
    public void editPrice(EditPriceParam param) {
        MallOrder order = mallOrderMapper.selectOne(
                new QueryWrapper<MallOrder>()
                        .select("order_id,site_id,status,order_type,goods_money,delivery_money,order_money,discount_money")
                        .eq("order_id", param.getOrderId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(order, "订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString()))
            throw new CommonException("只有待支付的订单可以改价");

        // 关闭已经发起的支付单据
        corePayService.closeByTrade(order.getOrderType(), order.getOrderId());

        BigDecimal goodsMoney = new BigDecimal(0);
        List<MallOrderGoods> orderGoods = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("order_id", order.getOrderId()).select("order_goods_id,order_goods_money,goods_money,price,discount_money,num"));

        for (MallOrderGoods item : orderGoods) {
            if (ObjectUtil.isNotEmpty(param.getOrderGoodsData().get(item.getOrderGoodsId()))) {
                BigDecimal money = param.getOrderGoodsData().get(item.getOrderGoodsId()).getMoney();
                if (money.compareTo(BigDecimal.ZERO) != 0) {
                    MallOrderGoods orderGoodsUpdate = new MallOrderGoods();
                    orderGoodsUpdate.setOrderGoodsId(item.getOrderGoodsId());
                    orderGoodsUpdate.setOrderGoodsMoney(item.getOrderGoodsMoney().add(money));
                    if (orderGoodsUpdate.getOrderGoodsMoney().compareTo(BigDecimal.ZERO) == -1) {
                        throw new CommonException("订单项小计总额不能小于0");
                    }
                    orderGoodsUpdate.setGoodsMoney(orderGoodsUpdate.getOrderGoodsMoney().add(item.getDiscountMoney()));
                    orderGoodsUpdate.setPrice(orderGoodsUpdate.getGoodsMoney().divide(new BigDecimal(item.getNum()), 2, BigDecimal.ROUND_HALF_UP));
                    mallOrderGoodsMapper.updateById(orderGoodsUpdate);
                    goodsMoney = goodsMoney.add(orderGoodsUpdate.getGoodsMoney());
                    continue;
                }
            }
            goodsMoney = goodsMoney.add(item.getGoodsMoney());
        }

        // 修改订单表
        MallOrder orderUpdate = new MallOrder();
        orderUpdate.setOrderId(order.getOrderId());
        orderUpdate.setOrderMoney(goodsMoney.add(param.getDeliveryMoney()).subtract(order.getDiscountMoney()));
        if (orderUpdate.getOrderMoney().compareTo(BigDecimal.ZERO) == -1) orderUpdate.setOrderMoney(BigDecimal.ZERO);
        orderUpdate.setDeliveryMoney(param.getDeliveryMoney());
        orderUpdate.setGoodsMoney(goodsMoney);
        this.mallOrderMapper.updateById(orderUpdate);

        // 添加订单操作日志
        AddOrderLogParam logParam = new AddOrderLogParam();
        logParam.setOrderId(order.getOrderId());
        logParam.setStatus(Integer.valueOf(order.getStatus()));
        logParam.setMainType(OrderLogMainTypeEnum.STORE.getType());
        logParam.setMainId(RequestUtils.uid());
        logParam.setType(OrderLogActionEnum.ORDER_EDIT_PRICE_ACTION.getAction());
        coreOrderLogService.add(logParam);

        // 如果调价后的订单金额为0
        if (orderUpdate.getOrderMoney().compareTo(BigDecimal.ZERO) == 0) {
            OrderPayParam payParam = new OrderPayParam();
            payParam.setTradeId(order.getOrderId());
            payParam.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
            payParam.setMainId(0);
            coreOrderPayService.pay(payParam);
        }
    }

    /**
     * 修改订单配送
     *
     * @param param
     */
    @Override
    public void editDelivery(EditDeliveryParam param) {
        EditDeliveryVo editDeliveryData = getEditDeliveryData(param);
        if (editDeliveryData.getErrorCode() < 0) {
            throw new CommonException(editDeliveryData.getErrorMsg());
        }
        EditDeliveryParam deliveryData = editDeliveryData.getDeliveryData();


        MallOrder model = new MallOrder();
        BeanUtil.copyProperties(deliveryData, model);

        mallOrderMapper.updateById(model);
    }

    /**
     * 获取订单配送修改信息
     *
     * @param param
     * @return
     */
    @Override
    public EditDeliveryVo getEditDeliveryData(EditDeliveryParam param) {
        Integer siteId = RequestUtils.siteId();
        MallOrder order = mallOrderMapper.selectOne(
                new LambdaQueryWrapper<MallOrder>()
                        .eq(MallOrder::getOrderId, param.getOrderId())
                        .eq(MallOrder::getSiteId, siteId));

        Assert.notNull(order, "订单不存在");

        List<String> errorLtatusList = List.of(OrderStatusEnum.WAIT_PAY.getStatus().toString(),
                OrderStatusEnum.WAIT_DELIVERY.getStatus().toString());
        if (!errorLtatusList.contains(order.getStatus())) {
            throw new CommonException("只有待支付或待发货的订单可以修改配送地址");
        }
        if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType())) {
            throw new CommonException("虚拟商品订单不可以修改配送地址");
        }

        List<DeliveryConfigVo> deliveryList = deliveryService.getDeliveryList();
        String deliveryType = StringUtils.isNotBlank(param.getDeliveryType()) ? param.getDeliveryType() : order.getDeliveryType();
        if (StringUtils.isBlank(param.getDeliveryType())) {
            param.setDeliveryType(deliveryType);
        }
        DeliveryConfigVo selectedDelivery = deliveryList.stream().filter(item -> item.getKey().equals(deliveryType)).findFirst().orElse(null);

        EditDeliveryVo result = new EditDeliveryVo();
        result.setDeliveryData(param);
        BeanUtils.copyProperties(order, result);
        result.setArea(sysAreaService.buildAreaText(result.getTakerProvince(), result.getTakerCity(), result.getTakerDistrict()));
        result.setErrorCode(1);
        result.setErrorMsg("");

        if (selectedDelivery == null) {
            result.setErrorCode(-1);
            result.setErrorMsg("不支持选择的配送方式");
            return result;
        } else if (selectedDelivery.getStatus() > 1) {
            result.setErrorCode(-1);
            result.setErrorMsg("商家未开启该配送方式");
            return result;
        }

        List<MallOrderGoods> goodsList = mallOrderGoodsMapper.selectList(new LambdaQueryWrapper<MallOrderGoods>()
                .eq(MallOrderGoods::getSiteId, siteId)
                .eq(MallOrderGoods::getGoodsType, "real")
                .eq(MallOrderGoods::getOrderId, param.getOrderId()));
        Set<Integer> goodsIds = goodsList.stream().map(MallOrderGoods::getGoodsId).collect(Collectors.toSet());
        List<MallGoods> mallGoods = mallGoodsMapper.selectList(new LambdaQueryWrapper<MallGoods>().in(MallGoods::getGoodsId, goodsIds));

        List<MallOrderGoods> orderGoods = mallGoods.stream().map(goods -> {
            MallOrderGoods vo = new MallOrderGoods();
            BeanUtils.copyProperties(goods, vo);

            // 检查商品是否支持该配送方式
            if (!goods.getDeliveryType().contains(param.getDeliveryType())) {
                result.setErrorCode(-1);
                result.setErrorMsg("当前商品不支持该配送方式");
            }
            return vo;
        }).collect(Collectors.toList());
        result.setOrderGoods(orderGoods);

        String unsupportedGoodsNames = "";
        if (result.getErrorCode() < 0) {
            unsupportedGoodsNames = orderGoods.stream()
                    .map(MallOrderGoods::getGoodsName)
                    .collect(Collectors.joining(","));
        }
        if (!unsupportedGoodsNames.isEmpty()) {
            result.setErrorCode(-1);
            result.setErrorMsg(unsupportedGoodsNames + "当前商品不支持该配送方式");
            return result;
        }

        EditDeliveryParam areaData = new EditDeliveryParam();
        BeanUtils.copyProperties(result, areaData);
        if (StringUtils.isBlank(areaData.getArea())) {
            areaData.setArea(result.getArea());
        }
        fillMissingTakerAreaCodes(areaData);
        result.setTakerProvince(areaData.getTakerProvince());
        result.setTakerCity(areaData.getTakerCity());
        result.setTakerDistrict(areaData.getTakerDistrict());
        if (StringUtils.isNotBlank(areaData.getArea())) {
            result.setArea(areaData.getArea());
        }

        EditDeliveryParam validateData = isSubmitDeliveryAddress(param) ? param : areaData;

        switch (selectedDelivery.getKey()) {
            case "express":
                if (StringUtils.isAnyBlank(
                        validateData.getTakerName(),
                        validateData.getTakerMobile(),
                        validateData.getTakerAddress(),
                        validateData.getTakerFullAddress())
                        || validateData.getTakerProvince() == null || validateData.getTakerProvince() <= 0
                        || validateData.getTakerCity() == null || validateData.getTakerCity() <= 0
                        || validateData.getTakerDistrict() == null || validateData.getTakerDistrict() <= 0) {
                    result.setErrorCode(-1);
                    result.setErrorMsg("地址信息有误");
                }
                break;

            case "local_delivery":
                if (StringUtils.isAnyBlank(
                        validateData.getTakerName(),
                        validateData.getTakerMobile(),
                        validateData.getTakerAddress(),
                        validateData.getTakerFullAddress(),
                        validateData.getTakerLongitude(),
                        validateData.getTakerLatitude())
                        || validateData.getTakerProvince() == null || validateData.getTakerProvince() <= 0
                        || validateData.getTakerCity() == null || validateData.getTakerCity() <= 0
                        || validateData.getTakerDistrict() == null || validateData.getTakerDistrict() <= 0) {
                    result.setErrorCode(-1);
                    result.setErrorMsg("地址信息有误");
                } else {
                    checkLocationInArea(result, validateData);
                }
                break;

            case "store":
                if (validateData.getTakeStoreId() == null) {
                    result.setErrorCode(-1);
                    result.setErrorMsg("地址信息有误");
                }
                break;

        }
        return result;
    }

    private boolean isSubmitDeliveryAddress(EditDeliveryParam param) {
        return StringUtils.isNotBlank(param.getTakerName())
                || StringUtils.isNotBlank(param.getTakerMobile())
                || StringUtils.isNotBlank(param.getTakerAddress())
                || StringUtils.isNotBlank(param.getTakerFullAddress())
                || (param.getTakerProvince() != null && param.getTakerProvince() > 0)
                || (param.getTakerCity() != null && param.getTakerCity() > 0)
                || (param.getTakerDistrict() != null && param.getTakerDistrict() > 0);
    }

    private void fillMissingTakerAreaCodes(EditDeliveryParam param) {
        if (hasValidAreaCodes(param)) {
            return;
        }
        String areaText = param.getArea();
        if (StringUtils.isBlank(areaText) && StringUtils.isNotBlank(param.getTakerFullAddress()) && StringUtils.isNotBlank(param.getTakerAddress())) {
            if (param.getTakerFullAddress().endsWith(param.getTakerAddress())) {
                areaText = param.getTakerFullAddress().substring(0, param.getTakerFullAddress().length() - param.getTakerAddress().length());
            }
        }
        if (StringUtils.isBlank(areaText)) {
            return;
        }
        SysAreaResolveVo resolved = sysAreaService.resolveAreaByText(areaText.trim());
        if (resolved == null) {
            return;
        }
        if (param.getTakerProvince() == null || param.getTakerProvince() <= 0) {
            param.setTakerProvince(resolved.getProvinceId());
        }
        if (param.getTakerCity() == null || param.getTakerCity() <= 0) {
            param.setTakerCity(resolved.getCityId());
        }
        if (param.getTakerDistrict() == null || param.getTakerDistrict() <= 0) {
            param.setTakerDistrict(resolved.getDistrictId());
        }
        if (StringUtils.isBlank(param.getArea())) {
            param.setArea(resolved.getArea());
        }
    }

    private boolean hasValidAreaCodes(EditDeliveryParam param) {
        return param.getTakerProvince() != null && param.getTakerProvince() > 0
                && param.getTakerCity() != null && param.getTakerCity() > 0
                && param.getTakerDistrict() != null && param.getTakerDistrict() > 0;
    }

    private void checkLocationInArea(EditDeliveryVo result, EditDeliveryParam param) {
        //配送门店
        DeliveryStore store = deliveryStoreMapper.selectOne(new LambdaQueryWrapper<DeliveryStore>()
                .eq(DeliveryStore::getStoreId, param.getTakeStoreId()));
        if (ObjectUtil.isEmpty(store)) {
            result.setErrorCode(-1);
            result.setErrorMsg("提货点不存在");
            return;
        }
        if (ObjectUtil.isNotEmpty(store) && StringUtils.isEmpty(store.getArea())) {
            result.setErrorCode(-1);
            result.setErrorMsg("该门店未设置配送范围");
            return;
        }

        // 判断所在区域
        LocalInfoVo.Area locatedInArea = null;
        JSONArray areas = JSONUtil.isTypeJSON(store.getArea()) ? JSONUtil.parseArray(store.getArea()) : new JSONArray();
        for (int i = 0; i < areas.size(); i++) {
            JSONObject area = areas.getJSONObject(i);
            JSONObject areaJson = area.getJSONObject("area_json");
            if (areaJson == null) {
                continue;
            }
            if ("radius".equals(area.getStr("area_type"))) {
                JSONObject center = areaJson.getJSONObject("center");
                if (center == null || center.get("lat") == null || center.get("lng") == null || areaJson.get("radius") == null) {
                    continue;
                }
                double distance = DistanceCalculateUtils.calculateDistance(
                        Double.parseDouble(param.getTakerLatitude()),
                        Double.parseDouble(param.getTakerLongitude()),
                        Double.parseDouble(center.getStr("lat")),
                        Double.parseDouble(center.getStr("lng"))
                );
                if (distance <= areaJson.getInt("radius")) {
                    locatedInArea = new LocalInfoVo.Area();
                    locatedInArea.setAreaName(area.getStr("area_name"));
                    locatedInArea.setAreaType(area.getStr("area_type"));
                    locatedInArea.setStartPrice(ObjectUtil.defaultIfNull(area.getBigDecimal("start_price"), BigDecimal.ZERO));
                    locatedInArea.setDeliveryPrice(ObjectUtil.defaultIfNull(area.getBigDecimal("delivery_price"), BigDecimal.ZERO));
                    break;
                }
            } else {
                JSONArray paths = areaJson.getJSONArray("paths");
                if (paths == null || paths.isEmpty()) {
                    continue;
                }
                double[] polyLats = new double[paths.size()];
                double[] polyLngs = new double[paths.size()];
                for (int j = 0; j < paths.size(); j++) {
                    JSONObject latLng = paths.getJSONObject(j);
                    if (latLng.get("lat") == null || latLng.get("lng") == null) {
                        continue;
                    }
                    polyLats[j] = latLng.getDouble("lat");
                    polyLngs[j] = latLng.getDouble("lng");
                }
                if (DistanceCalculateUtils.isPointInPolygon(Double.parseDouble(param.getTakerLatitude()), Double.parseDouble(param.getTakerLongitude()), polyLats, polyLngs)) {
                    locatedInArea = new LocalInfoVo.Area();
                    locatedInArea.setAreaName(area.getStr("area_name"));
                    locatedInArea.setAreaType(area.getStr("area_type"));
                    locatedInArea.setStartPrice(ObjectUtil.defaultIfNull(area.getBigDecimal("start_price"), BigDecimal.ZERO));
                    locatedInArea.setDeliveryPrice(ObjectUtil.defaultIfNull(area.getBigDecimal("delivery_price"), BigDecimal.ZERO));
                    break;
                }
            }
        }

        if (locatedInArea == null) {
            result.setErrorCode(-1);
            result.setErrorMsg("所选的收货地址不支持配送");
        }
    }


    @Override
    public BigDecimal getSelectOrderGoodsWeight(MallOrderGoodsWeightParam param) {
        // 与PHP逻辑一致：where([['order_goods_id', 'in', $data['order_goods_ids']], ['site_id', '=', $this->site_id]])
        BigDecimal weight = BigDecimal.ZERO;
        if (param.getOrder_goods_ids() != null && !param.getOrder_goods_ids().isEmpty()) {
            List<MallOrderGoods> list = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>()
                    .in("order_goods_id", param.getOrder_goods_ids()).eq("site_id", RequestUtils.siteId()));
            if (!list.isEmpty()) {
                for (MallOrderGoods item : list) {
                    MallGoodsSku sku = mallGoodsSkuMapper.selectById(item.getSkuId());
                    if (ObjectUtil.isNotEmpty(sku) && ObjectUtil.isNotEmpty(sku.getWeight()) && sku.getWeight().compareTo(BigDecimal.ZERO) > 0) {
                        weight = weight.add(sku.getWeight().multiply(new BigDecimal(item.getNum())));
                    }
                }
            }
        }
        return weight.max(BigDecimal.ONE);
    }

    @Override
    public OrderNumVo getOrderCount() {
        Integer siteId = RequestUtils.siteId();

        OrderNumVo vo = new OrderNumVo();

        vo.setWaitPay(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                .eq("site_id", siteId)
                .eq("status", OrderStatusEnum.WAIT_PAY.getStatus())
        ));

        vo.setWaitDelivery(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                .eq("site_id", siteId)
                .eq("status", OrderStatusEnum.WAIT_DELIVERY.getStatus())
        ));

        vo.setWaitTake(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                .eq("site_id", siteId)
                .eq("status", OrderStatusEnum.WAIT_TAKE.getStatus())
        ));

        vo.setWaitRefund(mallOrderRefundMapper.selectCount(new QueryWrapper<MallOrderRefund>()
                .eq("site_id", siteId)
                .eq("status", OrderRefundStatusEnum.BUYER_APPLY_WAIT_STORE.getStatus())
        ));

        vo.setWaitTransfer(siteShopCashOutMapper.selectCount(new QueryWrapper<SiteShopCashOut>()
                .eq("site_id", siteId)
                .eq("status", ShopCashOutEnum.WAIT_TRANSFER.getState())
        ));
        MPJQueryWrapper<MallOrderGoods> queryWrapperGoods = new MPJQueryWrapper<>();
        queryWrapperGoods.setAlias("og")
                .leftJoin("?_mall_order g ON og.order_id = g.order_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapperGoods.select("og.order_goods_id");
        queryWrapperGoods.eq("og.site_id", siteId);
        queryWrapperGoods.eq("og.is_verify", 1);
        queryWrapperGoods.eq("g.status", OrderStatusEnum.WAIT_TAKE.getStatus());
        Long waiteVerify = mallOrderGoodsMapper.selectCount(queryWrapperGoods);
        vo.setWaitVerify(waiteVerify);
        return vo;
    }

    @Override
    @Transactional
    public void delete(MallOrderDeleteParam param) {
        if (param.getOrderIds().isEmpty()) throw new AdminException("订单不存在");

        List<MallOrder> statusList = mallOrderMapper.selectList(new QueryWrapper<MallOrder>().select("order_id, status,order_no,create_time").in("order_id", param.getOrderIds()).eq("delete_time", 0));
        List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().select("site_id, order_id, goods_id").in("order_id", param.getOrderIds()));
        String errorOrderStr = "";
        for (MallOrder item : statusList) {
            if (OrderStatusEnum.CLOSE.getStatus().toString().equals(item.getStatus())) {
                continue;
            }
            errorOrderStr = errorOrderStr + item.getOrderNo() + ",";
        }

        if (StringUtils.isNotEmpty(errorOrderStr)) {
            throw new AdminException("删除订单中有未关闭订单：" + errorOrderStr);
        }

        //删除订单表
        mallOrderMapper.delete(new QueryWrapper<MallOrder>().in("order_id", param.getOrderIds()).eq("site_id", RequestUtils.siteId()));

        //删除订单商品项
        mallOrderGoodsMapper.delete(new QueryWrapper<MallOrderGoods>().in("order_id", param.getOrderIds()).eq("site_id", RequestUtils.siteId()));

        //删除订单退款
        mallOrderRefundMapper.delete(new QueryWrapper<MallOrderRefund>().in("order_id", param.getOrderIds()).eq("site_id", RequestUtils.siteId()));

        //删除统计
        for (MallOrder item : statusList) {
            List<MallOrderGoods> orderGoodsListThis = orderGoodsList.stream().filter(orderGoods -> orderGoods.getOrderId().equals(item.getOrderId())).toList();
            if (!orderGoodsListThis.isEmpty()) {
                for (MallOrderGoods orderGoods : orderGoodsListThis) {
                    GoodsDelStatParam paramGoods = new GoodsDelStatParam();
                    paramGoods.setSiteId(RequestUtils.siteId());
                    paramGoods.setDate(DateUtil.format(DateUtil.date(item.getCreateTime() * 1000), "yyyy-MM-dd"));
                    paramGoods.setGoodsId(orderGoods.getGoodsId());
                    paramGoods.setSaleNum(1);
                    coreGoodsStatService.delStat(paramGoods);
                }
            }
            DelStatParam delStatParam = new DelStatParam();
            delStatParam.setDate(DateUtil.format(DateUtil.date(item.getCreateTime() * 1000), "yyyy-MM-dd"));
            delStatParam.setOrderNum(1);
            coreStatService.delStat(RequestUtils.siteId(), delStatParam);
        }
    }

    @Override
    public PageResult<MallOrderMemberConsumeListVo> consumelists(PageParam pageParam, Integer memberId) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallOrder> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("order_id,order_no,goods_money,order_money,pay_time,taker_name,taker_mobile,taker_full_address")
                .eq("site_id", RequestUtils.siteId())
                .eq("member_id", memberId)
                .orderByDesc("create_time");

        IPage<MallOrderMemberConsumeListVo> iPage = mallOrderMapper.selectJoinPage(new Page<>(page, limit), MallOrderMemberConsumeListVo.class, queryWrapper);

        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    /**
     * 主表积分字段缺失时，从优惠明细或订单行项回填展示数据（兼容历史订单）。
     */
    private void backfillPointDeductFromRecords(MallOrderInfoVo vo, Integer orderId, List<MallOrderDiscount> platformRecords) {
        java.math.BigDecimal pointMoney = ObjectUtil.defaultIfNull(vo.getPointDeductMoney(), java.math.BigDecimal.ZERO);
        java.math.BigDecimal usePointVal = ObjectUtil.defaultIfNull(vo.getUsePoint(), java.math.BigDecimal.ZERO);
        if (pointMoney.compareTo(java.math.BigDecimal.ZERO) > 0 || usePointVal.compareTo(java.math.BigDecimal.ZERO) > 0) {
            return;
        }
        int usePoint = 0;
        if (platformRecords != null) {
            for (MallOrderDiscount record : platformRecords) {
                if (OrderDiscountTypeEnum.POINT_DEDUCT.getType().equals(record.getDiscountType())) {
                    pointMoney = pointMoney.add(ObjectUtil.defaultIfNull(record.getMoney(), java.math.BigDecimal.ZERO));
                    usePoint += record.getNum() != null ? record.getNum() : 0;
                }
            }
        }
        if (pointMoney.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            List<MallOrderGoods> goodsRows = mallOrderGoodsMapper.selectList(
                    new LambdaQueryWrapper<MallOrderGoods>().eq(MallOrderGoods::getOrderId, orderId));
            for (MallOrderGoods row : goodsRows) {
                pointMoney = pointMoney.add(ObjectUtil.defaultIfNull(row.getPointDeductMoney(), java.math.BigDecimal.ZERO));
                usePoint += row.getUsePoint() != null ? row.getUsePoint() : 0;
            }
        }
        if (pointMoney.compareTo(java.math.BigDecimal.ZERO) > 0) {
            vo.setPointDeductMoney(pointMoney);
            vo.setUsePoint(new java.math.BigDecimal(usePoint));
        }
    }
}
