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
public class DeliveryStoreInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    private Integer storeId;

    /**
     * 站点ID
     */
    private Integer siteId;
    /**
     * 门店编号
     */
    private String storeNo ;


    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店logo
     */
    private String storeLogo;

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

    /**
     * 支持的提货类型
     */
    private Integer supportLocalDelivery;

    /**
     * 支持的提货类型
     */
    private Integer supportStore;

    /**
     * 开启状态 1开启 0关闭
     */
    private Integer status;

    /**
     * 配送区域
     */
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String area;

    private Long createTime; // 添加时间

    private Integer isSystem;
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String pickUpTypeName;
    @JsonSerialize(using = BeanJsonSerializer.StringToJsonSerializer.class)
    private String pickUpType;

    /**
     * 配送时间设置 0关闭 1开启
     */
    private Integer timeIsOpen;

    /**
     * 时间选取类型 0每天 1自定义
     */
    private Integer timeType;

    public String getPickUpTypeName() {
        JSONArray pickUpType = new JSONArray();
        if (ObjectUtil.isNotEmpty(this.supportLocalDelivery)  && this.supportLocalDelivery == 1) {
            pickUpType.add(LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getName());
        }
        if (ObjectUtil.isNotEmpty(this.supportStore)  && this.supportStore == 1) {
            pickUpType.add(LocalDeliveryStoreTypeEnum.STORE.getName());
        }
        return pickUpType.toString();
    }

    public String getPickUpType() {
        JSONArray pickUpType = new JSONArray();
        if (ObjectUtil.isNotEmpty(this.supportLocalDelivery)  && this.supportLocalDelivery == 1) {
            pickUpType.add(LocalDeliveryStoreTypeEnum.LOCAL_DELIVERY.getType());
        }
        if (ObjectUtil.isNotEmpty(this.supportStore)  && this.supportStore == 1) {
            pickUpType.add(LocalDeliveryStoreTypeEnum.STORE.getType());
        }
        return pickUpType.toString();
    }

}
