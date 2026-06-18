package com.niu.mall.service.core.stat;

import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.stat.SysStat;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderRefund;

import java.util.List;
import java.util.Map;

public interface ICoreTradeStatService {

    /**
     * 付款金额渠道
     *
     * @param params 查询条件（例如 site_id, 时间范围等）
     * @return List<Map < String, Object>> 渠道名称和支付订单数
     */
    List<Map<String, Object>> getChannelData(MPJQueryWrapper<Pay> params);

    /**
     * 成交客户构成（按会员等级统计）
     *
     * @param params 查询条件
     * @return 每个等级的数据列表
     */
    List<Map<String, Object>> getDurationData(MPJQueryWrapper<Member> params);

    /**
     * 获取订单统计信息
     *
     * @param params 查询条件
     * @return Map 包含订单数量、支付订单数、会员数、销售额等
     */
    Map<String, Object> getOrderStat(MPJQueryWrapper<MallOrder> params);

    /**
     * 获取支付统计信息（总支付金额）
     *
     * @param params 查询条件
     * @return Map 包含 balance_pay_money
     */
    Map<String, Object> getPayStat(MPJQueryWrapper<Pay> params);

    /**
     * 获取站点支付统计信息（联表订单）
     *
     * @param params 查询条件
     * @return Map 包含 balance_pay_money
     */
    Map<String, Object> getSitePayStat(MPJQueryWrapper<Pay> params);

    /**
     * 获取订单商品统计信息（商品数量、支付数量）
     *
     * @param params 查询条件
     * @return Map 包含 order_num, pay_num
     */
    Map<String, Object> getOrderGoodsStat(MPJQueryWrapper<MallOrderGoods> params);

    /**
     * 获取退款统计信息
     *
     * @param params 查询条件
     * @return Map 包含 refund_num, refund_money, refund_member_count
     */
    Map<String, Object> getRefundStat(MPJQueryWrapper<MallOrderRefund> params);

    /**
     * 获取访客统计信息（访问次数、访问人数）
     *
     * @param params 查询条件
     * @return Map 包含 access_sum, visit_num
     */
    Map<String, Object> getVisitStat(MPJQueryWrapper<SysStat> params);

    /**
     * 获取两个日期之间的所有日期（yyyy-MM-dd）
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 日期字符串列表
     */
    public List<String> getDateRange(String startDate, String endDate);

    /**
     * 计算转化率（num / total * 100），保留两位小数，整数不带小数点
     *
     * @param num   分子
     * @param total 分母
     * @return 转化率数值（若为整数则返回int，否则返回double）
     */
    public Number calcRate(Number num, Number total);

    /**
     * 计算环比率（(current - previous) / previous * 100），保留两位小数，整数不带小数点
     *
     * @param current  当前值
     * @param previous 上期值
     * @return 环比率（%）
     */
    public Number calcLoopRate(Number current, Number previous);
}
