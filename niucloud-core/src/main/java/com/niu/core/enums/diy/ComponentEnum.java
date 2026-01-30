package com.niu.core.enums.diy;

import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;

/**
 * DIY组件枚举
 * 注意：PHP代码中强制使用site_id=0来加载所有插件，所以这里也使用0
 * PHP代码位置：core/dict/BaseDict.php 第52行：$site_id = 0;
 */
public class ComponentEnum {

    /**
     * 获取DIY组件列表
     * 使用siteId=0加载所有已安装插件，与PHP保持一致
     */
    public static JSONObject getComponent(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        // PHP强制使用site_id=0，所以这里也使用0
        return jsonModuleLoader.mergeResultElement(0, "diy/components.json");
    }

    /**
     * 获取DIY组件列表（带名称参数）
     * 使用siteId=0加载所有已安装插件，与PHP保持一致
     */
    public static JSONObject getComponent(String name){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        // PHP强制使用site_id=0，所以这里也使用0
        return jsonModuleLoader.mergeResultElement(0, "diy/components.json");
    }
}
