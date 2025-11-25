package com.niu.core.service.core.finance.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.pay.PayRefund;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteFinanceLog;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.site.SiteFinanceLogEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.pay.PayRefundMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.finance.ICoreSitePlatService;
import com.niu.core.service.core.finance.param.OrderPlatParam;
import com.niu.core.service.core.finance.param.SiteOrderPlatParam;
import com.niu.core.service.core.finance.param.SiteOrderRefundParam;
import com.niu.core.service.core.site.ICoreSiteFinanceService;
import com.niu.core.service.core.sys.ICoreSysStatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class CoreSitePlatServiceImpl implements ICoreSitePlatService {

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

    @Resource
    ICoreAddonService coreAddonService;

    @Override
    public void siteIncomeOrderPay(SiteOrderPlatParam orderPlatParam) {
        //判断支付方式,如果是支付实际金额,则生成商户收入流水
        //根据订单的支付方式来判断,如果是余额支付,则不需要生成商户收入流水
        // 判断订单金额是否大于0
        if (orderPlatParam.getOrderMoney().compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo()));
            if (pay != null) {

                // 根据支付类型处理不同的财务记录
                if (!pay.getType().equals("offlinepay")) {
                    // 查询站点信息
                    Site site = siteMapper.selectOne(
                            new QueryWrapper<Site>().eq("site_id", orderPlatParam.getSiteId())
                    );
                    String siteName = (site != null) ? site.getSiteName() : "";

                    String memo = "订单支付成功,支付金额" + orderPlatParam.getOrderMoney() + "元,商家收入" + orderPlatParam.getShopIncomeMoney() + "元,支出手续费" + orderPlatParam.getMallMoney() + "元";
                    if(checkAddon("mall_fenxiao")){
                        memo += "，支出佣金" + orderPlatParam.getShopCommission() + "元";
                    }
                    if(checkAddon("supply")){
                        memo += "，支出供应商货款" + orderPlatParam.getSupplyMoney() + "元";
                    }
                    // 非线下支付 - 记录收入
                    SiteFinanceLog logData = new SiteFinanceLog();
                    logData.setMainId(orderPlatParam.getSiteId());
                    logData.setMainName(siteName);
                    logData.setSiteId(orderPlatParam.getSiteId());
                    logData.setValue(orderPlatParam.getShopIncomeMoney());
                    logData.setType(SiteFinanceLogEnum.PAY.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                    logData.setTradeId(orderPlatParam.getOrderId().toString());
                    logData.setTradeNo(orderPlatParam.getOrderNo());
                    logData.setMemo(memo);

                    coreSiteFinanceService.addLog(logData);
                }
            }
        }
    }

    @Override
    public void siteIncomeOrderRefund(SiteOrderRefundParam orderRefundParam) {
        // 商户收入（订单应退）
        if (orderRefundParam.getShopRefundMoney().compareTo(BigDecimal.ZERO) > 0) {
            // 查询退款记录
            PayRefund refund = payRefundMapper.selectOne(
                    new QueryWrapper<PayRefund>().eq("refund_no", orderRefundParam.getRefundNo())
            );
            if (refund != null) {
                if (!refund.getType().equals("offlinepay")) {
                    // 查询会员信息
                    Member member = memberMapper.selectOne(
                            new QueryWrapper<Member>().eq("member_id", orderRefundParam.getMemberId())
                    );
                    String memberInfo = (member != null) ?
                            (member.getNickname() + " " + member.getMemberNo()) : "";

                    // 记录财务日志
                    SiteFinanceLog logData = buildBaseFinanceLog(orderRefundParam);
                    logData.setMainId(orderRefundParam.getMemberId());
                    logData.setMainName(memberInfo);
                    logData.setSiteId(orderRefundParam.getSiteId());
                    logData.setValue(orderRefundParam.getShopRefundMoney().negate());
                    logData.setType(SiteFinanceLogEnum.SITE_REFUND.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                    logData.setMemo("订单退款成功,退还金额" + orderRefundParam.getMoney() + "元,商户支出" + orderRefundParam.getShopRefundMoney()
                            + "元，退运费" + orderRefundParam.getRefundDeliveryMoney() + "元");

                    coreSiteFinanceService.addLog(logData);
                }
            }
        }
    }

    @Override
    public void siteIncomePlatCoupon(SiteOrderPlatParam orderPlatParam) {
        // 商户收入（平台优惠券补贴）
        if (orderPlatParam.getMallCouponMoney().compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(
                    new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo())
            );
            if (pay != null) {
                if (!pay.getType().equals("offlinepay")) {
                    // 查询站点信息
                    Site site = siteMapper.selectOne(
                            new QueryWrapper<Site>().eq("site_id", orderPlatParam.getSiteId())
                    );
                    String siteName = (site != null) ? site.getSiteName() : "";

                    // 记录财务日志
                    SiteFinanceLog logData = buildBaseFinanceLog(orderPlatParam);
                    logData.setMainId(orderPlatParam.getSiteId());
                    logData.setMainName(siteName);
                    logData.setSiteId(orderPlatParam.getSiteId());
                    logData.setValue(orderPlatParam.getMallCouponMoney());
                    logData.setType(SiteFinanceLogEnum.PLAT_COUPON.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                    logData.setMemo("订单支付成功,平台优惠券补贴" + orderPlatParam.getMallCouponMoney() + "元");

                    coreSiteFinanceService.addLog(logData);
                }
            }
        }
    }

    @Override
    public void siteIncomePlatRefundCoupon(SiteOrderRefundParam orderRefundParam) {
        // 商户收入（退还平台优惠券补贴）
        if (orderRefundParam.getMallRefundCouponMoney().compareTo(BigDecimal.ZERO) > 0) {
            // 查询退款记录
            PayRefund refund = payRefundMapper.selectOne(
                    new QueryWrapper<PayRefund>().eq("refund_no", orderRefundParam.getRefundNo())
            );
            if (refund != null) {
                if (!refund.getType().equals("offlinepay")) {
                    // 查询站点信息
                    Site site = siteMapper.selectOne(
                            new QueryWrapper<Site>().eq("site_id", orderRefundParam.getSiteId())
                    );
                    String siteName = (site != null) ? site.getSiteName() : "";

                    // 记录财务日志
                    SiteFinanceLog logData = buildBaseFinanceLog(orderRefundParam);
                    logData.setMainId(orderRefundParam.getSiteId());
                    logData.setMainName(siteName);
                    logData.setSiteId(orderRefundParam.getSiteId());
                    logData.setValue(orderRefundParam.getMallRefundCouponMoney().negate());
                    logData.setType(SiteFinanceLogEnum.PLAT_REFUND_COUPON.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                    logData.setMemo("订单退款成功,退还平台优惠券补贴" + orderRefundParam.getMallRefundCouponMoney() + "元");

                    coreSiteFinanceService.addLog(logData);
                }
            }
        }
    }

    @Override
    public void siteOutlayService(SiteOrderPlatParam orderPlatParam) {
        // 商户支出（支付手续费）
        if (orderPlatParam.getMallMoney().compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(
                    new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo())
            );
            if (pay != null) {
                if (!pay.getType().equals("offlinepay")) {
                    // 查询平台站点信息
                    Site site = siteMapper.selectOne(
                            new QueryWrapper<Site>().eq("site_id", orderPlatParam.getPlatSiteId())
                    );
                    String siteName = (site != null) ? site.getSiteName() : "";

                    // 记录财务日志
                    SiteFinanceLog logData = buildBaseFinanceLog(orderPlatParam);
                    logData.setMainId(orderPlatParam.getPlatSiteId());
                    logData.setMainName(siteName);
                    logData.setSiteId(orderPlatParam.getPlatSiteId());
                    logData.setValue(orderPlatParam.getMallMoney().negate());
                    logData.setType(SiteFinanceLogEnum.PLAT_SERVICE.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                    logData.setMemo("订单支付成功,支付手续费" + orderPlatParam.getMallMoney() + "元");

                    coreSiteFinanceService.addLog(logData);
                }
            }
        }
    }

    @Override
    public void siteOutlayRefundService(SiteOrderRefundParam orderRefundParam) {
        // 商户支出（退还手续费）
        if (orderRefundParam.getMallRefundMoney().compareTo(BigDecimal.ZERO) > 0) {
            // 查询退款记录
            PayRefund refund = payRefundMapper.selectOne(
                    new QueryWrapper<PayRefund>().eq("refund_no", orderRefundParam.getRefundNo())
            );
            if (refund != null) {
                if (!refund.getType().equals("offlinepay")) {
                    // 查询站点信息
                    Site site = siteMapper.selectOne(
                            new QueryWrapper<Site>().eq("site_id", orderRefundParam.getSiteId())
                    );
                    String siteName = (site != null) ? site.getSiteName() : "";

                    // 记录财务日志
                    SiteFinanceLog logData = buildBaseFinanceLog(orderRefundParam);
                    logData.setMainId(orderRefundParam.getSiteId());
                    logData.setMainName(siteName);
                    logData.setSiteId(orderRefundParam.getSiteId());
                    logData.setValue(orderRefundParam.getMallRefundMoney());
                    logData.setType(SiteFinanceLogEnum.PLAT_REFUND_SERVICE.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                    logData.setMemo("订单退款成功,退还手续费" + orderRefundParam.getMallRefundMoney() + "元");

                    coreSiteFinanceService.addLog(logData);
                }
            }
        }
    }

    @Override
    public void siteOutlayCommission(SiteOrderPlatParam orderPlatParam) {
        // 商户支出（支付佣金）
        if (orderPlatParam.getShopCommission().compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(
                    new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo())
            );
            if (pay != null) {
                if (!pay.getType().equals("offlinepay")) {
                    // 查询站点信息
                    Site site = siteMapper.selectOne(
                            new QueryWrapper<Site>().eq("site_id", orderPlatParam.getSiteId())
                    );
                    String siteName = (site != null) ? site.getSiteName() : "";

                    // 记录财务日志
                    SiteFinanceLog logData = buildBaseFinanceLog(orderPlatParam);
                    logData.setMainId(orderPlatParam.getSiteId());
                    logData.setMainName(siteName);
                    logData.setSiteId(orderPlatParam.getSiteId());
                    logData.setValue(orderPlatParam.getShopCommission());
                    logData.setType(SiteFinanceLogEnum.SITE_COMMISSION.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                    logData.setMemo("订单支付成功,支付佣金" + orderPlatParam.getShopCommission() + "元");

                    coreSiteFinanceService.addLog(logData);
                }
            }
        }
    }

    @Override
    public void siteOutlayRefundCommission(SiteOrderRefundParam orderRefundParam) {
        // 商户支出（退还佣金）
        if (orderRefundParam.getRefundCommission().compareTo(BigDecimal.ZERO) > 0) {
            // 查询退款记录
            PayRefund refund = payRefundMapper.selectOne(
                    new QueryWrapper<PayRefund>().eq("refund_no", orderRefundParam.getRefundNo())
            );
            if (refund != null) {
                if (!refund.getType().equals("offlinepay")) {
                    // 查询站点信息
                    Site site = siteMapper.selectOne(
                            new QueryWrapper<Site>().eq("site_id", orderRefundParam.getSiteId())
                    );
                    String siteName = (site != null) ? site.getSiteName() : "";

                    // 记录财务日志
                    SiteFinanceLog logData = buildBaseFinanceLog(orderRefundParam);
                    logData.setMainId(orderRefundParam.getSiteId());
                    logData.setMainName(siteName);
                    logData.setSiteId(orderRefundParam.getSiteId());
                    logData.setValue(orderRefundParam.getRefundCommission().negate());
                    logData.setType(SiteFinanceLogEnum.SITE_REFUND_COMMISSION.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                    logData.setMemo("订单退款成功,退还佣金" + orderRefundParam.getRefundCommission() + "元");

                    coreSiteFinanceService.addLog(logData);
                }
            }
        }
    }

    @Override
    public void siteOutlaySupplyMoney(SiteOrderPlatParam orderPlatParam) {
        // 商户支出（支付供货商货款）
        if (orderPlatParam.getSupplyMoney().compareTo(BigDecimal.ZERO) > 0) {
            // 查询支付记录
            Pay pay = payMapper.selectOne(
                    new QueryWrapper<Pay>().eq("out_trade_no", orderPlatParam.getOutTradeNo())
            );
            if (pay != null) {
                if (!pay.getType().equals("offlinepay")) {
                    // 查询供货商站点信息
                    Site site = siteMapper.selectOne(
                            new QueryWrapper<Site>().eq("site_id", orderPlatParam.getSupplyId())
                    );
                    String siteName = (site != null) ? site.getSiteName() : "";

                    // 记录财务日志
                    SiteFinanceLog logData = new SiteFinanceLog();
                    logData.setMainId(orderPlatParam.getSupplyId());
                    logData.setMainName(siteName);
                    logData.setSiteId(orderPlatParam.getSiteId());
                    logData.setValue(orderPlatParam.getSupplyMoney().negate());
                    logData.setType(SiteFinanceLogEnum.SITE_SUPPLY_MONEY.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                    logData.setTradeId(orderPlatParam.getOrderId().toString());
                    logData.setTradeNo(orderPlatParam.getOrderNo());
                    logData.setMemo("订单支付成功,支付供货商货款" + orderPlatParam.getSupplyMoney() + "元");

                    coreSiteFinanceService.addLog(logData);
                }
            }
        }
    }

    @Override
    public void siteOutlayRefundSupplyMoney(SiteOrderRefundParam orderRefundParam) {
        // 商户支出（退还供货商货款）
        if (orderRefundParam.getSupplyRefundMoney().compareTo(BigDecimal.ZERO) > 0) {
            // 查询退款记录
            PayRefund refund = payRefundMapper.selectOne(
                    new QueryWrapper<PayRefund>().eq("refund_no", orderRefundParam.getRefundNo())
            );
            if (refund != null) {
                if (!refund.getType().equals("offlinepay")) {
                    // 查询站点信息
                    Site site = siteMapper.selectOne(
                            new QueryWrapper<Site>().eq("site_id", orderRefundParam.getSiteId())
                    );
                    String siteName = (site != null) ? site.getSiteName() : "";

                    // 记录财务日志
                    SiteFinanceLog logData = buildBaseFinanceLog(orderRefundParam);
                    logData.setMainId(orderRefundParam.getSiteId());
                    logData.setMainName(siteName);
                    logData.setSiteId(orderRefundParam.getSiteId());
                    logData.setValue(orderRefundParam.getSupplyRefundMoney().negate());
                    logData.setType(SiteFinanceLogEnum.SITE_SUPPLY_REFUND_MONEY.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_REFUND.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.OUTLAY.getSource());
                    logData.setMemo("订单退款成功,退还供货商货款" + orderRefundParam.getSupplyRefundMoney() + "元");

                    coreSiteFinanceService.addLog(logData);
                }
            }
        }
    }

    /**
     * 构建基础财务日志数据
     * @param param 参数对象
     * @return 财务日志对象
     */
    private SiteFinanceLog buildBaseFinanceLog(SiteOrderPlatParam param) {
        SiteFinanceLog log = new SiteFinanceLog();
        log.setTradeId(param.getOrderId().toString());
        log.setTradeNo(param.getOrderNo());
        log.setSiteId(param.getSiteId());
        return log;
    }

    /**
     * 构建基础财务日志数据
     * @param param 参数对象
     * @return 财务日志对象
     */
    private SiteFinanceLog buildBaseFinanceLog(SiteOrderRefundParam param) {
        SiteFinanceLog log = new SiteFinanceLog();
        log.setTradeId(param.getOrderId().toString());
        log.setTradeNo(param.getOrderRefundNo());
        log.setSiteId(param.getSiteId());
        return log;
    }

    /**
     * 检查插件是否安装
     * @param key
     */
    private Boolean checkAddon(String key) {
        Map<String, InstallAddonListVo> addonListVoMap= coreAddonService.getInstallAddonList();
        return addonListVoMap.get(key) != null;
    }
}
