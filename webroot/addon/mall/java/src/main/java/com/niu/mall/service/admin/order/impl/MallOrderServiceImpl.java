package com.niu.mall.service.admin.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.diy_form.DiyFormRecordsFields;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.verify.Verify;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.diy_form.DiyFormRecordsFieldsMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.verify.VerifyMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderDelivery;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderLog;
import com.niu.mall.enums.goods.GoodsStatusEnum;
import com.niu.mall.enums.order.*;
import com.niu.mall.enums.trade.SettlementEnum;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.order.MallOrderDeliveryMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderLogMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.admin.order.IMallOrderService;
import com.niu.mall.service.admin.order.param.EditDeliveryParam;
import com.niu.mall.service.admin.order.param.EditPriceParam;
import com.niu.mall.service.admin.order.param.MallOrderSearchParam;
import com.niu.mall.service.admin.order.param.SetShopRemarkParam;
import com.niu.mall.service.admin.order.vo.*;
import com.niu.mall.service.core.order.ICoreOrderCloseService;
import com.niu.mall.service.core.order.ICoreOrderLogService;
import com.niu.mall.service.core.order.ICoreOrderPayService;
import com.niu.mall.service.core.order.param.AddOrderLogParam;
import com.niu.mall.service.core.order.param.OrderCloseParam;
import com.niu.mall.service.core.order.param.OrderPayParam;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    ICoreOrderCloseService coreOrderCloseService;

    @Resource
    ICoreOrderPayService coreOrderPayService;

    @Resource
    ICoreOrderLogService coreOrderLogService;

    @Resource
    ICorePayService corePayService;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    ISiteService siteService;

    @Resource
    private DiyFormRecordsFieldsMapper diyFormRecordsFieldsMapper;
    @Resource
    private DeliveryStoreMapper deliveryStoreMapper;
    @Resource
    private VerifyMapper verifyMapper;
    @Resource
    private DeliveryLocalDeliveryOrderMapper deliveryLocalDeliveryOrderMapper;

    /**
     * 订单列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopOrderListVo>
     */
    @Override
    public PageResult<MallOrderListVo> page(PageParam pageParam, MallOrderSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallOrder> queryWrapper = new MPJQueryWrapper<>();
        //ns_mall_order 表中没有 o.point,activity_type,,o.pay_money 该字段
        queryWrapper.setAlias("o")
                .leftJoin("?_member m ON o.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_pay p ON o.out_trade_no = p.out_trade_no".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("o.mall_money,o.shop_money,o.site_id,order_id,order_no,order_type,order_from,o.out_trade_no,o.status,o.member_id,ip,goods_money,delivery_money,order_money,o.create_time,o.pay_time,delivery_type,taker_name,taker_mobile,taker_full_address,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,o.mall_coupon_money,o.mall_refund_coupon_money");
        if(RequestUtils.siteId() != 0){
            queryWrapper.eq("o.site_id", RequestUtils.siteId());
        }

        queryWrapper.orderByDesc("o.create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getSearchType()) && ObjectUtil.isNotEmpty(searchParam.getSearchName())){
            queryWrapper.like("o."+searchParam.getSearchType(), searchParam.getSearchName());
        }else if(ObjectUtil.isNotEmpty(searchParam.getSearchName())){
            queryWrapper.and(wrapper -> {
                wrapper.like("o.order_no", searchParam.getSearchName())
                        .or()
                        .like("o.out_trade_no", searchParam.getSearchName());
            });
        }

        if (ObjectUtil.isNotEmpty(searchParam.getStatus())) queryWrapper.eq("o.status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderFrom())) queryWrapper.eq("o.order_from", searchParam.getOrderFrom());
        if (ObjectUtil.isNotEmpty(searchParam.getPayType())) queryWrapper.eq("p.type", searchParam.getPayType());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "o.create_time", searchParam.getCreateTime());
        if (ObjectUtil.isNotEmpty(searchParam.getPayTime())) QueryMapperUtils.buildByTime(queryWrapper, "p.pay_time", searchParam.getPayTime());
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) queryWrapper.and(i -> i.eq("taker_name", searchParam.getKeyword()).or().eq("taker_mobile", searchParam.getKeyword()));
        if (ObjectUtil.isNotEmpty(searchParam.getActivityType())) queryWrapper.eq("o.activity_type", searchParam.getActivityType());
        if (ObjectUtil.isNotEmpty(searchParam.getExtendField1())) queryWrapper.eq("o.extend_field_1", searchParam.getExtendField1());
        if (ObjectUtil.isNotEmpty(searchParam.getExtendField2())) queryWrapper.eq("o.extend_field_2", searchParam.getExtendField2());
        if (ObjectUtil.isNotEmpty(searchParam.getDeliveryType())) queryWrapper.eq("o.delivery_type", searchParam.getDeliveryType());
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsSource())){
            if(searchParam.getGoodsSource().equals("self_goods") || searchParam.getGoodsSource().equals("0")){
                queryWrapper.eq("o.supply_order_id", 0);
            }else{
                queryWrapper.ne("o.supply_order_id", 0);
            }
        }

        IPage<MallOrderListVo> iPage = mallOrderMapper.selectJoinPage(new Page<>(page, limit), MallOrderListVo.class, queryWrapper);
        for (MallOrderListVo vo : iPage.getRecords()) {
            MPJQueryWrapper<MallOrderGoods> orderGoodsWra = new MPJQueryWrapper<>();
            orderGoodsWra.setAlias("o")
                    .leftJoin("?_mall_order_refund r ON o.order_goods_id = r.order_goods_id ".replace("?_", GlobalConfig.tablePrefix));
            orderGoodsWra.select("o.*");
            orderGoodsWra.select("r.refund_id,r.apply_money,r.money refund_money");
            orderGoodsWra.eq("o.order_id", vo.getOrderId());
            orderGoodsWra.orderByDesc("r.refund_id");
            List<MallOrderListVo.OrderGoods> orderGoods = mallOrderGoodsMapper.selectJoinList(MallOrderListVo.OrderGoods.class,orderGoodsWra);

            //按照商品进行分组
            List<MallOrderListVo.OrderGoods> orderGoodsListAll = new ArrayList<>();
            Map<Integer, List<MallOrderListVo.OrderGoods>> orderGoodsMap = orderGoods.stream().collect(Collectors.groupingBy(MallOrderListVo.OrderGoods::getOrderGoodsId));
            for (Integer key : orderGoodsMap.keySet()){
                orderGoodsListAll.add(orderGoodsMap.get(key).get(0));
            }

            vo.setOrderGoods(orderGoodsListAll);

            MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
            Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", vo.getMemberId()));
            BeanUtil.copyProperties(member, memberVo);
            vo.setMember(memberVo);

            if (!vo.getOutTradeNo().isEmpty()) {
                Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", vo.getOutTradeNo()));
                if (pay != null) {
                    MallOrderListVo.Pay payVo = new MallOrderListVo.Pay();
                    BeanUtil.copyProperties(pay, payVo);

                    vo.setPay(payVo);
                }
            }
            SiteInfoVo siteInfoVo = siteService.info(vo.getSiteId());
            vo.setSiteName(siteInfoVo.getSiteName());

            if(ObjectUtil.isNotEmpty(vo.getSupplyId()) && vo.getSupplyId() > 0){
                SiteInfoVo siteInfo = siteService.info(vo.getSupplyId());
                vo.setSupplyId(siteInfo.getSiteId());
                MallOrderListVo.Supply supply = new MallOrderListVo.Supply();
                supply.setSiteId(siteInfo.getSiteId());
                supply.setSiteName(siteInfo.getSiteName());
                vo.setSupply(supply);
            }else{
                vo.setSupplyMoney(null);
                vo.setSupplyRefundMoney(null);
            }
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    /**
     * 订单详情
     * @param id 主键
     * @return ShopOrderInfoVo
     */
    @Override
    public MallOrderInfoVo detail(Integer id) {
        // ns_mall_order 表中没有这四个字段	relate_id,	activity_type,	point,	form_record_id
        MallOrder model = new MallOrder();
        if(RequestUtils.siteId() == 0){
            model = mallOrderMapper.selectOne(
                    new QueryWrapper<MallOrder>()
                            .eq("order_id", id)
                            .select("order_id,site_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,invoice_id,create_time,pay_time,delivery_time,take_time,finish_time,close_time,delivery_type,taker_name,taker_mobile,taker_province,taker_city,taker_district,taker_address,taker_full_address,taker_longitude,taker_latitude,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,discount_money,is_evaluate,mall_discount_money,mall_money,mall_refund_money,refund_money,shop_money,shop_refund_money,mall_coupon_money,mall_refund_coupon_money,supply_id,supply_money,form_record_id,supply_refund_money,mall_coupon_money,mall_refund_coupon_money,shop_commission,shop_refund_commission"));
        }else {
            model=mallOrderMapper.selectOne(
                    new QueryWrapper<MallOrder>()
                            .eq("order_id", id)
                            .eq("site_id", RequestUtils.siteId())
                            .select("order_id,site_id,order_no,order_type,order_from,out_trade_no,status,member_id,ip,goods_money,delivery_money,order_money,invoice_id,create_time,pay_time,delivery_time,take_time,finish_time,close_time,delivery_type,taker_name,taker_mobile,taker_province,taker_city,taker_district,taker_address,taker_full_address,taker_longitude,taker_latitude,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark,discount_money,is_evaluate,mall_discount_money,mall_money,mall_refund_money,refund_money,shop_money,shop_refund_money,mall_coupon_money,mall_refund_coupon_money,supply_id,supply_money,form_record_id,supply_refund_money,mall_coupon_money,mall_refund_coupon_money,shop_commission,shop_refund_commission"));
        }
        Assert.notNull(model, "订单不存在");

        MallOrderInfoVo vo = new MallOrderInfoVo();
        BeanUtils.copyProperties(model, vo);

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
        List<MallOrderInfoVo.OrderGoods> orderGoods = mallOrderGoodsMapper.selectJoinList(MallOrderInfoVo.OrderGoods.class,orderGoodsWra);

        vo.setIsRefundStatusShow(false);
        for(MallOrderInfoVo.OrderGoods orderGoodsVo : orderGoods){
            MallGoods goodsInfo = mallGoodsMapper.selectById(orderGoodsVo.getGoodsId());
            orderGoodsVo.setUnit(goodsInfo.getUnit().equals("") ? "件"  : goodsInfo.getUnit() );
            MallOrderDelivery mallOrderDelivery = mallOrderDeliveryMapper.selectById(orderGoodsVo.getDeliveryId());
            if(ObjectUtil.isNotEmpty(mallOrderDelivery)){
                orderGoodsVo.setDeliveryTypeName(vo.getDeliveryTypeName());
            }
            if (!OrderGoodsRefundEnum.NORMAL.getStatus().equals(orderGoodsVo.getStatus())){
                vo.setIsRefundStatusShow(true);
            }
        }

        vo.setOrderGoods(orderGoods);

        //查询订单配送信息 mall_order_delivery
        List<MallOrderDelivery> orderDelivery = mallOrderDeliveryMapper.selectList(new QueryWrapper<MallOrderDelivery>().eq("order_id", vo.getOrderId()));

        if (!orderDelivery.isEmpty()){
            List<MallOrderInfoVo.OrderDelivery> orderDeliveryVoList = new ArrayList<>();
            for (MallOrderDelivery orderDeliveryItem : orderDelivery){
                //查询订单配送信息 mall_order_delivery
                MallOrderInfoVo.OrderDelivery orderDeliveryVo = new MallOrderInfoVo.OrderDelivery();
                BeanUtils.copyProperties(orderDeliveryItem, orderDeliveryVo);
                if(ObjectUtil.isNotEmpty(orderDeliveryItem.getLocalDeliveryOrderId())){
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

        // 查询订单包裹信息
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
            List<MallOrderInfoVo.OrderDelivery> orderDelivery1 = CollectUtils.convert(mallOrderDeliveryMapper.selectList(new QueryWrapper<MallOrderDelivery>().eq("order_id", vo.getOrderId())), MallOrderInfoVo.OrderDelivery.class);
            vo.setOrderDelivery(orderDelivery1);
        }

        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType())) {
            DeliveryStore store = deliveryStoreMapper.selectById(vo.getTakeStoreId());
            MallOrderInfoVo.Store storeVo = new MallOrderInfoVo.Store();
            BeanUtil.copyProperties(store, storeVo);
            vo.setStore(storeVo);
            //查询核销记录
            Verify verify = verifyMapper.selectOne(new QueryWrapper<Verify>()
                    .eq("site_id", vo.getSiteId()).eq("type", "shopPickUpOrder").eq("relate_tag",id));
            if(ObjectUtil.isNotEmpty( verify)){
                //查询会员信息
                Member memberVerify = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", verify.getVerifierMemberId()));
                vo.setVerifyCode(verify.getCode());
                if(ObjectUtil.isNotEmpty(memberVerify)){
                    MallOrderInfoVo.VerifierMemberInfo memberVerifyObj = new MallOrderInfoVo.VerifierMemberInfo();
                    memberVerifyObj.setMemberId(memberVerify.getMemberId());
                    memberVerifyObj.setNickname(memberVerify.getNickname());
                    vo.setVerifierMember(memberVerifyObj);
                }
            }
        }

        // 查询订单包裹信息
        if (vo.getDeliveryType().equals(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
            DeliveryStore store = deliveryStoreMapper.selectById(vo.getTakeStoreId());
            MallOrderInfoVo.Store storeVo = new MallOrderInfoVo.Store();
            BeanUtil.copyProperties(store, storeVo);
            vo.setStore(storeVo);
        }

        //查询店铺信息
        SiteInfoVo siteInfoVo = siteService.info(vo.getSiteId());
        vo.setSiteName(siteInfoVo.getSiteName());
        if(ObjectUtil.isEmpty(vo.getShopCommission())){
            vo.setShopCommission(BigDecimal.ZERO);
        }
        if(ObjectUtil.isEmpty(vo.getShopRefundCommission())){
            vo.setShopRefundCommission(BigDecimal.ZERO);
        }

        List<MallOrderLogListVo> orderLog = CollectUtils.convert(mallOrderLogMapper.selectList(new QueryWrapper<MallOrderLog>().eq("order_id", vo.getOrderId()).orderByDesc("create_time")), MallOrderLogListVo.class);
        vo.setOrderLog(orderLog);
        //获取店铺信息
        if(ObjectUtil.isNotEmpty(vo.getSupplyId()) && vo.getSupplyId() >0){
            SiteInfoVo supplyInfo = siteService.info(vo.getSupplyId());
            MallOrderInfoVo.Supply supply = new MallOrderInfoVo.Supply();
            supply.setSiteId(supplyInfo.getSiteId());
            supply.setSiteName(supplyInfo.getSiteName());
            vo.setSupply(supply);
            if(ObjectUtil.isEmpty(vo.getSupplyRefundMoney())){
                vo.setSupplyRefundMoney(BigDecimal.ZERO);
            }
        }

        if (vo.getFormRecordId() != null && vo.getFormRecordId() > 0) {
            Long fieldCount = diyFormRecordsFieldsMapper.selectCount(
                    new QueryWrapper<DiyFormRecordsFields>()
                            .eq("record_id", vo.getFormRecordId())
            );
            vo.setFormRecordShow(fieldCount > 0);
        } else {
            vo.setFormRecordShow(false);
        }

        if (ObjectUtil.isNotEmpty(orderGoods)) {
            for (MallOrderInfoVo.OrderGoods item : orderGoods) {
                if (item.getFormRecordId() != null && item.getFormRecordId() > 0) {
                    Long fieldCount = diyFormRecordsFieldsMapper.selectCount(
                            new QueryWrapper<DiyFormRecordsFields>()
                                    .eq("record_id", item.getFormRecordId())
                    );
                    item.setFormRecordShow(fieldCount > 0);
                } else {
                    item.setFormRecordShow(false);
                }
            }
        }

        boolean isInstallFenxiao = false;
        if (siteInfoVo != null && ObjectUtil.isNotEmpty(siteInfoVo.getAddons())) {
            isInstallFenxiao = siteInfoVo.getAddons().contains("mall_fenxiao");
        }
        vo.setIsInstallFenxiao(isInstallFenxiao);
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
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString())) throw new CommonException("只有待支付的订单可以改价");

        // 关闭已经发起的支付单据
        corePayService.closeByTrade(order.getOrderType(), order.getOrderId());

        BigDecimal goodsMoney = new BigDecimal(0);
        List<MallOrderGoods> orderGoods = mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("order_id", order.getOrderId()).select("order_goods_id,order_goods_money,goods_money,price,discount_money,num"));

        for (MallOrderGoods item: orderGoods) {
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
     * 获取订单配送修改信息
     *
     * @param param
     * @return
     */
    @Override
    public EditDeliveryVo getEditDeliveryData(EditDeliveryParam param) {
        MallOrder order = mallOrderMapper.selectOne(
                new QueryWrapper<MallOrder>()
                        .eq("order_id", param.getOrderId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(order, "订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString())) throw new CommonException("只有待支付的订单可以修改");
        if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType())) throw new CommonException("虚拟商品订单不可以修改配送地址");

        EditDeliveryVo vo = new EditDeliveryVo();
        BeanUtil.copyProperties(order, vo);

        return vo;
    }

    /**
     * 修改订单配送
     *
     * @param param
     */
    @Override
    public void editDelivery(EditDeliveryParam param) {
        MallOrder order = mallOrderMapper.selectOne(
                new QueryWrapper<MallOrder>()
                        .eq("order_id", param.getOrderId())
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(order, "订单不存在");
        if (!order.getStatus().equals(OrderStatusEnum.WAIT_PAY.getStatus().toString())) throw new CommonException("只有待支付的订单可以修改");
        if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType())) throw new CommonException("虚拟商品订单不可以修改配送地址");

        if (param.getDeliveryType().equals(OrderDeliveryTypeEnum.EXPRESS.getType())) {
            if (ObjectUtil.isEmpty(param.getTakerName())
                    || ObjectUtil.isEmpty(param.getTakerMobile())
                    || ObjectUtil.isEmpty(param.getTakerProvince())
                    || ObjectUtil.isEmpty(param.getTakerCity())
                    || ObjectUtil.isEmpty(param.getTakerDistrict())
                    || ObjectUtil.isEmpty(param.getTakerAddress())
                    || ObjectUtil.isEmpty(param.getTakerFullAddress())
            ) {
                throw new CommonException("地址信息有误");
            }
        } else {
            throw new CommonException("不支持的配送方式");
        }

        MallOrder model = new MallOrder();
        BeanUtil.copyProperties(param, model);

        mallOrderMapper.updateById(model);
    }
}
