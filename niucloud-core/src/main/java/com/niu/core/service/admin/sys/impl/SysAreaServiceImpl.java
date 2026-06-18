package com.niu.core.service.admin.sys.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.mapper.sys.SysAreaMapper;
import com.niu.core.service.admin.sys.ISysAreaService;
import com.niu.core.service.admin.sys.param.SysAreaSearchParam;
import com.niu.core.service.admin.sys.vo.SysAreaByCodeVo;
import com.niu.core.service.admin.sys.vo.SysAreaListVo;
import com.niu.core.service.admin.sys.vo.SysAreaResolveVo;
import com.niu.core.service.admin.sys.vo.SysMapVo;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * 地址实现
 */
@Service
public class SysAreaServiceImpl implements ISysAreaService {

    @Resource
    SysAreaMapper sysAreaMapper;

    @Resource
    ICoreSysConfigService coreSysConfigService;

    @Override
    public List<SysArea> getListByPid(Integer pid) {

        QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pid", pid);
        return sysAreaMapper.selectList(queryWrapper);
    }

    @Override
    public JSONArray getAreaTree(Integer level) {
        QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("level", level);
        List<SysArea> list = sysAreaMapper.selectList(queryWrapper);
        JSONArray jsonArray = JSONUtil.parseArray(JSONUtil.toJsonStr(list));
        return TreeUtils.listToTree(jsonArray, "id", "pid", "child");
    }

    @Override
    public Map<String, SysAreaByCodeVo> getAreaByAreaCode(Integer id) {
        Map<String, SysAreaByCodeVo> area = new HashMap<>();
        String[] keys = { "", "province", "city", "district" };
        SysArea areaInfo = null;
        SysAreaByCodeVo vo = null;

        areaInfo = sysAreaMapper.selectOne(new QueryWrapper<SysArea>().eq("id", id).select("id,level,pid,name"));
        if (areaInfo != null) {
            vo = new SysAreaByCodeVo();
            BeanUtils.copyProperties(areaInfo, vo);
            area.put(keys[areaInfo.getLevel()], vo);

            while (areaInfo.getLevel() > 1) {
                areaInfo = sysAreaMapper.selectOne(new QueryWrapper<SysArea>().eq("id", areaInfo.getPid()).select("id,level,pid,name"));
                if (areaInfo != null) {
                    vo = new SysAreaByCodeVo();
                    BeanUtils.copyProperties(areaInfo, vo);
                    area.put(keys[areaInfo.getLevel()], vo);
                }
            }
        }

        return area;
    }

    @Override
    public Integer getAreaId(String name, Integer level) {
        SysArea areaInfo = sysAreaMapper.selectOne(new QueryWrapper<SysArea>().like("name", name).eq("level", level).last("limit 1"));
        if (areaInfo != null) {
            return areaInfo.getId();
        }
        return null;
    }

    @Override
    public String getAreaName(Integer id) {
        SysArea areaInfo = sysAreaMapper.selectOne(new QueryWrapper<SysArea>().eq("id", id).last("limit 1"));
        if (areaInfo != null) {
            return areaInfo.getName();
        }
        return null;
    }

    @Override
    public SysAreaResolveVo resolveAreaByName(String province, String city, String district) {
        if (StringUtils.isBlank(province)) {
            return null;
        }
        Integer provinceId = matchAreaName(province.trim(), 1, null);
        if (provinceId == null) {
            return null;
        }

        Integer cityId;
        if (StringUtils.isBlank(city) || province.trim().equals(city.trim())) {
            cityId = resolveMunicipalityCityId(provinceId);
        } else {
            cityId = matchAreaName(city.trim(), 2, provinceId);
        }
        if (cityId == null) {
            return null;
        }

        Integer districtId = null;
        if (StringUtils.isNotBlank(district)) {
            districtId = matchAreaName(district.trim(), 3, cityId);
        }
        if (districtId == null) {
            return null;
        }

        SysAreaResolveVo vo = new SysAreaResolveVo();
        vo.setProvinceId(provinceId);
        vo.setCityId(cityId);
        vo.setDistrictId(districtId);
        vo.setArea(buildAreaText(provinceId, cityId, districtId));
        return vo;
    }

    @Override
    public SysAreaResolveVo resolveAreaByText(String areaText) {
        if (StringUtils.isBlank(areaText)) {
            return null;
        }
        String text = areaText.trim();
        List<SysArea> provinces = getListByPid(0);
        SysArea province = matchPrefixArea(text, provinces);
        if (province == null) {
            return null;
        }

        String remainder = text.substring(province.getName().length());
        List<SysArea> cities = getListByPid(province.getId());
        SysArea city = matchPrefixArea(remainder, cities);
        if (city == null) {
            city = resolveMunicipalityCity(province, cities);
        }
        if (city == null) {
            return null;
        }

        String districtRemainder = remainder;
        if (remainder.startsWith(city.getName())) {
            districtRemainder = remainder.substring(city.getName().length());
        }

        List<SysArea> districts = getListByPid(city.getId());
        SysArea district = matchPrefixArea(districtRemainder, districts);
        if (district == null && StringUtils.isNotBlank(districtRemainder)) {
            Integer districtId = matchAreaName(districtRemainder.trim(), 3, city.getId());
            if (districtId != null) {
                district = sysAreaMapper.selectById(districtId);
            }
        }
        if (district == null) {
            return null;
        }

        SysAreaResolveVo vo = new SysAreaResolveVo();
        vo.setProvinceId(province.getId());
        vo.setCityId(city.getId());
        vo.setDistrictId(district.getId());
        vo.setArea(buildAreaText(province.getId(), city.getId(), district.getId()));
        return vo;
    }

    @Override
    public String buildAreaText(Integer provinceId, Integer cityId, Integer districtId) {
        StringBuilder sb = new StringBuilder();
        String provinceName = getAreaName(provinceId);
        if (StringUtils.isNotBlank(provinceName)) {
            sb.append(provinceName);
        }
        String cityName = getAreaName(cityId);
        if (StringUtils.isNotBlank(cityName) && !cityName.equals(provinceName)) {
            sb.append(cityName);
        }
        String districtName = getAreaName(districtId);
        if (StringUtils.isNotBlank(districtName)) {
            sb.append(districtName);
        }
        return sb.toString();
    }

    private Integer resolveMunicipalityCityId(Integer provinceId) {
        SysArea city = resolveMunicipalityCity(sysAreaMapper.selectById(provinceId), getListByPid(provinceId));
        return city != null ? city.getId() : null;
    }

    private SysArea resolveMunicipalityCity(SysArea province, List<SysArea> cities) {
        if (province == null || cities == null || cities.isEmpty()) {
            return null;
        }
        for (SysArea city : cities) {
            if (province.getName().equals(city.getName())) {
                return city;
            }
        }
        return cities.get(0);
    }

    private SysArea matchPrefixArea(String text, List<SysArea> areas) {
        if (StringUtils.isBlank(text) || areas == null || areas.isEmpty()) {
            return null;
        }
        areas.sort((a, b) -> b.getName().length() - a.getName().length());
        for (SysArea area : areas) {
            if (text.startsWith(area.getName())) {
                return area;
            }
        }
        return null;
    }

    private Integer matchAreaName(String name, int level, Integer pid) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id");
        queryWrapper.eq("level", level);
        queryWrapper.eq("name", name);
        if (pid != null) {
            queryWrapper.eq("pid", pid);
        }
        queryWrapper.last("limit 1");
        SysArea area = sysAreaMapper.selectOne(queryWrapper);
        if (area != null) {
            return area.getId();
        }

        queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id");
        queryWrapper.eq("level", level);
        queryWrapper.like("name", name);
        if (pid != null) {
            queryWrapper.eq("pid", pid);
        }
        queryWrapper.last("limit 1");
        area = sysAreaMapper.selectOne(queryWrapper);
        return area != null ? area.getId() : null;
    }

    /**
     * 地址列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysAreaListVo>
     */
    @Override
    public PageResult<SysAreaListVo> list(PageParam pageParam, SysAreaSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(Arrays.asList("sort", "id"));

        IPage<SysArea> iPage = sysAreaMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysAreaListVo> list = new LinkedList<>();
        for (SysArea item : iPage.getRecords()) {
            SysAreaListVo vo = new SysAreaListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 逆地址解析
     * @param location
     * @return
     */
    public JSONObject getAddressInfo(String location) {
        SysMapVo map = coreSysConfigService.getMap(RequestUtils.siteId());
        String result = HttpUtil.get("https://apis.map.qq.com/ws/geocoder/v1/?location="+ location +"&key=" + map.getKey());
        if (!JSONUtil.isJson(result)) return null;
        return JSONUtil.parseObj(result);
    }

    /**
     * 地址解析
     * @param address
     * @return
     */
    @Override
    public JSONObject getAddress(String address) {
        SysMapVo map = coreSysConfigService.getMap(RequestUtils.siteId());
        String result = HttpUtil.get("https://apis.map.qq.com/ws/geocoder/v1/?address="+ address +"&key=" + map.getKey());
        if (!JSONUtil.isJson(result)) return null;
        return JSONUtil.parseObj(result);
    }
}
