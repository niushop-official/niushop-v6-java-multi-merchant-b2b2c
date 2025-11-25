package com.niu.core.service.site.delivery.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 配送服务商列表视图
 */
@Data
public class DeliveryStoreListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配送类型key
     */
    private String key;

    /**
     * 配送类型名称
     */
    private String name;

    /**
     * 是否为商家配送
     */
    private Boolean isMerchant;

    /**
     * 开通状态
     */
    private Integer openStatus;

    /**
     * 业务类型
     */
    private String business;

    /**
     * 编辑状态
     */
    private String editStatus;

    /**
     * 原因
     */
    private String reason;

    /**
     * 业务类型列表
     */
    private String businessList;

    /**
     * 组件
     */
    private String component;

    /**
     * 扩展数据
     */
    private List<Object> extendData;

    /**
     * 参数
     */
    private List<Object> params;
}
