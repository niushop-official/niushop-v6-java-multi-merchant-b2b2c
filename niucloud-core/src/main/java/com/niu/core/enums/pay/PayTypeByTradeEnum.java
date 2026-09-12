package com.niu.core.enums.pay;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

/**
 * 按交易类型限制支付方式（对齐 PHP PayTypeByTradeDict）
 */
public class PayTypeByTradeEnum {

    public static JSONObject getConfig() {
        return JsonModuleLoader.build().mergeResultElement("pay/pay_type_by_trade.json");
    }

    /**
     * 当前交易类型是否允许使用该支付方式
     */
    public static boolean isPayTypeAllowed(String payTypeKey, String tradeType) {
        if (StrUtil.isBlank(tradeType) || StrUtil.isBlank(payTypeKey)) {
            return true;
        }
        JSONObject tradeConfig = getConfig().getJSONObject(tradeType);
        if (tradeConfig == null || tradeConfig.isEmpty()) {
            return true;
        }
        JSONArray notAllow = tradeConfig.getJSONArray("not_allow");
        if (notAllow == null || notAllow.isEmpty()) {
            return true;
        }
        return !notAllow.contains(payTypeKey);
    }
}
