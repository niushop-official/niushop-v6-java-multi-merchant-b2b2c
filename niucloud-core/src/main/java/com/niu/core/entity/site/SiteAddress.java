package com.niu.core.entity.site;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 商家地址库实体
 */
@Data
public class SiteAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id; // 
    private Integer siteId; // 站点id
    private String contactName; // 联系人
    private String mobile; // 手机号
    private Integer provinceId; // 省
    private Integer cityId; // 市
    private Integer districtId; // 区
    private String address; // 详细地址
    private String fullAddress; // 地址
    private String lat; // 纬度
    private String lng; // 经度
    private Integer isDeliveryAddress; // 是否是发货地址
    private Integer isRefundAddress; // 是否是退货地址
    private Integer isDefaultDelivery; // 默认发货地址
    private Integer isDefaultRefund; // 默认收货地址

    /** 门店编号 */
    private String shopNo;

    /** 门店名称 */
    private String shopName;

    /** 支持的三方配送 */
    private String supportDelivery;

    /** 第三方配送扩展数据 */
    private String extendData;

}