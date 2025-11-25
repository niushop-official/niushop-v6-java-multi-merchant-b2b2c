package com.niu.core.service.admin.local_delivery.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.delivery.OrderDeliveryStatusEnum;
import com.niu.core.enums.delivery.OrderDeliveryTypeEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.enums.order.TradeTypeEnum;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* 同城配送订单列表
*/
@Data
public class LocalDeliveryOrderListVo {

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

        /** 备注 */
        private String remark;

        /** 配送产品信息 */
        private String body;

        public String getStatusName() {
                return LocalDeliveryStatusEnum.getNameByStatus(Integer.parseInt(this.status));
        }

        public String getTradeTypeName() {
                return TradeTypeEnum.getType(this.tradeType);
        }

}
