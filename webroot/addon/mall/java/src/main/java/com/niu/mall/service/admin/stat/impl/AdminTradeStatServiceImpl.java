package com.niu.mall.service.admin.stat.impl;

import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.stat.SysStat;
import com.niu.core.enums.pay.PayStatusEnum;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.enums.order.OrderRefundStatusEnum;
import com.niu.mall.service.admin.stat.IAdminTradeStatService;
import com.niu.mall.service.core.stat.ICoreTradeStatService;
import com.niu.mall.service.core.stat.param.TradeStatParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AdminTradeStatServiceImpl implements IAdminTradeStatService {

    @Resource
    private ICoreTradeStatService coreTradeStatService;

    /**
     * 获取基础统计数据
     *
     * @param param 包含 start_time, end_time（可选）
     * @return 统计结果 Map
     */
    @Override
    public Map<String, Object> getBasicData(TradeStatParam param) {
        long startTime = getStartTimestamp(param.getStartTime());
        long endTime = getEndTimestamp(param.getEndTime());

        // 订单统计
        MPJQueryWrapper<MallOrder> ow = new MPJQueryWrapper<MallOrder>().between("create_time", startTime, endTime);
        Map<String, Object> orderStat = coreTradeStatService.getOrderStat(ow);

        // 订单商品统计
        MPJQueryWrapper<MallOrderGoods> gw = new MPJQueryWrapper<MallOrderGoods>().between("o.create_time", startTime, endTime);
        Map<String, Object> orderGoodsStat = coreTradeStatService.getOrderGoodsStat(gw);

        // 退款统计
        MPJQueryWrapper<MallOrderRefund> rw = new MPJQueryWrapper<MallOrderRefund>().between("r.create_time", startTime, endTime)
                .eq("r.status", OrderRefundStatusEnum.FINISH.getStatus());
        Map<String, Object> refundStat = coreTradeStatService.getRefundStat(rw);

        // 余额支付统计
        MPJQueryWrapper<Pay> pw = new MPJQueryWrapper<Pay>().between("pay_time", startTime, endTime)
                .eq("type", "balancepay")
                .eq("status", PayStatusEnum.STATUS_FINISH.getCode());
        Map<String, Object> payStat = coreTradeStatService.getPayStat(pw);

        Map<String, Object> data = new HashMap<>();
        data.put("sale_money", orderStat.getOrDefault("sale_money", BigDecimal.ZERO));
        data.put("order_money", orderStat.getOrDefault("order_money", BigDecimal.ZERO));
        data.put("balance_pay_money", payStat.getOrDefault("balance_pay_money", BigDecimal.ZERO));
        data.put("order_count", orderStat.getOrDefault("order_count", 0));
        data.put("pay_count", orderStat.getOrDefault("pay_count", 0));
        data.put("order_num", orderGoodsStat.getOrDefault("order_num", 0));
        data.put("pay_num", orderGoodsStat.getOrDefault("pay_num", 0));
        data.put("order_member_count", orderStat.getOrDefault("order_member_count", 0));
        data.put("pay_member_count", orderStat.getOrDefault("pay_member_count", 0));
        data.put("refund_num", refundStat.getOrDefault("refund_num", 0));
        data.put("refund_money", refundStat.getOrDefault("refund_money", BigDecimal.ZERO));
        data.put("refund_member_count", refundStat.getOrDefault("refund_member_count", 0));
        return data;
    }


    /**
     * 获取趋势统计数据（按天）
     *
     * @param param 包含 start_time, end_time（可选）
     * @return 趋势数据
     */
    @Override
    public Map<String, Object> getTrendData(TradeStatParam param) {
        String startDate = param.getStartTime() != null ? param.getStartTime() : LocalDate.now().toString();
        String endDate = param.getEndTime() != null ? param.getEndTime() : LocalDate.now().toString();

        List<String> dateRange = coreTradeStatService.getDateRange(startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("date_list", dateRange);
        result.put("visit_num", new ArrayList<>());
        result.put("order_money", new ArrayList<>());
        result.put("pay_member_count", new ArrayList<>());
        result.put("pay_num", new ArrayList<>());
        result.put("visit_order_rate", new ArrayList<>());
        result.put("order_pay_rate", new ArrayList<>());
        result.put("visit_pay_rate", new ArrayList<>());

        for (String date : dateRange) {
            long dayStart = toTimestamp(date + " 00:00:00");
            long dayEnd = toTimestamp(date + " 23:59:59");
            List<Long> dayRange = Arrays.asList(dayStart, dayEnd);

            // 访客统计
            MPJQueryWrapper<SysStat> vw = new MPJQueryWrapper<SysStat>().between("date_time", dayStart, dayEnd);
            Map<String, Object> visitStat = coreTradeStatService.getVisitStat(vw);
            long visitNum = ((Number) visitStat.getOrDefault("visit_num", 0)).longValue();

            // 订单统计
            MPJQueryWrapper<MallOrder> ow = new MPJQueryWrapper<MallOrder>().between("create_time", dayStart, dayEnd);
            Map<String, Object> orderStat = coreTradeStatService.getOrderStat(ow);
            BigDecimal orderMoney = (BigDecimal) orderStat.getOrDefault("order_money", BigDecimal.ZERO);
            long payMemberCount = ((Number) orderStat.getOrDefault("pay_member_count", 0)).longValue();
            long orderMemberCount = ((Number) orderStat.getOrDefault("order_member_count", 0)).longValue();

            // 订单商品统计
            MPJQueryWrapper<MallOrderGoods> gw = new MPJQueryWrapper<MallOrderGoods>().between("o.create_time", dayStart, dayEnd);
            Map<String, Object> goodsStat = coreTradeStatService.getOrderGoodsStat(gw);
            long payNum = ((Number) goodsStat.getOrDefault("pay_num", 0)).longValue();

            // 转化率计算
            Number visitOrderRate = coreTradeStatService.calcRate(orderMemberCount, visitNum);
            Number orderPayRate = coreTradeStatService.calcRate(payMemberCount, orderMemberCount);
            Number visitPayRate = coreTradeStatService.calcRate(payMemberCount, visitNum);

            // 添加到结果
            ((List<Long>) result.get("visit_num")).add(visitNum);
            ((List<BigDecimal>) result.get("order_money")).add(orderMoney);
            ((List<Long>) result.get("pay_member_count")).add(payMemberCount);
            ((List<Long>) result.get("pay_num")).add(payNum);
            ((List<Number>) result.get("visit_order_rate")).add(visitOrderRate);
            ((List<Number>) result.get("order_pay_rate")).add(orderPayRate);
            ((List<Number>) result.get("visit_pay_rate")).add(visitPayRate);
        }
        return result;
    }

    /**
     * 获取交易转化数据（含环比）
     *
     * @param param 包含 start_time, end_time（可选）
     * @return 转化数据
     */
    @Override
    public Map<String, Object> getConversionData(TradeStatParam param) {
        long startTime = getStartTimestamp(param.getStartTime());
        long endTime = getEndTimestamp(param.getEndTime());

        // 访客统计
        MPJQueryWrapper<SysStat> vw = new MPJQueryWrapper<SysStat>().between("date_time", startTime, endTime);
        Map<String, Object> visitStat = coreTradeStatService.getVisitStat(vw);
        long visitNum = ((Number) visitStat.getOrDefault("visit_num", 0)).longValue();

        // 订单统计
        MPJQueryWrapper<MallOrder> ow = new MPJQueryWrapper<MallOrder>().between("create_time", startTime, endTime);
        Map<String, Object> orderStat = coreTradeStatService.getOrderStat(ow);
        long orderMemberCount = ((Number) orderStat.getOrDefault("order_member_count", 0)).longValue();
        long orderCount = ((Number) orderStat.getOrDefault("order_count", 0)).longValue();
        BigDecimal creteOrderMoney = (BigDecimal) orderStat.getOrDefault("crete_order_money", BigDecimal.ZERO);
        long payMemberCount = ((Number) orderStat.getOrDefault("pay_member_count", 0)).longValue();
        long payCount = ((Number) orderStat.getOrDefault("pay_count", 0)).longValue();
        BigDecimal orderMoney = (BigDecimal) orderStat.getOrDefault("order_money", BigDecimal.ZERO);

        // 订单商品统计
        MPJQueryWrapper<MallOrderGoods> gw = new MPJQueryWrapper<MallOrderGoods>().between("o.create_time", startTime, endTime);
        Map<String, Object> goodsStat = coreTradeStatService.getOrderGoodsStat(gw);
        long orderNum = ((Number) goodsStat.getOrDefault("order_num", 0)).longValue();
        long payNum = ((Number) goodsStat.getOrDefault("pay_num", 0)).longValue();

        Map<String, Object> data = new HashMap<>();
        data.put("visit_num", visitNum);
        data.put("order_member_count", orderMemberCount);
        data.put("order_count", orderCount);
        data.put("order_num", orderNum);
        data.put("crete_order_money", creteOrderMoney);
        data.put("pay_member_count", payMemberCount);
        data.put("pay_count", payCount);
        data.put("pay_num", payNum);
        data.put("order_money", orderMoney);

        // 客单价
        Number memberUnitPrice = 0;
        if (orderMoney.compareTo(BigDecimal.ZERO) > 0 && payMemberCount > 0) {
            memberUnitPrice = coreTradeStatService.calcRate(orderMoney, payMemberCount);
        }
        data.put("member_unit_price", memberUnitPrice);

        // 转化率
        data.put("visit_order_rate", coreTradeStatService.calcRate(orderMemberCount, visitNum));
        data.put("order_pay_rate", coreTradeStatService.calcRate(payMemberCount, orderMemberCount));
        data.put("visit_pay_rate", coreTradeStatService.calcRate(payMemberCount, visitNum));

        // 环比数据
        Map<String, Object> loopRateData = getLoopRateData(param);
        data.putAll(loopRateData);
        return data;
    }

    /**
     * 计算环比数据（当前周期 vs 上一相同长度周期）
     *
     * @param param 包含 start_time, end_time（可选）
     * @return 环比数据 Map
     */
    private Map<String, Object> getLoopRateData(TradeStatParam param) {
        String startDate = param.getStartTime() != null ? param.getStartTime() : LocalDate.now().toString();
        String endDate = param.getEndTime() != null ? param.getEndTime() : LocalDate.now().toString();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        long days = ChronoUnit.DAYS.between(start, end) + 1;

        LocalDate prevStart = start.minusDays(days);
        LocalDate prevEnd = end.minusDays(days);

        long startBegin = toTimestamp(startDate + " 00:00:00");
        long startEnd = toTimestamp(endDate + " 23:59:59");
        long prevBegin = toTimestamp(prevStart + " 00:00:00");
        long prevOver = toTimestamp(prevEnd + " 23:59:59");

        Map<String, Map<String, Object>> currentStat = getOneDayStat(startBegin, startEnd);
        Map<String, Map<String, Object>> previousStat = getOneDayStat(prevBegin, prevOver);

        // 定义需要计算环比的字段映射
        Map<String, String[]> loopMap = new LinkedHashMap<>();
        loopMap.put("visit_num", new String[]{"visit", "visit_num"});
        loopMap.put("order_member_count", new String[]{"order", "order_member_count"});
        loopMap.put("order_count", new String[]{"order", "order_count"});
        loopMap.put("order_num", new String[]{"goods", "order_num"});
        loopMap.put("crete_order_money", new String[]{"order", "crete_order_money"});
        loopMap.put("pay_member_count", new String[]{"order", "pay_member_count"});
        loopMap.put("pay_count", new String[]{"order", "pay_count"});
        loopMap.put("pay_num", new String[]{"goods", "pay_num"});
        loopMap.put("order_money", new String[]{"order", "order_money"});

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, String[]> entry : loopMap.entrySet()) {
            String key = entry.getKey();
            String[] path = entry.getValue();
            Number current = (Number) currentStat.getOrDefault(path[0], new HashMap<>()).getOrDefault(path[1], 0);
            Number previous = (Number) previousStat.getOrDefault(path[0], new HashMap<>()).getOrDefault(path[1], 0);
            Number loopRate = coreTradeStatService.calcLoopRate(current, previous);
            result.put(key + "_loop_rate", loopRate);
        }

        // 客单价环比
        Number currentUnit = coreTradeStatService.calcRate(
                (Number) currentStat.getOrDefault("order", new HashMap<>()).getOrDefault("order_money", 0),
                (Number) currentStat.getOrDefault("order", new HashMap<>()).getOrDefault("pay_member_count", 0)
        );
        Number previousUnit = coreTradeStatService.calcRate(
                (Number) previousStat.getOrDefault("order", new HashMap<>()).getOrDefault("order_money", 0),
                (Number) previousStat.getOrDefault("order", new HashMap<>()).getOrDefault("pay_member_count", 0)
        );
        Number unitLoopRate = coreTradeStatService.calcLoopRate(currentUnit, previousUnit);
        result.put("member_unit_price_loop_rate", unitLoopRate);

        return result;
    }

    /**
     * 获取单日统计数据（访客、订单、商品）
     *
     * @param begin 开始时间戳（秒）
     * @param end   结束时间戳（秒）
     * @return Map 包含 visit, order, goods 三个子Map
     */
    private Map<String, Map<String, Object>> getOneDayStat(long begin, long end) {
        MPJQueryWrapper<SysStat> vw = new MPJQueryWrapper<SysStat>().between("date_time", begin, end);
        Map<String, Object> visitStat = coreTradeStatService.getVisitStat(vw);

        MPJQueryWrapper<MallOrder> ow = new MPJQueryWrapper<MallOrder>().between("create_time", begin, end);
        Map<String, Object> orderStat = coreTradeStatService.getOrderStat(ow);

        MPJQueryWrapper<MallOrderGoods> gw = new MPJQueryWrapper<MallOrderGoods>().between("o.create_time", begin, end);
        Map<String, Object> goodsStat = coreTradeStatService.getOrderGoodsStat(gw);

        Map<String, Map<String, Object>> result = new HashMap<>();
        result.put("visit", visitStat);
        result.put("order", orderStat);
        result.put("goods", goodsStat);
        return result;
    }

    /**
     * 付款金额渠道统计
     *
     * @param param 包含 start_time, end_time（可选）
     * @return 渠道数据列表
     */
    @Override
    public List<Map<String, Object>> getChannelData(TradeStatParam param) {
        long startTime = getStartTimestamp(param.getStartTime());
        long endTime = getEndTimestamp(param.getEndTime());

        var warpper = new MPJQueryWrapper<Pay>()
                .eq("p.status", PayStatusEnum.STATUS_FINISH.getCode())
                .between("p.create_time", startTime, endTime);

        return coreTradeStatService.getChannelData(warpper);
    }


    /**
     * 成交客户构成（按会员等级）
     *
     * @param param 包含 start_time, end_time（可选）
     * @return 等级统计数据列表
     */
    @Override
    public List<Map<String, Object>> getDurationData(TradeStatParam param) {
        long startTime = getStartTimestamp(param.getStartTime());
        long endTime = getEndTimestamp(param.getEndTime());

        var warpper = new MPJQueryWrapper<Member>()
                .gt("m.member_level", 0)
                .between("o.create_time", startTime, endTime);

        return coreTradeStatService.getDurationData(warpper);
    }

    // ===================== 辅助方法 =====================
    private long getStartTimestamp(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return toTimestamp(LocalDate.now() + " 00:00:00");
        }
        return toTimestamp(dateStr + " 00:00:00");
    }

    private long getEndTimestamp(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return toTimestamp(LocalDate.now() + " 23:59:59");
        }
        return toTimestamp(dateStr + " 23:59:59");
    }

    private long toTimestamp(String dateTimeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(dateTimeStr, formatter);
        return ldt.toEpochSecond(ZoneOffset.ofHours(8));
    }
}
