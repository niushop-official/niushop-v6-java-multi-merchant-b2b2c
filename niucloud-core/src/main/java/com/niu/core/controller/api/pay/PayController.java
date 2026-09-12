package com.niu.core.controller.api.pay;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.common.loader.pay.param.PayAsyncNotifyParam;
import com.niu.core.common.loader.pay.param.PayParam;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.api.pay.IPayService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class PayController {

    @Resource
    IPayService payService;

    /**
     * 支付
     *
     * @param param
     * @return
     */
    @PostMapping("/pay")
    public Result<?> pay(@Validated @RequestBody PayParam param) {
        return Result.success(ObjectUtil.defaultIfNull(payService.pay(param), new ArrayList<>()));
    }

    /**
     * 支付异步通知
     *
     * @param channel
     * @param type
     * @param action
     * @param request
     * @param response
     * @return
     */
    @SaIgnore
    @RequestMapping("/pay/notify/{channel}/{type}/{action}")
    public Object notify(@PathVariable(name = "channel") String channel, @PathVariable(name = "type") String type, @PathVariable(name = "action") String action, HttpServletRequest request, HttpServletResponse response) {
        PayAsyncNotifyParam param = new PayAsyncNotifyParam();
        param.setType(type);
        param.setAction(action);
        param.setChannel(channel);
        return payService.asyncNotify(param, request, response);
    }

    /**
     * 支付
     *
     * @return
     */
    @GetMapping("/pay/info/{trade_type}/{trade_id}")
    public Result<?> info(@PathVariable("trade_type") String tradeType, @PathVariable("trade_id") Integer tradeId, @RequestParam(name = "scene", defaultValue = "") String scene) {
        return Result.success(payService.getInfoByTrade(tradeType, tradeId, scene));
    }

    @GetMapping("/pay/friendspay/info/{trade_type}/{trade_id}")
    public Result<?> friendspayInfo(@PathVariable("trade_type") String tradeType, @PathVariable("trade_id") Integer tradeId) {
        return Result.success(payService.friendspayInfo(tradeType, tradeId));
    }

    /**
     * 获取某笔交易可用的支付方式
     *
     * @param tradeType 业务类型
     * @return 可用支付方式列表
     */
    @GetMapping("/pay/type/{trade_type}")
    public Result<?> getPayType(@PathVariable("trade_type") String tradeType) {
        return Result.success(payService.getPayTypeByTrade(tradeType));
    }

    /**
     * 关闭支付单（仅待支付状态、订单所属会员可关闭）
     *
     * @param param 支付交易号参数
     * @return 操作结果
     */
    @PostMapping("/pay/close")
    public Result<?> close(@RequestBody PayCloseParam param) {
        payService.close(param.getOutTradeNo());
        return Result.success();
    }

    /**
     * 关闭支付入参
     */
    @lombok.Data
    public static class PayCloseParam {
        /** 支付交易号 */
        @com.fasterxml.jackson.annotation.JsonProperty("out_trade_no")
        private String outTradeNo;
    }
}