package com.niu.mall.service.site.marketing.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import com.niu.core.enums.pay.PayTypeEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

/**
 * ShopActive列表视图
 */
@Data
public class MallDiscountOrderListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer orderId;  //
    private Integer siteId;  // 站点id
    private String orderNo;  // 订单编号
    private BigDecimal orderMoney;  // 订单金额
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long payTime;  // 订单支付时间
    private Integer memberId;
    private String nickname;
    private Member member;
    private Integer status;
    private String takerName;  // 收货人
    private String takerMobile;  // 收货人手机号
    private String takerFullAddress;
    private Map<String, Object> orderStatusData;
    private String type;
    private String typeName;
    private Pay pay;

    @Data
    public static class Member {
        private Integer memberId;
        private String nickname;
    }

    @Data
    public static class Pay {
        private String type;
        private String typeName;

        public String getTypeName() {
            return PayTypeEnum.getType().getByPath(this.type + ".name", String.class);
        }
    }

    public Map<String, Object> getOrderStatusData() {
        return OrderStatusEnum.getMapByStatus(this.status);
    }
}
