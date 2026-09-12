package com.niu.core.controller.adminapi.sys;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.pay.PayTypeEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 公共数据字典
 */
@RestController
@RequestMapping("adminapi/sys")
@SaCheckLogin
public class SysCommonController {

    /**
     * 获取支付方式
     */
    @GetMapping("/pay_type")
    public Result<JSONObject> getPayType() {
        return Result.success(PayTypeEnum.getType());
    }

    /**
     * 获取订单来源
     */
    @GetMapping("/order_from")
    public Result<Map<String, String>> getOrderFrom() {
        return Result.success(ChannelEnum.getMap());
    }
}
