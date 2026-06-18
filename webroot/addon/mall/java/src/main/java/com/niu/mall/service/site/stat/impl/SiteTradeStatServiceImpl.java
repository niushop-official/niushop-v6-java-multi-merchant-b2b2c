package com.niu.mall.service.site.stat.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.shop.SiteShopMember;
import com.niu.core.entity.stat.SysStat;
import com.niu.core.enums.pay.PayStatusEnum;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.shop.SiteShopMemberMapper;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.enums.order.OrderRefundStatusEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.stat.ICoreTradeStatService;
import com.niu.mall.service.core.stat.param.TradeStatParam;
import com.niu.mall.service.core.stat.vo.BasicDataVo;
import com.niu.mall.service.core.stat.vo.ConversionDataVo;
import com.niu.mall.service.core.stat.vo.DealMemberDataVo;
import com.niu.mall.service.core.stat.vo.TrendDataVo;
import com.niu.mall.service.site.stat.ISiteTradeStatService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SiteTradeStatServiceImpl implements ISiteTradeStatService {

    @Autowired
    private ICoreTradeStatService coreTradeStatService;

    private Integer getSiteId() {
        return RequestUtils.siteId();
    }

    @Override
    public BasicDataVo getBasicData(TradeStatParam param) {
        Integer siteId = getSiteId();
        long startTime = parseStartTime(param.getStartTime());
        long endTime = parseEndTime(param.getEndTime());

        // 订单统计
        MPJQueryWrapper<MallOrder> ow = new MPJQueryWrapper<MallOrder>()
                .eq("site_id", siteId)
                .between("create_time", startTime, endTime);
        Map<String, Object> orderStat = coreTradeStatService.getOrderStat(ow);

        // 订单商品统计
        MPJQueryWrapper<MallOrderGoods> gw = new MPJQueryWrapper<MallOrderGoods>()
                .eq("o.site_id", siteId)
                .between("o.create_time", startTime, endTime);
        Map<String, Object> orderGoodsStat = coreTradeStatService.getOrderGoodsStat(gw);

        // 退款统计
        MPJQueryWrapper<MallOrderRefund> rw = new MPJQueryWrapper<MallOrderRefund>()
                .eq("r.site_id", siteId)
                .eq("r.status", OrderRefundStatusEnum.FINISH.getStatus())
                .between("r.create_time", startTime, endTime);
        Map<String, Object> refundStat = coreTradeStatService.getRefundStat(rw);

        // 余额支付统计
        MPJQueryWrapper<Pay> pw = new MPJQueryWrapper<Pay>()
                .eq("o.site_id", siteId)
                .eq("p.status", PayStatusEnum.STATUS_FINISH.getCode())
                .eq("p.type", "balancepay")
                .between("p.pay_time", startTime, endTime);
        Map<String, Object> payStat = coreTradeStatService.getSitePayStat(pw);

        BasicDataVo vo = new BasicDataVo();
        vo.setSaleMoney(toBigDecimal(orderStat.get("sale_money")));
        vo.setOrderMoney(toBigDecimal(orderStat.get("order_money")));
        vo.setBalancePayMoney(toBigDecimal(payStat.get("balance_pay_money")));
        vo.setOrderCount(toInt(orderStat.get("order_count")));
        vo.setPayCount(toInt(orderStat.get("pay_count")));
        vo.setOrderNum(toInt(orderGoodsStat.get("order_num")));
        vo.setPayNum(toInt(orderGoodsStat.get("pay_num")));
        vo.setOrderMemberCount(toInt(orderStat.get("order_member_count")));
        vo.setPayMemberCount(toInt(orderStat.get("pay_member_count")));
        vo.setRefundNum(toInt(refundStat.get("refund_num")));
        vo.setRefundMoney(toBigDecimal(refundStat.get("refund_money")));
        vo.setRefundMemberCount(toInt(refundStat.get("refund_member_count")));
        return vo;
    }

    @Override
    public TrendDataVo getTrendData(TradeStatParam param) {
        Integer siteId = getSiteId();
        String startDate = param.getStartTime() != null ? param.getStartTime() : LocalDate.now().toString();
        String endDate = param.getEndTime() != null ? param.getEndTime() : LocalDate.now().toString();

        List<String> dateRange = coreTradeStatService.getDateRange(startDate, endDate);
        TrendDataVo vo = new TrendDataVo();
        vo.setDateList(dateRange);

        List<Integer> visitNumList = new ArrayList<>();
        List<BigDecimal> orderMoneyList = new ArrayList<>();
        List<Integer> payMemberCountList = new ArrayList<>();
        List<Integer> payNumList = new ArrayList<>();
        List<BigDecimal> visitOrderRateList = new ArrayList<>();
        List<BigDecimal> orderPayRateList = new ArrayList<>();
        List<BigDecimal> visitPayRateList = new ArrayList<>();

        for (String date : dateRange) {
            long dayStart = toTimestamp(date + " 00:00:00");
            long dayEnd = toTimestamp(date + " 23:59:59");

            // 访客统计
            MPJQueryWrapper<SysStat> vw = new MPJQueryWrapper<SysStat>()
                    .eq("site_id", siteId)
                    .between("date_time", dayStart, dayEnd);
            Map<String, Object> visitStat = coreTradeStatService.getVisitStat(vw);

            // 订单统计
            MPJQueryWrapper<MallOrder> ow = new MPJQueryWrapper<MallOrder>()
                    .eq("site_id", siteId)
                    .between("create_time", dayStart, dayEnd);
            Map<String, Object> orderStat = coreTradeStatService.getOrderStat(ow);

            // 订单商品统计
            MPJQueryWrapper<MallOrderGoods> gw = new MPJQueryWrapper<MallOrderGoods>()
                    .eq("o.site_id", siteId)
                    .between("o.create_time", dayStart, dayEnd);
            Map<String, Object> orderGoodsStat = coreTradeStatService.getOrderGoodsStat(gw);

            int visitNum = toInt(visitStat.get("visit_num"));
            int orderMemberCount = toInt(orderStat.get("order_member_count"));
            int payMemberCount = toInt(orderStat.get("pay_member_count"));
            int orderCount = toInt(orderStat.get("order_count"));
            int payCount = toInt(orderStat.get("pay_count"));
            int payNum = toInt(orderGoodsStat.get("pay_num"));
            BigDecimal orderMoney = toBigDecimal(orderStat.get("order_money"));

            // 计算转化率
            BigDecimal visitOrderRate = calcRate(orderMemberCount, visitNum);
            BigDecimal orderPayRate = calcRate(payMemberCount, orderMemberCount);
            BigDecimal visitPayRate = calcRate(payMemberCount, visitNum);

            visitNumList.add(visitNum);
            orderMoneyList.add(orderMoney);
            payMemberCountList.add(payMemberCount);
            payNumList.add(payNum);
            visitOrderRateList.add(visitOrderRate);
            orderPayRateList.add(orderPayRate);
            visitPayRateList.add(visitPayRate);
        }

        vo.setVisitNum(visitNumList);
        vo.setOrderMoney(orderMoneyList);
        vo.setPayMemberCount(payMemberCountList);
        vo.setPayNum(payNumList);
        vo.setVisitOrderRate(visitOrderRateList);
        vo.setOrderPayRate(orderPayRateList);
        vo.setVisitPayRate(visitPayRateList);
        return vo;
    }

    @Override
    public ConversionDataVo getConversionData(TradeStatParam param) {
        Integer siteId = getSiteId();
        long startTime = parseStartTime(param.getStartTime());
        long endTime = parseEndTime(param.getEndTime());

        // 访客统计
        MPJQueryWrapper<SysStat> vw = new MPJQueryWrapper<SysStat>()
                .eq("site_id", siteId)
                .between("date_time", startTime, endTime);
        Map<String, Object> visitStat = coreTradeStatService.getVisitStat(vw);

        // 订单统计
        MPJQueryWrapper<MallOrder> ow = new MPJQueryWrapper<MallOrder>()
                .eq("site_id", siteId)
                .between("create_time", startTime, endTime);
        Map<String, Object> orderStat = coreTradeStatService.getOrderStat(ow);

        // 订单商品统计
        MPJQueryWrapper<MallOrderGoods> gw = new MPJQueryWrapper<MallOrderGoods>()
                .eq("o.site_id", siteId)
                .between("o.create_time", startTime, endTime);
        Map<String, Object> orderGoodsStat = coreTradeStatService.getOrderGoodsStat(gw);

        int visitNum = toInt(visitStat.get("visit_num"));
        int orderMemberCount = toInt(orderStat.get("order_member_count"));
        int orderCount = toInt(orderStat.get("order_count"));
        int orderNum = toInt(orderGoodsStat.get("order_num"));
        BigDecimal creteOrderMoney = toBigDecimal(orderStat.get("crete_order_money"));
        int payMemberCount = toInt(orderStat.get("pay_member_count"));
        int payCount = toInt(orderStat.get("pay_count"));
        int payNum = toInt(orderGoodsStat.get("pay_num"));
        BigDecimal orderMoney = toBigDecimal(orderStat.get("order_money"));

        // 客单价
        BigDecimal memberUnitPrice = BigDecimal.ZERO;
        if (orderMoney.compareTo(BigDecimal.ZERO) > 0 && payMemberCount > 0) {
            memberUnitPrice = orderMoney.divide(BigDecimal.valueOf(payMemberCount), 2, RoundingMode.HALF_UP);
        }

        // 转化率
        BigDecimal visitOrderRate = calcRate(orderMemberCount, visitNum);
        BigDecimal orderPayRate = calcRate(payMemberCount, orderMemberCount);
        BigDecimal visitPayRate = calcRate(payMemberCount, visitNum);

        // 环比数据
        Map<String, BigDecimal> loopRates = getLoopRates(param);

        ConversionDataVo vo = new ConversionDataVo();
        vo.setVisitNum(visitNum);
        vo.setOrderMemberCount(orderMemberCount);
        vo.setOrderCount(orderCount);
        vo.setOrderNum(orderNum);
        vo.setCreteOrderMoney(creteOrderMoney);
        vo.setPayMemberCount(payMemberCount);
        vo.setPayCount(payCount);
        vo.setPayNum(payNum);
        vo.setOrderMoney(orderMoney);
        vo.setMemberUnitPrice(memberUnitPrice);
        vo.setVisitOrderRate(visitOrderRate);
        vo.setOrderPayRate(orderPayRate);
        vo.setVisitPayRate(visitPayRate);

        // 设置环比
        vo.setVisitNumLoopRate(loopRates.get("visit_num_loop_rate"));
        vo.setOrderMemberCountLoopRate(loopRates.get("order_member_count_loop_rate"));
        vo.setOrderCountLoopRate(loopRates.get("order_count_loop_rate"));
        vo.setOrderNumLoopRate(loopRates.get("order_num_loop_rate"));
        vo.setCreteOrderMoneyLoopRate(loopRates.get("crete_order_money_loop_rate"));
        vo.setPayMemberCountLoopRate(loopRates.get("pay_member_count_loop_rate"));
        vo.setPayCountLoopRate(loopRates.get("pay_count_loop_rate"));
        vo.setPayNumLoopRate(loopRates.get("pay_num_loop_rate"));
        vo.setOrderMoneyLoopRate(loopRates.get("order_money_loop_rate"));
        vo.setMemberUnitPriceLoopRate(loopRates.get("member_unit_price_loop_rate"));

        return vo;
    }

    @Override
    public List<Map<String, Object>> getChannelData(TradeStatParam param) {
        Integer siteId = getSiteId();
        long startTime = parseStartTime(param.getStartTime());
        long endTime = parseEndTime(param.getEndTime());
        var warpper = new MPJQueryWrapper<Pay>()
                .eq("o.site_id", siteId)
                .eq("p.status", PayStatusEnum.STATUS_FINISH.getCode())
                .between("p.create_time", startTime, endTime);
        return coreTradeStatService.getChannelData(warpper);
    }

    @Resource
    private MemberLevelMapper memberLevelMapper;

    @Resource
    private MallOrderMapper orderMapper;

    @Resource
    private SiteShopMemberMapper shopMemberMapper;

    @Resource
    private MemberMapper memberMapper;

    /**
     * 获取成交客户构成（按会员等级分组）
     *
     * @param param 开始时间 结束时间
     * @return 各等级统计数据列表
     */
    @Override
    public List<DealMemberDataVo> getDealMemberData(TradeStatParam param) {
        Integer siteId = getSiteId();
        long startTime = parseStartTime(param.getStartTime());
        long endTime = parseEndTime(param.getEndTime());
        // 1. 查询所有启用的会员等级（status=1）
        List<MemberLevel> levels = memberLevelMapper.selectList(
                new LambdaQueryWrapper<MemberLevel>()
                        .eq(MemberLevel::getStatus, 1)
                        .orderByAsc(MemberLevel::getLevelId)
        );
        if (levels.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 构建子查询：已完成订单按会员汇总（goods_money, order_money）
        // 模拟 PHP 的 buildSql，使用 QueryWrapper 生成 SQL 字符串，或用 MPJ 联查
        // 这里采用两步查询：先查出有订单的会员ID及其汇总数据，再与会员表关联

        // 2.1 查询已完成的订单汇总
        MPJQueryWrapper<MallOrder> orderWrapper = new MPJQueryWrapper<>();
        orderWrapper.eq("site_id", siteId)
                .between("create_time", startTime, endTime)
                .eq("status", OrderStatusEnum.FINISH.getStatus().toString())
                .groupBy("member_id")
                .select("member_id",
                        "SUM(goods_money) AS goods_money",
                        "SUM(order_money) AS order_money");
        List<Map<String, Object>> orderSumList = orderMapper.selectMaps(orderWrapper);

        Map<Integer, OrderSum> orderSumMap = orderSumList.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row.get("member_id")).intValue(),
                        row -> new OrderSum(
                                toBigDecimal(row.get("goods_money")),
                                toBigDecimal(row.get("order_money"))
                        )
                ));

        // 3. 查询站点下的会员（有等级的）以及关联订单汇总
        // 使用 MPJQueryWrapper 或左连接子查询结果
        // 由于子查询结果已在内存中，直接查询会员并按等级分组统计
        List<SiteShopMember> shopMembers = shopMemberMapper.selectList(
                new LambdaQueryWrapper<SiteShopMember>()
                        .eq(SiteShopMember::getSiteId, siteId)
                        .isNotNull(SiteShopMember::getMemberId)
        );
        if (shopMembers.isEmpty()) {
            // 返回空列表，每个等级数据为0
            return levels.stream()
                    .map(level -> createEmptyLevelData(level.getLevelName()))
                    .collect(Collectors.toList());
        }

        Set<Integer> memberIds = shopMembers.stream()
                .map(SiteShopMember::getMemberId)
                .collect(Collectors.toSet());

        // 查询会员的等级
        List<Member> members = memberMapper.selectList(
                new LambdaQueryWrapper<Member>()
                        .in(Member::getMemberId, memberIds)
                        .gt(Member::getMemberLevel, 0)
                        .select(Member::getMemberId, Member::getMemberLevel)
        );
        if (members.isEmpty()) {
            return levels.stream()
                    .map(level -> createEmptyLevelData(level.getLevelName()))
                    .collect(Collectors.toList());
        }

        // 4. 按等级分组统计
        Map<Integer, LevelStat> levelStatMap = new HashMap<>();
        for (Member member : members) {
            Integer levelId = member.getMemberLevel();
            LevelStat stat = levelStatMap.computeIfAbsent(levelId, k -> new LevelStat());
            stat.memberCount++;
            Integer memberId = member.getMemberId();
            OrderSum orderSum = orderSumMap.get(memberId);
            if (orderSum != null) {
                stat.payMemberCount++;
                stat.goodsMoney = stat.goodsMoney.add(orderSum.goodsMoney);
                stat.orderMoney = stat.orderMoney.add(orderSum.orderMoney);
            }
        }

        // 5. 组装返回数据（按等级顺序）
        List<DealMemberDataVo> result = new ArrayList<>();
        for (MemberLevel level : levels) {
            LevelStat stat = levelStatMap.getOrDefault(level.getLevelId(), new LevelStat());
            DealMemberDataVo vo = new DealMemberDataVo();
            vo.setLevelName(level.getLevelName());
            vo.setMemberCount(stat.memberCount);
            vo.setPayMemberCount(stat.payMemberCount);
            vo.setGoodsMoney(stat.goodsMoney);
            vo.setOrderMoney(stat.orderMoney);
            result.add(vo);
        }
        return result;
    }

    // 内部辅助类
    private static class OrderSum {
        BigDecimal goodsMoney;
        BigDecimal orderMoney;

        OrderSum(BigDecimal goodsMoney, BigDecimal orderMoney) {
            this.goodsMoney = goodsMoney;
            this.orderMoney = orderMoney;
        }
    }

    private static class LevelStat {
        int memberCount = 0;
        int payMemberCount = 0;
        BigDecimal goodsMoney = BigDecimal.ZERO;
        BigDecimal orderMoney = BigDecimal.ZERO;
    }

    private DealMemberDataVo createEmptyLevelData(String levelName) {
        DealMemberDataVo vo = new DealMemberDataVo();
        vo.setLevelName(levelName);
        vo.setMemberCount(0);
        vo.setPayMemberCount(0);
        vo.setGoodsMoney(BigDecimal.ZERO);
        vo.setOrderMoney(BigDecimal.ZERO);
        return vo;
    }

    // ===================== 辅助方法 =====================

    private long toTimestamp(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(dateTimeStr, formatter);
        return ldt.toEpochSecond(ZoneOffset.ofHours(8));
    }

    private long parseStartTime(String time) {
        if (time == null || time.isEmpty()) {
            return toTimestamp(LocalDate.now() + " 00:00:00");
        }
        return toTimestamp(time + " 00:00:00");
    }

    private long parseEndTime(String time) {
        if (time == null || time.isEmpty()) {
            return toTimestamp(LocalDate.now() + " 23:59:59");
        }
        return toTimestamp(time + " 23:59:59");
    }

    private int toInt(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Number) return ((Number) obj).intValue();
        return Integer.parseInt(obj.toString());
    }

    private BigDecimal toBigDecimal(Object obj) {
        if (obj == null) return BigDecimal.ZERO;
        if (obj instanceof BigDecimal) return (BigDecimal) obj;
        if (obj instanceof Number) return new BigDecimal(obj.toString());
        try {
            return new BigDecimal(obj.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calcRate(int num, int total) {
        if (num <= 0 || total <= 0) return BigDecimal.ZERO;
        double rate = (double) num / total * 100;
        BigDecimal result = BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP);
        // 如果整数部分不变，去掉小数
        if (result.stripTrailingZeros().scale() == 0) {
            return result.setScale(0, RoundingMode.UNNECESSARY);
        }
        return result;
    }

    private Map<String, BigDecimal> getLoopRates(TradeStatParam param) {
        // 计算环比数据：当前周期与上一周期（相同天数）对比
        String startDate = param.getStartTime() != null ? param.getStartTime() : LocalDate.now().toString();
        String endDate = param.getEndTime() != null ? param.getEndTime() : LocalDate.now().toString();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        long days = ChronoUnit.DAYS.between(start, end) + 1;

        String prevStart = LocalDate.parse(startDate).minusDays(days).toString();
        String prevEnd = LocalDate.parse(endDate).minusDays(days).toString();

        long currentStart = toTimestamp(startDate + " 00:00:00");
        long currentEnd = toTimestamp(endDate + " 23:59:59");
        long prevStartTs = toTimestamp(prevStart + " 00:00:00");
        long prevEndTs = toTimestamp(prevEnd + " 23:59:59");

        Integer siteId = getSiteId();

        // 访客统计
        MPJQueryWrapper<SysStat> vw = new MPJQueryWrapper<SysStat>()
                .eq("site_id", siteId)
                .between("date_time", currentStart, currentEnd);
        Map<String, Object> currentVisit = coreTradeStatService.getVisitStat(vw);
        // 订单统计
        MPJQueryWrapper<MallOrder> ow = new MPJQueryWrapper<MallOrder>()
                .eq("site_id", siteId)
                .between("create_time", currentStart, currentEnd);
        Map<String, Object> currentOrder = coreTradeStatService.getOrderStat(ow);
        // 订单商品统计
        MPJQueryWrapper<MallOrderGoods> gw = new MPJQueryWrapper<MallOrderGoods>()
                .eq("o.site_id", siteId)
                .between("o.create_time", currentStart, currentEnd);
        Map<String, Object> currentGoods = coreTradeStatService.getOrderGoodsStat(gw);

        // 访客统计
        MPJQueryWrapper<SysStat> pvw = new MPJQueryWrapper<SysStat>()
                .eq("site_id", siteId)
                .between("date_time", prevStartTs, prevEndTs);
        Map<String, Object> prevVisit = coreTradeStatService.getVisitStat(pvw);
        // 订单统计
        MPJQueryWrapper<MallOrder> pow = new MPJQueryWrapper<MallOrder>()
                .eq("site_id", siteId)
                .between("create_time", prevStartTs, prevEndTs);
        Map<String, Object> prevOrder = coreTradeStatService.getOrderStat(pow);
        // 订单商品统计
        MPJQueryWrapper<MallOrderGoods> pgw = new MPJQueryWrapper<MallOrderGoods>()
                .eq("o.site_id", siteId)
                .between("o.create_time", prevStartTs, prevEndTs);
        Map<String, Object> prevGoods = coreTradeStatService.getOrderGoodsStat(pgw);

        // 计算当前客单价
        BigDecimal currentOrderMoney = toBigDecimal(currentOrder.get("order_money"));
        int currentPayMember = toInt(currentOrder.get("pay_member_count"));
        BigDecimal currentUnit = currentPayMember > 0 ? currentOrderMoney.divide(BigDecimal.valueOf(currentPayMember), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        BigDecimal prevOrderMoney = toBigDecimal(prevOrder.get("order_money"));
        int prevPayMember = toInt(prevOrder.get("pay_member_count"));
        BigDecimal prevUnit = prevPayMember > 0 ? prevOrderMoney.divide(BigDecimal.valueOf(prevPayMember), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        Map<String, BigDecimal> rates = new LinkedHashMap<>();
        rates.put("visit_num_loop_rate", calcLoopRate(toInt(currentVisit.get("visit_num")), toInt(prevVisit.get("visit_num"))));
        rates.put("order_member_count_loop_rate", calcLoopRate(toInt(currentOrder.get("order_member_count")), toInt(prevOrder.get("order_member_count"))));
        rates.put("order_count_loop_rate", calcLoopRate(toInt(currentOrder.get("order_count")), toInt(prevOrder.get("order_count"))));
        rates.put("order_num_loop_rate", calcLoopRate(toInt(currentGoods.get("order_num")), toInt(prevGoods.get("order_num"))));
        rates.put("crete_order_money_loop_rate", calcLoopRate(toBigDecimal(currentOrder.get("crete_order_money")), toBigDecimal(prevOrder.get("crete_order_money"))));
        rates.put("pay_member_count_loop_rate", calcLoopRate(toInt(currentOrder.get("pay_member_count")), toInt(prevOrder.get("pay_member_count"))));
        rates.put("pay_count_loop_rate", calcLoopRate(toInt(currentOrder.get("pay_count")), toInt(prevOrder.get("pay_count"))));
        rates.put("pay_num_loop_rate", calcLoopRate(toInt(currentGoods.get("pay_num")), toInt(prevGoods.get("pay_num"))));
        rates.put("order_money_loop_rate", calcLoopRate(toBigDecimal(currentOrder.get("order_money")), toBigDecimal(prevOrder.get("order_money"))));
        rates.put("member_unit_price_loop_rate", calcLoopRate(currentUnit, prevUnit));
        return rates;
    }

    private BigDecimal calcLoopRate(BigDecimal current, BigDecimal prev) {
        if (prev.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        BigDecimal rate = current.subtract(prev).divide(prev, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
        if (rate.stripTrailingZeros().scale() == 0) {
            return rate.setScale(0, RoundingMode.UNNECESSARY);
        }
        return rate;
    }

    private BigDecimal calcLoopRate(int current, int prev) {
        return calcLoopRate(BigDecimal.valueOf(current), BigDecimal.valueOf(prev));
    }
}