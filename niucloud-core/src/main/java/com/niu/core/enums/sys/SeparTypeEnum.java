package com.niu.core.enums.sys;

import cn.hutool.json.JSONArray;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.core.service.core.sys.ICoreSysConfigService;

import java.util.List;

public class SeparTypeEnum {

    /**
     * 获取分账类型
     *
     * @return
     */
    public static JSONArray getType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return jsonModuleLoader.mergeResultSet("sys/separ_type.json");
    }

    /**
     * 按照siteId获取分账类型
     *
     * @return
     */
    public static JSONArray getTypeBySiteId(Integer siteId){
        ICoreSiteService iCoreSiteService = SpringContext.bean(ICoreSiteService.class);
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        JSONArray jsonArray = jsonModuleLoader.mergeResultSet("sys/separ_type.json");
        if (siteId.equals(0)){
            return jsonArray;
        }else{
            List<String> addonKeys= iCoreSiteService.getAddonKeysBySiteId(siteId);
            if(addonKeys.contains("mall_huifu")){
                return jsonArray;
            }else{
                return new JSONArray();
            }
        }

    }
}
