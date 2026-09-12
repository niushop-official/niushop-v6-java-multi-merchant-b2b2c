package com.niu.mall.service.admin.stat.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.enums.member.MemberRegisterChannelEnum;
import com.niu.core.enums.pay.PayStatusEnum;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.enums.recharge.RechargeRefundStatusEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.member.MemberVisitMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.mall.entity.cart.MallCart;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.enums.order.OrderRefundStatusEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.cart.MallCartMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.mall.service.admin.stat.IMemberStatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberStatServiceImpl implements IMemberStatService {

    @Resource
    private MemberMapper memberMapper;
    @Resource
    private MallOrderMapper orderMapper;
    @Resource
    private MallOrderGoodsMapper orderGoodsMapper;
    @Resource
    private MallOrderRefundMapper orderRefundMapper;
    @Resource
    private PayMapper payMapper;
    @Resource
    private MallCartMapper cartMapper;
    @Resource
    private MemberVisitMapper memberVisitMapper;
    @Resource
    private RechargeOrderMapper rechargeOrderMapper;

    // ===================== 基础统计 =====================
    @Override
    public Map<String, Object> getBasicData() {
        Map<String, Object> result = new HashMap<>();
        result.put("total_member", getCount());
        result.put("pay_member_count", getPaidMemberCount());
        long now = System.currentTimeMillis() / 1000;
        long thirtyDaysAgo = now - 30L * 86400;
        result.put("active_member_count", getActiveMemberCount(thirtyDaysAgo));
        result.put("silent_member_count", getSilentMemberCount(thirtyDaysAgo));
        return result;
    }

    /**
     * 会员总数（未删除）
     */
    public Long getCount() {
        return memberMapper.selectCount(new LambdaQueryWrapper<Member>().eq(Member::getIsDel, 0));
    }

    /**
     * 付费会员数（有支付成功的订单）
     */
    public Long getPaidMemberCount() {
        // 查询所有支付成功的会员ID
        List<Integer> memberIds = payMapper.selectList(
                new LambdaQueryWrapper<Pay>().eq(Pay::getStatus, PayStatusEnum.STATUS_FINISH.getCode())
                        .select(Pay::getMainId)
                        .groupBy(Pay::getMainId)
        ).stream().map(Pay::getMainId).collect(Collectors.toList());
        if (memberIds.isEmpty()) return 0L;
        return memberMapper.selectCount(new LambdaQueryWrapper<Member>().in(Member::getMemberId, memberIds).eq(Member::getIsDel, 0));
    }

    /**
     * 活跃会员数（最近访问时间 >= threshold）
     */
    public Long getActiveMemberCount(long threshold) {
        return memberMapper.selectCount(new LambdaQueryWrapper<Member>().ge(Member::getLastVisitTime, threshold).eq(Member::getIsDel, 0));
    }

    /**
     * 沉默会员数（最近访问时间 < threshold）
     */
    public Long getSilentMemberCount(long threshold) {
        return memberMapper.selectCount(new LambdaQueryWrapper<Member>().lt(Member::getLastVisitTime, threshold).eq(Member::getIsDel, 0));
    }

    // ===================== 数据概览（按时间范围） =====================

    @Override
    public Map<String, Object> getMemberData(Map<String, String> params) {
        long startTime = getStartTimestamp(params.get("start_time"));
        long endTime = getEndTimestamp(params.get("end_time"));
        List<Long> timeRange = Arrays.asList(startTime, endTime);

        Map<String, Object> data = new HashMap<>();
        data.put("new_member_count", getNewMemberCount(timeRange, false).get("value"));
        data.put("new_pay_member_count", getNewPaidMemberCount(timeRange, false).get("value"));
        data.put("visit_member_count", getVisitMemberCount(timeRange, false).get("value"));
        data.put("add_cart_member_count", getAddCartMemberCount(timeRange, false).get("value"));
        data.put("order_member_count", getOrderMemberCount(timeRange, false).get("value"));
        data.put("pay_member_count", getPayMemberCount(timeRange, false).get("value"));
        data.put("lost_member_count", getLostMemberCount(timeRange, false).get("value"));
        data.put("recharge_member_count", getRechargeMemberCount(timeRange, false).get("value"));
        data.put("member_unit_price", getMemberUnitPrice(timeRange, false).get("value"));
        return data;
    }

    // ===================== 趋势分析（按天分组） =====================

    @Override
    public Map<String, Object> getTrendData(Map<String, String> params) {
        long startTime = getStartTimestamp(params.get("start_time"));
        long endTime = getEndTimestamp(params.get("end_time"));
        List<Long> timeRange = Arrays.asList(startTime, endTime);
        String startDate = formatDate(startTime);
        String endDate = formatDate(endTime);
        List<String> dateList = getDateRange(startDate, endDate);

        Map<String, Map<String, BigDecimal>> rawData = new HashMap<>();
        rawData.put("new_member_count_list", getNewMemberCount(timeRange, true));
        rawData.put("new_pay_member_count_list", getNewPaidMemberCount(timeRange, true));
        rawData.put("visit_member_count_list", getVisitMemberCount(timeRange, true));
        rawData.put("add_cart_member_list", getAddCartMemberCount(timeRange, true));
        rawData.put("order_member_list", getOrderMemberCount(timeRange, true));
        rawData.put("pay_member_list", getPayMemberCount(timeRange, true));
        rawData.put("lost_member_count_list", getLostMemberCount(timeRange, true));
        rawData.put("recharge_member_count_list", getRechargeMemberCount(timeRange, true));
        rawData.put("member_unit_price_list", getMemberUnitPrice(timeRange, true));

        Map<String, Object> result = new HashMap<>();
        result.put("date_list", dateList);
        for (Map.Entry<String, Map<String, BigDecimal>> entry : rawData.entrySet()) {
            List<BigDecimal> values = new ArrayList<>();
            for (String date : dateList) {
                values.add(entry.getValue().getOrDefault(date, BigDecimal.ZERO));
            }
            result.put(entry.getKey(), values);
        }
        return result;
    }

    // ===================== 来源分布 =====================

    @Override
    public List<Map<String, Object>> getSourceData() {
        // 1. 获取所有渠道定义
        Map<String, String> allChannels = MemberRegisterChannelEnum.getMap(); // key=channel, value=channelName
        // 初始化结果 Map：渠道 -> 对应的数据Map
        Map<String, Map<String, Object>> resultMap = new LinkedHashMap<>(); // 保持定义顺序
        for (Map.Entry<String, String> entry : allChannels.entrySet()) {
            String channel = entry.getKey();
            String channelName = entry.getValue();
            Map<String, Object> item = new HashMap<>();
            item.put("register_channel", channel);
            item.put("register_channel_name", channelName);
            item.put("count", 0);
            resultMap.put(channel, item);
        }

        // 2. 查询数据库分组统计（实际有记录的渠道）
        List<Map<String, Object>> dbStats = memberMapper.selectMaps(new QueryWrapper<Member>()
                .select("register_channel", "count(*) as `count`")
                .eq("is_del", 0)
                .groupBy("register_channel")
        );

        // 3. 填充实际计数
        for (Map<String, Object> dbItem : dbStats) {
            String channel = String.valueOf(dbItem.get("register_channel"));
            if (resultMap.containsKey(channel)) {
                resultMap.get(channel).put("count", dbItem.get("count"));
            }
        }

        // 4. 返回所有渠道的列表（保持枚举定义顺序）
        return new ArrayList<>(resultMap.values());
    }

    // ===================== 时长分布 =====================

    @Override
    public List<Map<String, Object>> getDurationData() {
        long now = System.currentTimeMillis() / 1000;
        // 使用自定义 SQL 计算（也可用 Java 分组，但为了效率用 SQL）
        // 这里使用 MyBatis-Plus 的 selectMaps 写原生 SQL
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.select(
                "COALESCE(SUM(CASE WHEN FLOOR((UNIX_TIMESTAMP() - create_time)/86400) <= 30 THEN 1 ELSE 0 END), 0) AS d_0_30",
                "COALESCE(SUM(CASE WHEN FLOOR((UNIX_TIMESTAMP() - create_time)/86400) > 30 AND FLOOR((UNIX_TIMESTAMP() - create_time)/86400) <= 60 THEN 1 ELSE 0 END), 0) AS d_30_60",
                "COALESCE(SUM(CASE WHEN FLOOR((UNIX_TIMESTAMP() - create_time)/86400) > 60 AND FLOOR((UNIX_TIMESTAMP() - create_time)/86400) <= 90 THEN 1 ELSE 0 END), 0) AS d_60_90",
                "COALESCE(SUM(CASE WHEN FLOOR((UNIX_TIMESTAMP() - create_time)/86400) > 90 AND FLOOR((UNIX_TIMESTAMP() - create_time)/86400) <= 180 THEN 1 ELSE 0 END), 0) AS d_90_180",
                "COALESCE(SUM(CASE WHEN FLOOR((UNIX_TIMESTAMP() - create_time)/86400) > 180 AND FLOOR((UNIX_TIMESTAMP() - create_time)/86400) <= 365 THEN 1 ELSE 0 END), 0) AS d_180_365",
                "COALESCE(SUM(CASE WHEN FLOOR((UNIX_TIMESTAMP() - create_time)/86400) > 365 THEN 1 ELSE 0 END), 0) AS d_365_plus"
        ).eq("is_del", 0);
        List<Map<String, Object>> rows = memberMapper.selectMaps(wrapper);
        if (rows == null || rows.isEmpty()) {
            return Arrays.asList(
                    Map.of("name", "0-30天", "value", 0),
                    Map.of("name", "30-60天", "value", 0),
                    Map.of("name", "60-90天", "value", 0),
                    Map.of("name", "90-180天", "value", 0),
                    Map.of("name", "180-365天", "value", 0),
                    Map.of("name", "365天以上", "value", 0)
            );
        }
        Map<String, Object> row = rows.get(0);
        return Arrays.asList(
                Map.of("name", "0-30天", "value", ((Number) row.get("d_0_30")).intValue()),
                Map.of("name", "30-60天", "value", ((Number) row.get("d_30_60")).intValue()),
                Map.of("name", "60-90天", "value", ((Number) row.get("d_60_90")).intValue()),
                Map.of("name", "90-180天", "value", ((Number) row.get("d_90_180")).intValue()),
                Map.of("name", "180-365天", "value", ((Number) row.get("d_180_365")).intValue()),
                Map.of("name", "365天以上", "value", ((Number) row.get("d_365_plus")).intValue())
        );
    }

    // ===================== 会员排行（分页） =====================

    //使用下面的方法
    public PageResult<Map<String, Object>> getRankingData1(PageParam pageParam, Map<String, String> params) {
        var pageNo = pageParam.getPage();
        var limit = pageParam.getLimit();
        long startTime = 0, endTime = 0;
        if (StringUtils.hasText(params.get("start_time")) && StringUtils.hasText(params.get("end_time"))) {
            startTime = getStartTimestamp(params.get("start_time"));
            endTime = getEndTimestamp(params.get("end_time"));
        }
        String keyword = params.getOrDefault("keyword", "");
        String sortField = params.getOrDefault("sort_field", "pay_order_count");
        // 默认降序
        String sortType = "desc";

        // 构建子查询：退款数据
        LambdaQueryWrapper<MallOrderRefund> refundWrapper = new LambdaQueryWrapper<>();
        refundWrapper.eq(MallOrderRefund::getStatus, OrderRefundStatusEnum.FINISH.getStatus());
        if (startTime > 0 && endTime > 0) {
            refundWrapper.between(MallOrderRefund::getTransferTime, startTime, endTime);
        }
        List<MallOrderRefund> refundList = orderRefundMapper.selectList(refundWrapper);
        Map<Integer, RefundStat> refundMap = new HashMap<>();
        for (MallOrderRefund refund : refundList) {
            RefundStat stat = refundMap.computeIfAbsent(refund.getMemberId(), k -> new RefundStat());
            stat.refundGoodsCount += getRefundGoodsCount(refund.getOrderGoodsId()); // 需要查 order_goods
            stat.refundMoney = stat.refundMoney.add(refund.getMoney());
        }

        // 查询订单数据
        LambdaQueryWrapper<MallOrder> orderWrapper = new LambdaQueryWrapper<>();
        orderWrapper.ne(MallOrder::getStatus, OrderStatusEnum.WAIT_PAY.getStatus());
        if (startTime > 0 && endTime > 0) {
            orderWrapper.between(MallOrder::getPayTime, startTime, endTime);
        }
        if (StringUtils.hasText(keyword)) {
            // 会员关联查询需要联表，这里简化：先查出符合条件的会员ID再查订单
            List<Integer> memberIds = memberMapper.selectList(
                    new LambdaQueryWrapper<Member>().like(Member::getMobile, keyword)
                            .or().like(Member::getNickname, keyword)
                            .or().like(Member::getUsername, keyword)
                            .select(Member::getMemberId)
            ).stream().map(Member::getMemberId).collect(Collectors.toList());
            if (memberIds.isEmpty()) {
                return PageResult.build(new Page<>()); // 空分页
            }
            orderWrapper.in(MallOrder::getMemberId, memberIds);
        }
        List<MallOrder> orders = orderMapper.selectList(orderWrapper);

        // 按会员聚合
        Map<Integer, MemberStat> statMap = new HashMap<>();
        for (MallOrder order : orders) {
            MemberStat stat = statMap.computeIfAbsent(order.getMemberId(), k -> new MemberStat());
            stat.payOrderCount++;
            stat.payGoodsCount += Math.toIntExact(getOrderGoodsCount(order.getOrderId()));
            stat.payMoney = stat.payMoney.add(order.getOrderMoney());
        }
        // 合并退款数据
        for (Map.Entry<Integer, RefundStat> entry : refundMap.entrySet()) {
            MemberStat stat = statMap.computeIfAbsent(entry.getKey(), k -> new MemberStat());
            stat.refundGoodsCount = entry.getValue().refundGoodsCount;
            stat.refundMoney = entry.getValue().refundMoney;
        }

        // 构建结果列表
        List<Map<String, Object>> records = new ArrayList<>();
        for (Map.Entry<Integer, MemberStat> entry : statMap.entrySet()) {
            Integer memberId = entry.getKey();
            MemberStat stat = entry.getValue();
            Member member = memberMapper.selectById(memberId);
            if (member == null) continue;
            Map<String, Object> row = new HashMap<>();
            row.put("member_id", memberId);
            row.put("headimg", member.getHeadimg());
            row.put("nickname", member.getNickname());
            row.put("pay_order_count", stat.payOrderCount);
            row.put("pay_goods_count", stat.payGoodsCount);
            row.put("pay_money", stat.payMoney);
            row.put("refund_goods_count", stat.refundGoodsCount);
            row.put("refund_money", stat.refundMoney);
            records.add(row);
        }

        // 排序
        records.sort((a, b) -> {
            Object v1 = a.get(sortField);
            Object v2 = b.get(sortField);
            if (v1 instanceof BigDecimal) {
                int cmp = ((BigDecimal) v1).compareTo((BigDecimal) v2);
                return "desc".equals(sortType) ? -cmp : cmp;
            } else if (v1 instanceof Integer) {
                return "desc".equals(sortType) ? Integer.compare((Integer) v2, (Integer) v1) : Integer.compare((Integer) v1, (Integer) v2);
            }
            return 0;
        });

        // 手动分页（此处简化，实际可使用 MyBatis-Plus 的 Page 对象结合联表查询更优）
        int start = (pageNo - 1) * limit;
        int end = Math.min(start + limit, records.size());
        List<Map<String, Object>> subList = records.subList(start, end);
        Page<Map<String, Object>> page = new Page<>(pageNo, limit);
        page.setRecords(subList);
        page.setTotal(records.size());
        return PageResult.build(pageNo, limit, records.size(), subList);
    }

    @Override
    public PageResult<Map<String, Object>> getRankingData(PageParam pageParam, Map<String, String> params) {
        var pageNo = pageParam.getPage();
        var limit = pageParam.getLimit();
        long startTime = 0, endTime = 0;
        if (StringUtils.hasText(params.get("start_time")) && StringUtils.hasText(params.get("end_time"))) {
            startTime = getStartTimestamp(params.get("start_time"));
            endTime = getEndTimestamp(params.get("end_time"));
        }
        String keyword = params.getOrDefault("keyword", "");
        String sortField = params.getOrDefault("sort_field", "pay_order_count");
        // 默认降序
        String sortType = "DESC";

        String subr = "(SELECT r.member_id, SUM(g.num) AS refund_goods_count, SUM(r.money) AS refund_money " +
                "   FROM ?_mall_order_refund r " +
                "   LEFT JOIN ?_mall_order_goods g ON r.order_goods_id = g.order_goods_id " +
                "   WHERE r.status = " + OrderRefundStatusEnum.FINISH.getStatus() + " ";
        if (startTime != 0 && endTime != 0) {
            subr += "AND r.transfer_time BETWEEN " + startTime + " AND " + endTime + " ";
        }
        subr += "   GROUP BY r.member_id) r ON r.member_id = o.member_id";

        var wrapper = new MPJQueryWrapper<MallOrder>()
                .select("o.member_id", "m.headimg", "m.nickname",
                        "COUNT(DISTINCT o.order_id) AS pay_order_count",
                        "SUM(og.num) AS pay_goods_count",
                        "SUM(o.order_money) AS pay_money",
                        "COALESCE(r.refund_goods_count, 0) AS refund_goods_count",
                        "COALESCE(r.refund_money, 0) AS refund_money")
                .setAlias("o")
                .leftJoin("?_mall_order_goods og ON o.order_id = og.order_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_member m ON o.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin(subr.replace("?_", GlobalConfig.tablePrefix))
                .ne("o.status", OrderStatusEnum.WAIT_PAY.getStatus())
                .groupBy("o.member_id", "m.headimg", " m.nickname");
        wrapper.orderByDesc(sortField);
        if (startTime != 0 && endTime != 0) {
            wrapper.between("o.pay_time", startTime, endTime);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(wrapper1 -> {
                wrapper1.like("m.mobile", keyword)
                        .or(wrapper2 -> wrapper2.like("m.nickname", keyword))
                        .or(wrapper2 -> wrapper2.like("m.username", keyword));
            });
        }
        var iPage = orderMapper.selectMapsPage(new Page<>(pageNo, limit), wrapper);

        return PageResult.build(pageNo, limit, iPage.getTotal(), iPage.getRecords());
    }

    // ===================== 具体统计方法（支持分组） =====================

    /**
     * 新增会员数
     */
    public Map<String, BigDecimal> getNewMemberCount(List<Long> timeRange, boolean group) {
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.eq("is_del", 0);
        if (timeRange != null && timeRange.size() == 2) {
            wrapper.between("create_time", timeRange.get(0), timeRange.get(1));
        }
        if (group) {
            List<Map<String, Object>> list = memberMapper.selectMaps(
                    wrapper.select("FROM_UNIXTIME(create_time,'%Y-%m-%d') as d, count(*) as total")
                            .groupBy("d")
            );
            return list.stream().collect(Collectors.toMap(
                    m -> (String) m.get("d"),
                    m -> BigDecimal.valueOf((Long) m.get("total"))
            ));
        } else {
            return Map.of("value", BigDecimal.valueOf(memberMapper.selectCount(wrapper)));
        }
    }

    /**
     * 新增付费会员数（首次支付时间在时间范围内）
     */
    public Map<String, BigDecimal> getNewPaidMemberCount(List<Long> timeRange, boolean group) {
        // 1. 查询支付成功的会员ID（关联会员表，过滤未删除）
        LambdaQueryWrapper<Pay> payWrapper = new LambdaQueryWrapper<>();
        payWrapper.select(Pay::getMainId)
                .eq(Pay::getStatus, PayStatusEnum.STATUS_FINISH.getCode())
                .inSql(Pay::getMainId, "SELECT member_id FROM ?_member WHERE is_del = 0".replace("?_", GlobalConfig.tablePrefix))
                .groupBy(Pay::getMainId);
        List<Integer> memberIds = payMapper.selectList(payWrapper).stream()
                .map(Pay::getMainId)
                .collect(Collectors.toList());
        if (memberIds.isEmpty()) {
            return group ? new HashMap<>() : Map.of("", BigDecimal.ZERO);
        }

        // 2. 查询会员表，增加时间条件和会员ID条件
        LambdaQueryWrapper<Member> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.in(Member::getMemberId, memberIds)
                .eq(Member::getIsDel, 0);
        if (timeRange != null && timeRange.size() == 2) {
            memberWrapper.between(Member::getCreateTime, timeRange.get(0), timeRange.get(1));
        }

        if (group) {
            // 按天分组统计
            List<Map<String, Object>> list = memberMapper.selectMaps(
                    new QueryWrapper<Member>()
                            .select("FROM_UNIXTIME(create_time,'%Y-%m-%d') as d, COUNT(*) as total")
                            .in("member_id", memberIds)
                            .eq("is_del", 0)
                            .between(timeRange != null && timeRange.size() == 2,
                                    "create_time", timeRange != null ? timeRange.get(0) : null,
                                    timeRange != null ? timeRange.get(1) : null)
                            .groupBy("d")
            );
            return list.stream().collect(Collectors.toMap(
                    m -> (String) m.get("d"),
                    m -> BigDecimal.valueOf((Long) m.get("total"))
            ));
        } else {
            return Map.of("value", BigDecimal.valueOf(memberMapper.selectCount(memberWrapper)));
        }
    }

    /**
     * 访问会员数（member_visit）
     */
    public Map<String, BigDecimal> getVisitMemberCount(List<Long> timeRange, boolean group) {
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        if (timeRange != null && timeRange.size() == 2) {
            wrapper.between("last_visit_time", timeRange.get(0), timeRange.get(1));
        }
        if (group) {
            List<Map<String, Object>> list = memberMapper.selectMaps(
                    wrapper.select("FROM_UNIXTIME(last_visit_time,'%Y-%m-%d') as d, count(distinct member_id) as total")
                            .groupBy("d")
            );
            return list.stream().collect(Collectors.toMap(
                    m -> (String) m.get("d"),
                    m -> BigDecimal.valueOf((Long) m.get("total"))
            ));
        } else {
            long count = memberMapper.selectCount(wrapper);
            return Map.of("value", BigDecimal.valueOf(count));
        }
    }

    /**
     * 加购会员数
     */
    public Map<String, BigDecimal> getAddCartMemberCount(List<Long> timeRange, boolean group) {
        QueryWrapper<MallCart> wrapper = new QueryWrapper<>();
        if (timeRange != null && timeRange.size() == 2) {
            wrapper.between("create_time", timeRange.get(0), timeRange.get(1));
        }
        if (group) {
            List<Map<String, Object>> list = cartMapper.selectMaps(
                    wrapper.select("FROM_UNIXTIME(create_time,'%Y-%m-%d') as d, count(distinct member_id) as total")
                            .groupBy("d")
            );
            return list.stream().collect(Collectors.toMap(
                    m -> (String) m.get("d"),
                    m -> BigDecimal.valueOf((Long) m.get("total"))
            ));
        } else {
            long count = cartMapper.selectCount(wrapper);
            return Map.of("value", BigDecimal.valueOf(count));
        }
    }

    /**
     * 下单会员数
     */
    public Map<String, BigDecimal> getOrderMemberCount(List<Long> timeRange, boolean group) {
        QueryWrapper<MallOrder> wrapper = new QueryWrapper<>();
        if (timeRange != null && timeRange.size() == 2) {
            wrapper.between("create_time", timeRange.get(0), timeRange.get(1));
        }
        if (group) {
            List<Map<String, Object>> list = orderMapper.selectMaps(
                    wrapper.select("FROM_UNIXTIME(create_time,'%Y-%m-%d') as d, count(distinct member_id) as total")
                            .groupBy("d")
            );
            return list.stream().collect(Collectors.toMap(
                    m -> (String) m.get("d"),
                    m -> BigDecimal.valueOf((Long) m.get("total"))
            ));
        } else {
            long count = orderMapper.selectCount(wrapper);
            return Map.of("value", BigDecimal.valueOf(count));
        }
    }

    /**
     * 支付会员数
     */
    public Map<String, BigDecimal> getPayMemberCount(List<Long> timeRange, boolean group) {
        QueryWrapper<Pay> wrapper = new QueryWrapper<>();
        wrapper.eq("status", PayStatusEnum.STATUS_FINISH.getCode());
        if (timeRange != null && timeRange.size() == 2) {
            wrapper.between("pay_time", timeRange.get(0), timeRange.get(1));
        }
        if (group) {
            List<Map<String, Object>> list = payMapper.selectMaps(
                    wrapper.select("FROM_UNIXTIME(pay_time,'%Y-%m-%d') as d, count(distinct main_id) as total")
                            .groupBy("d")
            );
            return list.stream().collect(Collectors.toMap(
                    m -> (String) m.get("d"),
                    m -> BigDecimal.valueOf((Long) m.get("total"))
            ));
        } else {
            long count = payMapper.selectCount(wrapper);
            return Map.of("value", BigDecimal.valueOf(count));
        }
    }

    /**
     * 流失会员数（软删除时间）
     */
    public Map<String, BigDecimal> getLostMemberCount(List<Long> timeRange, boolean group) {
        // 软删除会员，delete_time 不为空
        QueryWrapper<Member> wrapper = new QueryWrapper<>();
        wrapper.isNotNull("delete_time");
        if (timeRange != null && timeRange.size() == 2) {
            wrapper.between("delete_time", timeRange.get(0), timeRange.get(1));
        }
        if (group) {
            List<Map<String, Object>> list = memberMapper.selectMaps(
                    wrapper.select("FROM_UNIXTIME(delete_time,'%Y-%m-%d') as d, count(*) as total")
                            .groupBy("d")
            );
            return list.stream().collect(Collectors.toMap(
                    m -> (String) m.get("d"),
                    m -> BigDecimal.valueOf((Long) m.get("total"))
            ));
        } else {
            long count = memberMapper.selectCount(wrapper);
            return Map.of("value", BigDecimal.valueOf(count));
        }
    }

    /**
     * 充值会员数
     */
    public Map<String, BigDecimal> getRechargeMemberCount(List<Long> timeRange, boolean group) {
        QueryWrapper<RechargeOrder> wrapper = new QueryWrapper<>();
        wrapper.eq("order_status", RechargeOrderStatusEnum.FINISH.getStatus())
                .eq("refund_status", RechargeRefundStatusEnum.NOT_APPLY.getStatus());
        if (timeRange != null && timeRange.size() == 2) {
            wrapper.between("create_time", timeRange.get(0), timeRange.get(1));
        }
        if (group) {
            List<Map<String, Object>> list = rechargeOrderMapper.selectMaps(
                    wrapper.select("FROM_UNIXTIME(create_time,'%Y-%m-%d') as d, count(distinct member_id) as total")
                            .groupBy("d")
            );
            return list.stream().collect(Collectors.toMap(
                    m -> (String) m.get("d"),
                    m -> BigDecimal.valueOf((Long) m.get("total"))
            ));
        } else {
            long count = rechargeOrderMapper.selectCount(wrapper);
            return Map.of("value", BigDecimal.valueOf(count));
        }
    }

    /**
     * 会员客单价（总支付金额 / 支付会员数）
     */
    public Map<String, BigDecimal> getMemberUnitPrice(List<Long> timeRange, boolean group) {
        if (group) {
            // 按天分组统计
            QueryWrapper<MallOrder> wrapper = new QueryWrapper<>();
            wrapper.eq("status", OrderStatusEnum.FINISH)
                    .eq("refund_status", 1);
            if (timeRange != null && timeRange.size() == 2) {
                wrapper.between("pay_time", timeRange.get(0), timeRange.get(1));
            }
            List<Map<String, Object>> list = orderMapper.selectMaps(
                    wrapper.select("FROM_UNIXTIME(pay_time,'%Y-%m-%d') as d, sum(order_money) as total_money,COALESCE(count(distinct member_id), 0)  as member_total")
                            .groupBy("d")
            );
            Map<String, BigDecimal> result = new HashMap<>();
            for (Map<String, Object> row : list) {
                String date = (String) row.get("d");
                long memberTotal = (Long) row.get("member_total");
                BigDecimal totalMoney = (BigDecimal) row.get("total_money");
                if (memberTotal > 0) {
                    result.put(date, totalMoney.divide(BigDecimal.valueOf(memberTotal), 2, RoundingMode.HALF_UP));
                } else {
                    result.put(date, BigDecimal.ZERO);
                }
            }
            return result;
        } else {
            LambdaQueryWrapper<MallOrder> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(MallOrder::getStatus, OrderStatusEnum.FINISH.getStatus())
                    .eq(MallOrder::getRefundStatus, 1);
            if (timeRange != null && timeRange.size() == 2) {
                wrapper.between(MallOrder::getPayTime, timeRange.get(0), timeRange.get(1));
            }
            BigDecimal totalMoney = orderMapper.selectList(wrapper).stream()
                    .map(MallOrder::getOrderMoney)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            List<MallOrder> memberIds = orderMapper.selectList(wrapper.select(MallOrder::getMemberId).groupBy(MallOrder::getMemberId));
            long memberCount = memberIds.size();
//            Long memberCount = orderMapper.selectCount(wrapper.select(MallOrder::getMemberId).groupBy(MallOrder::getMemberId));
            if (memberCount == 0) return Map.of("", BigDecimal.ZERO);
            return Map.of("value", totalMoney.divide(BigDecimal.valueOf(memberCount), 2, RoundingMode.HALF_UP));
        }
    }

    // ===================== 辅助方法 =====================

    private long getStartTimestamp(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            // 当天开始
            return LocalDate.now().atStartOfDay().toEpochSecond(ZoneOffset.ofHours(8));
        }
        return LocalDateTime.parse(dateStr + " 00:00:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .toEpochSecond(ZoneOffset.ofHours(8));
    }

    private long getEndTimestamp(String dateStr) {
        if (!StringUtils.hasText(dateStr)) {
            // 当天结束
            return LocalDate.now().atTime(23, 59, 59).toEpochSecond(ZoneOffset.ofHours(8));
        }
        return LocalDateTime.parse(dateStr + " 23:59:59", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .toEpochSecond(ZoneOffset.ofHours(8));
    }

    private String formatDate(long timestamp) {
        return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.ofHours(8))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private List<String> getDateRange(String startDate, String endDate) {
        List<String> dates = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        LocalDate current = start;
        while (!current.isAfter(end)) {
            dates.add(current.toString());
            current = current.plusDays(1);
        }
        return dates;
    }

    private Long getOrderGoodsCount(Integer orderId) {
        return orderGoodsMapper.selectCount(new LambdaQueryWrapper<MallOrderGoods>().eq(MallOrderGoods::getOrderId, orderId));
    }

    private Integer getRefundGoodsCount(Integer orderGoodsId) {
        MallOrderGoods goods = orderGoodsMapper.selectById(orderGoodsId);
        return goods == null ? 0 : goods.getNum();
    }

    // 内部类用于聚合统计
    static class MemberStat {
        Integer payOrderCount = 0;
        Integer payGoodsCount = 0;
        BigDecimal payMoney = BigDecimal.ZERO;
        Integer refundGoodsCount = 0;
        BigDecimal refundMoney = BigDecimal.ZERO;
    }

    static class RefundStat {
        Integer refundGoodsCount = 0;
        BigDecimal refundMoney = BigDecimal.ZERO;
    }
}
