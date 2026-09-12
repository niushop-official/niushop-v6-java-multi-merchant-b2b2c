package com.niu.core.service.site.marketing.param;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 优惠券参数
 */
@Data
public class MallCouponSendParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<CouponData> couponData;
    private List<Integer> couponIds;
    private List<Integer> memberIds;

    @Data
    public static class CouponData {

        private Integer price;
        private String title;
        private String typeName;
        private Integer couponId;
        private Integer minConditionMoney;
        private Integer validType;
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date validEndTime;
        private Integer length;
        private Integer num;
    }
}
