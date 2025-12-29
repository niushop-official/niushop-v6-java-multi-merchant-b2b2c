package com.niu.core.service.api.delivery.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.service.core.delivery_search.vo.DeliveryTraceVo;
import lombok.Data;

import java.io.Serializable;

/**
 * ShopDeliveryCompany列表视图
 */
@Data
public class DeliveryStoreListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    private Integer storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店logo
     */
    private String storeLogo;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 完整地址
     */
    private String fullAddress ;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 经度
     */
    private String longitude;


    /**
     * 营业时间(文本展示使用)
     */
    private String tradeTime;

    /**
     * 配送/自提时间（周）
     */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String timeWeek;

    /**
     * 配送/自提时间（时间段）
     */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String tradeTimeJson;

    /**
     * 时段设置（分钟）
     */
    private Integer timeInterval;


    private Double distance;

    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String storeTimeList;

}