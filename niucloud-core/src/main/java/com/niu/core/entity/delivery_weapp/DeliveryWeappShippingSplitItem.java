package com.niu.core.entity.delivery_weapp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 小程序发货物流信息表
 */
@Data
public class DeliveryWeappShippingSplitItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value="id", type= IdType.AUTO)
    private Integer id;
    /**
     * 站点id
     */
    private Integer siteId;

    /**
     * 拆单记录id（关联ns_delivery_weapp_shipping_split表id）
     */
    private Integer splitId;

    /**
     * 订单项id
     */
    private Integer itemTradeId;

    /**
     * 业务类型
     */
    private String tradeType;

    /**
     * 物流单号
     */
    private String trackingNo;

    /**
     * 物流公司编码
     */
    private String expressCompany;

    /**
     * 商品图片
     */
    private String itemImage;

    /**
     * 商品信息
     */
    private String itemDesc;

    /**
     * 寄件人联系方式（示例值: 189****1234）
     */
    private String consignorContact;

    /**
     * 收件人联系方式（示例值: 189****1234）
     */
    private String receiverContact;

}