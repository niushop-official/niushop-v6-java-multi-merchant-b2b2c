package com.niu.mall.service.core.delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.delivery.DeliveryLocalDelivery;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.member.MemberAddress;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryMapper;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.service.core.delivery.vo.LocalInfoVo;
import com.niu.mall.enums.order.OrderDeliveryTypeEnum;
import com.niu.mall.service.core.delivery.ICoreLocalDeliveryService;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import jakarta.annotation.Resource;
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

    @Override
    public void calculate(OrderCreateDataVo vo) {
        if (ObjectUtil.isEmpty(vo.getDelivery().getTakeAddress())) {
            vo.getError().add("需要先选择收货地址");
            return;
        }

        MemberAddress address = (MemberAddress) vo.getDelivery().getTakeAddress();
        if (ObjectUtil.hasEmpty(address.getLat(), address.getLng())) {
            vo.getError().add("选择的收货地址缺少经纬度");
            return;
        }

        BigDecimal goodsMoney = new BigDecimal(0);
        BigDecimal goodsWeight = new BigDecimal(0);

        for (OrderCreateDataVo.Sku item : vo.getGoodsData().values()) {
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

        feeCalculate(vo, goodsMoney, goodsWeight);
    }
    //同城配送 多门店
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
//
//        Map<Integer, List<OrderCreateDataVo.Sku>> siteGoodsData = new HashMap<>();
//        for (OrderCreateDataVo.Sku item : vo.getGiftGoods().values()){
//            Integer siteId = item.getSupplyId() > 0 ? item.getSupplyId() : item.getSiteId();
//            // 如果该站点ID还没有对应的列表，就创建一个新的列表
//            if (!siteGoodsData.containsKey(siteId)) {
//                siteGoodsData.put(siteId, new ArrayList<OrderCreateDataVo.Sku>());
//            }
//
//            // 将当前商品数据添加到对应站点的列表中
//            siteGoodsData.get(siteId).add(item);
//        }
//        for(Integer siteId : siteGoodsData.keySet()){
//            BigDecimal goodsMoney = new BigDecimal(0);
//            BigDecimal goodsWeight = new BigDecimal(0);
//            for(OrderCreateDataVo.Sku item :siteGoodsData.get(siteId)){
//                OrderCreateDataVo.Goods goods = item.getGoods();
//                if (goods.getGoodsType().equals("real")) {
//                    JSONArray deliveryType = JSONUtil.parseArray(goods.getDeliveryType());
//                    if (deliveryType.contains(OrderDeliveryTypeEnum.LOCAL_DELIVERY.getType())) {
//                        goodsMoney = goodsMoney.add(item.getGoodsMoney());
//                        goodsWeight = goodsWeight.add(item.getWeight().multiply(new BigDecimal(item.getNum())));
//                    } else {
//                        item.setNotSupportDelivery(1);
//                        vo.getError().add("不支持选择的配送方式");
//                    }
//                }
//            }
//            feeCalculate(vo, goodsMoney, goodsWeight, siteId);
//        }
//    }

    private void feeCalculate(OrderCreateDataVo vo, BigDecimal goodsMoney, BigDecimal goodsWeight) {
        MemberAddress address = (MemberAddress) vo.getDelivery().getTakeAddress();

        DeliveryLocalDelivery model = shopDeliveryLocalDeliveryMapper.selectOne(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", vo.getSiteId()));
        if (model == null) {
            vo.getError().add("商家未配置同城配送");
            return;
        }
        LocalInfoVo local = new LocalInfoVo();
        BeanUtil.copyProperties(model, local);
        local.setAreas(JSONUtil.toList(JSONUtil.parseArray(model.getArea()), LocalInfoVo.Area.class));
        local.setCenters(JSONUtil.toBean(JSONUtil.parseObj(model.getCenter()), LocalInfoVo.LatLnt.class));

        LocalInfoVo.Area inArea = null;

        GeometryFactory geometryFactory = new GeometryFactory();
        // 创建点对象
        Point point = geometryFactory.createPoint(new Coordinate(Double.valueOf(address.getLat()), Double.valueOf(address.getLng())));

        for (LocalInfoVo.Area item : local.getAreas()) {
            if (item.getAreaType().equals("radius")) {
                double distance = calculateDistance(
                        Double.valueOf(address.getLat()),
                        Double.valueOf(address.getLng()),
                        Double.valueOf(item.getAreaJson().getCenter().getLat().doubleValue()),
                        Double.valueOf(item.getAreaJson().getCenter().getLng().doubleValue())
                );
                if (new BigDecimal(item.getAreaJson().getRadius()).compareTo(new BigDecimal(distance)) >= 0) {
                    inArea = item;
                    break;
                }
            } else {
                // 创建电子围栏
                List<Coordinate> polygonCoordinates = new LinkedList<>();
                for (LocalInfoVo.LatLnt latLng: item.getAreaJson().getPaths()) {
                    polygonCoordinates.add(new Coordinate(latLng.getLat().doubleValue(), latLng.getLng().doubleValue()));
                }
                Polygon polygon = geometryFactory.createPolygon(polygonCoordinates.toArray(new Coordinate[0]));
                // 判断收货地址是否在范围内
                if (polygon.contains(point)) {
                    inArea = item;
                    break;
                }
            }
        }

        if (inArea == null) {
            vo.getError().add("所选的收货地址不支持配送");
            return;
        }

        // 判断是否满足起送价
        if (goodsMoney.compareTo(inArea.getStartPrice()) == -1) {
            vo.getError().add("未达到起送费用");
            return;
        }

        BigDecimal deliveryMoney = new BigDecimal(0);
        // 按距离收费
        // 计算收货地址与取货地址的距离
        if (local.getFeeType().equals("distance")) {
            deliveryMoney = deliveryMoney.add(local.getBasePrice());
            BigDecimal distance = new BigDecimal(calculateDistance(
                    Double.valueOf(address.getLat()),
                    Double.valueOf(address.getLng()),
                    Double.valueOf(local.getCenters().getLat().doubleValue()),
                    Double.valueOf(local.getCenters().getLng().doubleValue())
            )).divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);
            if (distance.compareTo(local.getBaseDist()) == 1) {
                BigDecimal gradPrice = new BigDecimal(Math.ceil(distance.subtract(local.getBaseDist()).divide(local.getGradDist(), 2, BigDecimal.ROUND_HALF_UP).doubleValue())).multiply(local.getGradPrice());
                deliveryMoney = deliveryMoney.add(gradPrice);
            }
        } else {
            deliveryMoney = deliveryMoney.add(inArea.getDeliveryPrice());
        }

        // 计算续重费用
        if (local.getWeightStart().compareTo(BigDecimal.ZERO) == 1
                && local.getWeightUnit().compareTo(BigDecimal.ZERO) == 1
                && local.getWeightPrice().compareTo(BigDecimal.ZERO) == 1
                && local.getWeightStart().compareTo(goodsWeight) == -1
        ) {
            BigDecimal weightPrice = new BigDecimal(Math.ceil(goodsWeight.subtract(local.getWeightStart()).divide(local.getWeightUnit(), 2, BigDecimal.ROUND_HALF_UP).doubleValue())).multiply(local.getWeightPrice());
            deliveryMoney = deliveryMoney.add(weightPrice);
        }

        vo.getBasic().setDeliveryMoney(deliveryMoney);
    }
//  多门店同城配送
//    private void feeCalculate(OrderCreateDataVo vo, BigDecimal goodsMoney, BigDecimal goodsWeight, Integer siteId) {
//        MemberAddress address = (MemberAddress) vo.getDelivery().getTakeAddress();
//        DeliveryStore takeStore = vo.getDelivery().getTakeStore();
//        DeliveryLocalDelivery mode = shopDeliveryLocalDeliveryMapper.selectOne(new QueryWrapper<DeliveryLocalDelivery>().eq("site_id", siteId));
//        if (mode == null) {
//            vo.getError().add("商家未配置同城配送");
//            return;
//        }
//        if(ObjectUtil.isEmpty(address.getLat()) || ObjectUtil.isEmpty(address.getLng())){
//            vo.getError().add("所选的收货地址不支持配送");
//            return;
//        }
//        if(!(ObjectUtil.isNotEmpty(takeStore.getStoreId()) && takeStore.getStoreId()>0)){
//            vo.getError().add("请选择配送门店");
//            return;
//        }
//        DeliveryStore store = deliveryStoreMapper.selectOne(new QueryWrapper<DeliveryStore>()
//                .eq("store_id", takeStore.getStoreId()).eq("site_id", siteId).last("limit 1"));
//        if(ObjectUtil.isEmpty(store)){
//            vo.getError().add("配送门店不存在");
//            return;
//        }
//
//        GeometryFactory geometryFactory = new GeometryFactory();
//        // 收货地址
//        Point addressPoint = geometryFactory.createPoint(new Coordinate(Double.parseDouble(address.getLat()), Double.parseDouble(address.getLng())));
//
//        DeliveryLocalDelivery local = new DeliveryLocalDelivery();
//        BeanUtil.copyProperties(mode, local);
//
//        List<LocalInfoVo.Area> inArea = new ArrayList<>();
//        if (store.getArea() != null && JSONUtil.isTypeJSON(store.getArea())){
//            inArea = JSONUtil.toList(JSONUtil.parseArray(store.getArea()), LocalInfoVo.Area.class);
//        }
//
//        LocalInfoVo.Area locatedInArea =null;
//        for (LocalInfoVo.Area item : inArea) {
//            if (item.getAreaType().equals("radius")) {
//                double distance = calculateDistance(
//                        Double.parseDouble(address.getLat()),
//                        Double.parseDouble(address.getLng()),
//                        Double.parseDouble(item.getAreaJson().getCenter().getLat().toString()),
//                        Double.parseDouble(item.getAreaJson().getCenter().getLng().toString())
//                );
//                if (new BigDecimal(item.getAreaJson().getRadius()).compareTo(new BigDecimal(distance)) >= 0) {
//                    locatedInArea = item;
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
//                if (polygon.contains(addressPoint)) {
//                    locatedInArea = item;
//                    break;
//                }
//            }
//        }
//
//        if (locatedInArea == null) {
//            vo.getError().add("不在配送范围内");
//            return;
//        }
//
//        // 判断是否满足起送价
//        if (goodsMoney.compareTo(locatedInArea.getStartPrice()) < 0) {
//            vo.getError().add("差"+locatedInArea.getStartPrice().subtract(goodsMoney)+"元起送" );
//            return;
//        }
//
//        BigDecimal deliveryMoney = new BigDecimal(0);
//        // 按距离收费
//        // 计算收货地址与取货地址的距离
//        if (local.getFeeType().equals("distance")) {
//            deliveryMoney = deliveryMoney.add(local.getBasePrice());
//            BigDecimal distance = BigDecimal.valueOf(calculateDistance(
//                    Double.parseDouble(address.getLat()),
//                    Double.parseDouble(address.getLng()),
//                    Double.parseDouble(store.getLatitude()),
//                    Double.parseDouble(store.getLongitude())
//            )).divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);
//            if (distance.compareTo(local.getBaseDist()) > 0 && local.getBaseDist().compareTo(BigDecimal.ZERO) > 0) {
//                BigDecimal gradPrice = BigDecimal.valueOf(Math.ceil(distance.subtract(local.getBaseDist()).divide(local.getGradDist(), 2, RoundingMode.HALF_UP).doubleValue())).multiply(local.getGradPrice());
//                deliveryMoney = deliveryMoney.add(gradPrice);
//            }
//        } else {
//            // 按配送区域收费
//            deliveryMoney = ObjectUtil.isNotEmpty(locatedInArea.getDeliveryPrice()) ? locatedInArea.getDeliveryPrice() : BigDecimal.ZERO;
//        }
//
//        // 计算续重费用
//        if (goodsWeight.compareTo(BigDecimal.ZERO) > 0
//                && goodsWeight.compareTo(local.getWeightStart()) > 0
//                && local.getWeightUnit().compareTo(BigDecimal.ZERO) > 0
//                && local.getWeightPrice().compareTo(BigDecimal.ZERO) > 0
//        ) {
//            BigDecimal weightPrice = BigDecimal.valueOf(Math.ceil(goodsWeight.subtract(local.getWeightStart()).divide(local.getWeightUnit(), 2, RoundingMode.HALF_UP).doubleValue())).multiply(local.getWeightPrice());
//            deliveryMoney = deliveryMoney.add(weightPrice);
//        }
//        vo.getBasic().setDeliveryMoney(deliveryMoney);
//    }

    /**
     * 计算两个经纬度间的距离
     *
     */
    private static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double EARTH_RADIUS_METERS = 6371000;

        // 将经纬度差值转换为弧度
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);

        // 将单个经纬度转换为弧度
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        // Haversine公式计算
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }
}
