package com.niu.core.service.core.weapp.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.WechatUtils;
import com.niu.core.service.core.weapp.ICoreWeappService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class CoreWeappServiceImpl implements ICoreWeappService {

    @Override
    public byte[] qrcodeBytes(Integer siteId, String page, Map<String, Object> data, Integer width) {
        try {
            List<String> scene = new LinkedList<>();
            for (String key : data.keySet()) {
                scene.add(key + "-" + data.get(key).toString());
            }

            return WechatUtils.miniapp(siteId).getQrcodeService().createWxaCodeUnlimitBytes(
                    String.join("&", scene),
                    page,
                    false,
                    "release",
                    width,
                    false,
                    null,
                    false
            );
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public File qrcodeFile(Integer siteId, String filePath, String page, Map<String, Object> data, Integer width) {
        try {
            List<String> scene = new LinkedList<>();
            for (String key : data.keySet()) {
                scene.add(key + "-" + data.get(key).toString());
            }

            return WechatUtils.miniapp(siteId).getQrcodeService().createWxaCodeUnlimit(
                    String.join("&", scene),
                    page,
                    filePath,
                    false,
                    "release",
                    width,
                    false,
                    null,
                    false
            );
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public String getUrlLink(String pageUrl, Map<String, Object> data) {
        // 构建查询参数
        List<String> queryParams = new ArrayList<>();
        for (String key : data.keySet()) {
            queryParams.add(key + "=" + data.get(key).toString());
        }

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("path", pageUrl);
        requestBody.addProperty("query", String.join("&", queryParams));
        requestBody.addProperty("expire_time", System.currentTimeMillis() / 1000 + 900);
        requestBody.addProperty("expire_type", 0);

        try {
            String response = WechatUtils.miniapp(0).post("/wxa/generate_urllink", requestBody);
            if(JSONUtil.isTypeJSON( response) && response.contains("errcode")){
                String errcode = JSONUtil.parseObj(response).getStr("errcode");
                if(errcode.equals("0")){
                    return  JSONUtil.parseObj(response).getStr("url_link");
                }else{
                    return "";
                }
            }else{
                throw new CommonException("小程序URL Link生成失败");
            }
        } catch (WxErrorException e) {
            throw new CommonException("小程序URL Link生成失败,原因{}",e.getMessage());
        }
    }
}
