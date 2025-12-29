package com.niu.core.service.admin.sys;

import cn.hutool.json.JSONArray;
import com.niu.core.service.admin.stat.vo.StatSystemVo;
import com.niu.core.service.admin.sys.vo.SysQrcodeVo;

import java.util.List;
import java.util.Map;

public interface ISystemService {
    /**
     * 获取版权信息
     * @return
     */
    StatSystemVo getSystemInfo();

    void clearCache();

    SysQrcodeVo getQrcode(String folder, String page, Map<String, String> allParams);
}
