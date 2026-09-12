package com.niu.mall.listener.order;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.event.order.OrderPageEventDefiner;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.entity.goods.MallGoods;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.core.enums.goods.GoodsAuditStatusEnum;
import com.niu.core.enums.goods.GoodsStatusEnum;
import com.niu.mall.enums.order.OrderRefundStatusEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@EventCallback("mall")
@Component
public class MallOrderPageListener extends OrderPageEventDefiner {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    @Resource
    MallOrderRefundMapper mallOrderRefundMapper;

    @Resource
    SiteShopMapper siteShopMapper;

    @Override
    public OrderEventResult handleCallback(OrderPageEvent event) {

        OrderEventResult result = new OrderEventResult();

        String[] todayTime = DateUtils.getStartEndByToday(new Date());
        LocalDate localDate = LocalDate.now().minusDays(+1);
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        String[] yesterdayTime = DateUtils.getStartEndByToday(Date.from(zonedDateTime.toInstant()));

        //今日订单总数
        QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("status", OrderStatusEnum.WAIT_PAY.getStatus());
        applyCircleOrderFilter(queryWrapper);
        QueryMapperUtils.buildByTime(queryWrapper, "create_time", todayTime);
        result.setTodayOrderCount(mallOrderMapper.selectCount(queryWrapper));

        //昨日订单总数
        queryWrapper.clear();
        queryWrapper.ge("status", OrderStatusEnum.WAIT_PAY.getStatus());
        applyCircleOrderFilter(queryWrapper);
        QueryMapperUtils.buildByTime(queryWrapper, "create_time", yesterdayTime);
        result.setYesterdayOrderCount(mallOrderMapper.selectCount(queryWrapper));

        //订单总数
        QueryWrapper<MallOrder> totalOrderWrapper = new QueryWrapper<>();
        totalOrderWrapper.ge("status", OrderStatusEnum.WAIT_PAY.getStatus());
        applyCircleOrderFilter(totalOrderWrapper);
        result.setTotalOrderCount(mallOrderMapper.selectCount(totalOrderWrapper));

        //今日销售额
        QueryWrapper<MallOrder> moneyWrapper = new QueryWrapper<>();
        moneyWrapper.select("sum(order_money) as order_money");
        moneyWrapper.gt("status", OrderStatusEnum.WAIT_PAY.getStatus());
        applyCircleOrderFilter(moneyWrapper);
        QueryMapperUtils.buildByTime(moneyWrapper, "create_time", todayTime);
        MallOrder todaySaleMoney = mallOrderMapper.selectOne(moneyWrapper);
        if (todaySaleMoney != null && ObjectUtil.isNotEmpty(todaySaleMoney.getOrderMoney())) {
            result.setTodaySaleMoney(todaySaleMoney.getOrderMoney().setScale(2, RoundingMode.HALF_UP).toString());
        }

        //昨日销售额
        moneyWrapper.clear();
        moneyWrapper.select("sum(order_money) as order_money");
        moneyWrapper.gt("status", OrderStatusEnum.WAIT_PAY.getStatus());
        applyCircleOrderFilter(moneyWrapper);
        QueryMapperUtils.buildByTime(moneyWrapper, "create_time", yesterdayTime);
        MallOrder yesterdaySaleMoney = mallOrderMapper.selectOne(moneyWrapper);
        if (yesterdaySaleMoney != null && ObjectUtil.isNotEmpty(yesterdaySaleMoney.getOrderMoney())) {
            result.setYesterdaySaleMoney(yesterdaySaleMoney.getOrderMoney().setScale(2, RoundingMode.HALF_UP).toString());
        }
        //销售总额
        moneyWrapper.clear();
        moneyWrapper.select("sum(order_money) as order_money");
        moneyWrapper.gt("status", OrderStatusEnum.WAIT_PAY.getStatus());
        applyCircleOrderFilter(moneyWrapper);
        MallOrder totalSaleMoney = mallOrderMapper.selectOne(moneyWrapper);
        if (totalSaleMoney != null && ObjectUtil.isNotEmpty(totalSaleMoney.getOrderMoney())) {
            result.setTotalSaleMoney(totalSaleMoney.getOrderMoney().setScale(2, RoundingMode.HALF_UP).toString());
        }
        //今日退款金额（PHP 不加 circle 过滤）
        QueryWrapper<MallOrderRefund> moneyRefundWrapper = new QueryWrapper<>();
        moneyRefundWrapper.select("sum(money) as money");
        moneyRefundWrapper.eq("status", OrderRefundStatusEnum.FINISH.getStatus());
        QueryMapperUtils.buildByTime(moneyRefundWrapper, "create_time", todayTime);
        MallOrderRefund todayRefundMoney = mallOrderRefundMapper.selectOne(moneyRefundWrapper);
        if (todayRefundMoney != null && ObjectUtil.isNotEmpty(todayRefundMoney.getMoney())) {
            result.setTodayRefundMoney(todayRefundMoney.getMoney().setScale(2, RoundingMode.HALF_UP).toString());
        }
        //昨日退款金额
        moneyRefundWrapper.clear();
        moneyRefundWrapper.select("sum(money) as money");
        moneyRefundWrapper.eq("status", OrderRefundStatusEnum.FINISH.getStatus());
        QueryMapperUtils.buildByTime(moneyRefundWrapper, "create_time", yesterdayTime);
        MallOrderRefund yesterdayRefundMoney = mallOrderRefundMapper.selectOne(moneyRefundWrapper);
        if (yesterdayRefundMoney != null && ObjectUtil.isNotEmpty(yesterdayRefundMoney.getMoney())) {
            result.setYesterdayRefundMoney(yesterdayRefundMoney.getMoney().setScale(2, RoundingMode.HALF_UP).toString());
        }
        //退款总额
        moneyRefundWrapper.clear();
        moneyRefundWrapper.select("sum(money) as money");
        moneyRefundWrapper.eq("status", OrderRefundStatusEnum.FINISH.getStatus());
        MallOrderRefund totalRefundMoney = mallOrderRefundMapper.selectOne(moneyRefundWrapper);
        if (totalRefundMoney != null && ObjectUtil.isNotEmpty(totalRefundMoney.getMoney())) {
            result.setTotalRefundMoney(totalRefundMoney.getMoney().setScale(2, RoundingMode.HALF_UP).toString());
        }
        //待付款
        QueryWrapper<MallOrder> payWrapper = new QueryWrapper<>();
        payWrapper.eq("status", OrderStatusEnum.WAIT_PAY.getStatus());
        applyCircleOrderFilter(payWrapper);
        result.setWaitPayOrder(mallOrderMapper.selectCount(payWrapper));

        //待发货
        QueryWrapper<MallOrder> deliveryWrapper = new QueryWrapper<>();
        deliveryWrapper.eq("status", OrderStatusEnum.WAIT_DELIVERY.getStatus());
        applyCircleOrderFilter(deliveryWrapper);
        result.setWaitDeliveryOrder(mallOrderMapper.selectCount(deliveryWrapper));

        //待收货
        QueryWrapper<MallOrder> takeWrapper = new QueryWrapper<>();
        takeWrapper.eq("status", OrderStatusEnum.WAIT_TAKE.getStatus());
        applyCircleOrderFilter(takeWrapper);
        result.setWaitTakeOrder(mallOrderMapper.selectCount(takeWrapper));

        //退款中（订单项）
        QueryWrapper<MallOrderRefund> refundOrderWrapper = new QueryWrapper<>();
        refundOrderWrapper.notIn("status", OrderRefundStatusEnum.CLOSE.getStatus(), OrderRefundStatusEnum.FINISH.getStatus());
        applyCircleRefundFilter(refundOrderWrapper);
        result.setRefundOrder(mallOrderRefundMapper.selectCount(refundOrderWrapper));

        //商品销售中
        QueryWrapper<MallGoods> goodsWrapper = new QueryWrapper<>();
        goodsWrapper.eq("status", GoodsStatusEnum.ONLINE.getStatus());
        applyCircleGoodsFilter(goodsWrapper);
        result.setSaleGoodsNum(mallGoodsMapper.selectCount(goodsWrapper));

        //商品待审核
        QueryWrapper<MallGoods> goodsAuditWrapper = new QueryWrapper<>();
        goodsAuditWrapper.eq("audit_status", GoodsAuditStatusEnum.WAIT_AUDIT.getStatus());
        applyCircleGoodsFilter(goodsAuditWrapper);
        result.setAuditGoodsNum(mallGoodsMapper.selectCount(goodsAuditWrapper));
        return result;
    }

    private boolean isCircleContext() {
        return AppTypeEnum.CIRCLE.getName().equals(RequestUtils.appType())
                && ObjectUtil.isNotEmpty(RequestUtils.circleIds());
    }

    private List<Integer> resolveCircleIds() {
        return RequestUtils.circleIds();
    }

    private void applyCircleOrderFilter(QueryWrapper<MallOrder> wrapper) {
        if (isCircleContext()) {
            wrapper.in("circle_id", resolveCircleIds());
        }
    }

    private void applyCircleRefundFilter(QueryWrapper<MallOrderRefund> wrapper) {
        if (isCircleContext()) {
            wrapper.in("circle_id", resolveCircleIds());
        }
    }

    private void applyCircleGoodsFilter(QueryWrapper<MallGoods> wrapper) {
        if (!isCircleContext()) {
            return;
        }
        List<Integer> siteIds = getSiteIdsByCircleIds();
        if (siteIds.isEmpty()) {
            wrapper.eq("site_id", -1);
        } else {
            wrapper.in("site_id", siteIds);
        }
    }

    private List<Integer> getSiteIdsByCircleIds() {
        QueryWrapper<SiteShop> shopWrapper = new QueryWrapper<>();
        shopWrapper.in("circle_id", resolveCircleIds()).select("site_id");
        return siteShopMapper.selectList(shopWrapper).stream()
                .map(SiteShop::getSiteId)
                .filter(Objects::nonNull)
                .toList();
    }
}
