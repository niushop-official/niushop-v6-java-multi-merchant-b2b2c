package com.niu.core.service.adminapp.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.adminapp.StatAppEnum;
import com.niu.core.service.adminapp.IStatService;
import com.niu.core.service.adminapp.param.PstarEditParam;
import com.niu.core.service.adminapp.vo.StatItemVo;
import com.niu.core.service.core.adminapp.ICoreAdminAppService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 统计服务实现类
 */
@Service
@Slf4j
public class StatServiceImpl implements IStatService {

    @Resource
    private ICoreAdminAppService coreAdminAppService;

    @Override
    public List<StatItemVo> getStatList() {
        Integer siteId = RequestUtils.siteId();
        JSONArray list = StatAppEnum.getStat(siteId);
        Integer uid = RequestUtils.uid();

        JSONArray keys = coreAdminAppService.getValue(uid, siteId, "stat");
        int statLimit = 6;
        List<JSONObject> statList;
        if (keys == null || keys.isEmpty()) {
            List<JSONObject> listItems = JSONUtil.toList(list, JSONObject.class);
            int limit = Math.min(statLimit, listItems.size());
            statList = listItems.subList(0, limit);
        } else {
            statList = new ArrayList<>();
            List<String> keysList = JSONUtil.toList(keys, String.class);
            for (int i = 0; i < list.size(); i++) {
                JSONObject item = list.getJSONObject(i);
                String itemKey = item.getStr("key");
                if (itemKey != null && keysList.contains(itemKey)) {
                    int sortIndex = keysList.indexOf(itemKey);
                    item.set("sort", sortIndex);
                    statList.add(item);
                }
            }
        }
        List<JSONObject> sortedList = statList.stream()
                .sorted(Comparator.comparingInt(item -> item.getInt("sort", 0)))
                .collect(Collectors.toList());

        return convertToVoList(sortedList);
    }

    @Override
    public List<StatItemVo> getAllStatList() {
        Integer siteId = RequestUtils.siteId();
        JSONArray list = StatAppEnum.getStat(siteId);
        List<JSONObject> sortedList = JSONUtil.toList(list, JSONObject.class)
                .stream()
                .sorted(Comparator.comparingInt(item -> item.getInt("sort", 0)))
                .collect(Collectors.toList());

        return convertToVoList(sortedList);
    }

    @Override
    public Boolean setStatList(PstarEditParam param) {
        Integer uid = RequestUtils.uid();
        Integer siteId = RequestUtils.siteId();
        JSONArray valueArray = new JSONArray();

        if (param != null && param.getValue() != null) {
            valueArray = new JSONArray(param.getValue());
        }

        return coreAdminAppService.setValue(uid, siteId, "stat", valueArray);
    }

    /**
     * 将JSONObject列表转换为StatItemVo列表
     */
    private List<StatItemVo> convertToVoList(List<JSONObject> jsonList) {
        List<StatItemVo> voList = new ArrayList<>();
        for (JSONObject jsonObject : jsonList) {
            StatItemVo vo = new StatItemVo();
            BeanUtil.copyProperties(jsonObject, vo);
            voList.add(vo);
        }
        return voList;
    }
}
