package com.niu.core.service.adminapp;


import cn.hutool.json.JSONArray;
import com.niu.core.service.adminapp.param.PstarEditParam;

public interface IAppsService {

    /**
     * 首页应用
     * @return JSONArray
     */
    JSONArray getAppsOfIndex();

    /**
     * 应用列表
     * @return JSONArray
     */
    JSONArray getAppsOfCheck();

    /**
     * 个人中心应用
     * @return JSONArray
     */
    JSONArray getAppOfUserCenter();

    Boolean setAppsOfIndex(PstarEditParam param);
}
