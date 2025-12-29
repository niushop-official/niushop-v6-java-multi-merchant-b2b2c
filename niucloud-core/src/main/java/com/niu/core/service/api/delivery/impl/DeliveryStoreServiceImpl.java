package com.niu.core.service.api.delivery.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.utils.DistanceCalculateUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.consts.local_delivery.LocalDeliveryConst;
import com.niu.core.entity.delivery.DeliveryStore;
import com.niu.core.entity.delivery.DeliveryTradePackage;
import com.niu.core.enums.local_delivery.LocalDeliveryStoreTypeEnum;
import com.niu.core.enums.order.OrderDeliveryEnum;
import com.niu.core.mapper.delivery.DeliveryStoreMapper;
import com.niu.core.mapper.delivery.DeliveryTradePackageMapper;
import com.niu.core.service.api.delivery.IDeliveryPackageService;
import com.niu.core.service.api.delivery.IDeliveryStoreService;
import com.niu.core.service.api.delivery.param.DeliveryStoreParam;
import com.niu.core.service.api.delivery.param.TradePackageSearchParam;
import com.niu.core.service.api.delivery.vo.DeliveryPackageInfoVo;
import com.niu.core.service.api.delivery.vo.DeliveryStoreListVo;
import com.niu.core.service.core.delivery_search.ICoreDeliverySearchService;
import com.niu.core.service.core.delivery_search.param.DeliverySearchParam;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 配送包裹服务层
 */
@Service
public class DeliveryStoreServiceImpl implements IDeliveryStoreService {

    @Resource
    private DeliveryStoreMapper deliveryStoreMapper;

    @Override
    public List<DeliveryStoreListVo> getLists(DeliveryStoreParam searchParam) {
        Integer siteId = ObjectUtil.isNotEmpty(searchParam.getSiteId()) ? searchParam.getSiteId() : RequestUtils.defaultSiteId();
        JSONObject positeion = ObjectUtil.isNotEmpty(searchParam.getPosition()) ? JSONUtil.parseObj(searchParam.getPosition()) : new JSONObject();
        List<DeliveryStoreListVo> resultList = new ArrayList<>();
        QueryWrapper<DeliveryStore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.eq("status",1);
        if(StringUtils.isNotEmpty(searchParam.getKeyword())){
            queryWrapper.and(item->item.like("store_name",searchParam.getKeyword()).or()
                    .like("mobile",searchParam.getKeyword()).or().like("full_address",searchParam.getKeyword()));
        }
        if(StringUtils.isNotEmpty(searchParam.getPickUpType())){
            if(searchParam.getPickUpType().equals(LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType())){
                queryWrapper.eq("support_local_delivery", 1);
            }
            if(searchParam.getPickUpType().equals(LocalDeliveryStoreTypeEnum.STORE.getType())){
                queryWrapper.eq("support_store", 1);
            }
        }
        List<DeliveryStore> deliveryStores = deliveryStoreMapper.selectList(queryWrapper);
        if(!deliveryStores.isEmpty() && !positeion.isEmpty() && positeion.containsKey("latitude") && positeion.containsKey("longitude")){
            for (DeliveryStore item : deliveryStores){
                DeliveryStoreListVo vo = new DeliveryStoreListVo();
                BeanUtils.copyProperties(item,vo);
                Double distance = DistanceCalculateUtils.calculateDistance(
                        Double.parseDouble(positeion.getStr("latitude")),
                        Double.parseDouble(positeion.getStr("longitude")),
                        Double.parseDouble(item.getLatitude()),
                        Double.parseDouble(item.getLongitude())
                );
                vo.setDistance( distance);
                resultList.add( vo);
            }
            resultList = resultList.stream().sorted(Comparator.comparing(DeliveryStoreListVo::getDistance)).toList();
        }

        int dayStartTime = (int) (DateUtil.beginOfDay(new Date()).getTime() / 1000);
        for (DeliveryStoreListVo item : resultList){
            List<Map<String, Object>> timeList = formatTime(item.getTimeWeek(), item.getTradeTimeJson(), dayStartTime, item.getTimeInterval());
            Map<Integer, Object> timeMap = new HashMap<>();
            for (Map<String, Object> item1 : timeList){
                timeMap.put(Integer.parseInt(item1.get("time").toString()), item1);
            }
            item.setStoreTimeList(JSONUtil.toJsonStr(timeMap));
        }
        return resultList;
    }

    public List<Map<String, Object>> formatTime(String weekList, String hourArr, int startTime, int interval) {
        if (StringUtils.isEmpty(weekList)) return new ArrayList<>();

        List<Map<String, Object>> result = new ArrayList<>();
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

        JSONArray weekArr = JSONUtil.parseArray(weekList);
        for (int i = 0; i < weekArr.size(); i++){
            Integer week = weekArr.getInt(i);
            String name = JSONUtil.parseObj(LocalDeliveryConst.getWeekList(week)).getStr("name");
            int time = 0;
            String dayStr = "";

            if (dayOfWeek == week) {
                dayStr = "今天";
                time = startTime;
            }
            if (dayOfWeek < week) {
                int day = week - dayOfWeek;
                time = addDays(startTime, day);
                if (dayOfWeek + 1 == week) dayStr = "明天";
                else dayStr = formatDate(time);
            }
            if(dayOfWeek>week){
                int day = dayOfWeek - week;
                time = addDays(addDays(startTime, -day), 7);
                dayStr = formatDate(time);
            }

            Map<String, Object> entry = new HashMap<>();
            entry.put("name", dayStr + "(" + name + ")");
            entry.put("time_list", getTimeArr(JSONUtil.parseArray(hourArr), time, interval));
            entry.put("time", time);
            result.add(entry);
        }

        result.sort(Comparator.comparingInt(e -> (int) e.get("time")));
        return result;
    }

    private int addDays(int time, int days) {
        return (int) (time * 1000L + days * 24 * 60 * 60 * 1000L) / 1000;
    }

    private String formatDate(int time) {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date(time * 1000L));
    }
    /**
     *
     * @param hourArr
     * @param startTime
     * @param interval
     * @return
     */
    public JSONArray getTimeArr(JSONArray hourArr, int startTime, int interval) {
        JSONArray result = new JSONArray();
        for (int j = 0; j < hourArr.size(); j++){
            JSONObject hour = hourArr.getJSONObject(j);
            int startSecond = hour.getInt("start_time");
            int endSecond = hour.getInt("end_time");
            int num = (endSecond - startSecond) / 60 / interval;
            for (int i = 1; i <= num; i++) {
                int startHour = startTime + startSecond + (i - 1) * interval * 60;
                int endHour = startTime + startSecond + i * interval * 60;
                if (endHour <= System.currentTimeMillis() / 1000) {
                    continue;
                }
                JSONObject timeSlot = new JSONObject();
                timeSlot.set("show_hour",  String.format("%02d:%02d-%02d:%02d",
                        startHour / 3600, (startHour % 3600) / 60,
                        endHour / 3600, (endHour % 3600) / 60));
                timeSlot.set("time_str",  String.format("%tF %02d:%02d-%02d:%02d",
                        new Date(startHour * 1000L),
                        startHour / 3600, (startHour % 3600) / 60,
                        endHour / 3600, (endHour % 3600) / 60));
                result.add(timeSlot);
            }
        }
        return result;
    }
}
