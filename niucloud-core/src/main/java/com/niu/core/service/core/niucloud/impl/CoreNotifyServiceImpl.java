package com.niu.core.service.core.niucloud.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.NiucloudUtils;
import com.niu.core.common.utils.RequestParamUtils;
import com.niu.core.service.core.niucloud.ICoreNotifyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class CoreNotifyServiceImpl implements ICoreNotifyService {

    @Override
    public Result notify(HttpServletRequest request, HttpServletResponse response) {
        String signature = ObjectUtil.defaultIfNull(request.getParameter("signature"), "");
        String timestamp = ObjectUtil.defaultIfNull(request.getParameter("timestamp"), "");
        String nonce = ObjectUtil.defaultIfNull(request.getParameter("nonce"), "");

        // 校验签名
        NiucloudUtils.Niucloud.checkSignature(signature, timestamp, nonce);

        JSONObject data = RequestParamUtils.parseNestedParamsAsJson(request);
        String msgType = data.getStr("MsgType", "");

        switch (msgType) {
            case "auth":
                Object token = data.getByPath("Message.AccessToken.token");
                if (ObjectUtil.isNotNull(token)) {
                    NiucloudUtils.Niucloud.setAccessToken(token.toString());
                }
                break;
        }

        return Result.success();
    }
}
