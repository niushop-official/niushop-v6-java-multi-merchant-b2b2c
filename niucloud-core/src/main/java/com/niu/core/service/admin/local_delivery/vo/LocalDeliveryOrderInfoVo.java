package com.niu.core.service.admin.local_delivery.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.delivery.OrderDeliveryTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryMainTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.enums.order.TradeTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
* 同城配送订单详情
*/
@Data
public class LocalDeliveryOrderInfoVo {

        /** 主键ID */
        private Integer id;

        /** 配送方标识 */
        private String deliveryService;
        /** 配送方标识名称 */
        private String deliveryServiceName;
        /** 配送单号 */
        private String deliveryNo;

        /** 三方配送单号 */
        private String outDeliveryNo;

        /** 交易类型，mall商城 supply供货商 */
        private String tradeType;
        /** 交易类型 */
        private String tradeTypeName;
        /** 交易单号 */
        private String tradeNo;

        /** 交易ID */
        private Integer tradeId;

        /** 配送费用 */
        private BigDecimal deliveryMoney;

        /** 配送起点（收货点） */
        private String deliveryStart;

        /** 配送起点经度 */
        private String deliveryStartLng;

        /** 配送起点纬度 */
        private String deliveryStartLat;

        /** 配送终点（收货点） */
        private String deliveryEnd;

        /** 配送终点经度 */
        private String deliveryEndLng;

        /** 配送终点纬度 */
        private String deliveryEndLat;

        /** 配送距离 */
        private BigDecimal deliveryDistance;

        /** 配送状态 */
        private String status;

        /** 配送状态 */
        private String statusName;

        /** 配送时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long createTime;

        /** 完成时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long finishTime;

        /** 取消时间 */
        @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
        private Long cancelTime;

        /** 骑手姓名 */
        private String riderName;

        /** 骑手电话 */
        private String riderMobile;

        /** 备注 */
        private String remark;

        /** 配送产品信息 */
        private String body;

        /** 订单日志 */
        @JsonProperty("orderLog")
        private List<OrderLog> orderLog;

        @Data
        public static class OrderLog {
                /** 主键ID */
                @TableId(value="id", type= IdType.AUTO)
                private Integer id;

                /** 订单ID */
                private Integer orderId;

                /** 操作人ID */
                private Integer mainId;

                /** 操作人类型 */
                private String mainType;

                /** 操作人类型 */
                private String mainTypeName;

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

                public String getMainTypeName() {
                        return LocalDeliveryMainTypeEnum.getNameByType(this.mainType);

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