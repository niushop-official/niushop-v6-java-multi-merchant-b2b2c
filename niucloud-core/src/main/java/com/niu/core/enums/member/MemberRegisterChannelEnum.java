package com.niu.core.enums.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum MemberRegisterChannelEnum {
    PC("PC", "pc"),
    H5("H5", "h5"),
    APP("APP", "app"),
    WECHAT("微信公众号", "wechat"),
    WEAPP("微信小程序", "weapp"),
    MANUAL("商家添加", "manual"),
    IMPORT("导入", "import"),
    ;

    private final String name;

    private final String type;

    public static Map<String, String> getMap() {
        Map<String, String> map = new LinkedHashMap<>();
        for (MemberRegisterChannelEnum item : MemberRegisterChannelEnum.values()) {
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    /**
     * 通过type获取当前名称
     *
     * @param type
     * @return
     */
    public static String getNameByType(String type) {
        for (MemberRegisterChannelEnum item : MemberRegisterChannelEnum.values()) {
            if (item.getType().equals(type)) {
                return item.getName();
            }
        }
        return "";
    }
}
