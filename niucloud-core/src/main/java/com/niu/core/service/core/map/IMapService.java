package com.niu.core.service.core.map;

import cn.hutool.json.JSONObject;

public interface IMapService {

    String getKey(Integer siteId);

    JSONObject locationToDetail(Integer siteId, String location);

    JSONObject addressToDetail(Integer siteId, String address);

    /**
     * 通过 IP 获取地理位置详情（对齐 PHP CoreQqMap/CoreTiandituMap ipToDetail）
     *
     * @param siteId 站点ID
     * @param ip     IP 地址，为空时由调用方自行获取请求 IP
     * @return 地图服务返回的完整 JSON 响应
     */
    JSONObject ipToDetail(Integer siteId, String ip);

    JSONObject getPolyline(Integer siteId, String from, String to);
}
