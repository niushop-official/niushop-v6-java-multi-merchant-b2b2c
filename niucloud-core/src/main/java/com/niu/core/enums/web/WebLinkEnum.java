package com.niu.core.enums.web;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class WebLinkEnum {
    private static final Logger logger = LoggerFactory.getLogger(WebLinkEnum.class);
    public static JSONObject getLink() {
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
      return   jsonModuleLoader.mergeResultElement("diy/web_link.json");
    }
}
