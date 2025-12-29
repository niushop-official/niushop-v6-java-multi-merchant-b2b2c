package com.niu.core.entity.delivery;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 业务配送表
 */
@Data
public class DeliveryTradePackage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 业务类型
     */
    private String tradeType;

    /**
     * 业务ID或编号
     */
    private String tradeNo;

    /**
     * 包裹名称
     */
    private String name;

    /**
     * 配送方式
     */
    private String deliveryType;

    /**
     * 详细配送方式
     */
    private String subDeliveryType;

    /**
     * 快递公司ID
     */
    private Integer expressCompanyId;

    /**
     * 配送单号
     */
    private String expressNumber;

    /**
     * 同城配送员ID
     */
    private Integer localDeliverId;

    /**
     * 包裹状态
     */
    private String status;

    /**
     * 创建时间（时间戳）
     */
    private Integer createTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 同城配送单号
     */
    private String localDeliveryOrderId;

    /**
     * 配送单号
     */
    private String code;

}