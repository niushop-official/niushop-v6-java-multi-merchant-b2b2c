package com.niu.core.enums.diy;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

public class ThemeColorEnum {


    /**
     * 获取所有主题色
     * @return
     */
    public static JSONArray getThemeColor(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeContentToArray("diy/theme_color.json");
    }

    /**
     * 获取插件主题色
     * @param addonName
     * @return
     */
    public static JSONObject getAddonThemeColor(String addonName){
        JSONArray themeColor = getThemeColor();
        if (ObjectUtil.isEmpty(themeColor)) {
            return new JSONObject();
        }
        String expectType = addonName + "_theme";
        for (int i = 0; i < themeColor.size(); i++) {
            Object item = themeColor.get(i);
            // mergeContentToArray 遇到根为数组的 json 会整段嵌套，需展开后再匹配
            if (item instanceof JSONArray nested) {
                JSONObject matched = findThemeByType(nested, expectType);
                if (matched != null) {
                    return matched;
                }
                continue;
            }
            if (item instanceof JSONObject addonThemeColor
                    && expectType.equals(addonThemeColor.getStr("theme_type"))) {
                return addonThemeColor;
            }
        }
        return new JSONObject();
    }

    private static JSONObject findThemeByType(JSONArray array, String expectType) {
        if (array == null) {
            return null;
        }
        for (int i = 0; i < array.size(); i++) {
            Object item = array.get(i);
            if (item instanceof JSONArray nested) {
                JSONObject matched = findThemeByType(nested, expectType);
                if (matched != null) {
                    return matched;
                }
                continue;
            }
            if (item instanceof JSONObject theme
                    && expectType.equals(theme.getStr("theme_type"))) {
                return theme;
            }
        }
        return null;
    }
}
