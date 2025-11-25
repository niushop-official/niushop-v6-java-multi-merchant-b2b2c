package com.niu.core.service.admin.stat.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.shop.SiteShopCashOut;
import com.niu.core.entity.stat.SysStat;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.mapper.shop.SiteShopCashOutMapper;
import com.niu.core.mapper.stat.SysStatMapper;
import com.niu.core.service.admin.stat.ISiteStatService;
import com.niu.core.entity.site.Site;
import com.niu.core.service.admin.stat.vo.StartChartVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.*;
import java.sql.Timestamp;

/**
 * 站点统计服务实现类
 */
@Service
public class SiteStatServiceImpl implements ISiteStatService {
    @Resource
    SysStatMapper sysStatMapper;
    @Resource
    SiteShopCashOutMapper siteShopCashOutMapper;
    @Override
    public Map<String, Object> getIndexData() {
/*        Map<String, Object> data = new HashMap<>();
        Site siteInfo = siteService.getInfo(site_id);
        data.put("site_info", siteInfo);
        long siteCreateTime = Timestamp.valueOf(siteInfo.getCreate_time()).getTime();
        long siteExpireTime = Timestamp.valueOf(siteInfo.getExpire_time()).getTime();
        long currentTime = System.currentTimeMillis();
        double mix = ((double) (currentTime - siteCreateTime) / (siteExpireTime - siteCreateTime)) * 100;
        siteInfo.setMix(String.format("%.2f%%", mix));
        double overDate = (siteExpireTime - currentTime) > 0 ? (siteExpireTime - currentTime) / (1000.0 * 60 * 60 * 24) : 0;
        siteInfo.setOver_date(String.format("%.2f", overDate));
        return data;*/
        return null;
    }

    @Override
    public double orderMoney(long start_time, long end_time) {
        // 这里需要实现订单金额的查询逻辑
        return 0;
    }

    @Override
    public int orderCount(long start_time, long end_time) throws Exception {
        // 这里需要实现订单数量的查询逻辑
        return 0;
    }

    @Override
    public Map<String, Object> getTransferCount() {
        Map<String, Object> data = new HashMap<>();
        // 这里需要实现待转账数量的查询逻辑
        int waitTransfer = siteShopCashOutMapper.selectCount(new QueryWrapper<SiteShopCashOut>().eq("'site_id'", RequestUtils.siteId())
                .eq("status", ShopCashOutEnum.WAIT_TRANSFER.getState())).intValue();
        data.put("wait_transfer", waitTransfer);
        return data;
    }

    @Override
    public Map<String, Object> getAddonCount() {
        Map<String, Object> data = new HashMap<>();
        Site siteInfo = new Site(); // 这里需要根据实际情况查询数据库
        // 这里需要实现站点已安装应用数量的查询逻辑
        data.put("addon_num", 0);
        return data;
    }

    @Override
    public StartChartVo getStatData(Integer siteId, String startDate, String endDate) {
        // 构建查询条件
        QueryWrapper<SysStat> queryWrapper = new QueryWrapper<>();
        if(siteId>0){
            queryWrapper.eq("site_id", siteId);
        }
        queryWrapper.ge("date", startDate);
        queryWrapper.le("date", endDate);

        // 查询统计数据并汇总
        // 使用select sum函数汇总统计字段
        queryWrapper.select(FINANCE_ACCOUNTS_DAY_FIELDS);
        queryWrapper.groupBy("date");
        // 执行查询获取统计数据
        List<SysStat> resultList = sysStatMapper.selectList(queryWrapper);
        //数据转换为图表数据
        return setStartChartList(resultList,startDate,endDate);
    }

    /**
     * 聚合统计字段列表
     */
    public static final List<String> FINANCE_ACCOUNTS_DAY_FIELDS = Arrays.asList(
            "date",
            "IFNULL(SUM(order_num), 0) AS order_num",          // 订单数
            "IFNULL(SUM(sale_money), 0) AS sale_money",        // 销售额
            "IFNULL(SUM(refund_money), 0) AS refund_money",    // 退款金额
            "IFNULL(SUM(access_sum), 0) AS access_sum",        // 访客数
            "IFNULL(SUM(order_receivable_money), 0) AS order_receivable_money",      // 订单应收
            "IFNULL(SUM(order_actual_money), 0) AS order_actual_money",              // 订单实收
            "IFNULL(SUM(mall_coupon_money), 0) AS mall_coupon_money",               // 平台优惠补贴
            "IFNULL(SUM(mall_refund_coupon_money), 0) AS mall_refund_coupon_money", // 返还平台优惠券补贴
            "IFNULL(SUM(order_refund_money), 0) AS order_refund_money",              // 订单应退
            "IFNULL(SUM(order_actual_refund_money), 0) AS order_actual_refund_money",// 订单实退
            "IFNULL(SUM(shop_separate_money), 0) AS shop_separate_money",            // 商家支付分账
            "IFNULL(SUM(shop_refund_separate_money), 0) AS shop_refund_separate_money", // 商家退款分账
            "IFNULL(SUM(shop_commission), 0) AS shop_commission",                    // 支付佣金
            "IFNULL(SUM(shop_refund_commission), 0) AS shop_refund_commission",      // 退还佣金
            "IFNULL(SUM(delivery_money), 0) AS delivery_money",                      // 支付运费
            "IFNULL(SUM(refund_delivery_money), 0) AS refund_delivery_money",        // 退还运费
            "IFNULL(SUM(mall_service_money), 0) AS mall_service_money",              // 支付手续费
            "IFNULL(SUM(mall_refund_service_money), 0) AS mall_refund_service_money",// 退还手续费
            "IFNULL(SUM(recharge_money), 0) AS recharge_money",                      // 充值金额
            "IFNULL(SUM(recharge_pay_money), 0) AS recharge_pay_money",              // 充值消费金额

            "IFNULL(SUM(supply_money), 0) AS supply_money",              // 支付供应商货款
            "IFNULL(SUM(supply_refund_money), 0) AS supply_refund_money",// 退还供应商货款
            "IFNULL(SUM(supply_separate_money), 0) AS supply_separate_money",                      // 供货商支付分账
            "IFNULL(SUM(supply_refund_separate_money), 0) AS supply_refund_separate_money"              // 供货商退款分账
    );

    private StartChartVo setStartChartList(List<SysStat> resultList, String startDate, String endDate) {
        StartChartVo vo = new StartChartVo();
        //获取时间集合
        List<DateTime> timeDate = DateUtil.rangeToList(DateUtil.parseDate(startDate),DateUtil.parseDate(endDate), DateField.DAY_OF_MONTH);
        List<String> time = new LinkedList<>();
        List<Long> orderNum = new LinkedList<>();
        List<String> saleMoney = new LinkedList<>();
        List<String> refundMoney = new LinkedList<>();
        List<Long> accessSum = new LinkedList<>();
        List<String> orderReceivableMoney = new LinkedList<>();
        List<String> orderActualMoney = new LinkedList<>();
        List<String> mallCouponMoney = new LinkedList<>();
        List<String> mallRefundCouponMoney = new LinkedList<>();
        List<String> orderRefundMoney = new LinkedList<>();
        List<String> orderActualRefundMoney = new LinkedList<>();
        List<String> shopSeparateMoney = new LinkedList<>();
        List<String> shopRefundSeparateMoney = new LinkedList<>();
        List<String> shopCommission = new LinkedList<>();
        List<String> shopRefundCommission = new LinkedList<>();
        List<String> deliveryMoney = new LinkedList<>();
        List<String> refundDeliveryMoney = new LinkedList<>();
        List<String> mallServiceMoney = new LinkedList<>();
        List<String> mallRefundServiceMoney = new LinkedList<>();
        List<String> rechargeMoney = new LinkedList<>();
        List<String> rechargePayMoney = new LinkedList<>();

        for (DateTime date : timeDate){
            String dateStr = DateUtil.formatDate(date);
            List<SysStat> sysStatList = resultList.stream().filter(item -> item.getDate().equals(dateStr)).toList();
            time.add(dateStr);
            if (!sysStatList.isEmpty()) {
                SysStat sysStat = sysStatList.get(0);
                orderNum.add(ObjectUtil.isNotEmpty(sysStat.getOrderNum()) ? Long.parseLong(sysStat.getOrderNum().toString()) : 0L);
                saleMoney.add(ObjectUtil.isNotEmpty(sysStat.getSaleMoney()) ? sysStat.getSaleMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                refundMoney.add(ObjectUtil.isNotEmpty(sysStat.getRefundMoney()) ? sysStat.getRefundMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                accessSum.add(ObjectUtil.isNotEmpty(sysStat.getAccessSum()) ? Long.parseLong(sysStat.getAccessSum().toString()) : 0L);
                orderReceivableMoney.add(ObjectUtil.isNotEmpty(sysStat.getOrderReceivableMoney()) ? sysStat.getOrderReceivableMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                orderActualMoney.add(ObjectUtil.isNotEmpty(sysStat.getOrderActualMoney()) ? sysStat.getOrderActualMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                mallCouponMoney.add(ObjectUtil.isNotEmpty(sysStat.getMallCouponMoney()) ? sysStat.getMallCouponMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                mallRefundCouponMoney.add(ObjectUtil.isNotEmpty(sysStat.getMallRefundCouponMoney()) ? sysStat.getMallRefundCouponMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                orderRefundMoney.add(ObjectUtil.isNotEmpty(sysStat.getOrderRefundMoney()) ? sysStat.getOrderRefundMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                orderActualRefundMoney.add(ObjectUtil.isNotEmpty(sysStat.getOrderActualRefundMoney()) ? sysStat.getOrderActualRefundMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                shopSeparateMoney.add(ObjectUtil.isNotEmpty(sysStat.getShopSeparateMoney()) ? sysStat.getShopSeparateMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                shopRefundSeparateMoney.add(ObjectUtil.isNotEmpty(sysStat.getShopRefundSeparateMoney()) ? sysStat.getShopRefundSeparateMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                shopCommission.add(ObjectUtil.isNotEmpty(sysStat.getShopCommission()) ? sysStat.getShopCommission().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                shopRefundCommission.add(ObjectUtil.isNotEmpty(sysStat.getShopRefundCommission()) ? sysStat.getShopRefundCommission().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                deliveryMoney.add(ObjectUtil.isNotEmpty(sysStat.getDeliveryMoney()) ? sysStat.getDeliveryMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                refundDeliveryMoney.add(ObjectUtil.isNotEmpty(sysStat.getRefundDeliveryMoney()) ? sysStat.getRefundDeliveryMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                mallServiceMoney.add(ObjectUtil.isNotEmpty(sysStat.getMallServiceMoney()) ? sysStat.getMallServiceMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                mallRefundServiceMoney.add(ObjectUtil.isNotEmpty(sysStat.getMallRefundServiceMoney()) ? sysStat.getMallRefundServiceMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                rechargeMoney.add(ObjectUtil.isNotEmpty(sysStat.getRechargeMoney()) ? sysStat.getRechargeMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
                rechargePayMoney.add(ObjectUtil.isNotEmpty(sysStat.getRechargePayMoney()) ? sysStat.getRechargePayMoney().setScale(2, RoundingMode.HALF_UP ).toString() : "0.00");
            }else{
                orderNum.add(0L);
                saleMoney.add("0.00");
                refundMoney.add("0.00");
                accessSum.add(0L);
                orderReceivableMoney.add("0.00");
                orderActualMoney.add("0.00");
                mallCouponMoney.add("0.00");
                mallRefundCouponMoney.add("0.00");
                orderRefundMoney.add("0.00");
                shopSeparateMoney.add("0.00");
                shopRefundSeparateMoney.add("0.00");
                shopCommission.add("0.00");
                shopRefundCommission.add("0.00");
                deliveryMoney.add("0.00");
                refundDeliveryMoney.add("0.00");
                mallServiceMoney.add("0.00");
                mallRefundServiceMoney.add("0.00");
                rechargeMoney.add("0.00");
                rechargePayMoney.add("0.00");
            }
        }
        vo.setTime( time);
        vo.setOrderNum(orderNum);
        vo.setSaleMoney(saleMoney);
        vo.setRefundMoney(refundMoney);
        vo.setAccessSum(accessSum);
        vo.setOrderReceivableMoney(orderReceivableMoney);
        vo.setOrderActualMoney(orderActualMoney);
        vo.setMallCouponMoney(mallCouponMoney);
        vo.setMallRefundCouponMoney(mallRefundCouponMoney);
        vo.setOrderRefundMoney(orderRefundMoney);
        vo.setOrderActualRefundMoney(orderActualRefundMoney);
        vo.setShopSeparateMoney(shopSeparateMoney);
        vo.setShopRefundSeparateMoney(shopRefundSeparateMoney);
        vo.setShopCommission(shopCommission);
        vo.setShopRefundCommission(shopRefundCommission);
        vo.setDeliveryMoney(deliveryMoney);
        vo.setRefundDeliveryMoney(refundDeliveryMoney);
        vo.setMallServiceMoney(mallServiceMoney);
        vo.setMallRefundServiceMoney(mallRefundServiceMoney);
        vo.setRechargeMoney(rechargeMoney);
        vo.setRechargePayMoney(rechargePayMoney);
        return vo;
    }
}