package com.niu.core.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 自提门店实体
 */
@Data
public class DeliveryStore implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 门店ID
     */
    @TableId(value="store_id", type= IdType.AUTO)
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
     * 支持的第三方配送
     */
    private String supportDelivery;

    /**
     * 第三方配送扩展数据
     */
    private String extendData;

    /**
     * 配送时间设置 0关闭 1开启
     */
    private Integer timeIsOpen;

    /**
     * 时间选取类型 0每天 1自定义
     */
    private Integer timeType;

    /**
     * 营业时间(文本展示使用)
     */
    private String tradeTime;

    /**
     * 配送/自提时间（周）
     */
    private String timeWeek;

    /**
     * 配送/自提时间（时间段）
     */
    private String tradeTimeJson;

    /**
     * 时段设置（分钟）
     */
    private Integer timeInterval;

    /**
     * 支持同城配送
     */
    private Integer supportLocalDelivery;

    /**
     * 支持门店自提
     */
    private Integer supportStore;

    /**
     * 开启状态 1开启 0关闭
     */
    private Integer status;

    /**
     * 配送区域
     */
    private String area;

    private Long createTime; // 添加时间
    private Long updateTime; // 更新时间

    /**
     * 是否系统门店
     */
    private Integer isSystem;
}