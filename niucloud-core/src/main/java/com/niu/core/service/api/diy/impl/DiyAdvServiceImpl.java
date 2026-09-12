package com.niu.core.service.api.diy.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.ApiException;
import com.niu.core.entity.diy.DiyAdv;
import com.niu.core.enums.diy.AdvPositionEnum;
import com.niu.core.mapper.diy.DiyAdvMapper;
import com.niu.core.service.api.diy.IDiyAdvService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DiyAdvServiceImpl implements IDiyAdvService {

    @Resource
    private DiyAdvMapper diyAdvMapper;

    @Override
    public Object advInfo(String appKey) {
        JSONArray advPosition =  AdvPositionEnum.getPosition();
        Map<String, JSONObject> result = new HashMap<>();
        for (int i = 0; i < advPosition.size(); i++){
            JSONObject obj = advPosition.getJSONObject(i);
            result.put(obj.getStr("keywords"), obj);
        }
        if(result.get(appKey) == null){
            throw new ApiException("广告位不存在");
        }
        List<DiyAdv>  list = diyAdvMapper.selectList(new QueryWrapper<DiyAdv>().eq("ap_key", appKey).orderByDesc("slide_sort"));
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (DiyAdv item : list){
            Map<String, Object> map = BeanUtil.beanToMap( item,true,false);
            JSONObject jsonObject = JSONUtil.parseObj(map.get("adv_url"));
            map.put("adv_url", jsonObject);
            mapList.add(map);
        }
        result.get(appKey).set("adv_list", JSONUtil.parseArray(mapList));
        return result.get(appKey);
    }
}
