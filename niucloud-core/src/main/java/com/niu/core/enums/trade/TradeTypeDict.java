package com.niu.core.enums.trade;

import cn.hutool.core.util.StrUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.trade.TradeTypeEventDefiner;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务类型字典，对齐 PHP TradeDict::getTradeType
 */
public final class TradeTypeDict {

    private TradeTypeDict() {
    }

    /**
     * @param supportType 支持场景，空则返回全部业务类型；如 goods_brand / goods_category / coupon
     */
    public static Map<String, String> getTradeType(String supportType) {
        TradeTypeEventDefiner.TradeTypeEvent event = new TradeTypeEventDefiner.TradeTypeEvent();
        event.setSupportType(StrUtil.blankToDefault(supportType, ""));

        Integer siteId = RequestUtils.siteId();
        List<TradeTypeEventDefiner.TradeTypeResult> results;
        // 商户：仅执行当前站点已有插件下的监听；非商户（平台）：全部执行
        if (siteId != null && siteId > 0) {
            event.setSiteId(siteId);
            event.setAuthority(true);
            results = EventAndSubscribeOfPublisher.publishAndCallbackBySite(event);
        } else {
            event.setAuthority(false);
            results = EventAndSubscribeOfPublisher.publishAndCallback(event);
        }

        Map<String, String> tradeType = new LinkedHashMap<>();
        if (results == null) {
            return tradeType;
        }
        for (TradeTypeEventDefiner.TradeTypeResult result : results) {
            if (result != null && result.getTypes() != null && !result.getTypes().isEmpty()) {
                tradeType.putAll(result.getTypes());
            }
        }
        return tradeType;
    }
}
