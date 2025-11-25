package com.niu.core.enums.diy;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.RequestUtils;
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
        if(ObjectUtil.isNotEmpty(themeColor)){

            for (int i = 0; i< themeColor.size(); i++) {
                JSONObject addonThemeColor = themeColor.getJSONObject(i);
                if(addonThemeColor.getStr("theme_type").equals(addonName + "_theme")){
                    return addonThemeColor;
                }
            }

        }
        return new JSONObject();
    }
}
