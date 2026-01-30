package com.niu.core.enums.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ShopTransferEnum {

    BANK("bank", "银行卡", false,1),
    ALIPAY("alipay", "支付宝", false,2),
    WECHAT_CODE("wechat_code", "微信收款码", false,3),
    WECHAT("wechatpay", "微信", true,4);

    private final String key;

    private final String name;

    //是否线上(线上1，线下0)
    private final Boolean isOnline;
    private  final  Integer  tag;

    /**
     * 获取枚举map
     * @return
     */
    public static Map<String, ShopTransferEnum> getMap() {
        Map<String, ShopTransferEnum> map = new HashMap<>();
        for(ShopTransferEnum item : ShopTransferEnum.values())
        {
            map.put(item.getKey(), item);
        }
        return map;
    }

    public static String getNameByKey(String key) {
        if (getMap().get(key) != null) {
            return getMap().get(key).getName();
        }
        return "";
    }

    public static Integer getTagByKey(String key) {
        if (getMap().get(key) != null) {
            return getMap().get(key).getTag();
        }
        return null;
    }
    // 按 tag 获取名称
    public static String getNameByTag(Integer tag) {
        ShopTransferEnum anEnum = getMapByTag().get(tag);
        return anEnum != null ? anEnum.getName() : "未知";
    }

    // 按 tag 获取名称
    public static String getKeyByTag(Integer tag) {
        ShopTransferEnum anEnum = getMapByTag().get(tag);
        return anEnum != null ? anEnum.getKey() : "";
    }
    // 按 tag 查询
    public static Map<Integer, ShopTransferEnum> getMapByTag() {
        Map<Integer, ShopTransferEnum> map = new HashMap<>();
        for (ShopTransferEnum item : values()) {
            map.put(item.getTag(), item);
        }
        return map;
    }

}
