package com.niu.mall.listener.fenxiao.trade;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.enums.fenxiao.FenxiaoTradeTypeEnum;
import com.niu.core.event.fenxiao.FenxiaoTradeTypeEventDefiner;
import com.niu.core.event.fenxiao.config.FenxiaoTradeTypeConfigLoader;
import com.niu.core.event.fenxiao.vo.FenxiaoTradeTypeVo;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册商城分销业务类型
 */
@Component
@EventCallback("mall")
public class FenxiaoTradeTypeListener extends FenxiaoTradeTypeEventDefiner {

    /**
     * 返回商城业务类型配置
     *
     * @param event 业务类型事件
     * @return FenxiaoTradeTypeResult
     */
    @Override
    public FenxiaoTradeTypeResult handleCallback(FenxiaoTradeTypeEvent event) {
        Map<String, FenxiaoTradeTypeVo> types = new HashMap<>();
        // 从 JSON + 枚举加载商城业务类型
        FenxiaoTradeTypeVo mall = FenxiaoTradeTypeConfigLoader.load("mall", "fenxiao/trade_type.json", FenxiaoTradeTypeEnum.MALL);
        types.put(FenxiaoTradeTypeEnum.MALL.getType(), mall);
        FenxiaoTradeTypeResult result = new FenxiaoTradeTypeResult();
        result.setTradeTypes(types);
        return result;
    }
}
