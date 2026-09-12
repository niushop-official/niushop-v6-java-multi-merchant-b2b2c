package com.niu.core.enums.fenxiao;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分销业务类型
 */
@Getter
@AllArgsConstructor
public enum FenxiaoTradeTypeEnum {

    /** 商城 */
    MALL("mall", "商城"),
    /** O2O本地服务 */
    O2O("o2o", "O2O本地服务"),
    /** 前置仓 */
    FRONT_WAREHOUSE("front_warehouse", "前置仓");

    /** 业务类型标识 */
    private final String type;
    /** 业务类型名称 */
    private final String name;

    /**
     * 按 type 获取枚举
     * @param type 业务类型标识
     * @return FenxiaoTradeTypeEnum
     */
    public static FenxiaoTradeTypeEnum of(String type) {
        if (type == null) {
            return null;
        }
        for (FenxiaoTradeTypeEnum item : values()) {
            if (item.type.equals(type)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 按 type 获取显示名称
     * @param type 业务类型标识
     * @return String
     */
    public static String getNameByType(String type) {
        FenxiaoTradeTypeEnum item = of(type);
        return item != null ? item.getName() : "";
    }

    /**
     * 校验 type 是否有效
     * @param type 业务类型标识
     * @return boolean
     */
    public static boolean isValid(String type) {
        return of(type) != null;
    }
}
