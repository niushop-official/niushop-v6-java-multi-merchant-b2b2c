package com.niu.core.controller.siteapi.order;

import com.niu.core.common.domain.Result;
import com.niu.core.enums.trade.TradeTypeDict;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易类型
 * 对齐 PHP: basic/get_trade → TradeDict::getTradeType()
 */
@RestController
@RequestMapping("adminapi/basic")
public class OrderController {

    @GetMapping("/get_trade")
    public Result<Object> getTradeType() {
        return Result.success(TradeTypeDict.getTradeType(""));
    }
}
