package com.niu.core.service.api.weapp;

import com.niu.core.service.api.login.vo.LoginVo;
import com.niu.core.service.api.weapp.parma.TradeManagedScanParam;
import com.niu.core.service.api.wechat.param.AuthRegisterParam;
import com.niu.core.service.api.wechat.param.WechatAuthParam;
import com.niu.core.service.core.delivery_weapp.vo.IsTradeManagedVo;

import java.util.List;
import java.util.Map;

public interface IWeappService {

    /**
     * 授权登录
     * @param param
     * @return
     */
    LoginVo loginByCode(WechatAuthParam param);

    /**
     * 授权信息注册账号
     * @param param
     * @return
     */
    LoginVo register(AuthRegisterParam param);

    /**
     * 小程序更新用户openid
     * @param param
     */
    void updateOpenid(WechatAuthParam param);

    List<String> subscribeMessage(String keys);

    /**
     * 查询小程序是否已开通发货信息管理服务
     * @return
     */
    IsTradeManagedVo getIsTradeManaged();

    /**
     * 通过外部交易号获取消息跳转路径
     * @return
     */
    String getMsgJumpPath(TradeManagedScanParam  param);


    /**
     * 检查微信小程序是否配置
     * @return
     */
    Boolean checkWeappConfig();
}
