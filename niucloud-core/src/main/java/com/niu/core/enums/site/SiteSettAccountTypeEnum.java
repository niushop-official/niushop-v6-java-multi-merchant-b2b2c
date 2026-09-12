package com.niu.core.enums.site;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 结算账户类型枚举
 */
@Getter
@AllArgsConstructor
public enum SiteSettAccountTypeEnum {

    BANK(1, "银行卡"),
    ALIPAY(2, "支付宝"),
    WECHAT(3, "微信"),
    WECHAT_CHANGE(4, "微信零钱");

    private final Integer type;
    private final String name;

    /**
     * 获取枚举map
     * @return
     */
    public static Map<Integer, SiteSettAccountTypeEnum> getMap() {
        Map<Integer, SiteSettAccountTypeEnum> map = new HashMap<>();
        for(SiteSettAccountTypeEnum item : SiteSettAccountTypeEnum.values()) {
            map.put(item.getType(), item);
        }
        return map;
    }

    /**
     * 根据类型获取名称
     * @param type
     * @return
     */
    public static String getNameByType(Integer type) {
        if (getMap().get(type) != null) {
            return getMap().get(type).getName();
        }
        return "";
    }
}