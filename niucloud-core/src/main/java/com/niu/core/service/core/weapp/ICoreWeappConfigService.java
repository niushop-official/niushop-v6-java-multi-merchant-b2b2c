package com.niu.core.service.core.weapp;

import cn.hutool.json.JSONObject;
import com.niu.core.service.core.weapp.param.WeappConfigParam;
import com.niu.core.service.core.weapp.vo.WeappConfigVo;
import com.niu.core.service.core.wechat.param.WechatConfigParam;
import com.niu.core.service.core.wechat.vo.WechatConfigVo;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;

/**
 * 微信配置core服务层
 */
public interface ICoreWeappConfigService {

    /**
     * 获取微信小程序配置
     *
     * @return
     */
    WeappConfigVo getWeappConfig();

    /**
     * 设置微信小程序配置
     *
     * @param weappConfigParam
     */
    void setWeappConfig(WeappConfigParam weappConfigParam);

    /**
     * 获取微信小程序授权信息
     * @return
     */
    WxOpenAuthorizerInfoResult getWeappAuthorizationInfo(Integer siteId);

    /*
     * 设置微信小程序授权信息
     *
     */
    void setWeappAuthorizationInfo(Integer siteId,  WxOpenAuthorizerInfoResult weappAuthorizationInfo);

}
