package com.niu.core.service.core.pay;

import cn.hutool.json.JSONObject;
import com.niu.core.common.loader.pay.BasePay;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.entity.pay.Pay;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.service.core.pay.vo.GetInfoByTradeVo;
import com.niu.core.service.core.pay.vo.PayTypeVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.List;

/**
 * 支付记录服务接口
 */
public interface ICorePayService {

    Object pay(PayParam param);

    BasePay driver(String channel, String type);

    GetInfoByTradeVo getInfoByTrade(String tradeType, Integer tradeId, String channel, String scene);

    Pay findPayInfoByTrade(String tradeType, Integer tradeId);

    Pay findPayInfoByOutTradeNo(String outTradeNo);

    Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response);

    void payNotify(PayNotifyParam param);

    Pay createByTrade(String tradeType, Integer tradeId);

    String buildNotifyUrl(String channel, String type, String action);

    void closeByTrade(String tradeType, Integer tradeId);

    List<PayTypeVo> getPayTypeByTrade(String tradeType, ChannelEnum channel);

    void payEdit(String json,Integer id);

    void payEditHuifu(BigDecimal feeAmt, BigDecimal confirmedAmt,BigDecimal unconfirmAmt, Integer id);

    JSONObject getPayCashierUrl(String payType, String tradeType, Integer tradeId);

    String create(Integer siteId,String mainType,Integer mainId,BigDecimal money,String tradeType,Integer tradeId,String body);
}
