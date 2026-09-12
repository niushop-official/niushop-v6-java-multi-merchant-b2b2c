package com.niu.core.service.core.map.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.exception.CommonException;
import com.niu.core.service.core.map.CurlRequest;
import com.niu.core.service.core.map.IMapService;
import com.niu.core.service.core.sys.ICoreConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 天地图地图服务类（对应 PHP CoreTiandituMap）
 * 将天地图 API 响应格式转换为腾讯地图格式，便于前端统一处理
 */
@Service
public class CoreTiandituMap implements IMapService {

    private static final Logger log = LoggerFactory.getLogger(CoreTiandituMap.class);

    @Autowired
    private ICoreConfigService coreConfigService;

    /**
     * 获取站点对应的天地图 Key
     *
     * @param siteId 站点ID
     * @return 天地图地图 Key
     * @throws CommonException 配置缺失或 key 为空时抛出
     */
    @Override
    public String getKey(Integer siteId) {
        JSONObject config = coreConfigService.getConfigValue(siteId, "MAPKEY");
        String key = resolveTiandituServerKey(config);
        if (StrUtil.isBlank(key)) {
            throw new CommonException("请检查天地图配置");
        }
        return key;
    }

    /**
     * 读取天地图服务端 KEY（对齐 PHP tianditu_map_key，兼容 camelCase 存量数据）
     */
    private String resolveTiandituServerKey(JSONObject config) {
        if (config == null) {
            return "";
        }
        String key = config.getStr("tianditu_map_key");
        if (StrUtil.isBlank(key)) {
            key = config.getStr("tiandituMapKey");
        }
        return key == null ? "" : key;
    }

    /**
     * 构建逆地理编码 postStr（对齐 PHP json_encode(['lon'=>..., 'lat'=>..., 'ver'=>1])）
     */
    private String buildReverseGeocodePostStr(String latStr, String lonStr) {
        Map<String, Object> postStr = new LinkedHashMap<>();
        postStr.put("lon", lonStr);
        postStr.put("lat", latStr);
        postStr.put("ver", 1);
        return JSONUtil.toJsonStr(postStr);
    }

    /**
     * 通过地址字符串获取地理位置详情（地理编码）
     *
     * @param siteId  站点ID
     * @param address 地址字符串
     * @return 腾讯地图格式的响应 Map
     * @throws Exception 网络请求异常
     */
    public JSONObject addressToDetail(Integer siteId, String address) {
        String key = getKey(siteId);
        CurlRequest curl = new CurlRequest();

        Map<String, Object> params = new HashMap<>();
        params.put("tk", key);
        params.put("ds", JSONUtil.toJsonStr(Collections.singletonMap("keyWord", address)));

        String response = null;
        try {
            response = curl.get("https://api.tianditu.gov.cn/geocoder", params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject json = JSONUtil.parseObj(response);

        // 提取地址信息
        JSONObject location = json.getJSONObject("location");
        String keyword = location.getStr("keyWord", "");
        double lon = location.getDouble("lon", 0.0);
        double lat = location.getDouble("lat", 0.0);

        // 解析省市区
        Map<String, String> addressComponents = parseAddress(keyword);

        // 构建腾讯地图格式返回
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", json.getInt("status", -1));
        result.put("message", "ok".equals(json.getStr("msg")) ? "success" : "error");
        result.put("request_id", "");

        Map<String, Object> resultData = new LinkedHashMap<>();
        resultData.put("title", keyword);
        Map<String, Double> loc = new LinkedHashMap<>();
        loc.put("lng", lon);
        loc.put("lat", lat);
        resultData.put("location", loc);

        Map<String, Object> adInfo = new LinkedHashMap<>();
        adInfo.put("adcode", "");
        resultData.put("ad_info", adInfo);

        Map<String, String> comp = new LinkedHashMap<>();
        comp.put("province", addressComponents.get("province"));
        comp.put("city", addressComponents.get("city"));
        comp.put("district", addressComponents.get("district"));
        comp.put("street", "");
        comp.put("street_number", "");
        resultData.put("address_components", comp);

        resultData.put("similarity", "");
        resultData.put("deviation", "");
        resultData.put("reliability", "");
        resultData.put("level", "");

        result.put("result", resultData);
        return JSONUtil.parseObj(result);
    }

    /**
     * 通过经纬度获取地理位置详情（逆地理编码）
     *
     * @param siteId   站点ID
     * @param location 经纬度字符串，格式 "lat,lng"
     * @return 腾讯地图格式的响应 Map
     * @throws Exception 网络请求异常
     */
    public JSONObject locationToDetail(Integer siteId, String location) {
        String key = getKey(siteId);
        CurlRequest curl = new CurlRequest();

        // 解析经纬度（格式 lat,lng，与 PHP explode 一致保留字符串传入 postStr）
        String[] parts = location.split(",");
        if (parts.length != 2) {
            throw new CommonException("经纬度格式错误，应为 lat,lng");
        }
        String latStr = parts[0].trim();
        String lonStr = parts[1].trim();
        if (StrUtil.isBlank(latStr) || StrUtil.isBlank(lonStr)) {
            throw new CommonException("经纬度格式错误，应为 lat,lng");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("tk", key);
        params.put("type", "geocode");
        params.put("postStr", buildReverseGeocodePostStr(latStr, lonStr));

        String response = null;
        try {
            response = curl.get("https://api.tianditu.gov.cn/geocoder", params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JSONObject json = JSONUtil.parseObj(response);

        if (!json.containsKey("status")) {
            throw new CommonException(json.getStr("message", json.getStr("msg", "请求参数非法或不合规")));
        }
        if (json.getInt("status", 0) != 0) {
            throw new CommonException(json.getStr("msg"));
        }
        JSONObject result = json.getJSONObject("result");
        if (result == null || result.isEmpty()) {
            throw new CommonException(json.getStr("msg", "未解析到地址"));
        }
        JSONObject addressComponent = result.getJSONObject("addressComponent");

        String province = addressComponent.getStr("province", "");
        String city = addressComponent.getStr("city", "");
        String district = addressComponent.getStr("county", "");

        // 处理直辖市
        List<String> directCities = Arrays.asList("北京市", "上海市", "天津市", "重庆市");
        if (directCities.contains(province) && StrUtil.isEmpty(city)) {
            city = province;
        }

        // 构建腾讯地图格式返回
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", 0);
        data.put("message", "Success");
        data.put("request_id", "");

        Map<String, Object> resultData = new LinkedHashMap<>();
        // location
        Map<String, Double> loc = new LinkedHashMap<>();
        JSONObject locJson = result.getJSONObject("location");
        loc.put("lat", locJson.getDouble("lat", 0.0));
        loc.put("lng", locJson.getDouble("lon", 0.0));
        resultData.put("location", loc);

        resultData.put("address", result.getStr("formatted_address", ""));

        // address_component
        Map<String, String> comp = new LinkedHashMap<>();
        comp.put("nation", addressComponent.getStr("nation", "中国"));
        comp.put("province", province);
        comp.put("city", city);
        comp.put("district", district);
        comp.put("street", addressComponent.getStr("road", ""));
        comp.put("street_number", addressComponent.getStr("address", ""));
        resultData.put("address_component", comp);

        // ad_info
        Map<String, Object> adInfo = new LinkedHashMap<>();
        adInfo.put("nation_code", "");
        adInfo.put("adcode", addressComponent.getStr("county_code", ""));
        adInfo.put("phone_area_code", "");
        adInfo.put("city_code", addressComponent.getStr("city_code", ""));
        String nation = addressComponent.getStr("nation", "中国");
        String name = String.join(",", Arrays.asList(nation, province, city, district));
        adInfo.put("name", name);
        Map<String, Double> adLoc = new LinkedHashMap<>();
        adLoc.put("lat", locJson.getDouble("lat", 0.0));
        adLoc.put("lng", locJson.getDouble("lon", 0.0));
        adInfo.put("location", adLoc);
        adInfo.put("nation", nation);
        adInfo.put("province", province);
        adInfo.put("city", city);
        adInfo.put("district", district);
        adInfo.put("_distance", 0);
        resultData.put("ad_info", adInfo);

        // address_reference
        Map<String, Object> addressRef = new LinkedHashMap<>();
        // town
        Map<String, Object> town = new LinkedHashMap<>();
        town.put("id", "");
        town.put("title", addressComponent.getStr("poi", ""));
        Map<String, Double> townLoc = new LinkedHashMap<>();
        townLoc.put("lat", 0.0);
        townLoc.put("lng", 0.0);
        town.put("location", townLoc);
        town.put("_distance", 0);
        town.put("_dir_desc", "");
        addressRef.put("town", town);
        // 其他占位
        addressRef.put("landmark_l2", createEmptyAddressRef());
        addressRef.put("street", createEmptyAddressRef());
        addressRef.put("street_number", createEmptyAddressRef());
        addressRef.put("crossroad", createEmptyAddressRef());
        resultData.put("address_reference", addressRef);

        // formatted_addresses
        Map<String, String> formatted = new LinkedHashMap<>();
        formatted.put("recommend", addressComponent.getStr("poi", ""));
        formatted.put("rough", addressComponent.getStr("poi", ""));
        formatted.put("standard_address", result.getStr("formatted_address", ""));
        resultData.put("formatted_addresses", formatted);

        data.put("result", resultData);
        return JSONUtil.parseObj(data);
    }

    /**
     * 通过 IP 获取地理位置详情（对齐 PHP CoreTiandituMap ipToDetail）
     * 天地图未提供公开的 IP 定位服务，抛出异常提示切换腾讯地图
     *
     * @param siteId 站点ID
     * @param ip     IP 地址
     * @throws CommonException 天地图不支持 IP 定位
     */
    @Override
    public JSONObject ipToDetail(Integer siteId, String ip) {
        throw new CommonException("天地图暂不支持IP定位，请使用腾讯地图");
    }

    /**
     * 获取规划路线（驾车）
     *
     * @param siteId 站点ID
     * @param from   起点经纬度，格式 "lat,lng"
     * @param to     终点经纬度，格式 "lat,lng"
     * @return 腾讯地图格式的路线结果 Map
     * @throws Exception 网络请求异常
     */
    @Override
    public JSONObject getPolyline(Integer siteId, String from, String to) {
        String key = getKey(siteId);
        CurlRequest curl = new CurlRequest();

        // 将 lat,lng 转换为 lng,lat（天地图要求）
        String[] fromParts = from.split(",");
        String[] toParts = to.split(",");
        if (fromParts.length != 2 || toParts.length != 2) {
            throw new CommonException("经纬度格式错误，应为 lat,lng");
        }
        String fromLngLat = fromParts[1].trim() + "," + fromParts[0].trim();
        String toLngLat = toParts[1].trim() + "," + toParts[0].trim();

        JSONObject postData = new JSONObject();
        postData.set("orig", fromLngLat);
        postData.set("dest", toLngLat);
        postData.set("style", "0"); // 最快路线

        Map<String, Object> params = new HashMap<>();
        params.put("tk", key);
        params.put("type", "search");
        params.put("postStr", postData.toString());

        String response = null;
        try {
            response = curl.get("http://api.tianditu.gov.cn/drive", params);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 天地图返回 XML
        JSONObject parsed = parseTiandituDriveResponse(response);
        return parsed;
    }

    /**
     * 解析天地图驾车规划 XML 响应，转换为腾讯地图格式
     */
    private JSONObject parseTiandituDriveResponse(String xmlResponse) {
        try {
            // 使用 JDK 的 XML 解析（或 Hutool 的 XmlUtil）
            // 这里使用简单的字符串替换或 XPath，但为了简便，使用 DOM 解析
            javax.xml.parsers.DocumentBuilderFactory factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            javax.xml.parsers.DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document doc = builder.parse(new java.io.ByteArrayInputStream(xmlResponse.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            doc.getDocumentElement().normalize();

            // 获取根节点 <Route>
            org.w3c.dom.Element root = doc.getDocumentElement();
            // 距离（公里）
            String distanceStr = root.getElementsByTagName("distance").item(0).getTextContent();
            double distanceKm = Double.parseDouble(distanceStr);
            // 时长（秒）
            String durationStr = root.getElementsByTagName("duration").item(0).getTextContent();
            double durationSec = Double.parseDouble(durationStr);
            // 路线点串
            String routelatlon = root.getElementsByTagName("routelatlon").item(0).getTextContent();
            // 简单指引信息
            String simpleStr = root.getElementsByTagName("simple").item(0).getTextContent();
            // 解析 simple 字符串（可能包含 XML 格式的 <item>）
            // 这里直接使用 XPath 或者解析 simple 节点
            org.w3c.dom.NodeList itemNodes = ((org.w3c.dom.Element) root.getElementsByTagName("simple").item(0)).getElementsByTagName("item");
            List<Map<String, Object>> steps = new ArrayList<>();
            for (int i = 0; i < itemNodes.getLength(); i++) {
                org.w3c.dom.Element item = (org.w3c.dom.Element) itemNodes.item(i);
                String strguide = item.getElementsByTagName("strguide").item(0).getTextContent();
                String streetDistance = item.getElementsByTagName("streetDistance").item(0).getTextContent();
                String linkStreetName = item.getElementsByTagName("linkStreetName").item(0).getTextContent();
                String streetNames = item.getElementsByTagName("streetNames").item(0).getTextContent();
                Map<String, Object> step = new LinkedHashMap<>();
                step.put("instruction", strguide);
                step.put("road_name", linkStreetName.isEmpty() ? streetNames.trim() : linkStreetName);
                step.put("distance", Double.parseDouble(streetDistance));
                step.put("polyline_idx", new int[]{0, 0}); // 暂时占位
                steps.add(step);
            }

            // 生成 polyline（差分压缩）
            List<Number> polyline = generatePolyline(routelatlon);

            // 构建腾讯地图 routes
            Map<String, Object> route = new LinkedHashMap<>();
            route.put("mode", "DRIVING");
            route.put("distance", (int) (distanceKm * 1000)); // 转为米
            route.put("duration", (int) (durationSec / 60)); // 转为分钟
            route.put("traffic_light_count", 0);
            route.put("toll", 0);
            Map<String, Object> restriction = new LinkedHashMap<>();
            restriction.put("status", 1);
            route.put("restriction", restriction);
            route.put("polyline", polyline);
            route.put("steps", steps);
            route.put("tags", List.of("大众常走"));
            Map<String, Object> taxiFare = new LinkedHashMap<>();
            taxiFare.put("fare", 0);
            route.put("taxi_fare", taxiFare);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("routes", List.of(route));
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            log.error("解析天地图驾车规划 XML 失败", e);
            throw new CommonException("获取规划路线失败：" + e.getMessage());
        }
    }

    /**
     * 将天地图的路点串转换为腾讯地图差分压缩格式
     *
     * @param routelatlon 天地图格式 "lon,lat;lon,lat;..."
     * @return polyline 数值列表
     */
    private List<Number> generatePolyline(String routelatlon) {
        String[] points = routelatlon.split(";");
        List<Number> polyline = new ArrayList<>();
        int prevLat = 0;
        int prevLon = 0;
        for (int i = 0; i < points.length; i++) {
            String point = points[i];
            if (point.isEmpty()) continue;
            String[] coords = point.split(",");
            if (coords.length != 2) continue;
            double lon = Double.parseDouble(coords[0]);
            double lat = Double.parseDouble(coords[1]);
            int latVal = (int) Math.round(lat * 1_000_000);
            int lonVal = (int) Math.round(lon * 1_000_000);
            if (i == 0) {
                polyline.add(lat);
                polyline.add(lon);
            } else {
                polyline.add(latVal - prevLat);
                polyline.add(lonVal - prevLon);
            }
            prevLat = latVal;
            prevLon = lonVal;
        }
        return polyline;
    }

    /**
     * 解析地址字符串，提取省、市、区/县
     */
    private Map<String, String> parseAddress(String keyword) {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("province", "");
        result.put("city", "");
        result.put("district", "");

        if (StrUtil.isBlank(keyword)) return result;

        // 正则匹配省市区（支持自治区、直辖市、特别行政区等）
        String pattern = "^(?<province>[^省]+省|.+?自治区|上海市|北京市|天津市|重庆市|澳门特别行政区|香港特别行政区|台湾省)?(?<city>[^市]+市|.+?自治州|.+?地区|.+?盟)?(?<district>[^县]+县|[^区]+区|[^市]+市|.+?旗|.+?海域|.+?岛)?";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(keyword);
        if (m.find()) {
            String province = m.group("province") == null ? "" : m.group("province");
            String city = m.group("city") == null ? "" : m.group("city");
            String district = m.group("district") == null ? "" : m.group("district");

            // 处理直辖市
            List<String> directCities = Arrays.asList("北京市", "上海市", "天津市", "重庆市");
            if (directCities.contains(province) && city.isEmpty()) {
                city = province;
            } else if (directCities.contains(province) && !city.isEmpty() && (city.endsWith("区") || city.endsWith("县"))) {
                // 如果 city 被错误匹配成区县
                district = city;
                city = province;
            }
            result.put("province", province);
            result.put("city", city);
            result.put("district", district);
        }
        return result;
    }

    /**
     * 创建空的地址参考对象
     */
    private Map<String, Object> createEmptyAddressRef() {
        Map<String, Object> ref = new LinkedHashMap<>();
        ref.put("id", "");
        ref.put("title", "");
        Map<String, Double> loc = new LinkedHashMap<>();
        loc.put("lat", 0.0);
        loc.put("lng", 0.0);
        ref.put("location", loc);
        ref.put("_distance", 0);
        ref.put("_dir_desc", "");
        return ref;
    }

    /**
     * 获取当前使用的 Key（用于测试）
     */
    public String getKeyForTest(Integer siteId) {
        return getKey(siteId);
    }
}
