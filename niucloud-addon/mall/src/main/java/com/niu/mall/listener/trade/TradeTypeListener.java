package com.niu.mall.listener.trade;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.trade.TradeTypeEventDefiner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对齐 PHP addon\mall\app\listener\TradeTypeListener
 */
@Component
@EventCallback("mall")
public class TradeTypeListener extends TradeTypeEventDefiner {

    private static final String MALL = "mall";
    private static final List<String> SUPPORT_TYPES = Arrays.asList("goods_category", "goods_brand", "coupon");

    @Override
    public TradeTypeResult handleCallback(TradeTypeEvent event) {
        String supportType = event.getSupportType();
        String tradeType = event.getTradeType();

        Map<String, String> types = new HashMap<>();
        types.put(MALL, "商城");

        Map<String, Object> config = new HashMap<>();
        config.put("support_type", SUPPORT_TYPES);
        config.put("hidden_menu", List.of());
        config.put("hidden_delivery_type", List.of());

        if (StrUtil.isNotBlank(tradeType)) {
            if (MALL.equals(tradeType)) {
                TradeTypeResult result = new TradeTypeResult();
                result.setConfig(config);
                return result;
            }
            return null;
        }
        if (StrUtil.isBlank(supportType) || SUPPORT_TYPES.contains(supportType)) {
            TradeTypeResult result = new TradeTypeResult();
            result.setTypes(types);
            return result;
        }
        return null;
    }
}
