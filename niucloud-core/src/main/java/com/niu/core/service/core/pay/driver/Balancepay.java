package com.niu.core.service.core.pay.driver;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.loader.pay.BasePay;
import com.niu.core.common.loader.pay.param.*;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.enums.member.MemberAccountChangeTypeEnum;
import com.niu.core.enums.pay.OnliepayStatusEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.core.enums.pay.RefundTransferStatusEnum;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.core.service.core.pay.ICoreRefundService;
import com.niu.core.service.core.pay.vo.PayTypeVo;
import com.niu.core.service.core.pay.vo.TransferQueryVo;
import com.niu.core.service.core.sys.ICoreSysStatService;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@Component
@Slf4j
public class Balancepay extends BasePay {

    public ICorePayService corePayService() {
        return SpringContext.bean(ICorePayService.class);
    }

    public ICoreRefundService coreRefundService() {
        return SpringContext.bean(ICoreRefundService.class);
    }

    public ICoreMemberAccountService coreMemberAccountService() {
        return SpringContext.bean(ICoreMemberAccountService.class);
    }

    public ICoreSysStatService coreSysStatService() {
        return SpringContext.bean(ICoreSysStatService.class);
    }

    public BasePay init(JSONObject config) {
        return this;
    }

    /**
     * 支付
     * @param param
     */
    public Object pay(PayParam param) {
        Pay pay = param.getPay();
        Integer statSiteId = pay.getSiteId() != null ? pay.getSiteId() : 0;
        String mainType = pay.getMainType();
        BigDecimal money = pay.getMoney();
        String outTradeNo = pay.getOutTradeNo();
        
        switch (mainType) {
            case "member":
                // 扣除账户余额（会员流水 site_id 对齐 PHP：不传 site，写入 defaultSiteId=0）
                String memo = MemberAccountChangeTypeEnum.getType(AccountTypeEnum.BALANCE.getType())
                    .getByPath("order.name", String.class);
                coreMemberAccountService().addLog(
                    null,
                    pay.getMainId(), 
                    AccountTypeEnum.BALANCE.getType(), 
                    money.negate().doubleValue(), 
                    "order", 
                    memo, 
                    outTradeNo
                );
                
                // 充值消费金额统计
                SysDataAddParam statParam = new SysDataAddParam();
                statParam.setSiteId(statSiteId);
                statParam.setRechargePayMoney(money);
                coreSysStatService().addStat(statParam);
                break;
                
            case "user":
                // 用户支付逻辑（预留）
                break;
        }

        // 支付成功回调
        PayNotifyParam notifyParam = new PayNotifyParam();
        notifyParam.setSiteId(statSiteId);
        notifyParam.setOutTradeNo(outTradeNo);
        notifyParam.setType("balancepay");
        notifyParam.setPayStatus(OnliepayStatusEnum.SUCCESS);
        corePayService().payNotify(notifyParam);

        return null;
    }

    /**
     * 扫码支付
     */
    public Object scan(PayParam param) {
        return null;
    }

    /**
     * 关闭支付
     */
    public Boolean close(Pay pay) {
        return true;
    }

    /**
     * 异步回调
     * @param request
     * @param response
     */
    public Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    /**
     * 转账
     */
    public Object transfer(TransferParam param) {
        return null;
    }

    @Override
    public Object getOrder(String outTradeNo) {
        return null;
    }

    /**
     * 转账撤销
     */
    @Override
    public Object transferCancel(String transferNo) {
        return null;
    }

    @Override
    public TransferQueryVo transferQuery(String transferNo) {
        return null;
    }

    /**
     * 退款
     */
    public void refund(RefundParam param) {
        String outTradeNo = param.getOutTradeNo();
        BigDecimal money = param.getMoney();
        String refundNo = param.getRefundNo();
        
        Pay pay = corePayService().findPayInfoByOutTradeNo(outTradeNo);
        String mainType = pay.getMainType();
        Integer mainId = pay.getMainId();
        Integer statSiteId = pay.getSiteId() != null ? pay.getSiteId() : 0;

        switch (mainType) {
            case "member":
                // 返还余额（会员流水 site_id 对齐 PHP：不传 site，写入 defaultSiteId=0）
                String memo = MemberAccountChangeTypeEnum.getType(AccountTypeEnum.BALANCE.getType())
                    .getByPath("order_refund.name", String.class);
                coreMemberAccountService().addLog(
                    null,
                    mainId, 
                    AccountTypeEnum.BALANCE.getType(), 
                    money.doubleValue(), 
                    "order_refund", 
                    memo, 
                    refundNo
                );
                
                // 充值消费金额统计（退款为负数）
                SysDataAddParam statParam = new SysDataAddParam();
                statParam.setSiteId(statSiteId);
                statParam.setRechargePayMoney(money.negate());
                coreSysStatService().addStat(statParam);
                
                // 退款成功回调
                RefundNotifyParam notifyParam = new RefundNotifyParam();
                BeanUtil.copyProperties(param, notifyParam);
                notifyParam.setSiteId(statSiteId);
                notifyParam.setType("balance");
                notifyParam.setRefundStatus(RefundTransferStatusEnum.SUCCESS);
                coreRefundService().refundNotify(notifyParam);
                break;
                
            case "user":
                // 用户退款逻辑（预留）
                break;
        }
    }

    /**
     * 过滤支付方式 通过交易类型
     * @param tradeType
     * @return
     */
    public PayTypeVo filterPayTypeByTradeType(String tradeType) {
        PayTypeVo vo = JSONUtil.toBean(PayTypeEnum.getType().getJSONObject("balancepay"), PayTypeVo.class);
        if (tradeType.equals("recharge")) return null;
        return vo;
    }
}
