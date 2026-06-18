package com.niu.mall.service.core.stat.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.stat.SysStat;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.stat.SysStatMapper;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.mall.service.core.stat.ICoreTradeStatService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 交易统计公共服务层
 */
@Slf4j
@Service
public class CoreTradeStatServiceImpl implements ICoreTradeStatService {

    @Resource
    private MallOrderMapper orderMapper;
    @Resource
    private PayMapper payMapper;
    @Resource
    private MallOrderGoodsMapper orderGoodsMapper;
    @Resource
    private MallOrderRefundMapper orderRefundMapper;
    @Resource
    private MemberMapper memberMapper;
    @Resource
    private MemberLevelMapper memberLevelMapper;
    @Resource
    private SysStatMapper sysStatMapper;

    /**
     * 付款金额渠道
     * Pay.setAlias("p")
     *
     * @param params 查询条件（例如 site_id, 时间范围等）
     * @return List<Map < String, Object>> 渠道名称和支付订单数
     */
    public List<Map<String, Object>> getChannelData(MPJQueryWrapper<Pay> params) {
        // 获取所有渠道类型
        Map<String, String> channelDict = ChannelEnum.getMap();

        // 查询支付表，按渠道分组统计订单数
        MPJQueryWrapper<Pay> wrapper;
        if (params == null) wrapper = new MPJQueryWrapper<>();
        else wrapper = params;
        wrapper.select("p.channel", "COUNT(*) as count")
                .setAlias("p")
                .leftJoin("?_mall_order o ON p.trade_id = o.group_id".replace("?_", GlobalConfig.tablePrefix))
                .groupBy("p.channel");
        List<Map<String, Object>> payDataList = payMapper.selectMaps(wrapper);

        Map<String, Long> channelCountMap = payDataList.stream()
                .collect(Collectors.toMap(
                        m -> (String) m.get("channel"),
                        m -> ((Number) m.get("count")).longValue()
                ));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : channelDict.entrySet()) {
            String key = entry.getKey();
            String name = entry.getValue();
            Map<String, Object> item = new HashMap<>();
            item.put("channel_name", name);
            item.put("count", channelCountMap.getOrDefault(key, 0L));
            result.add(item);
        }
        return result;
    }

    /**
     * 成交客户构成（按会员等级统计）
     * Member.setAlias("m")
     * leftJoin ?_mall_order o
     *
     * @param params 查询条件
     * @return 每个等级的数据列表
     */
    public List<Map<String, Object>> getDurationData(MPJQueryWrapper<Member> params) {
        // 获取所有启用的会员等级
        List<MemberLevel> levels = memberLevelMapper.selectList(
                new LambdaQueryWrapper<MemberLevel>().eq(MemberLevel::getStatus, 1)
                        .select(MemberLevel::getLevelId, MemberLevel::getLevelName)
        );
        if (levels.isEmpty()) return new ArrayList<>();

        List<Integer> levelIds = levels.stream()
                .map(MemberLevel::getLevelId)
                .collect(Collectors.toList());

        MPJQueryWrapper<Member> wrapper;
        if (params == null) wrapper = new MPJQueryWrapper<>();
        else wrapper = params;
        var sql = "?_mall_order o ON m.member_id = o.member_id AND o.status > " + OrderStatusEnum.WAIT_PAY.getStatus() + " ";
        wrapper.select("m.member_level",
                        "COUNT(DISTINCT m.member_id) AS member_count",
                        "COUNT(DISTINCT o.member_id) AS pay_member_count",
                        "IFNULL(SUM(o.goods_money), 0) AS goods_money",
                        "IFNULL(SUM(o.order_money), 0) AS order_money")
                .setAlias("m")
                .leftJoin(sql.replace("?_", GlobalConfig.tablePrefix))
                .in("m.member_level", levelIds)
                .groupBy("m.member_level");

        List<Map<String, Object>> durationData = memberMapper.selectMaps(wrapper);

        // 转换为 level_id -> 统计数据的映射
        Map<Integer, Map<String, Object>> dataMap = new HashMap<>();
        for (Map<String, Object> row : durationData) {
            Integer levelId = (Integer) row.get("member_level");
            dataMap.put(levelId, row);
        }

        // 按等级顺序组装结果
        List<Map<String, Object>> result = new ArrayList<>();
        for (MemberLevel level : levels) {
            Map<String, Object> row = dataMap.getOrDefault(level.getLevelId(), new HashMap<>());
            Map<String, Object> item = new HashMap<>();
            item.put("level_name", level.getLevelName());
            item.put("member_count", row.getOrDefault("member_count", 0));
            item.put("pay_member_count", row.getOrDefault("pay_member_count", 0));
            item.put("goods_money", row.getOrDefault("goods_money", BigDecimal.ZERO));
            item.put("order_money", row.getOrDefault("order_money", BigDecimal.ZERO));
            result.add(item);
        }
        return result;
    }

    /**
     * 获取订单统计信息
     *
     * @param params 查询条件
     * @return Map 包含订单数量、支付订单数、会员数、销售额等
     */
    public Map<String, Object> getOrderStat(MPJQueryWrapper<MallOrder> params) {
        MPJQueryWrapper<MallOrder> wrapper;
        if (params == null) wrapper = new MPJQueryWrapper<>();
        else wrapper = params;
        wrapper.select(
                "COUNT(*) as order_count",
                "COUNT(IF(status > " + OrderStatusEnum.WAIT_PAY.getStatus() + ", 1, NULL)) as pay_count",
                "COUNT(DISTINCT member_id) as order_member_count",
                "COUNT(DISTINCT IF(status > " + OrderStatusEnum.WAIT_PAY.getStatus() + ", member_id, NULL)) as pay_member_count",
                "IFNULL(SUM(IF(status > " + OrderStatusEnum.WAIT_PAY.getStatus() + ", goods_money, 0)), 0) as sale_money",
                "IFNULL(SUM(IF(status > " + OrderStatusEnum.WAIT_PAY.getStatus() + ", order_money, 0)), 0) as order_money",
                "IFNULL(SUM(order_money), 0) as crete_order_money");
        Map<String, Object> result = orderMapper.selectMaps(wrapper).stream().findFirst().orElse(new HashMap<>());
        // 默认值处理
        result.putIfAbsent("order_count", 0);
        result.putIfAbsent("pay_count", 0);
        result.putIfAbsent("order_member_count", 0);
        result.putIfAbsent("pay_member_count", 0);
        result.putIfAbsent("sale_money", BigDecimal.ZERO);
        result.putIfAbsent("order_money", BigDecimal.ZERO);
        result.putIfAbsent("crete_order_money", BigDecimal.ZERO);
        return result;
    }

    /**
     * 获取支付统计信息（总支付金额）
     *
     * @param params 查询条件
     * @return Map 包含 balance_pay_money
     */
    public Map<String, Object> getPayStat(MPJQueryWrapper<Pay> params) {
        MPJQueryWrapper<Pay> wrapper;
        if (params == null) wrapper = new MPJQueryWrapper<>();
        else wrapper = params;
        wrapper.select("IFNULL(SUM(money), 0) as balance_pay_money");
        Map<String, Object> result = payMapper.selectMaps(wrapper).stream().findFirst().orElse(new HashMap<>());
        result.putIfAbsent("balance_pay_money", BigDecimal.ZERO);
        return result;
    }

    /**
     * 获取站点支付统计信息（联表订单）
     * Pay.setAlias("p")
     * leftJoin ?_mall_order o
     *
     * @param params 查询条件
     * @return Map 包含 balance_pay_money
     */
    public Map<String, Object> getSitePayStat(MPJQueryWrapper<Pay> params) {
        MPJQueryWrapper<Pay> wrapper;
        if (params == null) wrapper = new MPJQueryWrapper<>();
        else wrapper = params;
        var sql = "?_mall_order o ON p.out_trade_no = o.out_trade_no AND o.status > " + OrderStatusEnum.FINISH.getStatus() + " ";
        wrapper.select(
                        "IFNULL(SUM(o.order_money), 0) as balance_pay_money")
                .setAlias("p")
                .leftJoin(sql.replace("?_", GlobalConfig.tablePrefix));
        Map<String, Object> result = payMapper.selectMaps(wrapper).stream().findFirst().orElse(new HashMap<>());
        result.putIfAbsent("balance_pay_money", BigDecimal.ZERO);
        return result;
    }

    /**
     * 获取订单商品统计信息（商品数量、支付数量）
     * MallOrderGoods.setAlias("og")
     * leftJoin ?_mall_order o
     *
     * @param params 查询条件
     * @return Map 包含 order_num, pay_num
     */
    public Map<String, Object> getOrderGoodsStat(MPJQueryWrapper<MallOrderGoods> params) {
        MPJQueryWrapper<MallOrderGoods> wrapper;
        if (params == null) wrapper = new MPJQueryWrapper<>();
        else wrapper = params;
        wrapper.select(
                        "IFNULL(SUM(og.num), 0) AS order_num",
                        "IFNULL(SUM(IF(o.status > 0, og.num, 0)), 0) AS pay_num")
                .setAlias("og")
                .leftJoin("?_mall_order o ON og.order_id = o.order_id ".replace("?_", GlobalConfig.tablePrefix));
        Map<String, Object> result = orderGoodsMapper.selectMaps(wrapper).stream().findFirst().orElse(new HashMap<>());
        // 默认值处理
        result.putIfAbsent("order_num", 0);
        result.putIfAbsent("pay_num", 0);
        return result;
    }

    /**
     * 获取退款统计信息
     * MallOrderRefund.setAlias("r")
     * leftJoin ?_mall_order_goods og
     *
     * @param params 查询条件
     * @return Map 包含 refund_num, refund_money, refund_member_count
     */
    public Map<String, Object> getRefundStat(MPJQueryWrapper<MallOrderRefund> params) {
        MPJQueryWrapper<MallOrderRefund> wrapper;
        if (params == null) wrapper = new MPJQueryWrapper<>();
        else wrapper = params;
        wrapper.select(
                        "IFNULL(SUM(og.num), 0) AS refund_num",
                        "IFNULL(SUM(r.money), 0) AS refund_money",
                        "COUNT(DISTINCT r.member_id) AS refund_member_count")
                .setAlias("r")
                .leftJoin("?_mall_order_goods og ON r.order_goods_id = og.order_goods_id ".replace("?_", GlobalConfig.tablePrefix));
        Map<String, Object> result = orderRefundMapper.selectMaps(wrapper).stream().findFirst().orElse(new HashMap<>());
        result.putIfAbsent("refund_num", 0);
        result.putIfAbsent("refund_money", BigDecimal.ZERO);
        result.putIfAbsent("refund_member_count", 0);
        return result;
    }

    /**
     * 获取访客统计信息（访问次数、访问人数）
     *
     * @param params 查询条件
     * @return Map 包含 access_sum, visit_num
     */
    public Map<String, Object> getVisitStat(MPJQueryWrapper<SysStat> params) {
        MPJQueryWrapper<SysStat> wrapper;
        if (params == null) wrapper = new MPJQueryWrapper<>();
        else wrapper = params;
        wrapper.select(
                "IFNULL(SUM(access_sum), 0) AS access_sum",
                "IFNULL(SUM(visit_num), 0) AS visit_num"
        );
        Map<String, Object> result = sysStatMapper.selectMaps(wrapper).stream().findFirst().orElse(new HashMap<>());
        result.putIfAbsent("access_sum", 0);
        result.putIfAbsent("visit_num", 0);
        return result;
    }

    // ===================== 辅助方法：格式转换 =====================

    /**
     * 获取两个日期之间的所有日期（yyyy-MM-dd）
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 日期字符串列表
     */
    public List<String> getDateRange(String startDate, String endDate) {
        List<String> dates = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current.format(DateTimeFormatter.ISO_LOCAL_DATE));
            current = current.plusDays(1);
        }
        return dates;
    }

    /**
     * 计算转化率（num / total * 100），保留两位小数，整数不带小数点
     *
     * @param num   分子
     * @param total 分母
     * @return 转化率数值（若为整数则返回int，否则返回double）
     */
    public Number calcRate(Number num, Number total) {
        if (num == null || total == null) return 0;
        double n = num.doubleValue();
        double t = total.doubleValue();
        if (n <= 0 || t <= 0) return 0;
        double rate = (n / t) * 100;
        // 保留两位小数
        BigDecimal bd = BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP);
        // 如果是整数（如 15.00）则返回 int
        if (bd.stripTrailingZeros().scale() <= 0) {
            return bd.intValue();
        }
        return bd.doubleValue();
    }

    /**
     * 计算环比率（(current - previous) / previous * 100），保留两位小数，整数不带小数点
     *
     * @param current  当前值
     * @param previous 上期值
     * @return 环比率（%）
     */
    public Number calcLoopRate(Number current, Number previous) {
        if (previous == null || previous.doubleValue() == 0) {
            return (current != null && current.doubleValue() > 0) ? 100 : 0;
        }
        double cur = current == null ? 0 : current.doubleValue();
        double prev = previous.doubleValue();
        double rate = ((cur - prev) / prev) * 100;
        BigDecimal bd = BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP);
        if (bd.stripTrailingZeros().scale() <= 0) {
            return bd.intValue();
        }
        return bd.doubleValue();
    }


}
