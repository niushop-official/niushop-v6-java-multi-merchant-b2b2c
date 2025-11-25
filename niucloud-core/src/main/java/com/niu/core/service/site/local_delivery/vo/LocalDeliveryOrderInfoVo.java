package com.niu.core.service.site.local_delivery.vo;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.delivery.OrderDeliveryTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryMainTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryOrderPreStatusEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.enums.order.TradeTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * LocaleDeliveryServiceListVo列表视图
 */
@Data
public class LocalDeliveryOrderInfoVo {


    private Integer id;

    /** 站点ID */
    private Integer siteId;

    /** 配送方标识 */
    private String deliveryService;

    /** 配送单号 */
    private String deliveryNo;

    /** 三方配送单号 */
    private String outDeliveryNo;

    /** 交易类型，mall商城 supply供货商 */
    private String tradeType;

    /** 交易单号 */
    private String tradeNo;

    /** 交易ID */
    private Integer tradeId;

    /** 配送费用 */
    private BigDecimal deliveryMoney;

    /** 配送起点（发货点） */
    private String deliveryStart;

    /** 配送起点省 */
    private Integer deliveryStartProvinceId;

    /** 配送起点市 */
    private Integer deliveryStartCityId;

    /** 配送起点区 */
    private Integer deliveryStartDistrictId;

    /** 配送起点经度 */
    private String deliveryStartLng;

    /** 配送起点纬度 */
    private String deliveryStartLat;

    /** 配送终点（收货点） */
    private String deliveryEnd;

    /** 配送终点省 */
    private Integer deliveryEndProvinceId;

    /** 配送终点市 */
    private Integer deliveryEndCityId;

    /** 配送终点区 */
    private Integer deliveryEndDistrictId;

    /** 配送终点经度 */
    private String deliveryEndLng;

    /** 配送终点纬度 */
    private String deliveryEndLat;

    /** 配送距离 */
    private BigDecimal deliveryDistance;

    /** 配送状态 */
    private String status;

    /** 三方配送状态 */
    private String outStatus;

    /** 配送时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 送达时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long finishTime;

    /** 取消时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long cancelTime;

    /** 骑手姓名 */
    private String riderName;

    /** 骑手电话 */
    private String riderMobile;

    /** 违约金 */
    private BigDecimal deductMoney;

    /** 备注 */
    private String remark;

    /** 配送产品信息 */
    private String body;

    /** 实际消费费用(订单取消或完成后实际定义) */
    private BigDecimal actualMoney;

    private String deliveryServiceName;

    private String statusName;

    private String tradeTypeName;

    @JsonProperty("orderLog")
    private List<orderLogInfo> orderLog;

    @Data
    public static class orderLogInfo{
        private Integer id;

        /** 订单ID */
        private Integer orderId;

        /** 操作人ID */
        private Integer mainId;

        /** 操作人类型 */
        private String mainType;

        /** 操作人名称 */
        private String mainName;

        /** 操作类型 */
        private String operate;

        /** 操作描述 */
        private String operateDesc;

        /** 操作后配送状态 */
        private String status;

        /** 配送时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;

        /** 备注 */
        private String remark;

        private String mainTypeName;

        public String getMainTypeName() {
            if (ObjectUtil.isNotEmpty(this.mainType)){
                return LocalDeliveryMainTypeEnum.getNameByType(this.mainType);
            }else{
                return "";
            }
        }
    }

    public String getStatusName() {
        return LocalDeliveryStatusEnum.getNameByStatus(Integer.parseInt(this.status));
    }

    public String getTradeTypeName() {
        return TradeTypeEnum.getType(this.tradeType);
    }

    public String getDeliveryServiceName() {
        return LocalDeliveryTypeEnum.getName(this.deliveryService);
    }
}
