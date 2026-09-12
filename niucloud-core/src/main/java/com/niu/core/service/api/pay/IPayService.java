package com.niu.core.service.api.pay;

import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.service.api.pay.vo.FriendspayInfoVo;
import com.niu.core.service.core.pay.vo.GetInfoByTradeVo;
import com.niu.core.service.core.pay.vo.PayTypeVo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface IPayService {

    Object pay(PayParam param);

    Object asyncNotify(PayAsyncNotifyParam param, HttpServletRequest request, HttpServletResponse response);

    GetInfoByTradeVo getInfoByTrade(String tradeType, Integer tradeId, String scene);

    FriendspayInfoVo friendspayInfo(String tradeType, Integer tradeId);

    /**
     * 获取某笔交易当前渠道下可用的支付方式
     *
     * @param tradeType 业务类型
     * @return 可用支付方式列表
     */
    List<PayTypeVo> getPayTypeByTrade(String tradeType);

    /**
     * 关闭支付单（仅允许订单所属会员操作，且仅待支付状态可关闭）
     *
     * @param outTradeNo 支付交易号
     */
    void close(String outTradeNo);
}
