package com.niu.core.service.admin.site;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.service.admin.site.param.*;
import com.niu.core.service.admin.site.vo.ShowAppListVo;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.site.vo.SiteListVo;
import com.niu.core.service.admin.site.vo.SiteNameListVo;

import java.util.List;
import java.util.Map;

/**
 * 站点服务接口
 */
public interface ISiteService {


    /**
     * 站点列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<SiteListVo>
     */
    PageResult<SiteListVo> list(PageParam pageParam, SiteSearchParam searchParam);

    /**
     * 站点列表（不分页），对齐 PHP SiteService::getList
     *
     * @param searchParam 搜索参数
     * @return List<SiteListVo>
     */
    List<SiteListVo> getList(SiteSearchParam searchParam);

    /**
     * 站点详情
     *
     * @param id 主键ID
     * @return SiteInfoVo
     */
    SiteInfoVo info(Integer id);

    /**
     * 站点添加
     *
     * @param addParam 添加参数
     * @return
     */
    Integer add(SiteAddParam addParam);

    /**
     * 站点编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, SiteEditParam editParam);

    /**
     * 站点删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 关闭站点
     *
     * @param siteId
     */
    void closeSite(Integer siteId);

    /**
     * 开启站点
     *
     * @param siteId
     */
    void openSite(Integer siteId);

    /**
     * 获取站点的菜单列表
     *
     * @param siteId
     * @param status
     * @return
     */
    Map<String, List<String>> getSiteApiList(Integer siteId, Integer status);

    /**
     * 通过条件检索 站点数量
     *
     * @param siteSearchParam
     * @return
     */
    Integer getSiteCountByCondition(SiteSearchParam siteSearchParam);

    /**
     * 获取站点的插件
     *
     * @return
     */
    List<Addon> getSiteAddons();

    /**
     *
     *
     * @param site
     * @param siteGroup
     */
    void siteAddonChange(Site site, SiteGroup siteGroup);

    /**
     * 获取站点拥有的应用工具列表
     *
     * @param param 搜索参数
     * @return
     */
    Map<String, ShowAppListVo> getSiteAppTools(SiteAppToolsParam param);

    /**
     * 获取平台拥有的应用工具列表
     *
     * @param param 搜索参数
     * @return
     */
    Map<String, ShowAppListVo> getAdminAppTools(SiteAppToolsParam param);

    PageResult<SiteNameListVo> getSiteListByAppName(String appName, String order, String sort, String keyword, PageParam pageParam);

    JSONArray getAdminMenuList(Integer siteId, Boolean isTree, Integer status, String addon, Integer isButton);

    Map<String, Map<String, Object>> getSitePromotionTools(SiteAppToolsParam param);

    Map<String, Map<String, Object>> getAdminPromotionTools(SiteAppToolsParam param);

    /**
     * 获取商圈列表（不分页），对齐 PHP SiteService::getCircleList
     *
     * @param param 搜索参数
     * @return 商圈树形列表
     */
    List<?> getCircleList(SiteCircleListParam param);

    /**
     * 编辑站点营业状态，对齐 PHP SiteService::editSiteBusinessStatus
     */
    void editBusinessStatus(Integer businessStatus);

    /**
     * 查询当前站点可以单独显示的菜单
     */
    List<SysMenu> getShowMenuList(Integer siteId);
}
