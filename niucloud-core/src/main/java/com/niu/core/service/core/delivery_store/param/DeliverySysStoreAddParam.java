package com.niu.core.service.core.delivery_store.param;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 配送方式设置参数
*/
@Data
public class DeliverySysStoreAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer siteId;

    private String siteName;

    private String phone;

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
}
