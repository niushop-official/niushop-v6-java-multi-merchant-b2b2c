package com.niu.mall.service.api.goods.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MallGoodsCouponVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String couponMinPrice;

    private String couponPrice;

    private Integer endTime;

    private Integer id;

    private Integer isAllSiteJoin;

    private Integer isReceive;

    private Integer joinSiteIds;

    private Integer length;

    private Integer limitCount;

    private Integer memberReceiveCount;

    private String minConditionMoney;

    private Integer needReceive;

    private String price;

    private Integer receiveCount;

    private Integer receiveType;

    private String receiveTypeName;

    private Integer remainCount;

    private Integer siteId;

    private String siteName;

    private Integer sort;

    private Integer startTime;

    private Integer status;

    private String sumCount;

    private String title;

    private Integer type;

    private String typeName;

    private Integer validEndTime;

    private Integer validStartTime;

    private Integer validType;

}
