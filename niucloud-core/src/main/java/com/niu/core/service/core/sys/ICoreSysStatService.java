package com.niu.core.service.core.sys;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import com.niu.core.service.core.sys.vo.CoreSysConfigVo;
import com.niu.core.service.core.sys.vo.SysDataVo;
import com.niu.core.service.core.sys.vo.SysStatListVo;
import com.niu.core.service.core.sys.vo.SysStatVo;

public interface ICoreSysStatService {

    void addStat(SysDataAddParam sysDataAddParam);

    SysDataVo getStatData(Integer siteId, String startDate, String endDate);

    SysStatVo getStat(Integer siteId, String startDate, String endDate);

    PageResult<SysStatListVo> getStatList(PageParam pageParam, Integer siteId, String startDate, String endDate, String type);
}
