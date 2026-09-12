package com.niu.core.enums.o2o;

import com.niu.core.common.utils.language.LanguageUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;

/**
 * O2O订单主状态枚举
 */
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    WAIT_PAY(1, "dict_o2o_order.status_wait_pay", 0),
    WAIT_DELIVERY(2, "dict_o2o_order.status_wait_shipping", 0),
    FINISH(5, "dict_o2o_order.status_finish", 0),
    CLOSE(-1, "dict_o2o_order.status_close", 0);

    /** 状态数值 */
    private final Integer code;
    /** 语言key / 默认名称 */
    @Getter(AccessLevel.NONE)
    private final String langKey;
    /** 是否退款 0否 */
    private final Integer isRefund;

    /**
     * 运行时取文案，避免启动早期语言包未就绪时把 key 冻进枚举
     */
    public String getLangKey() {
        return LanguageUtils.get(langKey);
    }

    public static List<Map<String,Object>> getStatus() {
        List<Map<String,Object>> list = new LinkedList<>();
        for (OrderStatusEnum status : values()) {
            Map<String, Object> map = Map.of("name", status.getLangKey(),
                    "status", status.code,
                    "isRefund", status.isRefund);
            list.add(map);
        }
        return list;
    }
}
