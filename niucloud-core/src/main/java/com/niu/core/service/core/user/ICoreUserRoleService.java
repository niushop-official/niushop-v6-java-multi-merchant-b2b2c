package com.niu.core.service.core.user;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.sys.param.SysUserRoleParam;
import com.niu.core.service.admin.sys.param.SysUserRoleSearchParam;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleListVo;

import java.util.List;

/**
 * 用户权限服务接口类
 */
public interface ICoreUserRoleService {

    /**
     * 用户权限列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SysUserRoleListedVo>
     */
    PageResult<SysUserRoleListVo> list(PageParam pageParam, SysUserRoleSearchParam searchParam);

    /**
     * 用户权限详情
     *
     * @param id 主键ID
     * @return SysUserRoleInfoVo
     */
    SysUserRoleInfoVo info(Integer id);

    /**
     * 用户权限添加
     *
     * @param addParam 参数
     */
    void add(SysUserRoleParam addParam);

    /**
     * 用户权限编辑
     *
     * @param editParam 参数
     */
    void edit(SysUserRoleParam editParam);

    /**
     * 用户权限删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 用户权限信息  获取用户对应站点权限
     *
     * @param site_id
     * @param uid
     * @return
     */
    SysUserRoleInfoVo getUserRole(Integer site_id, Integer uid);

    /**
     * 获取用户默认站点ID（平台管理端不可用，排除默认站点）
     *
     * @param uid 用户ID
     * @return 默认站点ID，若无则返回null
     */
    Integer getUserDefaultSiteId(Integer uid);

    /**
     * 根据角色ID列表获取角色名称列表（带缓存）
     *
     * @param roleIds 角色ID列表
     * @param siteId  站点ID
     * @return 角色名称列表
     */
    List<String> getRoleByUserRoleIds(List<Integer> roleIds, Integer siteId);

    /**
     * 获取站点管理员uid
     *
     * @param siteId 站点ID
     * @return 管理员uid
     */
    Integer getSiteManagerUid(Integer siteId);
}
