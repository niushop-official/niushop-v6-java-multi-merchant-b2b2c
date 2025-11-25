package com.niu.core.service.core.pay;

import cn.hutool.json.JSONObject;
import com.niu.core.service.core.pay.param.SetTradeSceneParam;
import com.niu.core.service.core.pay.vo.TransferSceneInfo;
import com.niu.core.service.core.pay.vo.WechatTransferSceneListVo;

import java.util.List;
import java.util.Map;

public interface ICoreTransferSceneService {

    /**
     * 获取微信转账场景配置
     */
    JSONObject getWechatTransferSceneConfig();

    /**
     * 设置微信转账场景配置
     */
    void setWechatTransferSceneConfig(JSONObject data);

    /**
     * 获取转账场景
     */
    Map<String, WechatTransferSceneListVo> getWechatTransferScene();

    /**
     * 通过业务设置转账场景的配置信息
     */
    void setTradeScene(String type, SetTradeSceneParam param);

    /**
     * 通过业务获取转账场景的配置信息
     */
    TransferSceneInfo getSceneInfoByType(String type);
}
