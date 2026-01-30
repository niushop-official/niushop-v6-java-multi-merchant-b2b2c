package com.niu.core.service.core.sys;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.core.sys.param.SysDataAddParam;
import com.niu.core.service.core.sys.vo.SysDataVo;
import com.niu.core.service.core.sys.vo.SysStatListVo;
import com.niu.core.service.core.sys.vo.SysStatVo;

import java.util.Map;

public interface ICoreSysStatService {

    void addStat(SysDataAddParam sysDataAddParam);

    SysDataVo getStatData(Integer siteId, String startDate, String endDate);

    SysStatVo getStat(Integer siteId, String startDate, String endDate);

    PageResult<SysStatListVo> getStatList(PageParam pageParam, Integer siteId, String startDate, String endDate, String type);

    /**
     * 获取账单详情
     * @param siteId 站点ID
     * @param date 日期（日账单格式：yyyy-MM-dd，月账单格式：yyyy-MM）
     * @param type 账单类型 day 日账单 month 月账单
     * @return 账单详情数据
     */
    SysStatListVo getStatDetail(Integer siteId, String date, String type);
}
