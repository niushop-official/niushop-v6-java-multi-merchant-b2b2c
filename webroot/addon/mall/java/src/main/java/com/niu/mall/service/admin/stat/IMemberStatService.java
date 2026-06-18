package com.niu.mall.service.admin.stat;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;

import java.util.List;
import java.util.Map;

public interface IMemberStatService {

    Map<String, Object> getBasicData();

    Map<String, Object> getMemberData(Map<String, String> params);

    Map<String, Object> getTrendData(Map<String, String> params);

    List<Map<String, Object>> getSourceData();

    List<Map<String, Object>> getDurationData();

    PageResult<Map<String, Object>> getRankingData(PageParam pageParam, Map<String, String> params);
}
