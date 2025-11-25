package com.niu.core.service.admin.diy.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.enums.diy.LinkEnum;
import com.niu.core.service.admin.diy.param.DiyRouteSearchParam;
import com.niu.core.service.admin.diy.IDiyRouteService;
import com.niu.core.service.admin.diy.param.DiyRouteShareParam;
import com.niu.core.service.admin.diy.vo.DiyRouteInfoVo;
import com.niu.core.service.admin.diy.vo.DiyRouteListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.diy.DiyRoute;
import com.niu.core.mapper.diy.DiyRouteMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 自定义路由实现类
 */
@Service
public class DiyRouteServiceImpl implements IDiyRouteService {
        
    @Resource
    DiyRouteMapper diyRouteMapper;

    /**
     * 自定义路由列表
     *
     * @param searchParam 搜索参数
     * @return List<DiyRouteListVo>
     */
    @Override
    public List<DiyRouteListVo> list(DiyRouteSearchParam searchParam) {
        
        JSONObject linkEnum = LinkEnum.getLink();
        List<DiyRouteListVo> diyRouteList = new LinkedList<>();
        int sort = 0;
        
        // 遍历第一层
        for (String key : linkEnum.keySet()) {
            JSONObject linkGroup = linkEnum.getJSONObject(key);
            
            if (linkGroup.containsKey("child_list")) {
                JSONArray childList = linkGroup.getJSONArray("child_list");
                
                // 遍历第二层
                for (int i = 0; i < childList.size(); i++) {
                    JSONObject childItem = childList.getJSONObject(i);
                    
                    if (childItem.containsKey("child_list")) {
                        JSONArray nestedChildList = childItem.getJSONArray("child_list");
                        
                        // 遍历第三层
                        for (int j = 0; j < nestedChildList.size(); j++) {
                            JSONObject nestedItem = nestedChildList.getJSONObject(j);
                            
                            // 应用过滤条件
                            if (shouldAddItem(nestedItem, linkGroup, searchParam)) {
                                DiyRouteListVo vo = createDiyRouteVo(nestedItem, linkGroup, key, ++sort);
                                diyRouteList.add(vo);
                            }
                        }
                    } else if (ObjectUtil.isNotEmpty(childItem.getStr("url"))) {
                        // 第二层直接有URL的项目
                        if (shouldAddItem(childItem, linkGroup, searchParam)) {
                            DiyRouteListVo vo = createDiyRouteVo(childItem, linkGroup, key, ++sort);
                            diyRouteList.add(vo);
                        }
                    }
                }
            }
        }
        
        return diyRouteList;
    }
    
    /**
     * 判断是否应该添加该项目
     */
    private boolean shouldAddItem(JSONObject item, JSONObject linkGroup, DiyRouteSearchParam searchParam) {
        // title模糊匹配
        if (ObjectUtil.isNotEmpty(searchParam.getTitle())) {
            String itemTitle = item.getStr("title");
            if (ObjectUtil.isEmpty(itemTitle) || !itemTitle.contains(searchParam.getTitle())) {
                return false;
            }
        }
        
        // url精确匹配
        if (ObjectUtil.isNotEmpty(searchParam.getUrl())) {
            String itemUrl = item.getStr("url");
            if (!searchParam.getUrl().equals(itemUrl)) {
                return false;
            }
        }
        
        // addon_name匹配
        if (ObjectUtil.isNotEmpty(searchParam.getAddonName())) {
            JSONObject addonInfo = linkGroup.getJSONObject("addon_info");
            if (addonInfo != null) {
                String addonKey = addonInfo.getStr("key");
                if (!searchParam.getAddonName().equals(addonKey)) {
                    return false;
                }
            } else {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 创建DiyRouteListVo对象
     */
    private DiyRouteListVo createDiyRouteVo(JSONObject item, JSONObject linkGroup, String parentKey, int sort) {
        DiyRouteListVo vo = new DiyRouteListVo();
        vo.setAddonInfo(ObjectUtil.defaultIfNull(linkGroup.getJSONObject("addon_info"), new JSONObject()));
        vo.setTitle(item.getStr("title"));
        vo.setName(item.getStr("name"));
        vo.setParent(parentKey);
        vo.setPage(item.getStr("url"));
        vo.setIsShare(ObjectUtil.defaultIfNull(item.getInt("is_share"), 0));
        vo.setAction(ObjectUtil.defaultIfNull(item.getStr("action"), ""));
        vo.setSort(sort);
        return vo;
    }

    /**
     * 自定义路由详情
     * @return DiyRoute
     */
    @Override
    public DiyRouteInfoVo getInfoByName(String name) {
        DiyRoute model = diyRouteMapper.selectOne(
                new QueryWrapper<DiyRoute>()
                    .eq("name", name)
                    .eq("site_id", RequestUtils.siteId())
                    .last("limit 1"));
        if (model == null) return null;

        DiyRouteInfoVo vo = new DiyRouteInfoVo();
        BeanUtils.copyProperties(model, vo);

        return vo;
    }

    /**
     * 自定义路由新增
     * @param editParam 参数
     */
    @Override
    public void modifyShare(DiyRouteShareParam editParam) {
        QueryWrapper queryWrapper = new QueryWrapper<DiyRoute>()
                .eq("name", editParam.getName())
                .eq("site_id", RequestUtils.siteId())
                .last("limit 1");
        DiyRoute model = diyRouteMapper.selectOne(queryWrapper);

        if (model != null) {
            BeanUtils.copyProperties(editParam, model);
            diyRouteMapper.update(model, queryWrapper);
        } else {
            DiyRoute insertModel = new DiyRoute();
            BeanUtils.copyProperties(editParam, insertModel);
            insertModel.setSiteId(RequestUtils.siteId());
            diyRouteMapper.insert(insertModel);
        }
    }

}
