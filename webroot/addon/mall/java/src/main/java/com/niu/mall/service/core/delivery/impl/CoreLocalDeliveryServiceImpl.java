package com.niu.mall.service.core.delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.DistanceCalculateUtils;
import com.niu.core.entity.delivery.DeliveryLocalDelivery;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.service.core.delivery.vo.LocalInfoVo;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.service.core.delivery.ICoreLocalDeliveryService;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class CoreLocalDeliveryServiceImpl implements ICoreLocalDeliveryService {

    @Resource
    DeliveryLocalDeliveryMapper shopDeliveryLocalDeliveryMapper;
    @Resource
    DeliveryStoreMapper deliveryStoreMapper;

//    @Override
//    public void calculate(OrderCreateDataVo vo) {
//        if (ObjectUtil.isEmpty(vo.getDelivery().getTakeAddress())) {
//            vo.getError().add("需要先选择收货地址");
//            return;
//        }
//
//        MemberAddress address = (MemberAddress) vo.getDelivery().getTakeAddress();
//        if (ObjectUtil.hasEmpty(address.getLat(), address.getLng())) {
//            vo.getError().add("选择的收货地址缺少经纬度");
//            return;
//        }
//
//        BigDecimal goodsMoney = new BigDecimal(0);
//        BigDecimal goodsWeight = new BigDecimal(0);
//
//        for (OrderCreateDataVo.Sku item : vo.getGoodsData().values()) {
//            OrderCreateDataVo.Goods goods = item.getGoods();
//            if (goods.getGoodsType().equals("real")) {
//                JSONArray deliveryType = JSONUtil.parseArray(goods.getDeliveryType());
//                if (deliveryType.contains(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
//                    goodsMoney = goodsMoney.add(item.getGoodsMoney());
//                    goodsWeight = goodsWeight.add(item.getWeight().multiply(new BigDecimal(item.getNum())));
//                } else {
//                    item.setNotSupportDelivery(1);
//                    vo.getError().add("不支持选择的配送方式");
//                }
//            }
//        }
//
//        feeCalculate(vo, goodsMoney, goodsWeight);
//    }
    //同城配送 多门店
    @Override
    public void calculate(OrderCreateDataVo vo) {
        if (ObjectUtil.isEmpty(vo.getDelivery().getTakeAddress())) {
            vo.getError().add("需要先选择收货地址");
            return;
        }

        if (ObjectUtil.isEmpty(vo.getDelivery().getTakeStore())){
            vo.getError().add("请选择配送门店");
            vo.getDelivery().setError("请选择配送门店");
            return;
        }

        Map<Integer, List<OrderCreateDataVo.Sku>> siteGoodsData = new HashMap<>();
        for (OrderCreateDataVo.Sku item : vo.getGoodsData().values()){
            Integer siteId = item.getSupplyId() > 0 ? item.getSupplyId() : item.getSiteId();
            // 如果该站点ID还没有对应的列表，就创建一个新的列表
            if (!siteGoodsData.containsKey(siteId)) {
                siteGoodsData.put(siteId, new ArrayList<OrderCreateDataVo.Sku>());
            }

            // 将当前商品数据添加到对应站点的列表中
            siteGoodsData.get(siteId).add(item);
        }
        for(Integer siteId : siteGoodsData.keySet()){
            BigDecimal goodsMoney = new BigDecimal(0);
            BigDecimal goodsWeight = new BigDecimal(0);
            for(OrderCreateDataVo.Sku item :siteGoodsData.get(siteId)){
                OrderCreateDataVo.Goods goods = item.getGoods();
                if (goods.getGoodsType().equals("real")) {
                    JSONArray deliveryType = JSONUtil.parseArray(goods.getDeliveryType());
                    if (deliveryType.contains(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
                        goodsMoney = goodsMoney.add(item.getGoodsMoney());
                        goodsWeight = goodsWeight.add(item.getWeight().multiply(new BigDecimal(item.getNum())));
                    } else {
                        item.setNotSupportDelivery(1);
                        vo.getError().add("不支持选择的配送方式");
                    }
                }
            }
            feeCalculate(vo, goodsMoney, goodsWeight, siteId);
        }
    }

    @Override
    public List<Integer> checkLocationInArea(List<Site> siteList, String latitude, String longitude) {
        List<Integer> siteIds = new ArrayList<>();
        GeometryFactory geometryFactory = new GeometryFactory();
        // 创建点对象
        Point point = geometryFactory.createPoint(new Coordinate(Double.valueOf(latitude), Double.valueOf(longitude)));
        //收货地址
        List<DeliveryStore> storeList = deliveryStoreMapper.selectList(new QueryWrapper<DeliveryStore>()
                .in("site_id", siteList.stream().map(Site::getSiteId).toList()).eq("status", 1)
                .eq("support_local_delivery", 1));
        if (!storeList.isEmpty()){
            for (DeliveryStore store : storeList){
                //判断所在区域
                if (StringUtils.isNotEmpty(store.getArea())){
                    JSONArray areas = JSONUtil.isTypeJSON(store.getArea()) ? JSONUtil.parseArray(store.getArea()) : new JSONArray();
                    if(!areas.isEmpty()){
                        JSONObject locatedInArea = null;
                        for (int i=0; i<areas.size(); i++){
                            JSONObject area = areas.getJSONObject(i);
                            JSONObject areaJson = area.getJSONObject("area_json");
                            if (areaJson == null) {
                                continue;
                            }
                            if ("radius".equals(area.getStr("area_type"))){
                                JSONObject center = areaJson.getJSONObject("center");
                                if (center == null || center.get("lat") == null || center.get("lng") == null || areaJson.get("radius") == null) {
                                    continue;
                                }
                                double distance = DistanceCalculateUtils.calculateDistance(
                                        Double.valueOf(latitude),
                                        Double.valueOf(longitude),
                                        Double.valueOf(center.getStr("lat")),
                                        Double.valueOf(center.getStr("lng"))
                                );
                                if (new BigDecimal(distance).compareTo(new BigDecimal(areaJson.getStr("radius"))) <= 0) {
                                    locatedInArea = area;
                                    break;
                                }
                            } else {
                                // 创建电子围栏
                                List<Coordinate> polygonCoordinates = new LinkedList<>();
                                JSONArray paths = areaJson.getJSONArray("paths");
                                if (paths == null || paths.isEmpty()) {
                                    continue;
                                }
                                for(int j =0; j<paths.size(); j++){
                                    JSONObject latLng = paths.getJSONObject(j);
                                    polygonCoordinates.add(new Coordinate(latLng.getDouble("lat"), latLng.getDouble("lng")));
                                }
                                Polygon polygon = geometryFactory.createPolygon(polygonCoordinates.toArray(new Coordinate[0]));
                                // 判断收货地址是否在范围内
                                if (polygon.contains(point)) {
                                    locatedInArea = area;
                                    break;
                                }
                            }
                        }
                        if(locatedInArea != null && !locatedInArea.isEmpty()){
                            siteIds.add(store.getSiteId());
                        }
                    }
                }
            }
        }
        return siteIds;
    }

    //    private void feeCalculate(OrderCreateDataVo vo, BigDecimal goodsMoney, BigDecimal goodsWeight) {
//        MemberAddress address = (MemberAddress) vo.getDelivery().getTakeAddress();
//
//        DeliveryLocalDelivery model = shopDeliveryLocalDeliveryMapper.selectOne(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", vo.getSiteId()));
//        if (model == null) {
//            vo.getError().add("商家未配置同城配送");
//            return;
//        }
//        LocalInfoVo local = new LocalInfoVo();
//        BeanUtil.copyProperties(model, local);
//        local.setAreas(JSONUtil.toList(JSONUtil.parseArray(model.getArea()), LocalInfoVo.Area.class));
//        local.setCenters(JSONUtil.toBean(JSONUtil.parseObj(model.getCenter()), LocalInfoVo.LatLnt.class));
//
//        LocalInfoVo.Area inArea = null;
//
//        GeometryFactory geometryFactory = new GeometryFactory();
//        // 创建点对象
//        Point point = geometryFactory.createPoint(new Coordinate(Double.valueOf(address.getLat()), Double.valueOf(address.getLng())));
//
//        for (LocalInfoVo.Area item : local.getAreas()) {
//            if (item.getAreaType().equals("radius")) {
//                double distance = DistanceCalculateUtils.calculateDistance(
//                        Double.valueOf(address.getLat()),
//                        Double.valueOf(address.getLng()),
//                        Double.valueOf(item.getAreaJson().getCenter().getLat().doubleValue()),
//                        Double.valueOf(item.getAreaJson().getCenter().getLng().doubleValue())
//                );
//                if (new BigDecimal(item.getAreaJson().getRadius()).compareTo(new BigDecimal(distance)) >= 0) {
//                    inArea = item;
//                    break;
//                }
//            } else {
//                // 创建电子围栏
//                List<Coordinate> polygonCoordinates = new LinkedList<>();
//                for (LocalInfoVo.LatLnt latLng: item.getAreaJson().getPaths()) {
//                    polygonCoordinates.add(new Coordinate(latLng.getLat().doubleValue(), latLng.getLng().doubleValue()));
//                }
//                Polygon polygon = geometryFactory.createPolygon(polygonCoordinates.toArray(new Coordinate[0]));
//                // 判断收货地址是否在范围内
//                if (polygon.contains(point)) {
//                    inArea = item;
//                    break;
//                }
//            }
//        }
//
//        if (inArea == null) {
//            vo.getError().add("所选的收货地址不支持配送");
//            return;
//        }
//
//        // 判断是否满足起送价
//        if (goodsMoney.compareTo(inArea.getStartPrice()) == -1) {
//            vo.getError().add("未达到起送费用");
//            return;
//        }
//
//        BigDecimal deliveryMoney = new BigDecimal(0);
//        // 按距离收费
//        // 计算收货地址与取货地址的距离
//        if (local.getFeeType().equals("distance")) {
//            deliveryMoney = deliveryMoney.add(local.getBasePrice());
//            BigDecimal distance = new BigDecimal(DistanceCalculateUtils.calculateDistance(
//                    Double.valueOf(address.getLat()),
//                    Double.valueOf(address.getLng()),
//                    Double.valueOf(local.getCenters().getLat().doubleValue()),
//                    Double.valueOf(local.getCenters().getLng().doubleValue())
//            )).divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);
//            if (distance.compareTo(local.getBaseDist()) == 1) {
//                BigDecimal gradPrice = new BigDecimal(Math.ceil(distance.subtract(local.getBaseDist()).divide(local.getGradDist(), 2, BigDecimal.ROUND_HALF_UP).doubleValue())).multiply(local.getGradPrice());
//                deliveryMoney = deliveryMoney.add(gradPrice);
//            }
//        } else {
//            deliveryMoney = deliveryMoney.add(inArea.getDeliveryPrice());
//        }
//
//        // 计算续重费用
//        if (local.getWeightStart().compareTo(BigDecimal.ZERO) == 1
//                && local.getWeightUnit().compareTo(BigDecimal.ZERO) == 1
//                && local.getWeightPrice().compareTo(BigDecimal.ZERO) == 1
//                && local.getWeightStart().compareTo(goodsWeight) == -1
//        ) {
//            BigDecimal weightPrice = new BigDecimal(Math.ceil(goodsWeight.subtract(local.getWeightStart()).divide(local.getWeightUnit(), 2, BigDecimal.ROUND_HALF_UP).doubleValue())).multiply(local.getWeightPrice());
//            deliveryMoney = deliveryMoney.add(weightPrice);
//        }
//
//        vo.getBasic().setDeliveryMoney(deliveryMoney);
//    }
//  多门店同城配送
    private void feeCalculate(OrderCreateDataVo vo, BigDecimal goodsMoney, BigDecimal goodsWeight, Integer siteId) {
        MemberAddress address = (MemberAddress) vo.getDelivery().getTakeAddress();
        OrderCreateDataVo.TakeStore takeStore = new OrderCreateDataVo.TakeStore();
        if(ObjectUtil.isNotEmpty(vo.getDelivery().getTakeStore()) && !vo.getDelivery().getTakeStore().toString().equals("[]")){
            takeStore = JSONUtil.toBean(JSONUtil.parseObj(vo.getDelivery().getTakeStore()), OrderCreateDataVo.TakeStore.class);
        }
        DeliveryLocalDelivery mode = shopDeliveryLocalDeliveryMapper.selectOne(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", siteId));
        if (ObjectUtil.isEmpty( mode)) {
            vo.getError().add("商家未配置同城配送");
            vo.getDelivery().setError("商家未配置同城配送");
            return;
        }
        if(ObjectUtil.isEmpty(address.getLat()) || ObjectUtil.isEmpty(address.getLng())){
            vo.getError().add("所选的收货地址不支持配送");
            vo.getDelivery().setError("所选的收货地址不支持配送");
            return;
        }
        if(!(ObjectUtil.isNotEmpty(takeStore.getStoreId()) && takeStore.getStoreId()>0)){
            vo.getError().add("请选择配送门店");
            vo.getDelivery().setError("请选择配送门店");
            return;
        }
        DeliveryStore store = deliveryStoreMapper.selectOne(new QueryWrapper<DeliveryStore>()
                .eq("store_id", takeStore.getStoreId()).eq("site_id", siteId).last("limit 1"));
        if(ObjectUtil.isEmpty(store)){
            vo.getError().add("提货点不存在");
            vo.getDelivery().setError("提货点不存在");
            return;
        }

        GeometryFactory geometryFactory = new GeometryFactory();
        // 收货地址
        Point addressPoint = geometryFactory.createPoint(new Coordinate(Double.parseDouble(address.getLat()), Double.parseDouble(address.getLng())));

        DeliveryLocalDelivery local = new DeliveryLocalDelivery();
        BeanUtil.copyProperties(mode, local);

        if (StringUtils.isEmpty(store.getArea())){
            vo.getError().add("该门店未设置配送范围");
            vo.getDelivery().setError("该门店未设置配送范围");
            return;
        }

        JSONArray areas = store.getArea() != null && JSONUtil.isTypeJSON(store.getArea())
                ? JSONUtil.parseArray(store.getArea()) : new JSONArray();

        LocalInfoVo.Area locatedInArea = null;
        for (int i = 0; i < areas.size(); i++) {
            JSONObject area = areas.getJSONObject(i);
            JSONObject areaJson = area.getJSONObject("area_json");
            if (areaJson == null) {
                continue;
            }
            if ("radius".equals(area.getStr("area_type"))) {
                JSONObject center = areaJson.getJSONObject("center");
                if (center == null || center.get("lat") == null || center.get("lng") == null || areaJson.get("radius") == null) {
                    continue;
                }
                double distance = DistanceCalculateUtils.calculateDistance(
                        Double.parseDouble(address.getLat()),
                        Double.parseDouble(address.getLng()),
                        Double.parseDouble(center.getStr("lat")),
                        Double.parseDouble(center.getStr("lng"))
                );
                if (new BigDecimal(areaJson.getStr("radius")).compareTo(new BigDecimal(distance)) >= 0) {
                    locatedInArea = toArea(area);
                    break;
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
                Polygon polygon = geometryFactory.createPolygon(polygonCoordinates.toArray(new Coordinate[0]));
                if (polygon.contains(addressPoint)) {
                    locatedInArea = toArea(area);
                    break;
                }
            }
        }

        if (ObjectUtil.isEmpty(locatedInArea)) {
            vo.getError().add("不在配送范围内");
            vo.getDelivery().setError("不在配送范围内");
            return;
        }

        // 判断是否满足起送价
        if (locatedInArea != null && goodsMoney.compareTo(locatedInArea.getStartPrice()) < 0) {
            vo.getError().add("差" + locatedInArea.getStartPrice().subtract(goodsMoney) + "元起送");
            vo.getDelivery().setError("差" + locatedInArea.getStartPrice().subtract(goodsMoney) + "元起送");
            return;
        }

        BigDecimal deliveryMoney = new BigDecimal(0);
        // 按距离收费
        // 计算收货地址与取货地址的距离
        if (local.getFeeType().equals("distance")) {
            deliveryMoney = deliveryMoney.add(local.getBasePrice());
            BigDecimal distance = BigDecimal.valueOf(DistanceCalculateUtils.calculateDistance(
                    Double.parseDouble(address.getLat()),
                    Double.parseDouble(address.getLng()),
                    Double.parseDouble(store.getLatitude()),
                    Double.parseDouble(store.getLongitude())
            )).divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);
            if (distance.compareTo(local.getBaseDist()) > 0 && local.getGradDist().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal gradPrice = BigDecimal.valueOf(Math.ceil(distance.subtract(local.getBaseDist()).divide(local.getGradDist(), 2, RoundingMode.HALF_UP).doubleValue())).multiply(local.getGradPrice());
                deliveryMoney = deliveryMoney.add(gradPrice);
            }
        } else {
            // 按配送区域收费
            deliveryMoney =ObjectUtil.isNotEmpty(locatedInArea) && ObjectUtil.isNotEmpty(locatedInArea.getDeliveryPrice())  ? locatedInArea.getDeliveryPrice() : BigDecimal.ZERO;

        }

        // 计算续重费用
        if (goodsWeight.compareTo(BigDecimal.ZERO) > 0
                && goodsWeight.compareTo(local.getWeightStart()) > 0
                && local.getWeightUnit().compareTo(BigDecimal.ZERO) > 0
                && local.getWeightPrice().compareTo(BigDecimal.ZERO) > 0
        ) {
            BigDecimal weightPrice = BigDecimal.valueOf(Math.ceil(goodsWeight.subtract(local.getWeightStart()).divide(local.getWeightUnit(), 2, RoundingMode.HALF_UP).doubleValue())).multiply(local.getWeightPrice());
            deliveryMoney = deliveryMoney.add(weightPrice);
        }
        // 满免运费：商品金额达到免运费阈值则免除配送费
        if (ObjectUtil.isNotNull(mode.getFreeGoodsMoney()) && mode.getFreeGoodsMoney().compareTo(BigDecimal.ZERO) > 0
                && goodsMoney.compareTo(mode.getFreeGoodsMoney()) >= 0) {
            deliveryMoney = BigDecimal.ZERO;
        }
        vo.getBasic().setDeliveryMoney(deliveryMoney);
    }

    private LocalInfoVo.Area toArea(JSONObject area) {
        LocalInfoVo.Area areaVo = new LocalInfoVo.Area();
        areaVo.setAreaName(area.getStr("area_name"));
        areaVo.setAreaType(area.getStr("area_type"));
        areaVo.setStartPrice(ObjectUtil.defaultIfNull(area.getBigDecimal("start_price"), BigDecimal.ZERO));
        areaVo.setDeliveryPrice(ObjectUtil.defaultIfNull(area.getBigDecimal("delivery_price"), BigDecimal.ZERO));
        return areaVo;
    }
}
