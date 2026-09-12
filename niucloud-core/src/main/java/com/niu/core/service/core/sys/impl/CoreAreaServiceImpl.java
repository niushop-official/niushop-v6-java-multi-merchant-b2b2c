package com.niu.core.service.core.sys.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.mapper.sys.SysAreaMapper;
import com.niu.core.service.core.sys.ICoreAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CoreAreaServiceImpl implements ICoreAreaService {

    @Autowired
    private SysAreaMapper areaMapper;

    @Override
    public SysArea getInfo(Integer id) {
        return areaMapper.selectById(id);
    }

    @Override
    public List<SysArea> getListByPid(Integer pid) {
        LambdaQueryWrapper<SysArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysArea::getPid, pid);
        wrapper.select(SysArea::getId, SysArea::getPid, SysArea::getName, SysArea::getShortname,
                SysArea::getLongitude, SysArea::getLatitude, SysArea::getLevel, SysArea::getSort, SysArea::getStatus);
        return areaMapper.selectList(wrapper);
    }

    @Override
    public List<Map<String, Object>> getTree(int level) {
        LambdaQueryWrapper<SysArea> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(SysArea::getLevel, level);
        wrapper.select(SysArea::getId, SysArea::getPid, SysArea::getName, SysArea::getShortname,
                SysArea::getLongitude, SysArea::getLatitude, SysArea::getLevel, SysArea::getSort, SysArea::getStatus);
        List<SysArea> list = areaMapper.selectList(wrapper);
        // 转换为树形结构（list_to_tree）
        return buildTree(list);
    }

    private List<Map<String, Object>> buildTree(List<SysArea> list) {
        // 使用 pid 映射
        Map<Integer, List<SysArea>> pidMap = list.stream()
                .filter(item -> item.getPid() != null)
                .collect(Collectors.groupingBy(SysArea::getPid));
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysArea area : list) {
            if (area.getPid() == 0) {
                Map<String, Object> node = BeanUtil.beanToMap(area);
                node.put("children", buildChildren(pidMap, area.getId()));
                result.add(node);
            }
        }
        return result;
    }

    private List<Map<String, Object>> buildChildren(Map<Integer, List<SysArea>> pidMap, Integer pid) {
        List<SysArea> children = pidMap.getOrDefault(pid, Collections.emptyList());
        return children.stream().map(child -> {
            Map<String, Object> node = BeanUtil.beanToMap(child);
            node.put("children", buildChildren(pidMap, child.getId()));
            return node;
        }).collect(Collectors.toList());
    }

    @Override
    public String getFullAddress(Integer provinceId, Integer cityId, Integer districtId, String address, String tag) {
        String provinceName = getProvinceName(provinceId);
        String cityName = getCityName(cityId);
        String districtName = getDistrictName(districtId);
        return provinceName + tag + cityName + tag + districtName + tag + address;
    }

    @Override
    public String getProvinceName(Integer provinceId) {
        if (provinceId == null || provinceId == 0) return "";
        SysArea area = areaMapper.selectOne(new LambdaQueryWrapper<SysArea>()
                .eq(SysArea::getId, provinceId)
                .select(SysArea::getName));
        return area != null ? area.getName() : "";
    }

    @Override
    public String getCityName(Integer cityId) {
        if (cityId == null || cityId == 0) return "";
        SysArea area = areaMapper.selectOne(new LambdaQueryWrapper<SysArea>()
                .eq(SysArea::getId, cityId)
                .select(SysArea::getName));
        return area != null ? area.getName() : "";
    }

    @Override
    public String getDistrictName(Integer districtId) {
        if (districtId == null || districtId == 0) return "";
        SysArea area = areaMapper.selectOne(new LambdaQueryWrapper<SysArea>()
                .eq(SysArea::getId, districtId)
                .select(SysArea::getName));
        return area != null ? area.getName() : "";
    }
}
