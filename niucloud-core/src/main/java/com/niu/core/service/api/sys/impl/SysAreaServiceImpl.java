package com.niu.core.service.api.sys.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.BaseException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.object.ObjectGroupUtils;
import com.niu.core.entity.sys.SysArea;
import com.niu.core.mapper.sys.SysAreaMapper;
import com.niu.core.service.api.sys.ISysAreaService;
import com.niu.core.service.api.sys.param.SysAreaAddressByLatlngParam;
import com.niu.core.service.api.sys.vo.SysAreaLevelVo;
import com.niu.core.service.api.sys.vo.SysAreaListVo;
import com.niu.core.service.core.map.ICoreMapService;
import com.niu.core.service.core.map.IMapService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SysAreaServiceImpl implements ISysAreaService {

    // 是否使用缓存
    static final boolean useCache = true;

    @Resource
    private Cached cached;

    @Autowired
    private SysAreaMapper sysAreaMapper;

    @Resource
    private ICoreMapService coreMapService;

    @Override
    public List<SysArea> getAreaListByIds(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return sysAreaMapper.selectList(new LambdaQueryWrapper<SysArea>()
                .in(SysArea::getId, ids)
                .select(SysArea::getId, SysArea::getName));
    }

    @Override
    public SysArea getAreaById(Integer id) {
        // 缓存分组名称
        String group = SysArea.class.getSimpleName();
        // 缓存Key值计算参数
        List<Object> paramList = Arrays.asList(id);
        // 自动缓存
        return cached.rememberObject(useCache, group, paramList, (uniqueKey) -> {
            SysArea sysArea = sysAreaMapper.selectById(id);
            log.info("result object ->" + sysArea);
            return sysArea;
        });
    }

    @Override
    public List<SysAreaListVo> getListByPid(Integer pid) {
        // 缓存分组名称
        String group = SysAreaListVo.class.getSimpleName();
        // 缓存Key值计算参数
        List<Object> paramList = Arrays.asList(pid);
        // 自动缓存
        List<SysAreaListVo> outterSysAreaList = cached.remember(useCache, group, paramList, uniqueKey -> {
            List<SysAreaListVo> sysAreaVoList = null;
            try {
                QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("id,name");
                queryWrapper.eq("pid", pid);
                List<SysArea> sysAreaList = sysAreaMapper.selectList(queryWrapper);
                sysAreaVoList = CollectUtils.convert(sysAreaList, SysAreaListVo.class);
            } catch (Exception e) {
                log.error("查询区域错误：", e.getMessage());
                throw new BaseException("查询区域错误");
            }
            return sysAreaVoList;
        });
        return outterSysAreaList;
    }

    @Override
    public List<SysAreaLevelVo> getAreaTree(Integer level) {
        if (level == 1) {
            // 参数级别：1，返回1级
            QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id, pid, name");
            queryWrapper.eq("level", level);
            List<SysArea> sysAreaList = sysAreaMapper.selectList(queryWrapper);
            return CollectUtils.convert(sysAreaList, SysAreaLevelVo.class);
        } else if (level == 2) {
            // 参数级别：2，返回1级包含2级
            QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id, pid, name, level");
            queryWrapper.in("level", 1, 2);
            List<SysArea> sysAreaList = sysAreaMapper.selectList(queryWrapper);
            List<SysAreaLevelVo> sysAreaLevelVoList = CollectUtils.convert(sysAreaList, SysAreaLevelVo.class);
            Map<Integer, List<SysAreaLevelVo>> sysAreaMap = ObjectGroupUtils.groupList(sysAreaLevelVoList, "pid");
            return assembleAreaTree(sysAreaLevelVoList, sysAreaMap, 1, 2);
        } else if (level == 3) {
            // 参数级别：3，返回1级包含2级，2级包含3级
            QueryWrapper<SysArea> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id, pid, name, level");
            queryWrapper.in("level", 1, 2, 3);
            List<SysArea> sysAreaList = sysAreaMapper.selectList(queryWrapper);
            List<SysAreaLevelVo> sysAreaLevelVoList = CollectUtils.convert(sysAreaList, SysAreaLevelVo.class);
            Map<Integer, List<SysAreaLevelVo>> sysAreaMap = ObjectGroupUtils.groupList(sysAreaLevelVoList, "pid");
            return assembleAreaTree(sysAreaLevelVoList, sysAreaMap, 1, 3);
        }
        return new ArrayList<>();
    }

    /**
     * 组装一级树
     *
     * @param sysAreaLevelList
     * @param sysAreaLevelVoMap
     * @param level
     * @param depth
     * @return
     */
    private List<SysAreaLevelVo> assembleAreaTree(List<SysAreaLevelVo> sysAreaLevelList, Map<Integer, List<SysAreaLevelVo>> sysAreaLevelVoMap, int level, int depth) {
        List<SysAreaLevelVo> sysAreaLevelVoTree = new ArrayList<>();
        for (SysAreaLevelVo sysAreaLevelVo : sysAreaLevelList) {
            if (sysAreaLevelVo.getLevel() == level) {
                SysAreaLevelVo newSysAreaLevelVo = new SysAreaLevelVo();
                BeanUtils.copyProperties(sysAreaLevelVo, newSysAreaLevelVo);
                sysAreaLevelVoTree.add(newSysAreaLevelVo);
                assembleAreaTree(newSysAreaLevelVo, sysAreaLevelVoMap, level, depth);
            }
        }
        return sysAreaLevelVoTree;
    }

    /**
     * 递归子树
     *
     * @param sysAreaLevelVo
     * @param sysAreaLevelVoMap
     * @param level
     * @param depth
     */
    private void assembleAreaTree(SysAreaLevelVo sysAreaLevelVo, Map<Integer, List<SysAreaLevelVo>> sysAreaLevelVoMap, int level, int depth) {
        List<SysAreaLevelVo> sysAreaLevelVoCacheList = sysAreaLevelVoMap.get(sysAreaLevelVo.getId());
        sysAreaLevelVo.setChild(sysAreaLevelVoCacheList);
        if (sysAreaLevelVoCacheList != null && sysAreaLevelVoCacheList.size() > 0) {
            if (level <= depth) {
                for (SysAreaLevelVo sysAreaLevelVoCache : sysAreaLevelVoCacheList) {
                    assembleAreaTree(sysAreaLevelVoCache, sysAreaLevelVoMap, level + 1, depth);
                }
            }
        }
    }

    @Override
    public JSONObject getAreaByAreaCode(Integer code) {
        SysArea sysArea = this.sysAreaMapper.selectById(code);
        JSONObject jsonObject = new JSONObject();
        if (sysArea != null) {
            if (sysArea.getLevel() == 1) {
                jsonObject.putOpt("province", convertJsonObject(sysArea));
            } else if (sysArea.getLevel() == 2) {
                jsonObject.putOpt("city", convertJsonObject(sysArea));
                SysArea provinceSysArea = this.sysAreaMapper.selectById(sysArea.getPid());
                if (provinceSysArea != null) {
                    jsonObject.putOpt("province", convertJsonObject(provinceSysArea));
                }
            } else if (sysArea.getLevel() == 3) {
                jsonObject.putOpt("district", convertJsonObject(sysArea));
                SysArea citySysArea = this.sysAreaMapper.selectById(sysArea.getPid());
                if (citySysArea != null) {
                    jsonObject.putOpt("city", convertJsonObject(citySysArea));
                    SysArea provinceSysArea = this.sysAreaMapper.selectById(citySysArea.getPid());
                    if (provinceSysArea != null) {
                        jsonObject.putOpt("province", convertJsonObject(provinceSysArea));
                    }
                }
            }
        }
        return jsonObject;
    }

    /**
     * @param sysArea
     * @return
     */
    private JSONObject convertJsonObject(SysArea sysArea) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("id", sysArea.getId());
        jsonObject.putOpt("level", sysArea.getLevel());
        jsonObject.putOpt("pid", sysArea.getPid());
        jsonObject.putOpt("name", sysArea.getName());
        return jsonObject;
    }

    @Override
    public JSONObject getAddressByLatlng(SysAreaAddressByLatlngParam param) {
        Integer siteId = RequestUtils.defaultSiteId();
        IMapService driver = coreMapService.getDriver(siteId);
        JSONObject locationResult = driver.locationToDetail(siteId, param.getLatlng());

        Integer status = locationResult.getInt("status");
        if (status == null || status != 0) {
            String message = locationResult.getStr("message", "地图服务请求失败");
            throw new CommonException("请检查地图配置：" + message);
        }

        JSONObject result = locationResult.getJSONObject("result");
        JSONObject addressComponent = result.getJSONObject("address_component");
        JSONObject addressReference = result.getJSONObject("address_reference");
        if (addressReference == null) {
            addressReference = new JSONObject();
        }

        String community = "";
        JSONObject landmarkL2 = addressReference.getJSONObject("landmark_l2");
        JSONObject landmarkL1 = addressReference.getJSONObject("landmark_l1");
        JSONObject town = addressReference.getJSONObject("town");
        if (landmarkL2 != null && StrUtil.isNotBlank(landmarkL2.getStr("title"))) {
            community = landmarkL2.getStr("title");
        } else if (landmarkL1 != null && StrUtil.isNotBlank(landmarkL1.getStr("title"))) {
            community = landmarkL1.getStr("title");
        } else if (town != null && StrUtil.isNotBlank(town.getStr("title"))) {
            community = town.getStr("title");
        }

        String provinceName = addressComponent.getStr("province", "");
        String cityName = addressComponent.getStr("city", "");
        String districtName = addressComponent.getStr("district", "");

        String provinceMatch = provinceName.replace("省", "").replace("市", "");
        String cityMatch = cityName;
        String districtMatch = districtName;

        int provinceId = 0;
        int cityId = 0;
        int districtId = 0;

        if (StrUtil.isNotBlank(provinceMatch)) {
            List<SysArea> provinces = sysAreaMapper.selectList(
                    new LambdaQueryWrapper<SysArea>()
                            .like(SysArea::getName, provinceMatch)
                            .eq(SysArea::getLevel, 1)
                            .last("limit 1")
            );
            if (!provinces.isEmpty()) {
                SysArea province = provinces.get(0);
                provinceId = province.getId();
                provinceName = province.getName();
            }
        }

        if (provinceId > 0 && StrUtil.isNotBlank(cityMatch)) {
            List<SysArea> cities = sysAreaMapper.selectList(
                    new LambdaQueryWrapper<SysArea>()
                            .like(SysArea::getName, cityMatch)
                            .eq(SysArea::getLevel, 2)
                            .eq(SysArea::getPid, provinceId)
                            .last("limit 1")
            );
            if (!cities.isEmpty()) {
                SysArea city = cities.get(0);
                cityId = city.getId();
                cityName = city.getName();
            }
        }

        if (cityId > 0 && provinceId > 0 && StrUtil.isNotBlank(districtMatch)) {
            List<SysArea> districts = sysAreaMapper.selectList(
                    new LambdaQueryWrapper<SysArea>()
                            .like(SysArea::getName, districtMatch)
                            .eq(SysArea::getLevel, 3)
                            .eq(SysArea::getPid, cityId)
                            .last("limit 1")
            );
            if (!districts.isEmpty()) {
                SysArea district = districts.get(0);
                districtId = district.getId();
                districtName = district.getName();
            }
        }

        JSONObject data = new JSONObject();
        data.putOpt("province_id", provinceId);
        data.putOpt("province", provinceName);
        data.putOpt("city_id", cityId);
        data.putOpt("city", cityName);
        data.putOpt("district_id", districtId);
        data.putOpt("district", districtName);
        data.putOpt("community", community);
        data.putOpt("full_address", result.getStr("address", ""));
        data.putOpt("formatted_addresses", result.getJSONObject("formatted_addresses"));
        return data;
    }

    /**
     * 通过客户端 IP 获取经纬度（对齐 PHP AreaService::getLatlngByIp）
     * 定位失败时的降级方案
     *
     * @return 经纬度对象，包含 lat、lng 字段
     */
    @Override
    public JSONObject getLatlngByIp() {
        Integer siteId = RequestUtils.defaultSiteId();
        String ip = RequestUtils.ip();

        IMapService driver = coreMapService.getDriver(siteId);
        JSONObject res = driver.ipToDetail(siteId, ip);

        if (res == null || res.isEmpty()) {
            throw new CommonException("获取IP定位失败");
        }

        Integer status = res.getInt("status");
        if (status == null || status != 0) {
            String message = res.getStr("message", "地图服务请求失败");
            throw new CommonException("请检查地图配置：" + message);
        }

        JSONObject result = res.getJSONObject("result");
        JSONObject location = result != null ? result.getJSONObject("location") : null;

        JSONObject data = new JSONObject();
        if (location != null) {
            data.set("lat", location.getStr("lat", ""));
            data.set("lng", location.getStr("lng", ""));
        } else {
            data.set("lat", "");
            data.set("lng", "");
        }
        return data;
    }

}
