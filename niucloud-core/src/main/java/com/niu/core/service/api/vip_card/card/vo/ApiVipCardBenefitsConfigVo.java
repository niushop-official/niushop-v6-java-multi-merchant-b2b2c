package com.niu.core.service.api.vip_card.card.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 权益配置（对齐 PHP benefits_config，含 gifts / gifts_content）
 */
@Data
public class ApiVipCardBenefitsConfigVo {

    /** 阶段返利：购卡后天数 */
    private Integer time;

    /** 周期返利：day / week / month */
    private String timeType;

    private Integer timeDay;
    private String timeWeek;
    private Integer timeMonth;

    private ApiVipCardGiftsVo gifts;

    private ApiVipCardGiftsVo giftsContent;

    @JsonIgnore
    public boolean isEmpty() {
        return time == null
                && timeType == null
                && timeDay == null
                && timeWeek == null
                && timeMonth == null
                && gifts == null
                && giftsContent == null;
    }

    @Data
    public static class ApiVipCardGiftsVo {
        private GiftBalanceVo balance;
        private GiftPointVo point;
        private GiftShopCouponVo shopCoupon;
    }

    @Data
    public static class GiftBalanceVo {
        private Integer isUse;
        private String money;
        private Object content;
    }

    @Data
    public static class GiftPointVo {
        private Integer isUse;
        private String num;
        private Object content;
    }

    @Data
    public static class GiftShopCouponVo {
        private Integer isUse;
        private List<Integer> couponId = new ArrayList<>();
        private Object couponList;
        private List<CouponContentItemVo> content = new ArrayList<>();
    }

    @Data
    public static class CouponContentItemVo {
        private Integer id;
        private String title;
        private BigDecimal price;
        private String minConditionMoney;
        private BigDecimal couponMinPrice;
        private String type;
        private Integer validType;
        private Integer length;
        private Long validStartTime;
        private Long validEndTime;
        private String typeName;
    }
}
