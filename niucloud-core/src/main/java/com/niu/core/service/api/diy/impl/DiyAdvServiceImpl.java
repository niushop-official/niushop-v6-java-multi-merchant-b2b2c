package com.niu.core.service.api.diy.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONPath;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.ApiException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.diy.DiyAdv;
import com.niu.core.entity.diy.DiyPage;
import com.niu.core.entity.diy.DiyRoute;
import com.niu.core.entity.diy.DiyTheme;
import com.niu.core.enums.diy.AdvPositionEnum;
import com.niu.core.enums.diy.PagesEnum;
import com.niu.core.enums.diy.TemplateEnum;
import com.niu.core.mapper.diy.DiyAdvMapper;
import com.niu.core.mapper.diy.DiyPageMapper;
import com.niu.core.mapper.diy.DiyRouteMapper;
import com.niu.core.mapper.diy.DiyThemeMapper;
import com.niu.core.service.admin.diy.param.TemplateParam;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.api.diy.IDiyAdvService;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.diy.param.DiyInfoParam;
import com.niu.core.service.api.diy.param.DiyShareParam;
import com.niu.core.service.api.diy.param.DiyTabbarListParam;
import com.niu.core.service.api.diy.param.DiyTabbarParam;
import com.niu.core.service.api.diy.vo.DiyInfoVo;
import com.niu.core.service.api.diy.vo.DiyTabbarVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.addon.vo.InstallAddonListVo;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import com.niu.core.service.core.diy.ICoreDiyService;
import com.niu.core.service.core.diy.vo.BottomConfigVo;
import com.niu.core.service.core.diy.vo.StartUpPageConfigVo;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            throw new ApiException("DIY_ADV_POSITION_NOT_EXIST");
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
