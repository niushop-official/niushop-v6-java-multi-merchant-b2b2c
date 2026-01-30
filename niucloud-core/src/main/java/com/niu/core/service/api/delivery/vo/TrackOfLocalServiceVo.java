package com.niu.core.service.api.delivery.vo;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.List;

/**
 * ShopDeliveryCompany列表视图
 */
@Data
public class TrackOfLocalServiceVo {

    private List<TrackPointsVo> points;
    private TrackCenterVo center;
    private JSONObject driving;
    private JSONObject localDeliveryOrder;
    private JSONArray step;
    private String expectedFinishTime;
}