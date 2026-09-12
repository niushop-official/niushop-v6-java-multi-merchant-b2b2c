package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.sys.param.SysUserLogParam;
import com.niu.core.service.admin.sys.param.SysUserLogSearchParam;
import com.niu.core.service.admin.sys.ISysUserLogService;
import com.niu.core.service.admin.sys.vo.SysUserLogInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserLogListVo;
import com.niu.core.common.domain.PageParam;
import com.niu.core.entity.sys.SysUserLog;
import com.niu.core.mapper.sys.SysUserLogMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 管理员操作记录实现类
 */
@Service
public class SysUserLogServiceImpl extends ServiceImpl<SysUserLogMapper, SysUserLog> implements ISysUserLogService {

    @Resource
    SysUserLogMapper sysUserLogMapper;

    RedisTemplate<String, Object> redisTemplate;

    /**
     * 管理员操作记录列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysUserLogListVo>
     */
    @Override
    public PageResult<SysUserLogListVo> list(PageParam pageParam, SysUserLogSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<SysUserLog> queryWrapper = new QueryWrapper<>();
        
        queryWrapper.select("id", "ip", "site_id", "uid", "username", "operation", "url", "params", "type", "create_time");
        queryWrapper.eq("site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("create_time");
        if(ObjectUtil.isNotNull(searchParam.getUsername()) && ObjectUtil.isNotEmpty(searchParam.getUsername())){
            queryWrapper.like("username", searchParam.getUsername());
        }
        if(ObjectUtil.isNotNull(searchParam.getIp()) && ObjectUtil.isNotEmpty(searchParam.getIp())){
            queryWrapper.like("ip", searchParam.getIp());
        }
        if(ObjectUtil.isNotNull(searchParam.getUrl()) && ObjectUtil.isNotEmpty(searchParam.getUrl())){
            queryWrapper.like("url", searchParam.getUrl());
        }
        if(ObjectUtil.isNotNull(searchParam.getUid()) && ObjectUtil.isNotEmpty(searchParam.getUid())){
            queryWrapper.eq("uid", searchParam.getUid());
        }
        if(ObjectUtil.isNotNull(searchParam.getType()) && ObjectUtil.isNotEmpty(searchParam.getType())){
            queryWrapper.eq("type", searchParam.getType());
        }


        IPage<SysUserLog> iPage = sysUserLogMapper.selectPage(new Page<>(page, limit), queryWrapper);

        List<SysUserLogListVo> list = new LinkedList<>();
        for (SysUserLog item : iPage.getRecords()) {
            SysUserLogListVo vo = new SysUserLogListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal(), list);
    }

    /**
     * 管理员操作记录详情
     *
     * @param id 主键参数
     * @return SysUserLogInfoVo
     */
    @Override
    public SysUserLogInfoVo info(Integer id) {
        QueryWrapper<SysUserLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                .eq("site_id", RequestUtils.siteId());

        // 选择字段，与PHP版本保持一致
        queryWrapper.select("id", "ip", "site_id", "uid", "username", "operation", "url", "params", "type", "create_time");

        SysUserLog model = sysUserLogMapper.selectOne(queryWrapper);

        Assert.notNull(model, "数据不存在");

        SysUserLogInfoVo vo = new SysUserLogInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 管理员操作记录新增
     *
     * @param addParam 参数
     */
    @Override
    public void add(SysUserLogParam addParam) {
        SysUserLog model = new SysUserLog();
        model.setIp(ObjectUtil.defaultIfNull(addParam.getIp(), ""));
        model.setSiteId(ObjectUtil.defaultIfNull(addParam.getSiteId(), RequestUtils.siteId()));
        model.setUid(ObjectUtil.defaultIfNull(addParam.getUid(), 0));
        model.setUsername(ObjectUtil.defaultIfNull(addParam.getUsername(), ""));
        model.setUrl(ObjectUtil.defaultIfNull(addParam.getUrl(), ""));
        model.setParams(ObjectUtil.defaultIfNull(addParam.getParams(), "{}"));
        model.setType(ObjectUtil.defaultIfNull(addParam.getType(), ""));
        model.setOperation(ObjectUtil.defaultIfNull(addParam.getOperation(), ""));
        model.setCreateTime(System.currentTimeMillis() / 1000);
        sysUserLogMapper.insert(model);
    }

    /**
     * 管理员操作记录编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, SysUserLogParam editParam) {
        SysUserLog model = sysUserLogMapper.selectOne(
                new QueryWrapper<SysUserLog>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        model.setId(id);
        model.setIp(editParam.getIp());
        model.setSiteId(editParam.getSiteId());
        model.setUid(editParam.getUid());
        model.setUsername(editParam.getUsername());
        model.setUrl(editParam.getUrl());
        model.setParams(editParam.getParams());
        model.setType(editParam.getType());
        model.setOperation(editParam.getOperation());
        sysUserLogMapper.updateById(model);
    }

    /**
     * 管理员操作记录删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysUserLog model = sysUserLogMapper.selectOne(
                new QueryWrapper<SysUserLog>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        sysUserLogMapper.delete(new QueryWrapper<SysUserLog>().eq("id", id));
    }

    /**
     * 清空当前站点操作日志（对齐 PHP UserLogService::destroy）
     */
    @Override
    public void destroy() {
        sysUserLogMapper.delete(new QueryWrapper<SysUserLog>()
                .eq("site_id", RequestUtils.siteId()));
    }

}
