package com.niu.core.service.site.delivery.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.local_delivery.LocalDeliveryStoreTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 配送服务商列表视图
 */
@Data
public class DeliveryStoreVo implements Serializable {

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
     * 联系人
     */
    private String contactName;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 省ID
     */
    private Integer provinceId;

    /**
     * 市ID
     */
    private Integer cityId;

    /**
     * 区ID
     */
    private Integer districtId;

    /**
     * 详细地址
     */
    private String address;

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
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime; // 添加时间

}
