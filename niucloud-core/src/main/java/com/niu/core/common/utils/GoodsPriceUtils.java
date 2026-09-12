package com.niu.core.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 商品价格工具
 * 会员价 / 付费会员价全局保底 0.01 元
 */
public final class GoodsPriceUtils {

    /** 会员价、付费会员价最低成交价 */
    public static final BigDecimal MIN_MEMBER_PRICE = new BigDecimal("0.01");

    private GoodsPriceUtils() {
    }

    /**
     * 低于 0.01 时按 0.01 返回；null 按 0.01 处理
     */
    public static BigDecimal ensureMinMemberPrice(BigDecimal price) {
        if (price == null || price.compareTo(MIN_MEMBER_PRICE) < 0) {
            return MIN_MEMBER_PRICE;
        }
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
