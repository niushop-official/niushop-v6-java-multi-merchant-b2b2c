package com.niu.core.service.core.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.component.context.task.ThreadPoolManager;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.stat.SysStat;
import com.niu.core.mapper.stat.SysStatMapper;
import com.niu.core.service.core.sys.ICoreSysStatService;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import com.niu.core.service.core.sys.vo.SysDataVo;
import com.niu.core.service.core.sys.vo.SysStatListVo;
import com.niu.core.service.core.sys.vo.SysStatVo;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Override
    public void addStat(SysDataAddParam sysDataAddParam) {
        // 获取当前日期信息
        LocalDate currentDate = LocalDate.now();
        String date = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String month = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        long dateTime = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;
        ThreadPoolManager.runTask(() -> {
            lock.lock();
            try {
                // 查询是否存在记录
                SysStat stat = sysStatMapper.selectOne(new QueryWrapper<SysStat>().eq("date", date).eq("month", month).eq("date_time", dateTime).eq("site_id", sysDataAddParam.getSiteId()).last("Limit 1"));

                if (stat == null) {
                    // 记录不存在，创建新记录
                    stat = new SysStat();
                    BeanUtil.copyProperties(sysDataAddParam, stat);
                    stat.setDate(date);
                    stat.setMonth(month);
                    stat.setDateTime(dateTime);
                    stat.setSiteId(sysDataAddParam.getSiteId());

                    // 保存新记录
                    sysStatMapper.insert(stat);
                } else {
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getAccessSum()) && sysDataAddParam.getAccessSum() > 0){
                        stat.setAccessSum(stat.getAccessSum() + sysDataAddParam.getAccessSum());
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getVisitNum()) && sysDataAddParam.getVisitNum() > 0){
                        stat.setVisitNum(stat.getVisitNum() + sysDataAddParam.getVisitNum());
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getDeliveryMoney()) && sysDataAddParam.getDeliveryMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setDeliveryMoney(stat.getDeliveryMoney().add(sysDataAddParam.getDeliveryMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getMallCouponMoney()) && sysDataAddParam.getMallCouponMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setMallCouponMoney(stat.getMallCouponMoney().add(sysDataAddParam.getMallCouponMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getMallRefundCouponMoney()) && sysDataAddParam.getMallRefundCouponMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setMallRefundCouponMoney(stat.getMallRefundCouponMoney().add(sysDataAddParam.getMallRefundCouponMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getMallServiceMoney()) && sysDataAddParam.getMallServiceMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setMallServiceMoney(stat.getMallServiceMoney().add(sysDataAddParam.getMallServiceMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getMallRefundServiceMoney()) && sysDataAddParam.getMallRefundServiceMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setMallRefundServiceMoney(stat.getMallRefundServiceMoney().add(sysDataAddParam.getMallRefundServiceMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getOrderActualMoney()) && sysDataAddParam.getOrderActualMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setOrderActualMoney(stat.getOrderActualMoney().add(sysDataAddParam.getOrderActualMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getOrderActualRefundMoney()) && sysDataAddParam.getOrderActualRefundMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setOrderActualRefundMoney(stat.getOrderActualRefundMoney().add(sysDataAddParam.getOrderActualRefundMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getOrderNum()) && sysDataAddParam.getOrderNum() > 0){
                        Integer oldOrderNum = stat.getOrderNum();
                        Integer newOrderNum = (oldOrderNum == null ? 0 : oldOrderNum) + sysDataAddParam.getOrderNum();
                        stat.setOrderNum(newOrderNum);
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getOrderReceivableMoney()) && sysDataAddParam.getOrderReceivableMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setOrderReceivableMoney(stat.getOrderReceivableMoney().add(sysDataAddParam.getOrderReceivableMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getOrderRefundMoney()) && sysDataAddParam.getOrderRefundMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setOrderRefundMoney(stat.getOrderRefundMoney().add(sysDataAddParam.getOrderRefundMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getRechargeMoney()) && sysDataAddParam.getRechargeMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setRechargeMoney(stat.getRechargeMoney().add(sysDataAddParam.getRechargeMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getRechargePayMoney()) && sysDataAddParam.getRechargePayMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setRechargePayMoney(stat.getRechargePayMoney().add(sysDataAddParam.getRechargePayMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getRechargeOrderMoney()) && sysDataAddParam.getRechargeOrderMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setRechargeOrderMoney(stat.getRechargeOrderMoney().add(sysDataAddParam.getRechargeOrderMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getRechargeGiveMoney()) && sysDataAddParam.getRechargeGiveMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setRechargeGiveMoney(stat.getRechargeGiveMoney().add(sysDataAddParam.getRechargeGiveMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getRefundDeliveryMoney()) && sysDataAddParam.getRefundDeliveryMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setRefundDeliveryMoney(stat.getRefundDeliveryMoney().add(sysDataAddParam.getRefundDeliveryMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getRefundMoney()) && sysDataAddParam.getRefundMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setRefundMoney(stat.getRefundMoney().add(sysDataAddParam.getRefundMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getSaleMoney()) && sysDataAddParam.getSaleMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setSaleMoney(stat.getSaleMoney().add(sysDataAddParam.getSaleMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getShopCommission()) && sysDataAddParam.getShopCommission().compareTo(BigDecimal.ZERO) > 0){
                        stat.setShopCommission(stat.getShopCommission().add(sysDataAddParam.getShopCommission()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getShopRefundCommission()) && sysDataAddParam.getShopRefundCommission().compareTo(BigDecimal.ZERO) > 0){
                        stat.setShopRefundCommission(stat.getShopRefundCommission().add(sysDataAddParam.getShopRefundCommission()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getShopSeparateMoney()) && sysDataAddParam.getShopSeparateMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setShopSeparateMoney(stat.getShopSeparateMoney().add(sysDataAddParam.getShopSeparateMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getShopRefundSeparateMoney()) && sysDataAddParam.getShopRefundSeparateMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setShopRefundSeparateMoney(stat.getShopRefundSeparateMoney().add(sysDataAddParam.getShopRefundSeparateMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getSupplyMoney()) && sysDataAddParam.getSupplyMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setSupplyMoney(stat.getSupplyMoney().add(sysDataAddParam.getSupplyMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getSupplyRefundMoney()) && sysDataAddParam.getSupplyRefundMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setSupplyRefundMoney(stat.getSupplyRefundMoney().add(sysDataAddParam.getSupplyRefundMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getSupplySeparateMoney()) && sysDataAddParam.getSupplySeparateMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setSupplySeparateMoney(stat.getSupplySeparateMoney().add(sysDataAddParam.getSupplySeparateMoney()));
                    }
                    if (ObjectUtil.isNotEmpty(sysDataAddParam.getSupplyRefundSeparateMoney()) && sysDataAddParam.getSupplyRefundSeparateMoney().compareTo(BigDecimal.ZERO) > 0){
                        stat.setSupplyRefundSeparateMoney(stat.getSupplyRefundSeparateMoney().add(sysDataAddParam.getSupplyRefundSeparateMoney()));
                    }
                    sysStatMapper.updateById(stat);
                }
            } catch (Exception e) {
                log.error("同步统计失败{}",e.getMessage());
            } finally {
                lock.unlock();  // 确保锁被释放
            }
        });
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
            if (!statMap.containsKey(date)) {
                vo.getAccessSum().add(statMap.get(date).getAccessSum());
                vo.getMallCouponMoney().add(statMap.get(date).getMallCouponMoney());
                vo.getDeliveryMoney().add(statMap.get(date).getDeliveryMoney());
                vo.getMallRefundCouponMoney().add(statMap.get(date).getMallRefundCouponMoney());
                vo.getMallServiceMoney().add(statMap.get(date).getMallServiceMoney());
                vo.getMallRefundServiceMoney().add(statMap.get(date).getMallRefundServiceMoney());
                vo.getOrderActualMoney().add(statMap.get(date).getOrderActualMoney());
                vo.getOrderActualRefundMoney().add(statMap.get(date).getOrderActualRefundMoney());
                vo.getOrderNum().add(statMap.get(date).getOrderNum());
                vo.getOrderReceivableMoney().add(statMap.get(date).getOrderReceivableMoney());
                vo.getOrderRefundMoney().add(statMap.get(date).getOrderRefundMoney());
                vo.getRechargeMoney().add(statMap.get(date).getRechargeMoney());
                vo.getRechargePayMoney().add(statMap.get(date).getRechargePayMoney());
                vo.getRefundDeliveryMoney().add(statMap.get(date).getRefundDeliveryMoney());
                vo.getRefundMoney().add(statMap.get(date).getRefundMoney());
                vo.getSaleMoney().add(statMap.get(date).getSaleMoney());
                vo.getShopCommission().add(statMap.get(date).getShopCommission());
                vo.getShopRefundCommission().add(statMap.get(date).getShopRefundCommission());
                vo.getShopSeparateMoney().add(statMap.get(date).getShopSeparateMoney());
                vo.getShopRefundSeparateMoney().add(statMap.get(date).getShopRefundSeparateMoney());
                vo.getSupplyMoney().add(statMap.get(date).getSupplyMoney());
                vo.getSupplyRefundMoney().add(statMap.get(date).getSupplyRefundMoney());
                vo.getSupplySeparateMoney().add(statMap.get(date).getSupplySeparateMoney());
                vo.getSupplyRefundSeparateMoney().add(statMap.get(date).getSupplyRefundSeparateMoney());
            }else{
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

        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();
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

        List<SysStat> sysStats = new LinkedList<>();
        if(type.equals("day"))
        {
            queryWrapper.orderByDesc("date");
            sysStats = sysStatMapper.selectList(queryWrapper);
        }else if(type.equals("month"))
        {
            queryWrapper.orderByDesc("month");
            queryWrapper.groupBy("month");
            sysStats = sysStatMapper.selectList(queryWrapper);
        }
        IPage<SysStat> iPage = sysStatMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysStatListVo> list = new LinkedList<>();
        for (SysStat item : iPage.getRecords()) {
            SysStatListVo vo = new SysStatListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public SysStatListVo getStatDetail(Integer siteId, String date, String type) {
        QueryWrapper<SysStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        SysStat stat = null;

        if ("day".equals(type)) {
            // 日账单：查询指定日期的记录（直接查询实体对象）
            queryWrapper.eq("date", date);
            queryWrapper.orderByDesc("date");
            stat = sysStatMapper.selectOne(queryWrapper);
        } else {
            // 月账单：按月份分组汇总
            queryWrapper.eq("month", date);
            queryWrapper.select("site_id",
                    "month",
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
                    "IFNULL(sum(recharge_order_money), 0) as recharge_order_money",
                    "IFNULL(sum(recharge_pay_money), 0) as recharge_pay_money",
                    "IFNULL(sum(recharge_give_money), 0) as recharge_give_money",
                    "IFNULL(sum(supply_money), 0) as supply_money",
                    "IFNULL(sum(supply_refund_money), 0) as supply_refund_money",
                    "IFNULL(sum(supply_separate_money), 0) as supply_separate_money",
                    "IFNULL(sum(supply_refund_separate_money), 0) as supply_refund_separate_money");
            queryWrapper.groupBy("month");
            queryWrapper.orderByDesc("month");
            stat = sysStatMapper.selectOne(queryWrapper);
        }

        if (stat == null) {
            return new SysStatListVo();
        }

        // 构建VO对象（与项目其他方法保持一致）
        SysStatListVo vo = new SysStatListVo();
        BeanUtils.copyProperties(stat, vo);
        vo.setSiteId(stat.getSiteId());
        vo.setTime("day".equals(type) ? stat.getDate() : stat.getMonth());
        vo.setOrderNum(Optional.ofNullable(stat.getOrderNum()).orElse(0));
        vo.setSaleMoney(Optional.ofNullable(stat.getSaleMoney()).orElse(BigDecimal.ZERO));
        vo.setRefundMoney(Optional.ofNullable(stat.getRefundMoney()).orElse(BigDecimal.ZERO));
        vo.setAccessNum(Optional.ofNullable(stat.getAccessSum()).orElse(0));
        vo.setOrderReceivableMoney(Optional.ofNullable(stat.getOrderReceivableMoney()).orElse(BigDecimal.ZERO));
        vo.setOrderActualMoney(Optional.ofNullable(stat.getOrderActualMoney()).orElse(BigDecimal.ZERO));
        vo.setMallCouponMoney(Optional.ofNullable(stat.getMallCouponMoney()).orElse(BigDecimal.ZERO));
        vo.setMallRefundCouponMoney(Optional.ofNullable(stat.getMallRefundCouponMoney()).orElse(BigDecimal.ZERO));
        vo.setOrderRefundMoney(Optional.ofNullable(stat.getOrderRefundMoney()).orElse(BigDecimal.ZERO));
        vo.setOrderActualRefundMoney(Optional.ofNullable(stat.getOrderActualRefundMoney()).orElse(BigDecimal.ZERO));
        vo.setShopSeparateMoney(Optional.ofNullable(stat.getShopSeparateMoney()).orElse(BigDecimal.ZERO));
        vo.setShopRefundSeparateMoney(Optional.ofNullable(stat.getShopRefundSeparateMoney()).orElse(BigDecimal.ZERO));
        vo.setShopCommission(Optional.ofNullable(stat.getShopCommission()).orElse(BigDecimal.ZERO));
        vo.setShopRefundCommission(Optional.ofNullable(stat.getShopRefundCommission()).orElse(BigDecimal.ZERO));
        vo.setDeliveryMoney(Optional.ofNullable(stat.getDeliveryMoney()).orElse(BigDecimal.ZERO));
        vo.setRefundDeliveryMoney(Optional.ofNullable(stat.getRefundDeliveryMoney()).orElse(BigDecimal.ZERO));
        vo.setMallServiceMoney(Optional.ofNullable(stat.getMallServiceMoney()).orElse(BigDecimal.ZERO));
        vo.setMallRefundServiceMoney(Optional.ofNullable(stat.getMallRefundServiceMoney()).orElse(BigDecimal.ZERO));
        vo.setRechargeMoney(Optional.ofNullable(stat.getRechargeMoney()).orElse(BigDecimal.ZERO));
        vo.setRechargeOrderMoney(Optional.ofNullable(stat.getRechargeOrderMoney()).orElse(BigDecimal.ZERO));
        vo.setRechargePayMoney(Optional.ofNullable(stat.getRechargePayMoney()).orElse(BigDecimal.ZERO));
        vo.setRechargeGiveMoney(Optional.ofNullable(stat.getRechargeGiveMoney()).orElse(BigDecimal.ZERO));
        vo.setSupplyMoney(Optional.ofNullable(stat.getSupplyMoney()).orElse(BigDecimal.ZERO));
        vo.setSupplyRefundMoney(Optional.ofNullable(stat.getSupplyRefundMoney()).orElse(BigDecimal.ZERO));
        vo.setSupplySeparateMoney(Optional.ofNullable(stat.getSupplySeparateMoney()).orElse(BigDecimal.ZERO));
        vo.setSupplyRefundSeparateMoney(Optional.ofNullable(stat.getSupplyRefundSeparateMoney()).orElse(BigDecimal.ZERO));

        return vo;
    }
}
