package com.niu.core.service.core.diy;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.entity.diy.BottomConfigValue;
import com.niu.core.service.core.diy.param.StartUpPageConfigParam;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;

import java.util.List;

public interface ICoreDiyService {

    /**
     * 获取主题色字典
     * @return
     */
    JSONArray getThemeColorDict();

    /**
     * 获取默认主题
     * @param addon
     * @return
     */
    JSONObject getDefaultThemeColor(String addon);

    /**
     * 初始化主题
     */
    void initDefaultDiyTheme();
}
