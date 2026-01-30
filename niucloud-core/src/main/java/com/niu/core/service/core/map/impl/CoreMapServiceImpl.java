package com.niu.core.service.core.map.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.map.ICoreMapService;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 同城配送服务层
 */
@Service
public class CoreMapServiceImpl implements ICoreMapService {

    @Resource
    private ICoreConfigService coreConfigService;

    /**
     * 获取规划路线
     */
    @Override
    public JSONObject getPolygon(String from, String to) {
        String path ="https://apis.map.qq.com/ws/direction/v1/driving/";
        JSONObject map = getMapConfig();

        // 构建查询参数
        String queryParams = String.format(
                "key=%s&from=%s&to=%s",
                URLEncoder.encode(map.getStr("key"), StandardCharsets.UTF_8),
                URLEncoder.encode(from, StandardCharsets.UTF_8),
                URLEncoder.encode(to, StandardCharsets.UTF_8)
        );

        String fullUrl = path + "?" + queryParams;
        try {
            // 创建HTTP连接
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求参数
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000); // 1秒超时
            connection.setReadTimeout(1000);
            connection.setDoOutput(false);
            connection.setInstanceFollowRedirects(true); // 自动重定向
            connection.setRequestProperty("User-Agent", "Java-HttpClient");

            // 获取响应
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                JSONObject jsonObject = JSONUtil.parseObj(response.toString());
                if(jsonObject.containsKey("status") && jsonObject.getInt("status") == 0){
                    return jsonObject.getJSONObject("result");
                }else{
                    throw new CommonException("请检查地图配置:{}", jsonObject.getStr("message"));
                }
            } else {
                throw new CommonException("HTTP请求失败,状态码{}", responseCode);
            }
        }catch (Exception e){
            throw new CommonException("获取规划路线失败:{}", e.getMessage());
        }
    }

    /**
     * 获取地图配置
     */
    @Override
    public JSONObject getMapConfig() {
        CoreSysConfigVo coreConfig = coreConfigService.getConfig(RequestUtils.defaultSiteId(), "MAPKEY");
        if(ObjectUtil.isEmpty(coreConfig)){
            coreConfig = new CoreSysConfigVo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("key", "IZQBZ-3UHEU-WTCVD-2464U-I5N4V-ZFFU3");
            jsonObject.set("is_open", 1); // 是否开启定位
            jsonObject.set("valid_time", 5); // 定位有效期/分钟，过期后将重新获取定位信息，0为不过期
            coreConfig.setValue(JSONUtils.toJSONString(jsonObject));
            coreConfig.setValueJson(jsonObject);
        }

        JSONObject jsonObject = ObjectUtil.defaultIfNull(coreConfig.getValueJson(), new JSONObject());
        jsonObject.set("key", jsonObject.getStr("key","IZQBZ-3UHEU-WTCVD-2464U-I5N4V-ZFFU3"));
        jsonObject.set("isOpen", jsonObject.getInt("isOpen",1));
        jsonObject.set("validTime", jsonObject.getInt("validTime",5));
        return jsonObject;
    }
}
