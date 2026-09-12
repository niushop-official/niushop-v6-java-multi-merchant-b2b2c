package com.niu.core.service.admin.stat.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberCashOut;
import com.niu.core.entity.shop.SiteShop;
import com.niu.core.entity.shop.SiteShopCashOut;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.stat.SysStat;
import com.niu.core.enums.member.MemberCashOutStatusEnum;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.event.order.OrderPageEventDefiner;
import com.niu.core.mapper.member.MemberCashOutMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.shop.SiteShopCashOutMapper;
import com.niu.core.mapper.shop.SiteShopMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.stat.SysStatMapper;
import com.niu.core.service.admin.article.IArticleService;
import com.niu.core.service.admin.article.param.ArticeSearchParam;
import com.niu.core.service.admin.article.vo.ArticleListVo;
import com.niu.core.service.admin.order.param.MallOrderSearchParam;
import com.niu.core.service.admin.stat.ISiteStatService;
import com.niu.core.service.admin.stat.IStatService;
import com.niu.core.service.admin.stat.param.SysStatSearchParam;
import com.niu.core.service.admin.stat.vo.*;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.sys.ICoreSysStatService;
import com.niu.core.service.core.sys.vo.SysStatVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class StatServiceImpl implements IStatService {

    @Resource
    SysStatMapper sysStatMapper;
    @Resource
    IArticleService articleService;
    @Resource
    MemberMapper memberMapper;
    @Resource
    SiteMapper siteMapper;
    @Resource
    SiteShopMapper siteShopMapper;
    @Resource
    MemberCashOutMapper memberCashOutMapper;
    @Resource
    SiteShopCashOutMapper siteShopCashOutMapper;
    @Resource
    ISiteStatService siteStatService;
    @Resource
    ICoreSysStatService coreSysStatService;

    /**
     * 首页统计数据
     *
     * @return
     */
    @Override
    public StatInfoVo getIndexData() {
        StatInfoVo statInfoVo = new StatInfoVo();

        //实现获取首页数据集
        String[] todayTime = DateUtils.getStartEndByToday(new Date());
        LocalDate localDate = LocalDate.now().minusDays(+1);
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        String[] yesterdayTime = DateUtils.getStartEndByToday(Date.from(zonedDateTime.toInstant()));


        StatInfoVo.OrderStat orderStat = new StatInfoVo.OrderStat();

        OrderPageEventDefiner.OrderPageEvent event = new OrderPageEventDefiner.OrderPageEvent();
        event.setPageParam(new PageParam());
        event.setSearchParam(new MallOrderSearchParam());
        event.setName("OrderPageEvent");
        List<OrderPageEventDefiner.OrderEventResult> eventResults = EventAndSubscribeOfPublisher.publishAndCallback(event);
        if(eventResults.isEmpty()){
            eventResults = new ArrayList<>();
            eventResults.add(new OrderPageEventDefiner.OrderEventResult());
        }

        orderStat.setTodayOrderCount(eventResults.get(0).getTodayOrderCount());
        orderStat.setTotalOrderCount(eventResults.get(0).getTotalOrderCount());
        orderStat.setYesterdayOrderCount(eventResults.get(0).getYesterdayOrderCount());
        statInfoVo.setOrderStat(orderStat);

        StatInfoVo.SaleStat saleStat = new StatInfoVo.SaleStat();
        saleStat.setTodaySaleMoney(eventResults.get(0).getTodaySaleMoney());
        saleStat.setYesterdaySaleMoney(eventResults.get(0).getYesterdaySaleMoney());
        saleStat.setTotalSaleMoney(eventResults.get(0).getTotalSaleMoney());
        statInfoVo.setSaleStat(saleStat);

        StatInfoVo.RefundStat refundStat = new StatInfoVo.RefundStat();
        refundStat.setTodayRefundMoney(eventResults.get(0).getTodayRefundMoney());
        refundStat.setTotalRefundMoney(eventResults.get(0).getTotalRefundMoney());
        refundStat.setYesterdayRefundMoney(eventResults.get(0).getYesterdayRefundMoney());
        statInfoVo.setRefundStat(refundStat);

        StatInfoVo.MyOrderStat myOrderStat = new StatInfoVo.MyOrderStat();
        myOrderStat.setRefundOrder(eventResults.get(0).getRefundOrder());
        myOrderStat.setWaitDeliveryOrder(eventResults.get(0).getWaitDeliveryOrder());
        myOrderStat.setWaitPayOrder(eventResults.get(0).getWaitPayOrder());
        myOrderStat.setWaitTakeOrder(eventResults.get(0).getWaitTakeOrder());
        statInfoVo.setMyOrderStat(myOrderStat);

        //总会员数
        StatInfoVo.MemberStat memberStat = new StatInfoVo.MemberStat();
        QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
        memberStat.setTotalMemberCount(memberMapper.selectCount(memberQueryWrapper));

        //今日会员数
        QueryMapperUtils.buildByTime(memberQueryWrapper, "create_time", todayTime);
        Long todayCount = memberMapper.selectCount(memberQueryWrapper);
        memberStat.setTodayMemberCount(todayCount);

        //昨日会员数
        QueryMapperUtils.buildByTime(memberQueryWrapper, "create_time", yesterdayTime);
        Long yesterdayCount = memberMapper.selectCount(memberQueryWrapper);
        memberStat.setYesterdayMemberCount(yesterdayCount);

        statInfoVo.setMemberStat(memberStat);

        //总站点数
        StatInfoVo.SiteStat siteStat = new StatInfoVo.SiteStat();
        QueryWrapper<Site> siteQueryWrapper = new QueryWrapper<>();
        siteQueryWrapper.ne("site_id", 0);
        siteStat.setTotalSiteCount(siteMapper.selectCount(siteQueryWrapper));
        //今日站点数
        QueryMapperUtils.buildByTime(siteQueryWrapper, "create_time", todayTime);
        siteStat.setTodaySiteCount(siteMapper.selectCount(siteQueryWrapper));
        //昨日站点数
        QueryMapperUtils.buildByTime(siteQueryWrapper, "create_time", yesterdayTime);
        siteStat.setYesterdaySiteCount(siteMapper.selectCount(siteQueryWrapper));
        statInfoVo.setSiteStat(siteStat);

        //会员提现
        StatInfoVo.CashOutStat cashOutStat = new StatInfoVo.CashOutStat();
        QueryWrapper<MemberCashOut> memberCashWrapper = new QueryWrapper<>();
        memberCashWrapper.in("status", MemberCashOutStatusEnum.WAIT_AUDIT.getStatus(),MemberCashOutStatusEnum.WAIT_TRANSFER.getStatus());
        cashOutStat.setMemberCount(memberCashOutMapper.selectCount(memberCashWrapper));

        //站点提现
        QueryWrapper<SiteShopCashOut> siteShopCashOutWrapper = new QueryWrapper<>();
        siteShopCashOutWrapper.in("status", ShopCashOutEnum.WAIT_AUDIT.getState(), ShopCashOutEnum.WAIT_TRANSFER.getState());
        cashOutStat.setSiteCount(siteShopCashOutMapper.selectCount(siteShopCashOutWrapper));
        statInfoVo.setCashOutStat(cashOutStat);

        StatInfoVo.GoodsStat goodsStat = new StatInfoVo.GoodsStat();
        goodsStat.setSaleGoodsNum(eventResults.get(0).getSaleGoodsNum());
        goodsStat.setAuditGoodsNum(eventResults.get(0).getAuditGoodsNum());
        statInfoVo.setGoodsStat(goodsStat);
        return statInfoVo;
    }

    public static void buildDateCondition(QueryWrapper<SysStat> wrapper, String[] dateRange) {
        if (dateRange != null && dateRange.length > 0) {
            String start = dateRange[0];
            String end = dateRange.length > 1 ? dateRange[1] : null;

            if (StringUtils.isNotBlank(start)) {
                if (StringUtils.isNotBlank(end)) {
                    wrapper.between("date", start, end);
                } else {
                    wrapper.ge("date", start);
                }
            } else if (StringUtils.isNotBlank(end)) {
                wrapper.le("date", end);
            }
        }
    }

    @Override
    public PageResult<SysStatListVo> financeAccountsList(PageParam pageParam, SysStatSearchParam searchParam) {
        String startDate = "";
        String endDate = "";
        if (searchParam.getDate() != null && searchParam.getDate().length > 0) {
            startDate = searchParam.getDate()[0];
            if (searchParam.getDate().length > 1) {
                endDate = searchParam.getDate()[1];
            }
        }
        Integer siteId = RequestUtils.siteId();
        String type = StringUtils.isNotEmpty(searchParam.getType()) ? searchParam.getType() : "day";

        PageResult<com.niu.core.service.core.sys.vo.SysStatListVo> coreResult =
                coreSysStatService.getStatList(pageParam, siteId, startDate, endDate, type);

        List<SysStatListVo> resultList = new ArrayList<>();
        if (coreResult.getData() != null) {
            for (com.niu.core.service.core.sys.vo.SysStatListVo coreVo : coreResult.getData()) {
                SysStatListVo vo = new SysStatListVo();
                BeanUtils.copyProperties(coreVo, vo);
                resultList.add(vo);
            }
        }
        return PageResult.build(coreResult.getCurrentPage(), coreResult.getPerPage(), coreResult.getTotal()).setData(resultList);
    }


    private BigDecimal sum(BigDecimal a, BigDecimal b) {
        return Optional.ofNullable(a).orElse(BigDecimal.ZERO)
                .add(Optional.ofNullable(b).orElse(BigDecimal.ZERO));
    }


    @Override
    public SysStatInfoVo info(Integer id) {
        SysStat model = sysStatMapper.selectOne(
                new QueryWrapper<SysStat>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));
        Assert.notNull(model, "不存在");
        SysStatInfoVo vo = new SysStatInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }


    @Override
    public Map<String, Object> financeAccountsTotal(SysStatSearchParam searchParam) {
        // 处理日期参数
        String startDate = "";
        String endDate = "";
        if (searchParam.getDate() != null && searchParam.getDate().length > 0) {
            startDate = searchParam.getDate()[0];
            if (searchParam.getDate().length > 1) {
                endDate = searchParam.getDate()[1];
            }
        }
        // 获取站点ID
        Integer siteId = RequestUtils.siteId();


        SysStatVo sysStatVo = coreSysStatService.getStat(siteId,startDate,endDate);

        Map<String, Object> statData = new HashMap<>();
        // 如果查询结果不为空，则取第一条记录
        if (ObjectUtil.isNotEmpty(sysStatVo)) {
            statData = BeanUtil.beanToMap(sysStatVo,true,false);
        }
        // 计算额外的统计字段
        if (statData.containsKey("supply_money") && statData.containsKey("supply_refund_money")) {
            // 供货商货款=支付供货商货款-退还供货商货款
            BigDecimal supplyMoney = new BigDecimal(statData.getOrDefault("supply_money", "0").toString());
            BigDecimal supplyRefundMoney = new BigDecimal(statData.getOrDefault("supply_refund_money", "0").toString());
            BigDecimal supplyGoodsMoney = supplyMoney.subtract(supplyRefundMoney).setScale(2, BigDecimal.ROUND_HALF_UP);
            statData.put("supply_goods_money", supplyGoodsMoney);

            // 运费=支付运费-退还运费
            BigDecimal deliveryMoney = new BigDecimal(statData.getOrDefault("delivery_money", "0").toString());
            BigDecimal refundDeliveryMoney = new BigDecimal(statData.getOrDefault("refund_delivery_money", "0").toString());
            BigDecimal shippingFee = deliveryMoney.subtract(refundDeliveryMoney).setScale(2, BigDecimal.ROUND_HALF_UP);
            statData.put("shipping_fee", shippingFee);
        }

        // 获取商店信息
        BigDecimal money = BigDecimal.ZERO;
        BigDecimal moneyGet = BigDecimal.ZERO;

        if (siteId != 0) {
            Site site = siteMapper.selectById(siteId);
            if (site != null) {
                money = Optional.ofNullable(site.getMoney()).orElse(BigDecimal.ZERO);
                moneyGet = Optional.ofNullable(site.getMoneyGet()).orElse(BigDecimal.ZERO);
            }
        }

        // 添加商店信息到结果中
        statData.put("money", money.setScale(2, RoundingMode.HALF_UP).toString());
        statData.put("money_get", moneyGet.setScale(2, RoundingMode.HALF_UP).toString());

        return statData;
    }

    @Override
    public SysStatListVo financeAccountsDetail(SysStatSearchParam searchParam) {
        String date = "";
        if (searchParam.getDate() != null && searchParam.getDate().length > 0) {
            date = searchParam.getDate()[0];
        }
        Integer siteId = RequestUtils.siteId();
        String type = StringUtils.isNotEmpty(searchParam.getType()) ? searchParam.getType() : "day";

        com.niu.core.service.core.sys.vo.SysStatListVo coreVo = coreSysStatService.getStatDetail(siteId, date, type);
        SysStatListVo vo = new SysStatListVo();
        BeanUtils.copyProperties(coreVo, vo);
        vo.setAccessSum(coreVo.getAccessNum());
        return vo;
    }

    /**
     * 聚合统计字段列表
     */
    public static final List<String> FINANCE_ACCOUNTS_TOTAL_FIELDS = Arrays.asList(
            "IFNULL(SUM(order_num), 0) AS order_num",          // 订单数
            "IFNULL(ROUND(SUM(sale_money),2), '0.00') AS sale_money",        // 销售额
            "IFNULL(ROUND(SUM(refund_money),2), '0.00') AS refund_money",    // 退款金额
            "IFNULL(SUM(access_sum), 0) AS access_sum",        // 访客数
            "IFNULL(ROUND(SUM(order_receivable_money),2), '0.00') AS order_receivable_money",      // 订单应收
            "IFNULL(ROUND(SUM(order_actual_money),2), '0.00') AS order_actual_money",              // 订单实收
            "IFNULL(ROUND(SUM(mall_coupon_money),2), '0.00') AS mall_coupon_money",               // 平台优惠补贴
            "IFNULL(ROUND(SUM(mall_refund_coupon_money),2), '0.00') AS mall_refund_coupon_money", // 返还平台优惠券补贴
            "IFNULL(ROUND(SUM(order_refund_money),2), '0.00') AS order_refund_money",              // 订单应退
            "IFNULL(ROUND(SUM(order_actual_refund_money),2), '0.00') AS order_actual_refund_money",// 订单实退
            "IFNULL(ROUND(SUM(shop_separate_money),2), '0.00') AS shop_separate_money",            // 商家支付分账
            "IFNULL(ROUND(SUM(shop_refund_separate_money),2), '0.00') AS shop_refund_separate_money", // 商家退款分账
            "IFNULL(ROUND(SUM(shop_commission),2), '0.00') AS shop_commission",                    // 支付佣金
            "IFNULL(ROUND(SUM(shop_refund_commission),2), '0.00') AS shop_refund_commission",      // 退还佣金
            "IFNULL(ROUND(SUM(delivery_money),2), '0.00') AS delivery_money",                      // 支付运费
            "IFNULL(ROUND(SUM(refund_delivery_money),2), '0.00') AS refund_delivery_money",        // 退还运费
            "IFNULL(ROUND(SUM(mall_service_money),2), '0.00') AS mall_service_money",              // 支付手续费
            "IFNULL(ROUND(SUM(mall_refund_service_money),2), '0.00') AS mall_refund_service_money",// 退还手续费
            "IFNULL(ROUND(SUM(recharge_money),2), '0.00') AS recharge_money",                      // 充值金额
            "IFNULL(ROUND(SUM(recharge_pay_money),2), '0.00') AS recharge_pay_money",              // 充值消费金额
            // 退还供货商货款
            "IFNULL(ROUND(SUM(supply_money),2), '0.00') AS supply_money",
            "IFNULL(ROUND(SUM(supply_refund_money),2), '0.00') AS supply_refund_money",
            "IFNULL(ROUND(SUM(supply_separate_money),2), '0.00') AS supply_separate_money",
            "IFNULL(ROUND(SUM(supply_refund_separate_money),2), '0.00') AS supply_refund_separate_money"

    );

    /**
     * 首页获取最新动态
     * @return
     */
    @Override
    public List<StatNewsVo> getArticleList(Integer num) {
        ArticeSearchParam searchParam = new ArticeSearchParam();
        if(ObjectUtil.isNotEmpty( num)){
            searchParam.setLimit(num);
        }
        List<ArticleListVo> articleListVoList = articleService.list(searchParam);
        List<StatNewsVo> result = new ArrayList<>();
        if(!articleListVoList.isEmpty()){
            for(ArticleListVo vo : articleListVoList){
                StatNewsVo statChartVo = new StatNewsVo();
                statChartVo.setId(vo.getId());
                statChartVo.setTitle(vo.getTitle());
                statChartVo.setCreateTime(vo.getCreateTime());
                result.add(statChartVo);
            }
        }
        return result;
    }

    /**
     * 首页获取数据集
     *
     * @return
     */
    @Override
    public StartChartVo getSartChartVoList() {
        String startDate = DateUtil.formatDate(DateUtil.offsetDay(new Date(), -6));
        String endDate = DateUtil.formatDate(new Date());
        // 获取站点ID
        Integer siteId = RequestUtils.siteId();
        //数据转换为图表数据
        return  siteStatService.getStatData(siteId, startDate, endDate);
    }

    @Override
    public ShopStatVo getStat(Integer siteId,String startDate, String endDate) {
        // 构建查询条件
        QueryWrapper<SysStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        if (ObjectUtil.isNotEmpty(startDate) && ObjectUtil.isNotEmpty(endDate)){
            queryWrapper.ge("date", startDate);
            queryWrapper.le("date", endDate);
        }else if(ObjectUtil.isNotEmpty(startDate)){
            queryWrapper.eq("date", startDate);
        }
        if (ObjectUtil.isEmpty(startDate) && ObjectUtil.isEmpty(endDate)){
            queryWrapper.gt("date_time", 0);
        }
        // 查询统计数据并汇总
        // 使用select sum函数汇总统计字段
        queryWrapper.select(STAT_FIELD);
        // 执行查询获取统计数据
        SysStat resultList = sysStatMapper.selectOne(queryWrapper);
        if(resultList == null){
            return new ShopStatVo();
        }else{
            ShopStatVo shopStatVo = new ShopStatVo();
            BeanUtils.copyProperties(resultList, shopStatVo);
            shopStatVo.setSaleMoney(ObjectUtil.isNotEmpty(resultList.getSaleMoney()) ? resultList.getSaleMoney().setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            shopStatVo.setRefundMoney(ObjectUtil.isNotEmpty(resultList.getRefundMoney()) ? resultList.getRefundMoney().setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            shopStatVo.setServiceMoney(ObjectUtil.isNotEmpty(resultList.getMallServiceMoney()) ? resultList.getMallServiceMoney().setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            shopStatVo.setRefundServiceMoney(ObjectUtil.isNotEmpty(resultList.getMallRefundServiceMoney()) ? resultList.getMallRefundServiceMoney().setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            shopStatVo.setOrderReceivableMoney(ObjectUtil.isNotEmpty(resultList.getOrderReceivableMoney()) ? resultList.getOrderReceivableMoney().setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            shopStatVo.setOrderActualMoney(ObjectUtil.isNotEmpty(resultList.getOrderActualMoney()) ? resultList.getOrderActualMoney().setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            shopStatVo.setOrderRefundMoney(ObjectUtil.isNotEmpty(resultList.getOrderRefundMoney()) ? resultList.getOrderRefundMoney().setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            shopStatVo.setOrderActualRefundMoney(ObjectUtil.isNotEmpty(resultList.getOrderActualRefundMoney()) ? resultList.getOrderActualRefundMoney().setScale(2, RoundingMode.HALF_UP).toString() : "0.00");
            return shopStatVo;
        }

    }


    @Override
    public SysStatRechargeVo getRechargeStat(Integer siteId, String startDate, String endDate) {
        // 构建查询条件
        QueryWrapper<SysStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(startDate) && org.apache.commons.lang3.StringUtils.isNotEmpty(endDate)){
            queryWrapper.ge("date", startDate);
            queryWrapper.le("date", endDate);
        }else if(org.apache.commons.lang3.StringUtils.isNotEmpty(startDate)){
            queryWrapper.eq("date", startDate);
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(startDate) && org.apache.commons.lang3.StringUtils.isEmpty(endDate)){
            queryWrapper.gt("date_time", 0);
        }
        queryWrapper.select("IFNULL(sum(recharge_money), 0) as recharge_money","IFNULL(sum(recharge_order_money), 0) as recharge_order_money",
                "IFNULL(sum(recharge_pay_money), 0) as recharge_pay_money","IFNULL(sum(recharge_give_money), 0) as recharge_give_money");

        // 执行查询获取统计数据
        SysStat resultList = sysStatMapper.selectOne(queryWrapper);
        SysStatRechargeVo vo = new SysStatRechargeVo();
        BeanUtils.copyProperties(resultList,vo);
        return vo;
    }

    @Override
    public PageResult<SysStatRechargeVo> getRechargeStatList(PageParam pageParam,Integer siteId, String startDate, String endDate, String type) {
        // 分页参数
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        // 构建查询条件
        QueryWrapper<SysStat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(startDate) && org.apache.commons.lang3.StringUtils.isNotEmpty(endDate)){
            queryWrapper.ge("date", startDate);
            queryWrapper.le("date", endDate);
        }else if(org.apache.commons.lang3.StringUtils.isNotEmpty(startDate)){
            queryWrapper.eq("date", startDate);
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(startDate) && org.apache.commons.lang3.StringUtils.isEmpty(endDate)){
            queryWrapper.gt("date_time", 0);
        }
        queryWrapper.select("site_id,IFNULL(sum(recharge_money), 0) as recharge_money","IFNULL(sum(recharge_order_money), 0) as recharge_order_money",
                "IFNULL(sum(recharge_pay_money), 0) as recharge_pay_money","IFNULL(sum(recharge_give_money), 0) as recharge_give_money");
        if(type.equals("month")){
            queryWrapper.select("month as date");
            queryWrapper.ne("month", "");
            queryWrapper.groupBy("month");
            queryWrapper.orderByDesc("month");
        }else{
            queryWrapper.select("date");
            queryWrapper.groupBy("date");
            queryWrapper.orderByDesc("date");
        }

        // 执行查询获取统计数据
        IPage<SysStat> pageList = sysStatMapper.selectPage(new Page<>(page, limit),queryWrapper);

        List<SysStatRechargeVo> resultList = new LinkedList<>();
        for (SysStat sysStat : pageList.getRecords()){
            SysStatRechargeVo vo = new SysStatRechargeVo();
            vo.setTime(sysStat.getDate());
            vo.setRechargeMoney(ObjectUtil.defaultIfNull(sysStat.getRechargeMoney(), BigDecimal.ZERO));
            vo.setRechargePayMoney(ObjectUtil.defaultIfNull(sysStat.getRechargePayMoney(), BigDecimal.ZERO));
            vo.setRechargeOrderMoney(ObjectUtil.defaultIfNull(sysStat.getRechargeOrderMoney(), BigDecimal.ZERO));
            vo.setRechargeGiveMoney(ObjectUtil.defaultIfNull(sysStat.getRechargeGiveMoney(), BigDecimal.ZERO));
            resultList.add(vo);
        }

        return PageResult.build(page, limit, pageList.getTotal()).setData(resultList);
    }

    /**
     * 聚合统计字段列表
     */
    public static final List<String> STAT_FIELD = Arrays.asList(
            "IFNULL(SUM(order_num), 0) AS order_num",          // 订单数
            "IFNULL(ROUND(SUM(sale_money),2), '0.00') AS sale_money",        // 销售额
            "IFNULL(ROUND(SUM(refund_money),2), '0.00') AS refund_money",    // 退款金额
            "IFNULL(ROUND(SUM(order_receivable_money),2), '0.00') AS order_receivable_money",      // 订单应收
            "IFNULL(ROUND(SUM(order_actual_money),2), '0.00') AS order_actual_money",              // 订单实收
            "IFNULL(ROUND(SUM(order_refund_money),2), '0.00') AS order_refund_money",              // 订单应退
            "IFNULL(ROUND(SUM(order_actual_refund_money),2), '0.00') AS order_actual_refund_money",//
            "IFNULL(ROUND(SUM(mall_service_money),2), '0.00') AS mall_service_money",              // 支付手续费
            "IFNULL(ROUND(SUM(mall_refund_service_money),2), '0.00') AS mall_refund_service_money"// 退还手续费
    );
}
