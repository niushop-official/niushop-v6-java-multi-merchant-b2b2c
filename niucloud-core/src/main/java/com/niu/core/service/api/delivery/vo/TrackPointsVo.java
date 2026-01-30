package com.niu.core.service.api.delivery.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TrackPointsVo列表视图
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackPointsVo {

    private String pointsType;
    private String latitude;
    private String longitude;
    private String iconPath;
    private CalloutInfo callout;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CalloutInfo{
        private String content;
    }
}