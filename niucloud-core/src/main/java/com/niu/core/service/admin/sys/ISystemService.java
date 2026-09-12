package com.niu.core.service.admin.sys;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.sys.vo.SysQrcodeVo;
import com.niu.core.service.admin.sys.vo.SysSystemInfoVo;

import java.util.List;
import java.util.Map;

public interface ISystemService {
    /**
     * 获取系统环境信息（对齐 PHP SystemService::getSystemInfo）
     */
    SysSystemInfoVo getSystemInfo();

    void clearCache();

    SysQrcodeVo getQrcode(String folder, String page, Map<String, String> allParams);

    /**
     * 校验消息队列是否正常运行
     */
    boolean checkJob();

    /**
     * 校验计划任务是否正常运行
     */
    boolean checkSchedule();

    /**
     * 清理表缓存
     */
    void schemaCache();

    /**
     * 获取环境变量
     */
    Map<String, Object> getEnvInfo();

    /**
     * 获取布局列表
     */
    JSONArray getLayoutList();

    /**
     * 设置布局
     */
    Boolean setLayout(String key);
}
