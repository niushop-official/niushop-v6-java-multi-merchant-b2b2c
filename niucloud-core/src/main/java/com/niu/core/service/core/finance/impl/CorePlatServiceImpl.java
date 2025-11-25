package com.niu.core.service.core.finance.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteFinanceLog;
import com.niu.core.enums.pay.PayChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.enums.site.SiteFinanceLogEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.pay.PayRefundMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.finance.ICorePlatService;
import com.niu.core.service.core.finance.param.OrderPlatParam;
import com.niu.core.service.core.finance.param.OrderRefundParam;
import com.niu.core.service.core.site.ICoreSiteFinanceService;
import com.niu.core.service.core.sys.ICoreSysStatService;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class CorePlatServiceImpl implements ICorePlatService {

    @Resource
    PayMapper payMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    PayRefundMapper payRefundMapper;

    @Resource
    ICoreSiteFinanceService coreSiteFinanceService;

    @Resource
    ICoreSysStatService coreStatService;

    @Resource
    SiteMapper siteMapper;



    @Override
    public void platIncomeOrderPay(OrderPlatParam orderPlatParam) {
        // 判断订单金额是否大于0
        if (orderPlatParam.getOrderMoney().compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo()));
            if (pay != null) {
                // 查询会员信息
                Member member = memberMapper.selectById(orderPlatParam.getMemberId());
                String nickname = member.getNickname();
                String memberNo = member.getMemberNo();
                String mainName = nickname + " " + memberNo;

                // 根据支付类型处理不同的财务记录
                if (pay.getType().equals("balancepay")) {
                    // 余额支付 - 记录支出
                    SiteFinanceLog logData = new SiteFinanceLog();
                    logData.setSiteId(orderPlatParam.getSiteId());
                    logData.setMainId(orderPlatParam.getMemberId());
                    logData.setMainName(mainName);
                    logData.setValue(orderPlatParam.getOrderMoney().negate());
                    logData.setType(SiteFinanceLogEnum.BALANCE_PAY.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                    logData.setTradeId(orderPlatParam.getOrderId().toString());
                    logData.setTradeNo(orderPlatParam.getOrderNo());
                    logData.setMemo("余额支付成功,扣除用户余额" + orderPlatParam.getOrderMoney() + "元");

                    coreSiteFinanceService.addPlatLog(logData);

                    // 充值消费金额统计
                    SysDataAddParam statData = new SysDataAddParam();
                    statData.setSiteId(RequestUtils.defaultSiteId());
                    statData.setRechargePayMoney(orderPlatParam.getOrderMoney());
                    coreStatService.addStat(statData);
                } else if (!pay.getType().equals("offlinepay")) {

                    // 非线下支付 - 记录收入
                    SiteFinanceLog logData = new SiteFinanceLog();
                    logData.setMainId(orderPlatParam.getMemberId());
                    logData.setMainName(mainName);
                    logData.setValue(orderPlatParam.getOrderMoney());
                    logData.setType(SiteFinanceLogEnum.PAY.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                    logData.setTradeId(orderPlatParam.getOrderId().toString());
                    logData.setTradeNo(orderPlatParam.getOrderNo());
                    logData.setMemo("订单支付成功,支付金额" + orderPlatParam.getOrderMoney() + "元");

                    coreSiteFinanceService.addPlatLog(logData);
                }
            }
        }
    }
    @Override
    public void platIncomeOrderRefund(OrderRefundParam orderRefundParam) {
        // 获取退款金额
        BigDecimal refundMoney = orderRefundParam.getMoney();

        // 检查退款金额是否大于0
        if (refundMoney.compareTo(BigDecimal.ZERO) > 0) {
            // 查询退款记录
            PayRefund payRefund = payRefundMapper.selectOne(
                    new QueryWrapper<PayRefund>().eq("refund_no", orderRefundParam.getRefundNo())
            );

            if (payRefund != null) {
                // 查询站点信息
                Site site = siteMapper.selectOne(
                        new QueryWrapper<Site>().eq("site_id", orderRefundParam.getSiteId())
                );
                String siteName = (site != null) ? site.getSiteName() : "";

                // 根据支付类型处理不同的财务记录
                if (!payRefund.getType().equals("offlinepay") && ObjectUtil.isNotEmpty(orderRefundParam.getMallRefundMoney()) && orderRefundParam.getMallRefundMoney().compareTo(BigDecimal.ZERO) > 0) {
                    // 非线下支付退款 - 记录收入
                    SiteFinanceLog logData = new SiteFinanceLog();
                    logData.setMainId(orderRefundParam.getSiteId());
                    logData.setMainName(siteName);
                    logData.setValue(orderRefundParam.getMallRefundMoney().negate());
                    logData.setType(SiteFinanceLogEnum.PLAT_REFUND_SERVICE.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                    logData.setTradeId(orderRefundParam.getOrderId().toString());
                    logData.setTradeNo(orderRefundParam.getOrderRefundNo());
                    logData.setMemo("订单退款成功,平台返还分账金额" + orderRefundParam.getMallRefundMoney() + "元");

                    coreSiteFinanceService.addPlatLog(logData);
                }
            }
        }
    }
    @Override
    public void platIncomePlatCoupon(OrderPlatParam orderPlatParam) {
        // 获取平台优惠券金额
        BigDecimal mallCouponMoney = orderPlatParam.getMallCouponMoney();

        // 检查优惠券金额是否大于0
        if (mallCouponMoney != null && mallCouponMoney.compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(
                    new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo())
            );

            if (pay != null && !pay.getType().equals("offlinepay")) {
                // 查询站点信息
                Site site = siteMapper.selectOne(
                        new QueryWrapper<Site>().eq("site_id", orderPlatParam.getSiteId())
                );
                String siteName = (site != null) ? site.getSiteName() : "";

                // 记录平台优惠券补贴 - 支出记录
                SiteFinanceLog logData = new SiteFinanceLog();
                logData.setMainId(orderPlatParam.getSiteId());
                logData.setMainName(siteName);
                logData.setValue(mallCouponMoney.negate()); // 负值表示支出
                logData.setType(SiteFinanceLogEnum.PLAT_COUPON.getSource());
                logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                logData.setTradeId(orderPlatParam.getOrderId().toString());
                logData.setTradeNo(orderPlatParam.getOrderNo());
                logData.setMemo("订单支付成功,平台优惠券补贴" + mallCouponMoney + "元");

                coreSiteFinanceService.addPlatLog(logData);
            }
        }
    }

    @Override
    public void platIncomePlatRefundCoupon(OrderRefundParam orderRefundParam) {
        // 获取退还的平台优惠券金额
        BigDecimal mallRefundCouponMoney = orderRefundParam.getMallRefundCouponMoney();

        // 检查退还金额是否大于0
        if (mallRefundCouponMoney != null && mallRefundCouponMoney.compareTo(BigDecimal.ZERO) > 0) {
            // 查询退款记录
            PayRefund payRefund = payRefundMapper.selectOne(
                    new QueryWrapper<PayRefund>().eq("refund_no", orderRefundParam.getRefundNo())
            );

            if (payRefund != null && !payRefund.getType().equals("offlinepay")) {
                // 查询站点信息
                Site site = siteMapper.selectOne(
                        new QueryWrapper<Site>().eq("site_id", orderRefundParam.getSiteId())
                );
                String siteName = (site != null) ? site.getSiteName() : "";

                // 记录退还平台优惠券补贴 - 收入记录
                SiteFinanceLog logData = new SiteFinanceLog();
                logData.setMainId(orderRefundParam.getSiteId());
                logData.setMainName(siteName);
                logData.setValue(mallRefundCouponMoney); // 正值表示收入
                logData.setType(SiteFinanceLogEnum.PLAT_REFUND_COUPON.getSource());
                logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                logData.setTradeId(orderRefundParam.getOrderId().toString());
                logData.setTradeNo(orderRefundParam.getOrderRefundNo());
                logData.setMemo("订单退款成功,退还平台优惠券补贴" + mallRefundCouponMoney + "元");

                coreSiteFinanceService.addPlatLog(logData);
            }
        }
    }
    @Override
    public void platIncomeMallService(OrderPlatParam orderPlatParam) {
        // 获取商城手续费金额
        BigDecimal mallMoney = orderPlatParam.getMallMoney();

        // 检查手续费金额是否大于0
        if (mallMoney != null && mallMoney.compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(
                    new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo())
            );

            if (pay != null && !pay.getType().equals("offlinepay")) {
                // 查询商城站点信息
                Site site = siteMapper.selectOne(
                        new QueryWrapper<Site>().eq("site_id", orderPlatParam.getMallSiteId())
                );
                String siteName = (site != null) ? site.getSiteName() : "";

                // 记录商城手续费收入
                SiteFinanceLog logData = new SiteFinanceLog();
                logData.setMainId(orderPlatParam.getMallSiteId());
                logData.setMainName(siteName);
                logData.setValue(mallMoney); // 正值表示收入
                logData.setType(SiteFinanceLogEnum.PLAT_SERVICE.getSource());
                logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                logData.setTradeId(orderPlatParam.getOrderId().toString());
                logData.setTradeNo(orderPlatParam.getOrderNo());
                logData.setMemo("订单支付成功,收入商城手续费" + mallMoney + "元");

                coreSiteFinanceService.addPlatLog(logData);
            }
        }
    }

    @Override
    public void platIncomeSupplyService(OrderPlatParam orderPlatParam) {
        // 获取供货商手续费金额
        BigDecimal platMoney = orderPlatParam.getPlatMoney();

        // 检查手续费金额是否大于0
        if (platMoney != null && platMoney.compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(
                    new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo())
            );

            if (pay != null && !pay.getType().equals("offlinepay")) {
                // 查询供货商站点信息
                Site site = siteMapper.selectOne(
                        new QueryWrapper<Site>().eq("site_id", orderPlatParam.getSupplyId())
                );
                String siteName = (site != null) ? site.getSiteName() : "";

                // 记录供货商手续费收入
                SiteFinanceLog logData = new SiteFinanceLog();
                logData.setMainId(orderPlatParam.getSupplyId());
                logData.setMainName(siteName);
                logData.setValue(platMoney); // 正值表示收入
                logData.setType(SiteFinanceLogEnum.PLAT_SERVICE.getSource());
                logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                logData.setTradeId(orderPlatParam.getOrderId().toString());
                logData.setTradeNo(orderPlatParam.getOrderNo());
                logData.setMemo("订单支付成功,供货商手续费" + platMoney + "元");

                coreSiteFinanceService.addPlatLog(logData);
            }
        }
    }

    @Override
    public void platOutlaySeparate(OrderPlatParam orderPlatParam) {
        // 获取商户分账金额
        BigDecimal shopSeparateMoney = orderPlatParam.getShopSeparateMoney();

        // 检查分账金额是否大于0
        if (shopSeparateMoney != null && shopSeparateMoney.compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(
                    new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo())
            );

            if (pay != null && !pay.getType().equals("offlinepay")) {
                // 查询商城站点信息
                Site site = siteMapper.selectOne(
                        new QueryWrapper<Site>().eq("site_id", orderPlatParam.getMallSiteId())
                );
                String siteName = (site != null) ? site.getSiteName() : "";

                // 记录商户分账支出
                SiteFinanceLog logData = new SiteFinanceLog();
                logData.setMainId(orderPlatParam.getMallSiteId());
                logData.setMainName(siteName);
                logData.setValue(shopSeparateMoney.negate()); // 负值表示支出
                logData.setType(SiteFinanceLogEnum.SITE_SEPARATE.getSource());
                logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                logData.setTradeId(orderPlatParam.getOrderId().toString());
                logData.setTradeNo(orderPlatParam.getOrderNo());
                logData.setMemo("订单支付成功,商户分账" + shopSeparateMoney + "元");

                coreSiteFinanceService.addPlatLog(logData);
            }
        }
    }

    @Override
    public void platOutlayRefundSeparate(OrderRefundParam orderRefundParam) {
        // 获取退款分账金额
        BigDecimal shopRefundSeparateMoney = orderRefundParam.getShopRefundSeparateMoney();

        // 检查退款分账金额是否大于0
        if (shopRefundSeparateMoney != null && shopRefundSeparateMoney.compareTo(BigDecimal.ZERO) > 0) {
            // 查询退款记录
            PayRefund payRefund = payRefundMapper.selectOne(
                    new QueryWrapper<PayRefund>().eq("refund_no", orderRefundParam.getRefundNo())
            );

            if (payRefund != null && !payRefund.getType().equals("offlinepay")) {
                // 查询站点信息
                Site site = siteMapper.selectOne(
                        new QueryWrapper<Site>().eq("site_id", orderRefundParam.getSiteId())
                );
                String siteName = (site != null) ? site.getSiteName() : "";

                // 记录退款分账支出
                SiteFinanceLog logData = new SiteFinanceLog();
                logData.setMainId(orderRefundParam.getSiteId());
                logData.setMainName(siteName);
                logData.setValue(shopRefundSeparateMoney.negate()); // 负值表示支出
                logData.setType(SiteFinanceLogEnum.SITE_REFUND_SEPARATE.getSource());
                logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                logData.setTradeId(orderRefundParam.getOrderId().toString());
                logData.setTradeNo(orderRefundParam.getOrderRefundNo());
                logData.setMemo("订单退款成功,平台退款分账" + shopRefundSeparateMoney + "元");

                coreSiteFinanceService.addPlatLog(logData);
            }
        }
    }

    @Override
    public void platOutlayCommission(OrderPlatParam orderPlatParam) {
        // 获取佣金金额
        BigDecimal shopCommission = orderPlatParam.getShopCommission();

        // 检查佣金金额是否大于0
        if (shopCommission != null && shopCommission.compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(
                    new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo())
            );

            if (pay != null && !pay.getType().equals("offlinepay")) {
                // 查询站点信息
                Site site = siteMapper.selectOne(
                        new QueryWrapper<Site>().eq("site_id", orderPlatParam.getSiteId())
                );
                String siteName = (site != null) ? site.getSiteName() : "";

                // 记录佣金支出
                SiteFinanceLog logData = new SiteFinanceLog();
                logData.setMainId(orderPlatParam.getSiteId());
                logData.setMainName(siteName);
                logData.setValue(shopCommission); // 负值表示支出
                logData.setType(SiteFinanceLogEnum.PLAT_COMMISSION.getSource());
                logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                logData.setTradeId(orderPlatParam.getOrderId().toString());
                logData.setTradeNo(orderPlatParam.getOrderNo());
                logData.setMemo("订单支付成功,平台代扣佣金" + shopCommission + "元");

                coreSiteFinanceService.addPlatLog(logData);
            }
        }
    }

    @Override
    public void platOutlayRefundCommission(OrderRefundParam orderRefundParam) {
        // 获取退还佣金金额
        BigDecimal refundCommission = orderRefundParam.getRefundCommission();

        // 检查退还佣金金额是否大于0
        if (refundCommission != null && refundCommission.compareTo(BigDecimal.ZERO) > 0) {
            // 查询退款记录
            PayRefund payRefund = payRefundMapper.selectOne(
                    new QueryWrapper<PayRefund>().eq("refund_no", orderRefundParam.getRefundNo())
            );

            if (payRefund != null && !payRefund.getType().equals("offlinepay")) {
                // 查询站点信息
                Site site = siteMapper.selectOne(
                        new QueryWrapper<Site>().eq("site_id", orderRefundParam.getSiteId())
                );
                String siteName = (site != null) ? site.getSiteName() : "";

                // 记录退还佣金支出
                SiteFinanceLog logData = new SiteFinanceLog();
                logData.setMainId(orderRefundParam.getSiteId());
                logData.setMainName(siteName);
                logData.setValue(refundCommission.negate()); // 负值表示支出
                logData.setType(SiteFinanceLogEnum.PLAT_REFUND_COMMISSION.getSource());
                logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                logData.setTradeId(orderRefundParam.getOrderId().toString());
                logData.setTradeNo(orderRefundParam.getOrderRefundNo());
                logData.setMemo("订单退款成功,平台代扣佣金返还" + refundCommission + "元");

                coreSiteFinanceService.addPlatLog(logData);
            }
        }
    }

    @Override
    public void platOutlaySupplyMoney(OrderPlatParam orderPlatParam) {
        // 获取供货商货款金额
        BigDecimal supplyMoney = orderPlatParam.getSupplyMoney();

        // 检查货款金额是否大于0
        if (supplyMoney != null && supplyMoney.compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(
                    new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo())
            );

            if (pay != null && !pay.getType().equals("offlinepay")) {
                // 查询供货商站点信息
                Site site = siteMapper.selectOne(
                        new QueryWrapper<Site>().eq("site_id", orderPlatParam.getSupplyId())
                );
                String siteName = (site != null) ? site.getSiteName() : "";

                // 记录供货商货款支出
                SiteFinanceLog logData = new SiteFinanceLog();
                logData.setMainId(orderPlatParam.getSupplyId());
                logData.setMainName(siteName);
                logData.setValue(supplyMoney.negate()); // 负值表示支出
                logData.setType(SiteFinanceLogEnum.PLAT_SUPPLY_MONEY.getSource());
                logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                logData.setTradeId(orderPlatParam.getOrderId().toString());
                logData.setTradeNo(orderPlatParam.getOrderNo());
                logData.setMemo("订单支付成功,支付供货商货款" + supplyMoney + "元");

                coreSiteFinanceService.addPlatLog(logData);
            }
        }
    }

    @Override
    public void platOutlayRefundSupplyMoney(OrderRefundParam orderRefundParam) {
        // 获取退还供货商货款金额
        BigDecimal supplyRefundMoney = orderRefundParam.getSupplyRefundMoney();

        // 检查退还货款金额是否大于0
        if (supplyRefundMoney != null && supplyRefundMoney.compareTo(BigDecimal.ZERO) > 0) {
            // 查询退款记录
            PayRefund payRefund = payRefundMapper.selectOne(
                    new QueryWrapper<PayRefund>().eq("refund_no", orderRefundParam.getRefundNo())
            );

            if (payRefund != null && !payRefund.getType().equals("offlinepay")) {
                // 查询站点信息
                Site site = siteMapper.selectOne(
                        new QueryWrapper<Site>().eq("site_id", orderRefundParam.getSiteId())
                );
                String siteName = (site != null) ? site.getSiteName() : "";

                // 记录退还供货商货款支出
                SiteFinanceLog logData = new SiteFinanceLog();
                logData.setMainId(orderRefundParam.getSiteId());
                logData.setMainName(siteName);
                logData.setValue(supplyRefundMoney.negate()); // 负值表示支出
                logData.setType(SiteFinanceLogEnum.PLAT_SUPPLY_REFUND_MONEY.getSource());
                logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                logData.setTradeId(orderRefundParam.getOrderId().toString());
                logData.setTradeNo(orderRefundParam.getOrderRefundNo());
                logData.setMemo("订单退款成功,退还供货商货款" + supplyRefundMoney + "元");

                coreSiteFinanceService.addPlatLog(logData);
            }
        }
    }

    @Override
    public void platIncomeOrderRefundUnsettleMoney(OrderRefundParam orderRefundParam) {

        // 查询退款记录
        PayRefund payRefund = payRefundMapper.selectOne(
                new QueryWrapper<PayRefund>().eq("refund_no", orderRefundParam.getRefundNo())
        );
        // 检查退还货款金额是否大于0
        if (ObjectUtil.isNotEmpty(payRefund) && ObjectUtil.isNotEmpty(orderRefundParam.getMoney()) && orderRefundParam.getMoney().compareTo(BigDecimal.ZERO) > 0) {
            // 查询会员信息
            Member member = memberMapper.selectOne(
                    new QueryWrapper<Member>().eq("member_id", orderRefundParam.getMemberId())
            );
            String memberName = (member != null) ?
                    (member.getNickname() + " " + member.getMemberNo()) : "";

            if("balancepay".equals(payRefund.getType())){
                // 记录退还供货商货款支出
                SiteFinanceLog logData = new SiteFinanceLog();
                logData.setMainId(orderRefundParam.getSiteId());
                logData.setMainName(memberName);
                logData.setValue(orderRefundParam.getMoney()); // 负值表示支出
                logData.setType(SiteFinanceLogEnum.BALANCE_REFUND.getSource());
                logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                logData.setTradeId(orderRefundParam.getOrderId().toString());
                logData.setTradeNo(orderRefundParam.getOrderRefundNo());
                logData.setMemo("余额退款成功,退还用户余额" + orderRefundParam.getMoney() + "元");

                coreSiteFinanceService.addPlatLog(logData);

                //充值消费金额统计
                SysDataAddParam sysDataAddParam = new SysDataAddParam();
                sysDataAddParam.setSiteId(RequestUtils.defaultSiteId());
                sysDataAddParam.setRechargePayMoney(orderRefundParam.getMoney());
                coreStatService.addStat(sysDataAddParam);
            }else{
                if("offlinepay".equals(payRefund.getType())){
                    SiteFinanceLog logData = new SiteFinanceLog();
                    logData.setMainId(orderRefundParam.getSiteId());
                    logData.setMainName(memberName);
                    logData.setValue(orderRefundParam.getMoney()); // 负值表示支出
                    logData.setType(SiteFinanceLogEnum.PLAT_REFUND.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                    logData.setTradeId(orderRefundParam.getOrderId().toString());
                    logData.setTradeNo(orderRefundParam.getOrderRefundNo());
                    logData.setMemo("订单退款成功,退款金额" + orderRefundParam.getMoney() + "元");

                    coreSiteFinanceService.addPlatLog(logData);
                }
            }
        }
    }

}
