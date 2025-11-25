package com.niu.mall.service.admin.finance.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.admin.finance.SettlementOrderService;
import com.niu.mall.service.admin.finance.param.SettlementSelectParam;
import com.niu.mall.service.admin.finance.vo.SettlementDataVo;
import com.niu.mall.service.admin.finance.vo.SettlementOrderListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


/**
 * 结算订单实现
 */
@Service
public class SettlementOrderServiceImpl implements SettlementOrderService {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Override
    public PageResult<SettlementOrderListVo> getPage(PageParam pageParam, SettlementSelectParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallOrder> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("o")
                .leftJoin("?_site s ON o.site_id = s.site_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("o.order_id", "o.site_id", "o.order_no", "o.order_money", "o.shop_money", "o.mall_money", "o.create_time", "o.pay_time", "o.settlement_status", "o.order_from");
        // 同时返回前端期望的字段名（与PHP保持一致）
        queryWrapper.select("s.site_id,s.site_name,s.logo as front_end_logo,s.phone");

        queryWrapper.notIn("o.status", OrderStatusEnum.WAIT_PAY.getStatus(), OrderStatusEnum.CLOSE.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(it -> it.like("o.order_no", searchParam.getKeyword()).or()
                    .like("s.phone", searchParam.getKeyword()).or()
                    .like("s.site_name", searchParam.getKeyword()));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "o.create_time", searchParam.getCreateTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getPayTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "o.pay_time", searchParam.getPayTime());
        }
        queryWrapper.orderByDesc("o.create_time");
        queryWrapper.eq("o.delete_time", 0);
        IPage<SettlementOrderListVo> iPage = mallOrderMapper.selectJoinPage(new Page<>(page, limit), SettlementOrderListVo.class, queryWrapper);
        iPage.getRecords().forEach(item -> {
            SettlementOrderListVo.SiteInfo siteInfo = new SettlementOrderListVo.SiteInfo();
            BeanUtils.copyProperties(item, siteInfo);
            item.setSite(siteInfo);
        });
        return PageResult.build(page, limit, iPage.getTotal()).setData(iPage.getRecords());
    }

    @Override
    public SettlementDataVo getSettlementData() {
        SettlementDataVo data = new SettlementDataVo();

        // 店铺待结总金额
        MallOrder shopWaitSettlementMoneyObj = mallOrderMapper.selectOne(
                new MPJQueryWrapper<MallOrder>()
                        .select("SUM(shop_money) as shop_money")
                        .notIn("status", OrderStatusEnum.WAIT_PAY.getStatus(), OrderStatusEnum.FINISH.getStatus(), OrderStatusEnum.CLOSE.getStatus())
        );
        BigDecimal shopWaitSettlementMoney = shopWaitSettlementMoneyObj != null ? shopWaitSettlementMoneyObj.getShopMoney() : BigDecimal.ZERO;

        // 平台待结算金额
        MallOrder mallWaitSettlementMoneyObj = mallOrderMapper.selectOne(
                new MPJQueryWrapper<MallOrder>()
                        .select("SUM(mall_money) as mall_money")
                        .notIn("status", OrderStatusEnum.WAIT_PAY.getStatus(), OrderStatusEnum.FINISH.getStatus(), OrderStatusEnum.CLOSE.getStatus())
        );
        BigDecimal mallWaitSettlementMoney = mallWaitSettlementMoneyObj != null ? mallWaitSettlementMoneyObj.getMallMoney() : BigDecimal.ZERO;

        // 已结算金额
        MallOrder shopMoneyObj = mallOrderMapper.selectOne(
                new MPJQueryWrapper<MallOrder>()
                        .select("SUM(shop_money) as shop_money")
                        .eq("status", OrderStatusEnum.FINISH.getStatus())
        );
        BigDecimal shopMoney = shopMoneyObj != null ? shopMoneyObj.getShopMoney() : BigDecimal.ZERO;

        MallOrder shopRefundMoneyObj = mallOrderMapper.selectOne(
                new MPJQueryWrapper<MallOrder>()
                        .select("SUM(shop_refund_money) as shop_refund_money")
                        .eq("status", OrderStatusEnum.FINISH.getStatus())
        );
        BigDecimal shopRefundMoney = shopRefundMoneyObj != null ? shopRefundMoneyObj.getShopRefundMoney() : BigDecimal.ZERO;

        MallOrder mallCouponMoneyObj = mallOrderMapper.selectOne(
                new MPJQueryWrapper<MallOrder>()
                        .select("SUM(mall_coupon_money) as mall_coupon_money")
                        .eq("status", OrderStatusEnum.FINISH.getStatus())
        );
        BigDecimal mallCouponMoney = mallCouponMoneyObj != null ? mallCouponMoneyObj.getMallCouponMoney() : BigDecimal.ZERO;

        MallOrder mallRefundCouponMoneyObj = mallOrderMapper.selectOne(
                new MPJQueryWrapper<MallOrder>()
                        .select("SUM(mall_refund_coupon_money) as mall_refund_coupon_money")
                        .eq("status", OrderStatusEnum.FINISH.getStatus())
        );
        BigDecimal mallRefundCouponMoney = mallRefundCouponMoneyObj != null ? mallRefundCouponMoneyObj.getMallRefundCouponMoney() : BigDecimal.ZERO;

        // 平台已结算总金额
        MallOrder mallSettlementMoneyObj = mallOrderMapper.selectOne(
                new MPJQueryWrapper<MallOrder>()
                        .select("SUM(mall_money) as mall_money")
                        .eq("status", OrderStatusEnum.FINISH.getStatus())
        );
        BigDecimal mallSettlementMoney = mallSettlementMoneyObj != null ? mallSettlementMoneyObj.getMallMoney() : BigDecimal.ZERO;

        // 设置数据
        data.setShopWaitSettlementMoney(shopWaitSettlementMoney);
        data.setMallWaitSettlementMoney(mallWaitSettlementMoney);
        data.setShopSettlementMoney(shopMoney.subtract(shopRefundMoney).add(mallCouponMoney).subtract(mallRefundCouponMoney));
        data.setMallSettlementMoney(mallSettlementMoney);

        return data;
    }
}
