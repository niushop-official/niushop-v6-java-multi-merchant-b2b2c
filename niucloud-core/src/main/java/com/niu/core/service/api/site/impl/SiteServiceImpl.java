package com.niu.core.service.api.site.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.utils.DistanceCalculateUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.site.Site;
import com.niu.core.event.site.GetCircleListEventDefiner;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.api.site.ISiteService;
import com.niu.core.service.api.site.param.ApiSiteAreaCheckParam;
import com.niu.core.service.api.site.param.ApiSiteCircleListParam;
import com.niu.core.service.api.site.param.ApiSiteInfoParam;
import com.niu.core.service.api.site.param.ApiSiteNearestFrontWarehouseParam;
import com.niu.core.service.api.site.param.ApiSitePositionParam;
import com.niu.core.service.api.site.vo.ApiSiteAreaCheckVo;
import com.niu.core.service.api.site.vo.ApiSiteInfoVo;
import com.niu.core.service.api.site.vo.ApiSiteNearestFrontWarehouseVo;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import jakarta.annotation.Resource;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("apiSiteServiceImpl")
public class SiteServiceImpl implements ISiteService {

    @Resource
    private com.niu.core.service.admin.site.impl.SiteServiceImpl adminSiteService;

    @Resource
    private SiteMapper siteMapper;

    @Resource
    private DeliveryStoreMapper deliveryStoreMapper;

    @Resource
    private ICoreDiyConfigService coreDiyConfigService;

    @Override
    public List<?> getCircleList(ApiSiteCircleListParam param) {
        GetCircleListEventDefiner.GetCircleListEvent event = new GetCircleListEventDefiner.GetCircleListEvent();
        event.setStatus(1);
        if (param != null && StrUtil.isNotBlank(param.getName())) {
            event.setCircleName(param.getName());
        }
        return adminSiteService.publishGetCircleList(event);
    }

    @Override
    public ApiSiteInfoVo getInfo(ApiSiteInfoParam param) {
        if (param == null || param.getSiteId() == null || param.getSiteId() <= 0) {
            return new ApiSiteInfoVo();
        }
        Integer siteId = param.getSiteId();
        ApiSiteInfoVo vo;
        ApiSitePositionParam position = param.getPosition();
        if (position != null && position.hasLatLng()) {
            double userLat = Double.parseDouble(position.getLatitude());
            double userLng = Double.parseDouble(position.getLongitude());
            MPJQueryWrapper<Site> wrapper = new MPJQueryWrapper<>();
            wrapper.setAlias("s");
            wrapper.select("s.site_id, s.site_name, s.front_end_logo, s.latitude, s.longitude, " +
                    "s.province_id, s.city_id, s.district_id, s.address, s.full_address");
            wrapper.select(String.format(
                    "ROUND(6371 * acos(cos(radians(%f)) * cos(radians(s.latitude)) * " +
                            "cos(radians(s.longitude) - radians(%f)) + sin(radians(%f)) * sin(radians(s.latitude))), 2) AS distance",
                    userLat, userLng, userLat));
            wrapper.eq("s.site_id", siteId);
            Map<String, Object> row = siteMapper.selectJoinMap(wrapper);
            vo = mapSiteInfo(row);
        } else {
            Site site = siteMapper.selectOne(new LambdaQueryWrapper<Site>().eq(Site::getSiteId, siteId));
            vo = site != null ? BeanUtil.copyProperties(site, ApiSiteInfoVo.class) : new ApiSiteInfoVo();
        }
        Map<String, Object> qrcodeConfig = coreDiyConfigService.getSiteQrcodeConfig(siteId, "cash");
        vo.setQrcodeCashConfig(qrcodeConfig);
        return vo;
    }

    @Override
    public ApiSiteAreaCheckVo checkSiteInArea(ApiSiteAreaCheckParam param) {
        ApiSiteAreaCheckVo result = new ApiSiteAreaCheckVo();
        result.setInArea(false);
        if (param == null || param.getSiteId() == null || param.getSiteId() <= 0) {
            return result;
        }
        ApiSitePositionParam position = param.getPosition();
        if (position == null || !position.hasLatLng()) {
            return result;
        }
        return checkLocationInAreaBySiteId(param.getSiteId(), position.getLatitude(), position.getLongitude());
    }

    @Override
    public ApiSiteNearestFrontWarehouseVo getNearestFrontWarehouse(ApiSiteNearestFrontWarehouseParam param) {
        if (param == null || param.getPosition() == null) {
            return null;
        }
        ApiSitePositionParam position = param.getPosition();
        String lat = position.getLatitude();
        String lng = position.getLongitude();
        Integer provinceId = position.getProvinceId();
        Integer cityId = position.getCityId();

        String shopTable = GlobalConfig.tablePrefix + "site_front_warehouse";
        try {
            MPJQueryWrapper<Site> idWrapper = new MPJQueryWrapper<>();
            idWrapper.setAlias("s")
                    .innerJoin(shopTable + " o ON o.site_id = s.site_id")
                    .select("o.site_id")
                    .eq("s.status", 1)
                    .eq("o.business_status", 1);

            Integer circleId = RequestUtils.circleId();
            if (circleId != null && circleId > 0) {
                idWrapper.eq("o.circle_id", circleId);
            } else if (provinceId != null && provinceId > 0 && cityId != null && cityId > 0) {
                idWrapper.eq("s.province_id", provinceId).eq("s.city_id", cityId);
            }

            List<Map<String, Object>> candidateRows = siteMapper.selectJoinMaps(idWrapper);
            if (ObjectUtil.isEmpty(candidateRows)) {
                return null;
            }
            List<Integer> allSiteIds = candidateRows.stream()
                    .map(row -> toInteger(row.get("site_id")))
                    .filter(ObjectUtil::isNotEmpty)
                    .distinct()
                    .collect(Collectors.toList());
            if (allSiteIds.isEmpty()) {
                return null;
            }

            List<Integer> inAreaSiteIds = allSiteIds;
            if (StrUtil.isNotBlank(lat) && StrUtil.isNotBlank(lng)) {
                inAreaSiteIds = filterSiteIdsInArea(allSiteIds, lat, lng);
            }
            if (inAreaSiteIds.isEmpty()) {
                return null;
            }

            MPJQueryWrapper<Site> query = new MPJQueryWrapper<>();
            query.setAlias("s")
                    .innerJoin(shopTable + " o ON o.site_id = s.site_id")
                    .in("o.site_id", inAreaSiteIds)
                    .eq("s.status", 1)
                    .eq("o.business_status", 1);

            if (circleId != null && circleId > 0) {
                query.eq("o.circle_id", circleId);
            } else if (provinceId != null && provinceId > 0 && cityId != null && cityId > 0) {
                query.eq("s.province_id", provinceId).eq("s.city_id", cityId);
            }

            String fields = "o.sale_num, o.keywords, o.site_score, o.evaluate_num, " +
                    "s.full_address, s.business_hours, s.site_id, s.site_name, s.front_end_logo, s.latitude, s.longitude, s.logo";
            if (StrUtil.isNotBlank(lat) && StrUtil.isNotBlank(lng)) {
                double userLat = Double.parseDouble(lat);
                double userLng = Double.parseDouble(lng);
                fields += String.format(
                        ", ROUND(6371 * acos(cos(radians(%f)) * cos(radians(s.latitude)) * " +
                                "cos(radians(s.longitude) - radians(%f)) + sin(radians(%f)) * sin(radians(s.latitude))), 2) AS distance",
                        userLat, userLng, userLat);
                query.orderByAsc("distance");
            }
            query.select(fields);
            query.last("limit 1");
            Map<String, Object> row = siteMapper.selectJoinMap(query);
            if (row == null || row.isEmpty()) {
                return null;
            }
            return BeanUtil.fillBeanWithMap(row, new ApiSiteNearestFrontWarehouseVo(), true, true);
        } catch (Exception e) {
            // front_warehouse 表未安装或插件未迁移时返回空
            return null;
        }
    }

    /**
     * 对齐 PHP CoreLocalDeliveryService::checkLocationInAreaBySiteId
     */
    private ApiSiteAreaCheckVo checkLocationInAreaBySiteId(Integer siteId, String latitude, String longitude) {
        ApiSiteAreaCheckVo data = new ApiSiteAreaCheckVo();
        data.setInArea(false);

        DeliveryStore store = deliveryStoreMapper.selectOne(new LambdaQueryWrapper<DeliveryStore>()
                .eq(DeliveryStore::getSiteId, siteId)
                .eq(DeliveryStore::getStatus, 1)
                .eq(DeliveryStore::getSupportLocalDelivery, 1)
                .last("limit 1"));
        if (store == null || StrUtil.isBlank(store.getArea())) {
            return data;
        }

        JSONArray areas = JSONUtil.isTypeJSON(store.getArea()) ? JSONUtil.parseArray(store.getArea()) : new JSONArray();
        if (areas.isEmpty()) {
            return data;
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        Point addressPoint = geometryFactory.createPoint(new Coordinate(
                Double.parseDouble(latitude), Double.parseDouble(longitude)));

        for (int i = 0; i < areas.size(); i++) {
            JSONObject area = areas.getJSONObject(i);
            JSONObject areaJson = area.getJSONObject("area_json");
            if (areaJson == null) {
                continue;
            }
            boolean matched = false;
            if ("radius".equals(area.getStr("area_type"))) {
                JSONObject center = areaJson.getJSONObject("center");
                if (center == null || center.get("lat") == null || center.get("lng") == null || areaJson.get("radius") == null) {
                    continue;
                }
                double distance = DistanceCalculateUtils.calculateDistance(
                        Double.parseDouble(latitude),
                        Double.parseDouble(longitude),
                        Double.parseDouble(center.getStr("lat")),
                        Double.parseDouble(center.getStr("lng"))
                );
                if (new BigDecimal(distance).compareTo(new BigDecimal(areaJson.getStr("radius"))) <= 0) {
                    matched = true;
                }
            } else {
                JSONArray paths = areaJson.getJSONArray("paths");
                if (paths == null || paths.isEmpty()) {
                    continue;
                }
                List<Coordinate> polygonCoordinates = new LinkedList<>();
                for (int j = 0; j < paths.size(); j++) {
                    JSONObject latLng = paths.getJSONObject(j);
                    if (latLng.get("lat") == null || latLng.get("lng") == null) {
                        continue;
                    }
                    polygonCoordinates.add(new Coordinate(latLng.getDouble("lat"), latLng.getDouble("lng")));
                }
                if (polygonCoordinates.size() < 3) {
                    continue;
                }
                // JTS Polygon 要求首尾闭合
                Coordinate first = polygonCoordinates.get(0);
                Coordinate last = polygonCoordinates.get(polygonCoordinates.size() - 1);
                if (!first.equals2D(last)) {
                    polygonCoordinates.add(new Coordinate(first));
                }
                Polygon polygon = geometryFactory.createPolygon(polygonCoordinates.toArray(new Coordinate[0]));
                if (polygon.contains(addressPoint)) {
                    matched = true;
                }
            }
            if (matched) {
                data.setInArea(true);
                BigDecimal startPrice = ObjectUtil.defaultIfNull(area.getBigDecimal("start_price"), BigDecimal.ZERO);
                if (data.getStartPrice() == null) {
                    data.setStartPrice(startPrice);
                } else {
                    data.setStartPrice(data.getStartPrice().min(startPrice));
                }
                break;
            }
        }
        return data;
    }

    private List<Integer> filterSiteIdsInArea(List<Integer> siteIds, String latitude, String longitude) {
        List<Integer> result = new LinkedList<>();
        for (Integer siteId : siteIds) {
            ApiSiteAreaCheckVo check = checkLocationInAreaBySiteId(siteId, latitude, longitude);
            if (Boolean.TRUE.equals(check.getInArea())) {
                result.add(siteId);
            }
        }
        return result;
    }

    private ApiSiteInfoVo mapSiteInfo(Map<String, Object> row) {
        ApiSiteInfoVo vo = new ApiSiteInfoVo();
        if (row == null || row.isEmpty()) {
            return vo;
        }
        vo.setSiteId(toInteger(row.get("site_id")));
        vo.setSiteName(toString(row.get("site_name")));
        vo.setFrontEndLogo(toString(row.get("front_end_logo")));
        vo.setLatitude(toString(row.get("latitude")));
        vo.setLongitude(toString(row.get("longitude")));
        vo.setProvinceId(toInteger(row.get("province_id")));
        vo.setCityId(toInteger(row.get("city_id")));
        vo.setDistrictId(toInteger(row.get("district_id")));
        vo.setAddress(toString(row.get("address")));
        vo.setFullAddress(toString(row.get("full_address")));
        Object distance = row.get("distance");
        if (distance instanceof Number number) {
            vo.setDistance(BigDecimal.valueOf(number.doubleValue()));
        }
        return vo;
    }

    private Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private String toString(Object value) {
        return value != null ? value.toString() : null;
    }
}
