package com.niu.core.service.core.user.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.google.gson.JsonArray;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.sys.SysRole;
import com.niu.core.entity.sys.SysUserRole;
import com.niu.core.enums.member.StatusEnum;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.mapper.sys.SysRoleMapper;
import com.niu.core.mapper.sys.SysUserRoleMapper;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.admin.sys.param.SysUserRoleSearchParam;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleListVo;
import com.niu.core.service.core.user.ICoreUserRoleService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 用户权限实现类
 */
@Service
public class CoreUserRoleServiceImpl implements ICoreUserRoleService {

    @Resource
    SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Resource
    private Cached cached;

    /**
     * 用户权限列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysUserRoleListVo>
     */
    @Override
    public PageResult<SysUserRoleListVo> list(PageParam pageParam, SysUserRoleSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<SysUserRole> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("nsu.uid, nsu.username, nsu.head_img, nsu.password, nsu.real_name, nsu.last_ip, nsu.last_time, nsu.create_time, nsu.login_count, nsu.status, nsu.is_del, nsu.delete_time, nsu.update_time, nsur.id, nsur.site_id, nsur.role_ids, nsur.is_admin")
                .setAlias("nsur")
                .leftJoin("?_sys_user nsu ON nsur.uid = nsu.uid".replace("?_", GlobalConfig.tablePrefix));

        //查询条件判断组装
        if (ObjectUtil.isNotEmpty(searchParam.getUid())) {
            queryWrapper.eq("nsu.uid", searchParam.getUid());
        }

        //排序
        queryWrapper.orderByDesc("id");
        //分页查询
        IPage<SysUserRoleListVo> iPage = sysUserRoleMapper.selectJoinPage(
                new Page<>(page, limit),
                SysUserRoleListVo.class,
                queryWrapper);


        return PageResult.build(iPage);
    }

    /**
     * 用户权限详情
     *
     * @param id 主键参数
     * @return SysUserRole
     */
    @Override
    public SysUserRoleInfoVo info(Integer id) {
        SysUserRole model = sysUserRoleMapper.selectOne(
                new QueryWrapper<SysUserRole>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        SysUserRoleInfoVo vo = new SysUserRoleInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 用户权限新增
     *
     * @param addParam 参数
     */
    @Override
    public void add(SysUserRoleParam addParam) {
        Integer siteId = addParam.getSiteId() == null ? RequestUtils.siteId() : addParam.getSiteId();
        Long userRoleCount = sysUserRoleMapper.selectCount(
                new QueryWrapper<SysUserRole>()
                        .eq("uid", addParam.getUid())
                        .eq("site_id", siteId)
        );
        if (userRoleCount > 0) {
            throw new CommonException("SITE_USER_EXIST");
        }

        Integer isAdmin = addParam.getIsAdmin() == null ? 0 : addParam.getIsAdmin();
        SysUserRole model = new SysUserRole();
        model.setUid(addParam.getUid());
        model.setSiteId(siteId);
//        model.setRoleIds(addParam.getRoleIds());
        model.setCreateTime(DateUtils.currTime());
        model.setIsAdmin(isAdmin);
        model.setStatus(ObjectUtil.isNull(addParam.getStatus()) ? StatusEnum.ON.getStatus() : addParam.getStatus());
//        if (model.getStatus() < 1) {
        if (isAdmin <= 0) {
            model.setRoleIds(addParam.getRoleIds() == null ? JSONUtil.toJsonStr(new JsonArray()) : addParam.getRoleIds());
        }
        sysUserRoleMapper.insert(model);
    }

    /**
     * 更新用户权限(编辑站点用户，仅更新角色)
     */
    @Override
    public void edit(SysUserRoleParam editParam) {
        SysUserRole model = sysUserRoleMapper.selectOne(
                new QueryWrapper<SysUserRole>()
                        .eq("uid", editParam.getUid())
                        .eq("site_id", editParam.getSiteId())
                        .last("limit 1"));
        if (model == null) {
            throw new CommonException("NO_SITE_USER_ROLE");
        }
        Integer isAdmin = model.getIsAdmin();
        if (isAdmin != null && isAdmin > 0) { ////超级管理员不允许改动权限
            throw new CommonException("ADMIN_NOT_ALLOW_EDIT_ROLE");
        }
//        model.setStatus(editParam.getStatus());
        List<String> newRoleIds = parseRoleIds(editParam.getRoleIds());
        List<String> currentRoleIds = parseRoleIds(model.getRoleIds());
        if (!roleIdsChanged(currentRoleIds, newRoleIds)) {
            return;
        }
        model.setRoleIds(JSONUtil.toJsonStr(newRoleIds));
        sysUserRoleMapper.updateById(model);
        cached.remove("user_role_" + editParam.getUid() + "_" + editParam.getSiteId());
    }

    /**
     * 用户权限删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        SysUserRole model = sysUserRoleMapper.selectOne(
                new QueryWrapper<SysUserRole>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        sysUserRoleMapper.delete(new QueryWrapper<SysUserRole>().eq("id", id));
    }

    /**
     * 用户权限信息  获取用户对应站点权限
     *
     * @param site_id
     * @param uid
     * @return
     */
    @Override
    public SysUserRoleInfoVo getUserRole(Integer site_id, Integer uid) {
        String cacheKey = "user_role_" + uid + "_" + site_id;
        Object cachedValue = cached.tag(CacheTagEnum.USER_ROLE_CACHE.getTagName()).get(cacheKey);
        if (cachedValue instanceof SysUserRoleInfoVo) {
            return (SysUserRoleInfoVo) cachedValue;
        }
        SysUserRole model = sysUserRoleMapper.selectOne(
                new QueryWrapper<SysUserRole>().eq("uid", uid).eq("site_id", site_id).last(" limit 1"));
        if (ObjectUtil.isNull(model)) {
            return null;
        }
        SysUserRoleInfoVo vo = new SysUserRoleInfoVo();
        BeanUtils.copyProperties(model, vo);
        cached.tag(CacheTagEnum.USER_ROLE_CACHE.getTagName()).put(cacheKey, vo);
        return vo;
    }

    @Override
    public Integer getUserDefaultSiteId(Integer uid) {
        // 获取默认站点ID（平台站点）
        Integer defaultSiteId = RequestUtils.defaultSiteId();
        // 查询该用户除默认站点外的第一个站点
        SysUserRole userRole = sysUserRoleMapper.selectOne(
                new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUid, uid)
                        .ne(SysUserRole::getSiteId, defaultSiteId)
                        .last("limit 1")
        );
        return userRole != null ? userRole.getSiteId() : null;
    }

    @Override
    public List<String> getRoleByUserRoleIds(List<Integer> roleIds, Integer siteId) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        // 排序并生成缓存key（对应PHP md5(implode(',', $role_ids))）
        List<Integer> sortedIds = new ArrayList<>(roleIds);
        Collections.sort(sortedIds);
        String keyStr = String.join(",", sortedIds.stream().map(String::valueOf).collect(Collectors.toList()));
        String cacheKey = "role_by_ids_" + DigestUtils.md5DigestAsHex(keyStr.getBytes()) + "_" + siteId;
        // 这里使用简单缓存（可改用Redis）
        // 为了演示，直接查询数据库
        List<SysRole> roles = sysRoleMapper.selectList(
                new LambdaQueryWrapper<SysRole>()
                        .in(SysRole::getRoleId, roleIds)
                        .eq(SysRole::getSiteId, siteId)
        );
        return roles.stream().map(SysRole::getRoleName).collect(Collectors.toList());
    }

    @Override
    public Integer getSiteManagerUid(Integer siteId) {
        SysUserRole userRole = sysUserRoleMapper.selectOne(
                new QueryWrapper<SysUserRole>()
                        .eq("site_id", siteId)
                        .eq("is_admin", 1)
                        .last("limit 1")
        );
        return userRole != null ? userRole.getUid() : null;
    }

    private List<String> parseRoleIds(String roleIds) {
        if (ObjectUtil.isEmpty(roleIds) || JSONUtil.parseArray(roleIds).isEmpty()) {
            return Collections.emptyList();
        }
        return JSONUtil.toList(JSONUtil.parseArray(roleIds), String.class);
    }

    private boolean roleIdsChanged(List<String> currentRoleIds, List<String> newRoleIds) {
        if (currentRoleIds.size() != newRoleIds.size()) {
            return true;
        }
        for (int i = 0; i < currentRoleIds.size(); i++) {
            if (!currentRoleIds.get(i).equals(newRoleIds.get(i))) {
                return true;
            }
        }
        return false;
    }

}
