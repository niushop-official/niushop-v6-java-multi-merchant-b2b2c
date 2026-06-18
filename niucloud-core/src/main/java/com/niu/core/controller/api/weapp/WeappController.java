package com.niu.core.controller.api.weapp;

import com.niu.core.common.domain.Result;
import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.weapp.IWeappService;
import com.niu.core.service.api.weapp.parma.TradeManagedScanParam;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;
import org.apache.commons.collections.map.AbstractMapDecorator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.niu.core.service.core.delivery_weapp.vo.IsTradeManagedVo;
import jakarta.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/weapp")
public class WeappController {

    @Resource
    IWeappService weappService;

    /**
     * 通过授权code登录
     * @param param
     */
    @PostMapping("/login")
    public Result<LoginVo> login(@Validated @RequestBody WechatAuthParam param) {
        return Result.success(weappService.loginByCode(param));
    }

    /**
     * 授权信息注册账号
     * @param param
     */
    @PostMapping("/register")
    public Result<LoginVo> register(@Validated @RequestBody AuthRegisterParam param) {
        return Result.success(weappService.register(param));
    }

    /**
     * 公众号更新用户openid
     * @return
     */
    @PutMapping("/update_openid")
    public Result<Object> updateOpenid(@Validated @RequestBody WechatAuthParam param) {
        weappService.updateOpenid(param);
        return Result.success();
    }

    /**
     * 获取小程序订阅消息模板id
     * @return
     */
    @GetMapping("/subscribemsg")
    public Result<List<String>> subscribeMessage(@RequestParam("keys") String keys) {
        return Result.success(weappService.subscribeMessage(keys));
    }

    /**
     * 查询小程序是否已开通发货信息管理服务
     * @return
     */
    @GetMapping("/getIsTradeManaged")
    public Result<IsTradeManagedVo> getIsTradeManaged() {
        return Result.success(weappService.getIsTradeManaged());
    }

    /**
     * 通过外部交易号获取消息跳转路径
     * @return
     */
    @GetMapping("/getMsgJumpPath")
    public Result getMsgJumpPath(@Validated TradeManagedScanParam  param) {
        Map<String,String> data = new HashMap<>();
        data.put("path",weappService.getMsgJumpPath(param));
        return Result.success(data);
    }

    /**
     * 通过外部交易号获取消息跳转路径
     * @return
     */
    @GetMapping("/check")
    public Result check() {
        return Result.success(weappService.checkWeappConfig());
    }

}