package com.niu.core.enums.sys;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;

/**
 * 初始化数据枚举
 */
public class WebInitDataEnum {

    /**
     * 数据类型
     *
     * @return
     */
    public static JSONObject getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONObject jsonObject = jsonModuleLoader.mergeResultElement(RequestUtils.siteId(), "web_init_data.json");
        String str = jsonObject.toString();
        //替换站点id
        str = str.replace("$site_id$", RequestUtils.defaultSiteId().toString());
        //替换时间
        str = str.replace("$time$", (System.currentTimeMillis() / 1000) + "");
        return JSONUtil.parseObj(str);
    }
}
