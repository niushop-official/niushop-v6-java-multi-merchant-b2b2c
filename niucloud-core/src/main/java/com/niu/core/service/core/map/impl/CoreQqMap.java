package com.niu.core.service.core.map.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.service.core.map.CurlRequest;
import com.niu.core.service.core.map.IMapService;
import com.niu.core.service.core.sys.ICoreConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 腾讯地图服务类（对应 PHP CoreQqMap）
 */
@Service
public class CoreQqMap implements IMapService {

    @Autowired
    private ICoreConfigService coreConfigService;

    /**
     * 获取站点对应的腾讯地图 Key
     *
     * @param siteId 站点ID
     * @return 地图 Key
     * @throws CommonException 配置不存在或 key 为空时抛出
     */
    @Override
    public String getKey(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "MAPKEY");
        if (config == null) {
            throw new CommonException("请检查腾讯地图配置");
        }
        String key = config.getStr("key");
        if (key == null || key.isEmpty()) {
            throw new CommonException("请检查腾讯地图配置");
        }
        return key;
    }

    /**
     * 通过地址字符串获取地理位置详情
     *
     * @param siteId  站点ID
     * @param address 地址字符串
     * @return 接口返回的原始 JSON 字符串
     * @throws Exception 网络请求异常
     */
    public JSONObject addressToDetail(Integer siteId, String address) {
        String key = getKey(siteId);
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("address", address);
        CurlRequest curl = new CurlRequest();
        String response = null;
        try {
            response = curl.get("https://apis.map.qq.com/ws/geocoder/v1/", params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject json = JSONUtil.parseObj(response);
        Integer status = json.getInt("status");
        if (status == 0) {
            return json.getJSONObject("result");
        } else {
            String message = json.getStr("message");
            throw new CommonException("请检查地图配置：" + message);
        }
    }

    /**
     * 通过 IP 获取地理位置详情（对齐 PHP CoreQqMap ipToDetail）
     * 调用腾讯地图 IP 定位接口，返回完整 JSON 响应（status==0 时包含 result.location.lat/lng）
     *
     * @param siteId 站点ID
     * @param ip     IP 地址，为空时不传 ip 参数（由腾讯接口按请求 IP 自动定位）
     * @return 完整 JSON 响应（含 status、message、result）
     */
    @Override
    public JSONObject ipToDetail(Integer siteId, String ip) {
        String key = getKey(siteId);
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        if (ip != null && !ip.isEmpty()) {
            params.put("ip", ip);
        }
        CurlRequest curl = new CurlRequest();
        String response = null;
        try {
            response = curl.get("https://apis.map.qq.com/ws/location/v1/ip", params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return JSONUtil.parseObj(response);
    }

    /**
     * 通过经纬度获取地理位置详情
     *
     * @param siteId   站点ID
     * @param location 经纬度字符串，格式为 "纬度,经度"
     * @return 接口返回的原始 JSON 字符串
     * @throws Exception 网络请求异常
     */
    public JSONObject locationToDetail(Integer siteId, String location) {
        String key = getKey(siteId);
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("location", location);
        params.put("get_poi", 0);   // 不返回周边 POI
        CurlRequest curl = new CurlRequest();
        String response = null;
        try {
            response = curl.get("https://apis.map.qq.com/ws/geocoder/v1/", params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject json = JSONUtil.parseObj(response);
        Integer status = json.getInt("status");
        if (status == 0) {
            return json;
        } else {
            String message = json.getStr("message");
            throw new CommonException("请检查地图配置：" + message);
        }
    }

    /**
     * 获取驾车路线规划
     *
     * @param siteId 站点ID
     * @param from   起点经纬度，格式 "lat,lng"
     * @param to     终点经纬度，格式 "lat,lng"
     * @return 路线规划结果对象（status=0 时返回 result 部分）
     * @throws Exception 网络请求异常或接口返回错误时抛出
     */
    @Override
    public JSONObject getPolyline(Integer siteId, String from, String to) {
        String key = getKey(siteId);
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("from", from);
        params.put("to", to);

        CurlRequest curl = new CurlRequest();
        String response = null;
        try {
            response = curl.get("https://apis.map.qq.com/ws/direction/v1/driving/", params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        JSONObject json = JSONUtil.parseObj(response);
        Integer status = json.getInt("status");
        if (status == 0) {
            return json.getJSONObject("result");
        } else {
            String message = json.getStr("message");
            throw new CommonException("请检查地图配置：" + message);
        }
    }

    /**
     * 获取当前实例使用的 Key（仅用于测试）
     */
    public String getKeyForTest(Integer siteId) {
        return getKey(siteId);
    }
}
