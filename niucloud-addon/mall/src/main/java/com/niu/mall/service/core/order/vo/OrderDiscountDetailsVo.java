package com.niu.mall.service.core.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单优惠详情
 */
@Data
public class OrderDiscountDetailsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private DiscountSectionVo shopDiscount;

    private DiscountSectionVo platformDiscount;

    @Data
    public static class DiscountSectionVo implements Serializable {

        private static final long serialVersionUID = 1L;

        private DiscountTotalVo discountTotal;

        private List<DiscountItemVo> discountList;
    }

    @Data
    public static class DiscountTotalVo implements Serializable {

        private static final long serialVersionUID = 1L;

        private String discountTypeName;

        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal discountMoney;
    }

    @Data
    public static class DiscountItemVo implements Serializable {

        private static final long serialVersionUID = 1L;

        private String discountTypeName;

        @JsonSerialize(using = BeanJsonSerializer.BigDecimalFormatSerializer.class)
        private BigDecimal discountMoney;
    }
}
