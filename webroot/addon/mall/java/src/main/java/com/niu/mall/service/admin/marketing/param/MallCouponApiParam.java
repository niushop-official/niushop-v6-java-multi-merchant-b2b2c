package com.niu.mall.service.admin.marketing.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
@Data
public class MallCouponApiParam  implements Serializable {


    private static final long serialVersionUID = 1L;

    private Integer siteId;  // 站点id

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long startTime; // 活动开启时间

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long endTime; // 活动结束时间

    private Integer status;  //  状态 1 正常 2 未开启 3 已无效

    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 添加时间

    private Integer receiveStatus;  //  状态 1 正常 2 关闭

    private String joinSiteIds; // 优惠券可使用站点id

    private Integer isAllSiteJoin; // 是否所有站点可用
}
