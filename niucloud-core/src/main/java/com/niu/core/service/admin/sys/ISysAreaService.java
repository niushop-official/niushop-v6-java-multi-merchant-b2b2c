package com.niu.core.service.admin.sys;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.service.admin.sys.param.SysAreaSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.sys.vo.SysAreaByCodeVo;
import com.niu.core.service.admin.sys.vo.SysAreaListVo;
import com.niu.core.service.admin.sys.vo.SysAreaResolveVo;
import com.niu.core.service.api.sys.param.SysAreaAddressByLatlngParam;

import java.util.List;
import java.util.Map;

/**
 * 地址服务接口
 */
public interface ISysAreaService {

    List<SysArea> getListByPid(Integer pid);

    JSONArray getAreaTree(Integer level);

    Map<String, SysAreaByCodeVo> getAreaByAreaCode(Integer id);

    Integer getAreaId(String name, Integer level);

    String getAreaName(Integer id);

    /**
     * 根据省市区名称解析区划 ID
     */
    SysAreaResolveVo resolveAreaByName(String province, String city, String district);

    /**
     * 根据拼接的省市区文本解析区划 ID
     */
    SysAreaResolveVo resolveAreaByText(String areaText);

    /**
     * 根据区划 ID 拼接省市区展示文本
     */
    String buildAreaText(Integer provinceId, Integer cityId, Integer districtId);

    /**
     * 地址列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysAreaListVo>
     */
    PageResult<SysAreaListVo> list(PageParam pageParam, SysAreaSearchParam searchParam);

    /**
     * 逆地址解析
     * @param location
     */
    JSONObject getAddressInfo(String location);

    /**
     * 地址解析
     * @param address
     * @return
     */
    JSONObject getAddress(String address);
}
