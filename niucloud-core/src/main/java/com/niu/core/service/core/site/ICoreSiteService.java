package com.niu.core.service.core.site;

import com.niu.core.entity.addon.Addon;
import com.niu.core.service.admin.shop.param.SiteShopBasicInfoParam;
import com.niu.core.service.admin.site.param.SiteAddParam;
import com.niu.core.service.admin.site.param.SiteEditParam;
import com.niu.core.service.admin.site.vo.ShowAppListVo;
import com.niu.core.service.admin.site.vo.SiteInfoVo;

import java.util.List;
import java.util.Map;

/**
 * 插件服务接口类
 */
public interface ICoreSiteService {

    /**
     * 通过站点id获取支持的应用插件
     *
     * @param siteId
     */
    List<String> getAddonKeysBySiteId(Integer siteId);

    /**
     * 获取店铺业务类型（站点 app 字段），对齐 PHP CoreSiteService::getSiteTradeType
     *
     * @param siteId 站点ID
     * @return 业务类型列表，如 mall / waimai / front_warehouse
     */
    List<String> getSiteTradeType(Integer siteId);

    /**
     * 站点详情
     *
     * @param siteId 主键ID
     * @return SiteInfoVo
     */
    SiteInfoVo getSiteCache(Integer siteId);

    /**
     * @param siteId
     * @return
     */
    List<Addon> getSiteAddons(Integer siteId);

    /**
     * 站点插件是否已执行初始化
     *
     * @param siteId
     * @param addon
     * @return
     */
    boolean siteAddonIsInit(Integer siteId, String addon);

    /**
     * 添加站点插件初始化记录
     *
     * @param siteId
     * @param addon
     */
    void addSiteAddonInitRecord(Integer siteId, String addon);

    /**
     * 站点到期自动关闭
     *
     */
    void siteExpireClose();

    /**
     * 清除站点缓存
     */
    void clearSiteCache(Integer siteId);

    SiteInfoVo getShopBasicInfo(Integer siteId);

    Map<String, ShowAppListVo> getShowAppTools(List<String> addons, String appType, Integer siteId);

    Map<String, Map<String, Object>> getShowPromotionTools(List<String> addons, String appType, Integer siteId);

    void editBasicInfo(Integer siteId, Map<String, Object> data);

    /**
     * 更新站点基础信息（不含插件扩展表）
     */
    void updateSiteBasicFields(Integer siteId, SiteShopBasicInfoParam editParam);

    /**
     * 站点基础信息编辑后置：发布事件并清除缓存
     */
    void publishSiteEditAfter(Integer siteId);

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
     * 通过事件聚合获取站点营业状态（对齐 PHP GetSiteBusinessStatus）
     */
    Integer getSiteBusinessStatus(Integer siteId);

    /**
     * 通过事件分发编辑站点营业状态（对齐 PHP EditSiteBusinessStatus）
     */
    void editSiteBusinessStatus(Integer siteId, Integer businessStatus);

}
