package com.niu.core.service.core.recharge.finance.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.pay.Pay;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteFinanceLog;
import com.niu.core.enums.site.SiteFinanceLogEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.pay.PayMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.recharge.finance.ICorePlatService;
import com.niu.core.service.core.recharge.finance.param.OrderPlatParam;
import com.niu.core.service.core.recharge.finance.param.OrderSeparateParam;
import com.niu.core.service.core.site.ICoreSiteFinanceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class CorePlatServiceImpl implements ICorePlatService {

    @Resource
    MemberMapper memberMapper;

    @Resource
    PayMapper payMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    ICoreSiteFinanceService coreSiteFinanceService;

    @Override
    public void platIncomeOrderSeparate(OrderSeparateParam orderSeparateParam) {
        if (orderSeparateParam.getOrderMoney().compareTo(BigDecimal.ZERO) > 0){
            // 查询支付记录
            Pay pay = payMapper.selectOne(
                    new QueryWrapper<Pay>().eq("out_trade_no", orderSeparateParam.getOutTradeNo())
            );
            if (ObjectUtil.isNotEmpty( pay)){
                if (!"offlinepay".equals(pay.getType())){
                    Site site = siteMapper.selectOne(
                            new QueryWrapper<Site>().eq("site_id", orderSeparateParam.getSiteId())
                    );
                    SiteFinanceLog logData = new SiteFinanceLog();
                    logData.setMainId(orderSeparateParam.getSiteId());
                    logData.setMainName(ObjectUtil.isNotEmpty(site) && StringUtils.isNotEmpty(site.getSiteName()) ? site.getSiteName() : "");
                    logData.setValue(orderSeparateParam.getOrderMoney());
                    logData.setType(SiteFinanceLogEnum.PLAT_SEPARATE.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                    logData.setTradeId(orderSeparateParam.getOrderId().toString());
                    logData.setTradeNo(orderSeparateParam.getOrderNo());
                    logData.setMemo("订单支付成功,平台分账" + orderSeparateParam.getOrderMoney() + "元");

                    coreSiteFinanceService.addPlatLog(logData);
                }
            }
        }
    }

    @Override
    public void platIncomeOrderPay(OrderPlatParam orderPlatParam) {
        //平台
        //判断支付方式,如果是支付实际金额,则生成平台支出流水
        //根据订单的支付方式来判断,如果是余额支付,则不需要生成平台支出流水(支出)
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
                    logData.setMainId(orderPlatParam.getMemberId());
                    logData.setMainName(mainName);
                    logData.setValue(orderPlatParam.getOrderMoney());
                    logData.setType(SiteFinanceLogEnum.PAY.getSource());
                    logData.setTradeType(SiteFinanceLogEnum.ORDER_TRADE.getSource());
                    logData.setRaeType(SiteFinanceLogEnum.INCOME.getSource());
                    logData.setTradeId(orderPlatParam.getOrderId().toString());
                    logData.setTradeNo(orderPlatParam.getOrderNo());
                    logData.setMemo("充值订单支付成功,用户充值金额" + orderPlatParam.getOrderMoney() + "元,赠送金额" + orderPlatParam.getOrderGiveMoney() + "元");

                    coreSiteFinanceService.addPlatLog(logData);
                }
            }
        }
    }
}
