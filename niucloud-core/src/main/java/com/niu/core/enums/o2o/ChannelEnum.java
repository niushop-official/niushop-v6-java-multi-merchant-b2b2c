package com.niu.core.enums.o2o;

import com.niu.core.common.utils.language.LanguageUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 渠道枚举类
 */
@Getter
@AllArgsConstructor
public enum ChannelEnum {

    /** 微信小程序 */
    WEAPP("weapp", "dict_channel.channel_weapp"),
    /** 微信公众号 */
    WECHAT("wechat", "dict_channel.channel_wechat"),
    /** 手机端H5 */
    H5("h5", "dict_channel.channel_h5"),
    /** 电脑端PC */
    PC("pc", "dict_channel.channel_pc"),
    /** app端 */
    APP("app", "dict_channel.channel_app");

    /** 渠道编码 */
    private final String code;
    /** 语言包key */
    @Getter(AccessLevel.NONE)
    private final String langKey;

    public String getLangKey() {
        return LanguageUtils.get(langKey);
    }
    /**
     * getType 方法
     * @param type 渠道编码，为空返回全量字典；传编码返回对应语言key
     * @return 全量map / 单个语言key
     */
    public static Object getType(String type) {
        Map<String, String> data = new HashMap<>();
        for (ChannelEnum channel : values()) {
            data.put(channel.getCode(), channel.getLangKey());
        }
        if (type == null || type.isEmpty()) {
            return data;
        }
        return data.getOrDefault(type, "");
    }

    /**
     * 根据编码获取枚举实例
     */
    public static ChannelEnum getByCode(String code) {
        for (ChannelEnum channel : values()) {
            if (channel.getCode().equals(code)) {
                return channel;
            }
        }
        return null;
    }

    /**
     * 获取全渠道字典Map
     */
    public static Map<String, String> getType() {
        return (Map<String, String>) getType("");
    }
}
