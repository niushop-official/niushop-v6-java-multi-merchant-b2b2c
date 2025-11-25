package com.niu.core.enums.pay;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PayChannelEnum {
    weapp("微信小程序", "weapp"),
    wechat("公众号", "wechat"),
    h5("H5", "h5"),
    pc("电脑端", "pc"),
    app("APP", "app");

    private final String name;
    private final String key;

    /**
     * 通过status获取当前名称
     * @param key
     * @return
     */
    public static String getNameByStatus(String key){
        for(PayChannelEnum item : PayChannelEnum.values())
        {
            if(item.getKey().equals(key))
            {
                return item.getName();
            }
        }
        return "";
    }
}
