package com.niu.core.enums.active;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.enums.account.SeparStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件状态枚举类
 */
@Getter
@AllArgsConstructor
public enum ActiveEnum {
    DISCOUNT("限时折扣", "discount"),
    PINTUAN("拼团", "pintuan")
    ;
    private final String name;
    private final String type;

    public static Map<String, String> getMap(){
        Map<String, String> map = new HashMap<>();
        for(ActiveEnum item : ActiveEnum.values()){
            map.put(item.getType(), item.getName());
        }
        return map;
    }

    public static Object getActiveShort(String  type){
        Map<String, String> discount = new HashMap<>();
        discount.put("name", "折");
        discount.put("active_name", "限时折扣");
        discount.put("bg_color", "#FFA322");

        Map<String, String> pintuan = new HashMap<>();
        pintuan.put("name", "拼");
        pintuan.put("active_name", "拼团");
        pintuan.put("bg_color", "#FF1C77");

        Map<String, Object> map = new HashMap<>();
        map.put(DISCOUNT.getType(), discount);
        map.put(PINTUAN.getType(), pintuan);
        return StringUtils.isNotEmpty( type) && map.containsKey(type) ? map.get(type) : map;
    }

    public static JSONObject getActiveBgEffect(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("active/active_bg_effect.json");
    }

    public static JSONObject getActiveBgMusic(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultElement("active/active_bg_music.json");
    }
}
