package com.niu.core.service.core.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.component.context.task.ThreadPoolManager;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.stat.SysStat;
import com.niu.core.mapper.stat.SysStatMapper;
import com.niu.core.service.core.sys.ICoreSysStatService;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import com.niu.core.service.core.sys.param.SysDataDecParam;
import com.niu.core.service.core.sys.vo.SysDataVo;
import com.niu.core.service.core.sys.vo.SysStatListVo;
import com.niu.core.service.core.sys.vo.SysStatVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
@Log4j2
@Service
public class CoreSysStatServiceImpl implements ICoreSysStatService {

    @Resource
    SysStatMapper sysStatMapper;
    private static final ReentrantLock lock = new ReentrantLock();

    private static final String[] STAT_SUM_FIELDS = {
            "IFNULL(sum(order_num), 0) as order_num",
            "IFNULL(sum(sale_money), 0) as sale_money",
            "IFNULL(sum(refund_money), 0) as refund_money",
            "IFNULL(sum(access_sum), 0) as access_sum",
            "IFNULL(sum(order_receivable_money), 0) as order_receivable_money",
            "IFNULL(sum(order_actual_money), 0) as order_actual_money",
            "IFNULL(sum(mall_coupon_money), 0) as mall_coupon_money",
            "IFNULL(sum(mall_refund_coupon_money), 0) as mall_refund_coupon_money",
            "IFNULL(sum(order_refund_money), 0) as order_refund_money",
            "IFNULL(sum(order_actual_refund_money), 0) as order_actual_refund_money",
            "IFNULL(sum(shop_separate_money), 0) as shop_separate_money",
            "IFNULL(sum(shop_refund_separate_money), 0) as shop_refund_separate_money",
            "IFNULL(sum(shop_commission), 0) as shop_commission",
            "IFNULL(sum(shop_refund_commission), 0) as shop_refund_commission",
            "IFNULL(sum(delivery_money), 0) as delivery_money",
            "IFNULL(sum(refund_delivery_money), 0) as refund_delivery_money",
            "IFNULL(sum(mall_service_money), 0) as mall_service_money",
            "IFNULL(sum(mall_refund_service_money), 0) as mall_refund_service_money",
            "IFNULL(sum(recharge_money), 0) as recharge_money",
            "IFNULL(sum(recharge_pay_money), 0) as recharge_pay_money",
            "IFNULL(sum(recharge_order_money), 0) as recharge_order_money",
            "IFNULL(sum(recharge_give_money), 0) as recharge_give_money",
            "IFNULL(sum(supply_separate_money), 0) as supply_separate_money",
            "IFNULL(sum(supply_refund_separate_money), 0) as supply_refund_separate_money",
            "IFNULL(sum(supply_money), 0) as supply_money",
            "IFNULL(sum(supply_refund_money), 0) as supply_refund_money",
            "IFNULL(sum(circle_commission), 0) as circle_commission",
            "IFNULL(sum(circle_refund_commission), 0) as circle_refund_commission"
    };

    @Override
    public void addStat(SysDataAddParam sysDataAddParam) {
        ThreadPoolManager.runTask(() -> {
            lock.lock();
            try {
                doAddStat(sysDataAddParam);
            } catch (Exception e) {
                log.error("同步统计失败", e);
            } finally {
                lock.unlock();
            }
        });
    }

    private void doAddStat(SysDataAddParam param) {
        LocalDate currentDate = LocalDate.now();
        String date = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String month = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        long dateTime = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;

        SysStat stat = sysStatMapper.selectOne(new QueryWrapper<SysStat>()
                .eq("site_id", param.getSiteId())
                .eq("date", date)
                .last("LIMIT 1"));

        if (stat == null) {
            stat = new SysStat();
            BeanUtil.copyProperties(param, stat);
            stat.setDate(date);
            stat.setMonth(month);
            stat.setDateTime(dateTime);
            stat.setSiteId(param.getSiteId());
            sysStatMapper.insert(stat);
            return;
        }

        stat.setAccessSum(safeAddInt(stat.getAccessSum(), param.getAccessSum()));
        stat.setVisitNum(safeAddInt(stat.getVisitNum(), param.getVisitNum()));
        stat.setOrderNum(safeAddInt(stat.getOrderNum(), param.getOrderNum()));
        stat.setDeliveryMoney(safeAddDecimal(stat.getDeliveryMoney(), param.getDeliveryMoney()));
        stat.setMallCouponMoney(safeAddDecimal(stat.getMallCouponMoney(), param.getMallCouponMoney()));
        stat.setMallRefundCouponMoney(safeAddDecimal(stat.getMallRefundCouponMoney(), param.getMallRefundCouponMoney()));
        stat.setMallServiceMoney(safeAddDecimal(stat.getMallServiceMoney(), param.getMallServiceMoney()));
        stat.setMallRefundServiceMoney(safeAddDecimal(stat.getMallRefundServiceMoney(), param.getMallRefundServiceMoney()));
        stat.setOrderActualMoney(safeAddDecimal(stat.getOrderActualMoney(), param.getOrderActualMoney()));
        stat.setOrderActualRefundMoney(safeAddDecimal(stat.getOrderActualRefundMoney(), param.getOrderActualRefundMoney()));
        stat.setOrderReceivableMoney(safeAddDecimal(stat.getOrderReceivableMoney(), param.getOrderReceivableMoney()));
        stat.setOrderRefundMoney(safeAddDecimal(stat.getOrderRefundMoney(), param.getOrderRefundMoney()));
        stat.setRechargeMoney(safeAddDecimal(stat.getRechargeMoney(), param.getRechargeMoney()));
        stat.setRechargePayMoney(safeAddDecimal(stat.getRechargePayMoney(), param.getRechargePayMoney()));
        stat.setRechargeOrderMoney(safeAddDecimal(stat.getRechargeOrderMoney(), param.getRechargeOrderMoney()));
        stat.setRechargeGiveMoney(safeAddDecimal(stat.getRechargeGiveMoney(), param.getRechargeGiveMoney()));
        stat.setRefundDeliveryMoney(safeAddDecimal(stat.getRefundDeliveryMoney(), param.getRefundDeliveryMoney()));
        stat.setRefundMoney(safeAddDecimal(stat.getRefundMoney(), param.getRefundMoney()));
        stat.setSaleMoney(safeAddDecimal(stat.getSaleMoney(), param.getSaleMoney()));
        stat.setShopCommission(safeAddDecimal(stat.getShopCommission(), param.getShopCommission()));
        stat.setShopRefundCommission(safeAddDecimal(stat.getShopRefundCommission(), param.getShopRefundCommission()));
        stat.setShopSeparateMoney(safeAddDecimal(stat.getShopSeparateMoney(), param.getShopSeparateMoney()));
        stat.setShopRefundSeparateMoney(safeAddDecimal(stat.getShopRefundSeparateMoney(), param.getShopRefundSeparateMoney()));
        stat.setSupplyMoney(safeAddDecimal(stat.getSupplyMoney(), param.getSupplyMoney()));
        stat.setSupplyRefundMoney(safeAddDecimal(stat.getSupplyRefundMoney(), param.getSupplyRefundMoney()));
        stat.setSupplySeparateMoney(safeAddDecimal(stat.getSupplySeparateMoney(), param.getSupplySeparateMoney()));
        stat.setSupplyRefundSeparateMoney(safeAddDecimal(stat.getSupplyRefundSeparateMoney(), param.getSupplyRefundSeparateMoney()));
        stat.setCircleCommission(safeAddDecimal(stat.getCircleCommission(), param.getCircleCommission()));
        stat.setCircleRefundCommission(safeAddDecimal(stat.getCircleRefundCommission(), param.getCircleRefundCommission()));
        sysStatMapper.updateById(stat);
    }

    @Override
    public void decStat(SysDataDecParam param) {
        if (param == null || param.getSiteId() == null || param.getTime() == null) {
            return;
        }
        String date = DateUtils.timestampToString(param.getTime(), "yyyy-MM-dd");
        SysStat stat = sysStatMapper.selectOne(new QueryWrapper<SysStat>()
                .eq("site_id", param.getSiteId())
                .eq("date", date)
                .last("LIMIT 1"));
        if (stat == null) {
            return;
        }

        List<String> sqlSets = new LinkedList<>();
        addDecSql(sqlSets, "order_num", param.getOrderNum());
        addDecSql(sqlSets, "access_sum", param.getAccessSum());
        addDecSql(sqlSets, "visit_num", param.getVisitNum());
        addDecSql(sqlSets, "sale_money", param.getSaleMoney());
        addDecSql(sqlSets, "refund_money", param.getRefundMoney());
        addDecSql(sqlSets, "order_receivable_money", param.getOrderReceivableMoney());
        addDecSql(sqlSets, "order_actual_money", param.getOrderActualMoney());
        addDecSql(sqlSets, "mall_coupon_money", param.getMallCouponMoney());
        addDecSql(sqlSets, "mall_refund_coupon_money", param.getMallRefundCouponMoney());
        addDecSql(sqlSets, "order_refund_money", param.getOrderRefundMoney());
        addDecSql(sqlSets, "order_actual_refund_money", param.getOrderActualRefundMoney());
        addDecSql(sqlSets, "shop_separate_money", param.getShopSeparateMoney());
        addDecSql(sqlSets, "shop_refund_separate_money", param.getShopRefundSeparateMoney());
        addDecSql(sqlSets, "shop_commission", param.getShopCommission());
        addDecSql(sqlSets, "shop_refund_commission", param.getShopRefundCommission());
        addDecSql(sqlSets, "delivery_money", param.getDeliveryMoney());
        addDecSql(sqlSets, "refund_delivery_money", param.getRefundDeliveryMoney());
        addDecSql(sqlSets, "mall_service_money", param.getMallServiceMoney());
        addDecSql(sqlSets, "mall_refund_service_money", param.getMallRefundServiceMoney());
        addDecSql(sqlSets, "recharge_money", param.getRechargeMoney());
        addDecSql(sqlSets, "recharge_pay_money", param.getRechargePayMoney());
        addDecSql(sqlSets, "recharge_order_money", param.getRechargeOrderMoney());
        addDecSql(sqlSets, "recharge_give_money", param.getRechargeGiveMoney());
        addDecSql(sqlSets, "supply_money", param.getSupplyMoney());
        addDecSql(sqlSets, "supply_refund_money", param.getSupplyRefundMoney());
        addDecSql(sqlSets, "supply_separate_money", param.getSupplySeparateMoney());
        addDecSql(sqlSets, "supply_refund_separate_money", param.getSupplyRefundSeparateMoney());
        addDecSql(sqlSets, "circle_commission", param.getCircleCommission());
        addDecSql(sqlSets, "circle_refund_commission", param.getCircleRefundCommission());
        if (sqlSets.isEmpty()) {
            return;
        }

        UpdateWrapper<SysStat> wrapper = new UpdateWrapper<>();
        wrapper.eq("id", stat.getId());
        wrapper.setSql(String.join(", ", sqlSets));
        sysStatMapper.update(null, wrapper);
    }

    private Integer safeAddInt(Integer current, Integer delta) {
        if (delta == null || delta <= 0) {
            return current;
        }
        return Optional.ofNullable(current).orElse(0) + delta;
    }

    private BigDecimal safeAddDecimal(BigDecimal current, BigDecimal delta) {
        if (delta == null || delta.compareTo(BigDecimal.ZERO) <= 0) {
            return current;
        }
        return Optional.ofNullable(current).orElse(BigDecimal.ZERO).add(delta);
    }

    private void addDecSql(List<String> sqlSets, String column, Integer value) {
        if (value == null || value <= 0) {
            return;
        }
        sqlSets.add(column + " = CASE WHEN " + column + " - " + value + " >= 0 THEN " + column + " - " + value + " ELSE 0 END");
    }

    private void addDecSql(List<String> sqlSets, String column, BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        sqlSets.add(column + " = CASE WHEN " + column + " - " + value + " >= 0 THEN " + column + " - " + value + " ELSE 0 END");
    }

    private void applyAggregateSelect(QueryWrapper<SysStat> queryWrapper, String groupColumn) {
        List<String> selectColumns = new LinkedList<>();
        selectColumns.add("site_id");
        selectColumns.add(groupColumn);
        selectColumns.addAll(Arrays.asList(STAT_SUM_FIELDS));
        queryWrapper.select(selectColumns.toArray(new String[0]));
    }

    private void applyDateCondition(QueryWrapper<SysStat> queryWrapper, String startDate, String endDate) {
        long startTime = DateUtils.StringToTimestamp(startDate);
        long endTime = DateUtils.StringToTimestamp(endDate);
        if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            queryWrapper.ge("date_time", startTime);
            queryWrapper.le("date_time", endTime);
        } else if (StringUtils.isNotEmpty(startDate)) {
            queryWrapper.eq("date_time", startTime);
        } else {
            queryWrapper.gt("date_time", 0);
        }
    }

    @Override
    public SysDataVo getStatData(Integer siteId, String startDate, String endDate) {
        long startTime = DateUtils.StringToTimestamp(startDate);
        long endTime = DateUtils.StringToTimestamp(endDate);

        List<SysStat> statData = new LinkedList<>();
        if(siteId == 0) {
            statData = sysStatMapper.selectList(new QueryWrapper<SysStat>().ge("date_time", startTime).le("date_time", endTime));
        }else{
            statData = sysStatMapper.selectList(new QueryWrapper<SysStat>().eq("site_id", siteId).ge("date_time", startTime).le("date_time", endTime));
        }

        Map<String, SysStat> statMap = statData.stream().collect(Collectors.toMap(SysStat::getDate, Function.identity()));
        int day = (int) ((endTime - startTime) / (60 * 60 * 24));

        SysDataVo vo = new SysDataVo();

        for (int i = 0; i <= day; i++) {
            String date = DateUtils.timestampToString(startTime + ((long) i * 60 * 60 * 24), "yyyy-MM-dd");
            vo.getTime().add(date);
            if (statMap.containsKey(date)) {
                SysStat dayStat = statMap.get(date);
                vo.getAccessSum().add(dayStat.getAccessSum());
                vo.getMallCouponMoney().add(dayStat.getMallCouponMoney());
                vo.getDeliveryMoney().add(dayStat.getDeliveryMoney());
                vo.getMallRefundCouponMoney().add(dayStat.getMallRefundCouponMoney());
                vo.getMallServiceMoney().add(dayStat.getMallServiceMoney());
                vo.getMallRefundServiceMoney().add(dayStat.getMallRefundServiceMoney());
                vo.getOrderActualMoney().add(dayStat.getOrderActualMoney());
                vo.getOrderActualRefundMoney().add(dayStat.getOrderActualRefundMoney());
                vo.getOrderNum().add(dayStat.getOrderNum());
                vo.getOrderReceivableMoney().add(dayStat.getOrderReceivableMoney());
                vo.getOrderRefundMoney().add(dayStat.getOrderRefundMoney());
                vo.getRechargeMoney().add(dayStat.getRechargeMoney());
                vo.getRechargePayMoney().add(dayStat.getRechargePayMoney());
                vo.getRefundDeliveryMoney().add(dayStat.getRefundDeliveryMoney());
                vo.getRefundMoney().add(dayStat.getRefundMoney());
                vo.getSaleMoney().add(dayStat.getSaleMoney());
                vo.getShopCommission().add(dayStat.getShopCommission());
                vo.getShopRefundCommission().add(dayStat.getShopRefundCommission());
                vo.getShopSeparateMoney().add(dayStat.getShopSeparateMoney());
                vo.getShopRefundSeparateMoney().add(dayStat.getShopRefundSeparateMoney());
                vo.getSupplyMoney().add(dayStat.getSupplyMoney());
                vo.getSupplyRefundMoney().add(dayStat.getSupplyRefundMoney());
                vo.getSupplySeparateMoney().add(dayStat.getSupplySeparateMoney());
                vo.getSupplyRefundSeparateMoney().add(dayStat.getSupplyRefundSeparateMoney());
            } else {
                vo.getAccessSum().add(0);
                vo.getMallCouponMoney().add(BigDecimal.ZERO);
                vo.getDeliveryMoney().add(BigDecimal.ZERO);
                vo.getMallRefundCouponMoney().add(BigDecimal.ZERO);
                vo.getMallServiceMoney().add(BigDecimal.ZERO);
                vo.getMallRefundServiceMoney().add(BigDecimal.ZERO);
                vo.getOrderActualMoney().add(BigDecimal.ZERO);
                vo.getOrderActualRefundMoney().add(BigDecimal.ZERO);
                vo.getOrderNum().add(0);
                vo.getOrderReceivableMoney().add(BigDecimal.ZERO);
                vo.getOrderRefundMoney().add(BigDecimal.ZERO);
                vo.getRechargeMoney().add(BigDecimal.ZERO);
                vo.getRechargePayMoney().add(BigDecimal.ZERO);
                vo.getRefundDeliveryMoney().add(BigDecimal.ZERO);
                vo.getRefundMoney().add(BigDecimal.ZERO);
                vo.getSaleMoney().add(BigDecimal.ZERO);
                vo.getShopCommission().add(BigDecimal.ZERO);
                vo.getShopRefundCommission().add(BigDecimal.ZERO);
                vo.getShopSeparateMoney().add(BigDecimal.ZERO);
                vo.getShopRefundSeparateMoney().add(BigDecimal.ZERO);
                vo.getSupplyMoney().add(BigDecimal.ZERO);
                vo.getSupplyRefundMoney().add(BigDecimal.ZERO);
                vo.getSupplySeparateMoney().add(BigDecimal.ZERO);
                vo.getSupplyRefundSeparateMoney().add(BigDecimal.ZERO);
            }
        }


        return vo;
    }

    @Override
    public SysStatVo getStat(Integer siteId, String startDate, String endDate) {

        long startTime = DateUtils.StringToTimestamp(startDate);
        long endTime = DateUtils.StringToTimestamp(endDate);
        QueryWrapper<SysStat> queryWrapper = new QueryWrapper<>();
        if(ObjectUtil.isNotEmpty(siteId))
        {
            queryWrapper.eq("site_id", siteId);
        }
        if(ObjectUtil.isNotEmpty(startDate) && ObjectUtil.isNotEmpty(endDate))
        {
            queryWrapper.between("date_time", startTime, endTime);
        }else if(ObjectUtil.isNotEmpty(startDate))
        {
            queryWrapper.ge("date_time", startTime);
        }else{
            queryWrapper.ge("date_time", 0);
        }
        queryWrapper.select("IFNULL(sum(sale_money), 0) AS sale_money, " +
                        "IFNULL(sum(access_sum), 0) AS access_sum, " +
                        "IFNULL(sum(order_receivable_money), 0) AS order_receivable_money," +
                        "IFNULL(sum(order_actual_money), 0) AS order_actual_money," +
                        "IFNULL(sum(mall_coupon_money), 0) AS mall_coupon_money," +
                        "IFNULL(sum(mall_refund_coupon_money), 0) AS mall_refund_coupon_money," +
                        "IFNULL(sum(order_refund_money), 0) AS order_refund_money," +
                        "IFNULL(sum(order_actual_refund_money), 0) AS order_actual_refund_money," +
                        "IFNULL(sum(shop_separate_money), 0) AS shop_separate_money," +
                        "IFNULL(sum(shop_refund_separate_money), 0) AS shop_refund_separate_money," +
                        "IFNULL(sum(shop_commission), 0) AS shop_commission," +
                        "IFNULL(sum(shop_refund_commission), 0) AS shop_refund_commission," +
                        "IFNULL(sum(delivery_money), 0) AS delivery_money," +
                        "IFNULL(sum(refund_delivery_money), 0) AS refund_delivery_money," +
                        "IFNULL(sum(mall_service_money), 0) AS mall_service_money," +
                        "IFNULL(sum(mall_refund_service_money), 0) AS mall_refund_service_money," +
                        "IFNULL(sum(recharge_money), 0) AS recharge_money," +
                        "IFNULL(sum(recharge_pay_money), 0) AS recharge_pay_money," +
                        "IFNULL(sum(supply_money), 0) AS supply_money," +
                        "IFNULL(sum(supply_separate_money), 0) AS supply_separate_money," +
                        "IFNULL(sum(supply_refund_separate_money), 0) AS supply_refund_separate_money," +
                        "IFNULL(sum(supply_refund_money), 0) AS supply_refund_money, "+
                        "IFNULL(sum(order_num), 0) AS order_num, " +
                        "IFNULL(sum(refund_money), 0) AS refund_money " );
        SysStat sysStat = sysStatMapper.selectOne(queryWrapper);
        SysStatVo vo = new SysStatVo();
        if (sysStat != null) {
            BeanUtil.copyProperties(sysStat, vo);
        }
        vo.setSiteId(siteId);
        return vo;
    }

    @Override
    public PageResult<SysStatListVo> getStatList(PageParam pageParam, Integer siteId, String startDate, String endDate, String type) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        QueryWrapper<SysStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        applyDateCondition(queryWrapper, startDate, endDate);

        if (!"month".equals(type)) {
            type = "day";
        }

        if ("day".equals(type)) {
            applyAggregateSelect(queryWrapper, "date");
            queryWrapper.groupBy("date");
            queryWrapper.orderByDesc("date");
        } else {
            applyAggregateSelect(queryWrapper, "month");
            queryWrapper.groupBy("month");
            queryWrapper.orderByDesc("month");
        }

        IPage<SysStat> iPage = sysStatMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysStatListVo> list = new LinkedList<>();
        for (SysStat item : iPage.getRecords()) {
            list.add(toStatListVo(item, type));
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public SysStatListVo getStatDetail(Integer siteId, String date, String type) {
        QueryWrapper<SysStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        SysStat stat;

        if ("day".equals(type)) {
            queryWrapper.eq("date", date);
            applyAggregateSelect(queryWrapper, "date");
            queryWrapper.groupBy("date");
            queryWrapper.orderByDesc("date");
            stat = sysStatMapper.selectOne(queryWrapper);
        } else {
            queryWrapper.eq("month", date);
            applyAggregateSelect(queryWrapper, "month");
            queryWrapper.groupBy("month");
            queryWrapper.orderByDesc("month");
            stat = sysStatMapper.selectOne(queryWrapper);
        }

        if (stat == null) {
            return new SysStatListVo();
        }
        return toStatListVo(stat, type);
    }

    private SysStatListVo toStatListVo(SysStat stat, String type) {
        SysStatListVo vo = new SysStatListVo();
        BeanUtils.copyProperties(stat, vo);
        vo.setSiteId(stat.getSiteId());
        if (StringUtils.isNotEmpty(stat.getDate())) {
            vo.setTime(stat.getDate());
        } else if (StringUtils.isNotEmpty(stat.getMonth())) {
            vo.setTime(stat.getMonth());
        }
        vo.setAccessNum(Optional.ofNullable(stat.getAccessSum()).orElse(0));
        buildFinanceStatVo(vo, type);
        return vo;
    }

    /**
     * 账单展示字段计算，对齐 PHP CoreStatService::getStatList / getStatDetail
     */
    private void buildFinanceStatVo(SysStatListVo vo, String type) {
        Integer siteId = vo.getSiteId();

        BigDecimal shopSeparateMoney = Optional.ofNullable(vo.getShopSeparateMoney()).orElse(BigDecimal.ZERO);
        BigDecimal shopRefundSeparateMoney = Optional.ofNullable(vo.getShopRefundSeparateMoney()).orElse(BigDecimal.ZERO);
        BigDecimal separateMoney = shopSeparateMoney.subtract(shopRefundSeparateMoney).setScale(2, RoundingMode.HALF_UP);
        vo.setSeparateMoney(separateMoney);

        BigDecimal shopCommission = Optional.ofNullable(vo.getShopCommission()).orElse(BigDecimal.ZERO);
        BigDecimal shopRefundCommission = Optional.ofNullable(vo.getShopRefundCommission()).orElse(BigDecimal.ZERO);
        BigDecimal commission = shopCommission.subtract(shopRefundCommission).setScale(2, RoundingMode.HALF_UP);
        vo.setCommission(commission);

        BigDecimal deliveryMoney = Optional.ofNullable(vo.getDeliveryMoney()).orElse(BigDecimal.ZERO);
        BigDecimal refundDeliveryMoney = Optional.ofNullable(vo.getRefundDeliveryMoney()).orElse(BigDecimal.ZERO);
        BigDecimal shippingFee = deliveryMoney.subtract(refundDeliveryMoney).setScale(2, RoundingMode.HALF_UP);
        vo.setShippingFee(shippingFee);

        BigDecimal mallServiceMoney = Optional.ofNullable(vo.getMallServiceMoney()).orElse(BigDecimal.ZERO);
        BigDecimal mallRefundServiceMoney = Optional.ofNullable(vo.getMallRefundServiceMoney()).orElse(BigDecimal.ZERO);
        BigDecimal serviceMoney = mallServiceMoney.subtract(mallRefundServiceMoney).setScale(2, RoundingMode.HALF_UP);
        vo.setServiceMoney(serviceMoney);

        BigDecimal orderActualMoney = Optional.ofNullable(vo.getOrderActualMoney()).orElse(BigDecimal.ZERO);
        BigDecimal orderReceivableMoney = Optional.ofNullable(vo.getOrderReceivableMoney()).orElse(BigDecimal.ZERO);
        BigDecimal orderRefundMoney = Optional.ofNullable(vo.getOrderRefundMoney()).orElse(BigDecimal.ZERO);
        BigDecimal incomeMoney;
        if (siteId != null && siteId == 0) {
            incomeMoney = orderActualMoney.subtract(orderRefundMoney).setScale(2, RoundingMode.HALF_UP);
        } else {
            incomeMoney = orderReceivableMoney.subtract(orderRefundMoney).setScale(2, RoundingMode.HALF_UP);
        }
        vo.setIncomeMoney(incomeMoney);

        BigDecimal supplyMoney = Optional.ofNullable(vo.getSupplyMoney()).orElse(BigDecimal.ZERO);
        BigDecimal supplyRefundMoney = Optional.ofNullable(vo.getSupplyRefundMoney()).orElse(BigDecimal.ZERO);
        vo.setSupplyGoodsMoney(supplyMoney.subtract(supplyRefundMoney).setScale(2, RoundingMode.HALF_UP));

        BigDecimal supplySeparateMoney = Optional.ofNullable(vo.getSupplySeparateMoney()).orElse(BigDecimal.ZERO);
        BigDecimal supplyRefundSeparateMoney = Optional.ofNullable(vo.getSupplyRefundSeparateMoney()).orElse(BigDecimal.ZERO);
        vo.setSupplyAllSeparateMoney(supplySeparateMoney.subtract(supplyRefundSeparateMoney).setScale(2, RoundingMode.HALF_UP));

        if (vo.getOrderRefundMoney() != null) {
            vo.setOrderRefundMoney(vo.getOrderRefundMoney().negate());
        }
        if (vo.getOrderActualRefundMoney() != null) {
            vo.setOrderActualRefundMoney(vo.getOrderActualRefundMoney().negate());
        }
        if (vo.getShopRefundSeparateMoney() != null) {
            vo.setShopRefundSeparateMoney(vo.getShopRefundSeparateMoney().negate());
        }
        if (vo.getShopRefundCommission() != null) {
            vo.setShopRefundCommission(vo.getShopRefundCommission().negate());
        }
        if (vo.getRefundDeliveryMoney() != null) {
            vo.setRefundDeliveryMoney(vo.getRefundDeliveryMoney().negate());
        }
        if (vo.getMallRefundServiceMoney() != null) {
            vo.setMallRefundServiceMoney(vo.getMallRefundServiceMoney().negate());
        }
        if (vo.getMallCouponMoney() != null) {
            vo.setMallCouponMoney(siteId != null && siteId == 0 ? vo.getMallCouponMoney().negate() : vo.getMallCouponMoney());
        }
        if (vo.getMallRefundCouponMoney() != null) {
            vo.setMallRefundCouponMoney(siteId != null && siteId == 0 ? vo.getMallRefundCouponMoney() : vo.getMallRefundCouponMoney().negate());
        }
        if (vo.getSupplyRefundMoney() != null) {
            vo.setSupplyRefundMoney(vo.getSupplyRefundMoney().negate());
        }
        if (vo.getSupplyRefundSeparateMoney() != null) {
            vo.setSupplyRefundSeparateMoney(vo.getSupplyRefundSeparateMoney().negate());
        }

        BigDecimal outlayMoney;
        if (siteId != null && siteId == 0) {
            outlayMoney = separateMoney.add(commission).add(shippingFee).add(vo.getSupplyAllSeparateMoney()).setScale(2, RoundingMode.HALF_UP);
        } else {
            outlayMoney = serviceMoney.add(commission).add(vo.getSupplyGoodsMoney()).setScale(2, RoundingMode.HALF_UP);
        }
        vo.setOutlayMoney(outlayMoney);
        vo.setBalanceMoney(incomeMoney.subtract(outlayMoney).setScale(2, RoundingMode.HALF_UP));
        vo.setType(type);
    }
}
