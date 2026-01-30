package com.niu.core.service.core.adminapp.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.entity.sys.SysAdminapp;
import com.niu.core.mapper.sys.SysAdminappMapper;
import com.niu.core.service.core.adminapp.ICoreAdminAppService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 手机管理端应用核心服务实现类
 */
@Service
public class CoreAdminAppServiceImpl implements ICoreAdminAppService {

    @Resource
    private SysAdminappMapper sysAdminappMapper;

    @Override
    public JSONArray getValue(Integer uid, Integer siteId, String type) {
        SysAdminapp adminapp = sysAdminappMapper.selectOne(
                new QueryWrapper<SysAdminapp>()
                        .eq("uid", uid)
                        .eq("site_id", siteId)
                        .eq("type", type)
                        .last("limit 1")
        );

        if (adminapp == null || ObjectUtil.isEmpty(adminapp.getValue())) {
            return new JSONArray();
        }

        try {
            return JSONUtil.parseArray(adminapp.getValue());
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    @Override
    public Boolean setValue(Integer uid, Integer siteId, String type, JSONArray value) {
        SysAdminapp adminapp = sysAdminappMapper.selectOne(
                new QueryWrapper<SysAdminapp>()
                        .eq("uid", uid)
                        .eq("site_id", siteId)
                        .eq("type", type)
                        .last("limit 1")
        );

        if (adminapp == null) {
            // 创建新记录
            SysAdminapp newAdminapp = new SysAdminapp();
            newAdminapp.setUid(uid);
            newAdminapp.setSiteId(siteId);
            newAdminapp.setType(type);
            newAdminapp.setValue(value.toString());
            newAdminapp.setCreateTime(System.currentTimeMillis() / 1000);
            newAdminapp.setUpdateTime(System.currentTimeMillis() / 1000);
            sysAdminappMapper.insert(newAdminapp);
        } else {
            // 更新现有记录
            adminapp.setValue(value.toString());
            adminapp.setUpdateTime(System.currentTimeMillis() / 1000);
            sysAdminappMapper.updateById(adminapp);
        }

        return true;
    }
}
