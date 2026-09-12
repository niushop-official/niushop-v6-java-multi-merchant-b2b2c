package com.niu.core.service.core.sys;


import com.niu.core.entity.sys.SysArea;

import java.util.List;
import java.util.Map;

public interface ICoreAreaService {
    SysArea getInfo(Integer id);

    List<SysArea> getListByPid(Integer pid);

    List<Map<String, Object>> getTree(int level);

    String getFullAddress(Integer provinceId, Integer cityId, Integer districtId, String address, String tag);

    String getProvinceName(Integer provinceId);

    String getCityName(Integer cityId);

    String getDistrictName(Integer districtId);
}
