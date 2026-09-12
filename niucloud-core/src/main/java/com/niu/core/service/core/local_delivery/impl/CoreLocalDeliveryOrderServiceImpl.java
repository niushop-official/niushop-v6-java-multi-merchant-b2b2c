package com.niu.core.service.core.local_delivery.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.loader.local_delivery.BaseLocalDelivery;
import com.niu.core.common.loader.local_delivery.LocalDeliveryLoader;
import com.niu.core.common.utils.DistanceCalculateUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.entity.member.Member;
import com.niu.core.enums.delivery.DeliveryStoreTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.event.local_delivery.GetLocalDeliveryTrackEventDefiner;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.api.delivery.param.TrackOfLocalServiceParam;
import com.niu.core.service.api.delivery.vo.RiderLocationVo;
import com.niu.core.service.api.delivery.vo.TrackCenterVo;
import com.niu.core.service.api.delivery.vo.TrackOfLocalServiceVo;
import com.niu.core.service.api.delivery.vo.TrackPointsVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.delivery.ICoreDeliveryConfigService;
import com.niu.core.service.core.delivery.vo.AdminDeliveryConfigVo;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryOrderService;
import com.niu.core.service.core.local_delivery.ICoreLocalDeliveryService;
import com.niu.core.service.core.map.ICoreMapService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 同城配送事件服务实现
 */
@Log4j2
@Service
public class CoreLocalDeliveryOrderServiceImpl implements ICoreLocalDeliveryOrderService {

    @Resource
    private DeliveryLocalDeliveryOrderMapper deliveryLocalDeliveryOrderMapper;
    @Resource
    private ICoreLocalDeliveryService coreLocalDeliveryService;
    @Resource
    private ICoreDeliveryConfigService coreDeliveryConfigService;
    @Resource
    private ICoreMapService coreMapService;
    @Resource
    private MemberMapper memberMapper;

    @Override
    @Transactional
    public Integer syncOrder(Integer id) {
        DeliveryLocalDeliveryOrder info = deliveryLocalDeliveryOrderMapper.selectById(id);
        if (ObjectUtil.isEmpty(info)) {
            throw new CommonException("DELIVERY_ORDER_NOT_EXIT");
        }
        Map<String, Object> localDeliveryConfig = new HashMap<>();
        if (!DeliveryStoreTypeEnum.MERCHANT.getKey().equals(info.getDeliveryService())) {
            localDeliveryConfig = coreLocalDeliveryService.getConfig(info.getDeliveryService(), info.getSiteId());
        }

        localDeliveryConfig.put("siteId", info.getSiteId());
        try {
            //第三方配送下单 驱动
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(info.getDeliveryService()).init(JSONUtil.parseObj(localDeliveryConfig));
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("delivery_no", info.getDeliveryNo());
            Map<String, Object> saveData = driver.queryOrderInfo(requestData);
            if (ObjectUtil.isEmpty(saveData)) {
                return 0;
            } else {
                DeliveryLocalDeliveryOrder updateData = BeanUtil.mapToBean(saveData, DeliveryLocalDeliveryOrder.class, true, null);
                updateData.setId(info.getId());
                return deliveryLocalDeliveryOrderMapper.updateById(updateData);
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    @Override
    public Boolean getUnderwayDeliveryOrder(String tradeType, String tradeNo) {
        List<DeliveryLocalDeliveryOrder> list = deliveryLocalDeliveryOrderMapper.selectList(new QueryWrapper<DeliveryLocalDeliveryOrder>()
                .eq("trade_type", tradeType).eq("trade_no", tradeNo).ne("status", LocalDeliveryStatusEnum.CANCELED.getStatus()));
        if (list.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public RiderLocationVo getRiderLocation(TrackOfLocalServiceParam param) {
        DeliveryLocalDeliveryOrder localDeliveryOrder = deliveryLocalDeliveryOrderMapper.selectOne(new LambdaQueryWrapper<DeliveryLocalDeliveryOrder>()
                .eq(DeliveryLocalDeliveryOrder::getTradeType, param.getTradeType())
                .eq(DeliveryLocalDeliveryOrder::getTradeNo, param.getTradeNo())
                .orderByDesc(DeliveryLocalDeliveryOrder::getCreateTime)
                .last("limit 1"));
        if (ObjectUtil.isEmpty(localDeliveryOrder)) {
            throw new CommonException("DELIVERY_ORDER_NOT_EXIT");
        }
        syncMerchantInDeliveryStatus(localDeliveryOrder);
        RiderLocationVo location = RiderLocationVo.builder().latitude("").longitude("").build();
        Integer status;
        try {
            status = Integer.parseInt(String.valueOf(localDeliveryOrder.getStatus()));
        } catch (Exception e) {
            return location;
        }
        if (!LocalDeliveryStatusEnum.PENDING_PICKUP.getStatus().equals(status)
                && !LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().equals(status)) {
            return location;
        }
        try {
            Map<String, Object> config = new HashMap<>();
            if (!DeliveryStoreTypeEnum.MERCHANT.getKey().equals(localDeliveryOrder.getDeliveryService())) {
                config = coreLocalDeliveryService.getConfig(localDeliveryOrder.getDeliveryService(), localDeliveryOrder.getSiteId());
            }
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(localDeliveryOrder.getDeliveryService())
                    .init(JSONUtil.parseObj(config));
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("delivery_no", localDeliveryOrder.getDeliveryNo());
            Map<String, Object> responseData = driver.getTransporterPosition(requestData);
            if (ObjectUtil.isNotEmpty(responseData)) {
                location.setLatitude(ObjectUtil.defaultIfNull(responseData.get("rider_latitude"), "").toString());
                location.setLongitude(ObjectUtil.defaultIfNull(responseData.get("rider_longitude"), "").toString());
            }
        } catch (Exception e) {
            log.error("获取骑手位置失败 tradeType={}, tradeNo={}, err={}",
                    param.getTradeType(), param.getTradeNo(), e.getMessage());
        }
        return location;
    }

    @Override
    public TrackOfLocalServiceVo getTrackInfo(TrackOfLocalServiceParam param) {
        DeliveryLocalDeliveryOrder localDeliveryOrder = deliveryLocalDeliveryOrderMapper.selectOne(new LambdaQueryWrapper<DeliveryLocalDeliveryOrder>()
                .eq(DeliveryLocalDeliveryOrder::getTradeType, param.getTradeType())
                .eq(DeliveryLocalDeliveryOrder::getTradeNo, param.getTradeNo())
                .orderByDesc(DeliveryLocalDeliveryOrder::getCreateTime)
                .last("limit 1")
        );
        if (ObjectUtil.isEmpty(localDeliveryOrder)) {
            return new TrackOfLocalServiceVo();
        }
        syncMerchantInDeliveryStatus(localDeliveryOrder);
        //查询配送订单的起始点
        GetLocalDeliveryTrackEventDefiner.GetLocalDeliveryTrackEvent event = new GetLocalDeliveryTrackEventDefiner.GetLocalDeliveryTrackEvent();
        event.setSiteId(localDeliveryOrder.getSiteId());
        event.setLocalDeliveryOrder(localDeliveryOrder);
        event.setMemberId(param.getMemberId());
        List<GetLocalDeliveryTrackEventDefiner.GetLocalDeliveryTrackResult> resultList = EventAndSubscribeOfPublisher.publishAndCallback(event);
        List<TrackPointsVo> points = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            GetLocalDeliveryTrackEventDefiner.GetLocalDeliveryTrackResult itemResult = resultList.get(i);
            if (ObjectUtil.isNotEmpty(itemResult) && ObjectUtil.isNotEmpty(itemResult.getPoints())) {
                points = itemResult.getPoints();
            }
        }
        // 商家配送无三方轨迹监听，按 PHP 地图展示逻辑兜底生成点位
        if (points.isEmpty() && DeliveryStoreTypeEnum.MERCHANT.getKey().equals(localDeliveryOrder.getDeliveryService())) {
            points = buildMerchantTrackPoints(localDeliveryOrder, param.getMemberId());
        }
        if (points.isEmpty()) {
            return new TrackOfLocalServiceVo();
        }
        TrackOfLocalServiceVo data = new TrackOfLocalServiceVo();
        data.setPoints(points);
        //增加
        AdminDeliveryConfigVo configVo = coreDeliveryConfigService.getAdminDeliveryConfig();
        Integer isShowPolyline = ObjectUtil.isNotEmpty(configVo.getIsShowPolyline()) ? configVo.getIsShowPolyline() : 0;
        JSONObject driving = new JSONObject();
        TrackCenterVo center = new TrackCenterVo();
        if (points.size() > 1) {
            center = calculateCenter(points);
            if (isShowPolyline > 0) {
                TrackPointsVo fromPoint = points.get(0);
                TrackPointsVo toPoint = points.get(1);
                try {
//                    driving = coreMapService.getPolygon(fromPoint.getLatitude() + "," + fromPoint.getLongitude(), toPoint.getLatitude() + "," + toPoint.getLongitude());
                    driving = coreMapService.getPolyline(event.getSiteId(), fromPoint.getLatitude() + "," + fromPoint.getLongitude(), toPoint.getLatitude() + "," + toPoint.getLongitude());
                } catch (Exception e) {
                    log.error(" 获取配送订单轨迹失败 - 堆栈信息:{}", e.getMessage());
                }
            }
        } else {
            center.setLongitude(points.get(0).getLongitude());
            center.setLatitude(points.get(0).getLatitude());
        }
        data.setCenter(center);
        data.setDriving(driving);
        data.setLocalDeliveryOrder(JSONUtil.parseObj(localDeliveryOrder));
        JSONArray step = new JSONArray();
        step.add(Map.of(
                "desc", "已接单",
                "logo", "addon/mall/detail/step1.png",
                "logo_selected", "addon/mall/detail/step1-selected.png",
                "status", Arrays.asList(
                        LocalDeliveryStatusEnum.WAIT_RELEASE.getStatus(),
                        LocalDeliveryStatusEnum.ORDER_ACCEPTED.getStatus(),
                        LocalDeliveryStatusEnum.CANCELED.getStatus(),
                        LocalDeliveryStatusEnum.CREATE_ORDER_FAILED.getStatus()
                ).toArray()));
        step.add(Map.of(
                "desc", "备货中",
                "logo", "addon/mall/detail/step2.png",
                "logo_selected", "addon/mall/detail/step2-selected.png",
                "status", Arrays.asList(
                        LocalDeliveryStatusEnum.PENDING_ACCEPTANCE.getStatus(),
                        LocalDeliveryStatusEnum.PENDING_PICKUP.getStatus(),
                        LocalDeliveryStatusEnum.RIDER_ARRIVED.getStatus()
                ).toArray()));
        step.add(Map.of(
                "desc", "配送中",
                "logo", "addon/mall/detail/step3.png",
                "logo_selected", "addon/mall/detail/step3-selected.png",
                "status", List.of(
                        LocalDeliveryStatusEnum.IN_DELIVERY.getStatus()
                ).toArray()));
        step.add(Map.of(
                "desc", "已送达",
                "logo", "addon/mall/detail/step4.png",
                "logo_selected", "addon/mall/detail/step4-selected.png",
                "status", List.of(
                        LocalDeliveryStatusEnum.COMPLETED.getStatus()
                ).toArray()));
        data.setStep(step);
        data.setExpectedFinishTime(getExpectedFinishTime(data, localDeliveryOrder));
        return data;
    }

    public String getExpectedFinishTime(TrackOfLocalServiceVo data, DeliveryLocalDeliveryOrder localDeliveryOrder) {
        List<TrackPointsVo> points = data.getPoints();
        long currentTime = System.currentTimeMillis() / 1000; // 当前时间戳(秒)
        double riderSpeed = 25.0; // 骑手速度，单位：公里/小时
        long expectedFinishTime = currentTime;

        switch (Integer.parseInt(localDeliveryOrder.getStatus())) {
            case 0:
            case 1: // 待接单
                double distance = DistanceCalculateUtils.calculateDistance(
                        Double.parseDouble(localDeliveryOrder.getDeliveryStartLat()),
                        Double.parseDouble(localDeliveryOrder.getDeliveryStartLng()),
                        Double.parseDouble(localDeliveryOrder.getDeliveryEndLat()),
                        Double.parseDouble(localDeliveryOrder.getDeliveryEndLng())
                );
                expectedFinishTime += (long) ((distance / riderSpeed) * 3600 + 600);
                break;

            case 2: //待取货
                double distance1;
                if (points.size() > 1) {
                    distance1 = DistanceCalculateUtils.calculateDistance(
                            Double.parseDouble(points.get(0).getLatitude()),
                            Double.parseDouble(points.get(0).getLongitude()),
                            Double.parseDouble(points.get(1).getLatitude()),
                            Double.parseDouble(points.get(1).getLongitude())
                    );
                } else {
                    distance1 = 0;
                }
                double distance2 = DistanceCalculateUtils.calculateDistance(
                        Double.parseDouble(localDeliveryOrder.getDeliveryStartLat()),
                        Double.parseDouble(localDeliveryOrder.getDeliveryStartLng()),
                        Double.parseDouble(localDeliveryOrder.getDeliveryEndLat()),
                        Double.parseDouble(localDeliveryOrder.getDeliveryEndLng())
                );
                expectedFinishTime += (long) ((distance1 + distance2) / riderSpeed * 3600);
                break;

            case 3: //骑士到店
                double distance3 = DistanceCalculateUtils.calculateDistance(
                        Double.parseDouble(points.get(0).getLatitude()),
                        Double.parseDouble(points.get(0).getLongitude()),
                        Double.parseDouble(points.get(1).getLatitude()),
                        Double.parseDouble(points.get(1).getLongitude())
                );
                double distance4 = DistanceCalculateUtils.calculateDistance(
                        Double.parseDouble(localDeliveryOrder.getDeliveryStartLat()),
                        Double.parseDouble(localDeliveryOrder.getDeliveryStartLng()),
                        Double.parseDouble(localDeliveryOrder.getDeliveryEndLat()),
                        Double.parseDouble(localDeliveryOrder.getDeliveryEndLng())
                );
                expectedFinishTime += (long) ((distance3 + distance4) / riderSpeed * 3600);
                break;

            case 4: //配送中
                double distance5 = DistanceCalculateUtils.calculateDistance(
                        Double.parseDouble(points.get(0).getLatitude()),
                        Double.parseDouble(points.get(0).getLongitude()),
                        Double.parseDouble(points.get(1).getLatitude()),
                        Double.parseDouble(points.get(1).getLongitude())
                );
                expectedFinishTime += (long) ((distance5 / riderSpeed) * 3600);
                break;
        }

        // 转换为HH:mm格式
        LocalDateTime dateTime = LocalDateTime.ofEpochSecond(expectedFinishTime, 0, java.time.ZoneOffset.UTC);
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    /**
     * 商家配送发货后同城单应为配送中；历史数据若仍停在接单/待取货，按是否已有商家配送单纠正
     */
    private void syncMerchantInDeliveryStatus(DeliveryLocalDeliveryOrder order) {
        if (order == null || !DeliveryStoreTypeEnum.MERCHANT.getKey().equals(order.getDeliveryService())) {
            return;
        }
        int status;
        try {
            status = Integer.parseInt(String.valueOf(order.getStatus()));
        } catch (Exception e) {
            return;
        }
        if (status < 0
                || LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().equals(status)
                || LocalDeliveryStatusEnum.COMPLETED.getStatus().equals(status)) {
            return;
        }
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("siteId", order.getSiteId());
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(DeliveryStoreTypeEnum.MERCHANT.getKey())
                    .init(JSONUtil.parseObj(config));
            Map<String, Object> pos = driver.getTransporterPosition(Map.of("delivery_no", order.getDeliveryNo()));
            if (ObjectUtil.isEmpty(pos)) {
                return;
            }
            DeliveryLocalDeliveryOrder update = new DeliveryLocalDeliveryOrder();
            update.setId(order.getId());
            update.setStatus(LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().toString());
            if (ObjectUtil.isNotEmpty(pos.get("rider_name"))) {
                update.setRiderName(String.valueOf(pos.get("rider_name")));
                order.setRiderName(update.getRiderName());
            }
            if (ObjectUtil.isNotEmpty(pos.get("rider_mobile"))) {
                update.setRiderMobile(String.valueOf(pos.get("rider_mobile")));
                order.setRiderMobile(update.getRiderMobile());
            }
            deliveryLocalDeliveryOrderMapper.updateById(update);
            order.setStatus(update.getStatus());
        } catch (Exception e) {
            log.warn("同步商家配送状态失败 deliveryNo={}, err={}", order.getDeliveryNo(), e.getMessage());
        }
    }

    /**
     * 商家配送轨迹点（对齐 PHP 配送中地图：骑手 + 买家）
     */
    private List<TrackPointsVo> buildMerchantTrackPoints(DeliveryLocalDeliveryOrder order, Integer memberId) {
        List<TrackPointsVo> points = new ArrayList<>();
        int status;
        try {
            status = Integer.parseInt(String.valueOf(order.getStatus()));
        } catch (Exception e) {
            return points;
        }
        String headimg = "static/resource/images/default_headimg.png";
        if (memberId != null && memberId > 0) {
            Member member = memberMapper.selectById(memberId);
            if (member != null && ObjectUtil.isNotEmpty(member.getHeadimg())) {
                headimg = member.getHeadimg();
            }
        }
        Map<String, Object> riderPos = new HashMap<>();
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("siteId", order.getSiteId());
            BaseLocalDelivery driver = LocalDeliveryLoader.getDriver(DeliveryStoreTypeEnum.MERCHANT.getKey())
                    .init(JSONUtil.parseObj(config));
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("delivery_no", order.getDeliveryNo());
            riderPos = ObjectUtil.defaultIfNull(driver.getTransporterPosition(requestData), new HashMap<>());
        } catch (Exception e) {
            log.warn("商家配送获取骑手位置失败 deliveryNo={}, err={}", order.getDeliveryNo(), e.getMessage());
        }
        String riderLat = StrUtil.blankToDefault(
                ObjectUtil.isNotEmpty(riderPos.get("rider_latitude")) ? String.valueOf(riderPos.get("rider_latitude")) : "",
                ObjectUtil.defaultIfNull(order.getDeliveryStartLat(), ""));
        String riderLng = StrUtil.blankToDefault(
                ObjectUtil.isNotEmpty(riderPos.get("rider_longitude")) ? String.valueOf(riderPos.get("rider_longitude")) : "",
                ObjectUtil.defaultIfNull(order.getDeliveryStartLng(), ""));

        if (LocalDeliveryStatusEnum.ORDER_ACCEPTED.getStatus().equals(status)
                || LocalDeliveryStatusEnum.PENDING_ACCEPTANCE.getStatus().equals(status)) {
            points.add(TrackPointsVo.builder()
                    .pointsType("shop")
                    .latitude(order.getDeliveryStartLat())
                    .longitude(order.getDeliveryStartLng())
                    .iconPath("static/resource/images/local_delivery/in_stock.png")
                    .callout(TrackPointsVo.CalloutInfo.builder().content(status == 0 ? "已接单" : "备货中").build())
                    .build());
            points.add(TrackPointsVo.builder()
                    .pointsType("member")
                    .latitude(order.getDeliveryEndLat())
                    .longitude(order.getDeliveryEndLng())
                    .iconPath(headimg)
                    .callout(TrackPointsVo.CalloutInfo.builder().content("").build())
                    .build());
            return points;
        }
        if (LocalDeliveryStatusEnum.PENDING_PICKUP.getStatus().equals(status)
                || LocalDeliveryStatusEnum.RIDER_ARRIVED.getStatus().equals(status)
                || LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().equals(status)) {
            String tip = LocalDeliveryStatusEnum.IN_DELIVERY.getStatus().equals(status)
                    ? "配送中"
                    : (LocalDeliveryStatusEnum.RIDER_ARRIVED.getStatus().equals(status) ? "骑手到店取餐中" : "骑手正赶往商家");
            String pointsType = LocalDeliveryStatusEnum.RIDER_ARRIVED.getStatus().equals(status) ? "rider_arrived" : "rider";
            points.add(TrackPointsVo.builder()
                    .pointsType(pointsType)
                    .latitude(riderLat)
                    .longitude(riderLng)
                    .iconPath("static/resource/images/local_delivery/in_delivery.png")
                    .callout(TrackPointsVo.CalloutInfo.builder().content(tip).build())
                    .build());
            if (LocalDeliveryStatusEnum.PENDING_PICKUP.getStatus().equals(status)) {
                points.add(TrackPointsVo.builder()
                        .pointsType("shop")
                        .latitude(order.getDeliveryStartLat())
                        .longitude(order.getDeliveryStartLng())
                        .iconPath("static/resource/images/local_delivery/in_stock.png")
                        .callout(TrackPointsVo.CalloutInfo.builder().content("").build())
                        .build());
            } else {
                points.add(TrackPointsVo.builder()
                        .pointsType("member")
                        .latitude(order.getDeliveryEndLat())
                        .longitude(order.getDeliveryEndLng())
                        .iconPath(headimg)
                        .callout(TrackPointsVo.CalloutInfo.builder().content("").build())
                        .build());
            }
        }
        return points;
    }

    /**
     * 计算坐标集合的经纬度中心点
     *
     * @param points
     * @return
     */
    public static TrackCenterVo calculateCenter(List<TrackPointsVo> points) {
        TrackCenterVo center = new TrackCenterVo();
        double sumX = 0;
        double sumY = 0;
        int count = points.size();
        for (TrackPointsVo point : points) {
            sumX += Double.parseDouble(point.getLatitude());
            sumY += Double.parseDouble(point.getLongitude());
        }
        BigDecimal xCenter = new BigDecimal(sumX / count);
        BigDecimal yCenter = new BigDecimal(sumY / count);
        center.setLatitude(xCenter.toString());
        center.setLongitude(yCenter.toString());
        return center;
    }
}
