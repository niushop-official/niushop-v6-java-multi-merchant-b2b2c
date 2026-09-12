package com.niu.core.service.core.map.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.map.MapCommonDict;
import com.niu.core.service.admin.sys.vo.SysMapVo;
import com.niu.core.service.core.map.ICoreMapService;
import com.niu.core.service.core.map.IMapService;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
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
        String path = "https://apis.map.qq.com/ws/direction/v1/driving/";
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
                if (jsonObject.containsKey("status") && jsonObject.getInt("status") == 0) {
                    return jsonObject.getJSONObject("result");
                } else {
                    throw new CommonException("请检查地图配置:{}", jsonObject.getStr("message"));
                }
            } else {
                throw new CommonException("HTTP请求失败,状态码{}", responseCode);
            }
        } catch (Exception e) {
            throw new CommonException("获取规划路线失败:{}", e.getMessage());
        }
    }

    /**
     * 获取规划路线
     *
     * @param siteId 站点ID
     * @param from   参数， from 经纬度
     * @param to     参数， to 经纬度
     * @return 路线规划结果 Map
     */
    @Override
    public JSONObject getPolyline(Integer siteId, String from, String to) {
        IMapService driver = getDriver(siteId);
        // 如果返回的是字符串（JSON），则解析为Map（但驱动已经返回Map，无需转换）
        return driver.getPolyline(siteId, from, to);
    }

    /**
     * 获取地图配置
     */
    @Override
    public JSONObject getMapConfig() {
        CoreSysConfigVo coreConfig = coreConfigService.getConfig(RequestUtils.defaultSiteId(), "MAPKEY");
        if (ObjectUtil.isEmpty(coreConfig)) {
            coreConfig = new CoreSysConfigVo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("key", "IZQBZ-3UHEU-WTCVD-2464U-I5N4V-ZFFU3");
            jsonObject.set("is_open", 1); // 是否开启定位
            jsonObject.set("valid_time", 5); // 定位有效期/分钟，过期后将重新获取定位信息，0为不过期
            coreConfig.setValue(JSONUtils.toJSONString(jsonObject));
            coreConfig.setValueJson(jsonObject);
        }

        JSONObject jsonObject = ObjectUtil.defaultIfNull(coreConfig.getValueJson(), new JSONObject());
        jsonObject.set("key", jsonObject.getStr("key", "IZQBZ-3UHEU-WTCVD-2464U-I5N4V-ZFFU3"));
        jsonObject.set("isOpen", jsonObject.getInt("isOpen", 1));
        jsonObject.set("validTime", jsonObject.getInt("validTime", 5));
        return jsonObject;
    }

    @Override
    public SysMapVo getMapConfig(Integer siteId, boolean needEncrypt) {
        if (siteId == null) siteId = RequestUtils.defaultSiteId();
        JSONObject config = coreConfigService.getConfigValue(siteId, "MAPKEY");
        SysMapVo mapVo;

        if (config == null || config.isEmpty()) {
            mapVo = new SysMapVo();
            mapVo.setMapType("tianditu");
            mapVo.setKey("IZQBZ-3UHEU-WTCVD-2464U-I5N4V-ZFFU3");
            mapVo.setTiandituMapKey("");
            mapVo.setTiandituMapWebKey("");
            mapVo.setIsOpen(1);
            mapVo.setValidTime(5);
        } else {
            mapVo = new SysMapVo();
            mapVo.setKey(config.getStr("key"));
            mapVo.setMapType(config.getStr("map_type", config.getStr("mapType", "tencent")));
            mapVo.setTiandituMapKey(config.getStr("tianditu_map_key", config.getStr("tiandituMapKey", "")));
            mapVo.setTiandituMapWebKey(config.getStr("tianditu_map_web_key", config.getStr("tiandituMapWebKey", "")));
            Integer isOpen = config.getInt("is_open");
            if (isOpen == null) {
                isOpen = config.getInt("isOpen");
            }
            mapVo.setIsOpen(isOpen);
            Integer validTime = config.getInt("valid_time");
            if (validTime == null) {
                validTime = config.getInt("validTime");
            }
            mapVo.setValidTime(validTime);
            if (!config.containsKey("key")) {
                mapVo.setKey(null);
            }
        }

        if (mapVo.getIsOpen() == null) mapVo.setIsOpen(1);
        if (mapVo.getValidTime() == null) mapVo.setValidTime(5);
        if (ObjectUtil.isEmpty(mapVo.getMapType())) mapVo.setMapType("tencent");
        if (mapVo.getTiandituMapKey() == null) mapVo.setTiandituMapKey("");
        if (mapVo.getTiandituMapWebKey() == null) mapVo.setTiandituMapWebKey("");

        if (needEncrypt) {
            final String ENCRYPT_PLACEHOLDER = MapCommonDict.ENCRYPT_STR.getValue();
            if (ObjectUtil.isNotEmpty(mapVo.getKey())) {
                mapVo.setKey(ENCRYPT_PLACEHOLDER);
            }
            if (ObjectUtil.isNotEmpty(mapVo.getTiandituMapKey())) {
                mapVo.setTiandituMapKey(ENCRYPT_PLACEHOLDER);
            }
            if (ObjectUtil.isNotEmpty(mapVo.getTiandituMapWebKey())) {
                mapVo.setTiandituMapWebKey(ENCRYPT_PLACEHOLDER);
            }
        }

        return mapVo;
    }

    @Autowired
    private CoreQqMap coreQqMap;

    @Autowired
    private CoreTiandituMap coreTiandituMap;

    /**
     * 获取地图驱动
     *
     * @param siteId 站点ID
     * @return 对应的地图服务实例
     */
    public IMapService getDriver(Integer siteId) {
        SysMapVo config = getMapConfig(siteId, false);
        String mapType = config.getMapType();
        if ("tencent".equals(mapType)) {
            return coreQqMap;
        }
        return coreTiandituMap;
    }
}
