package com.niu.core.service.core.shop;

/**
 * 店铺套餐服务（对齐 PHP CoreShopSiteGroupService）
 */
public interface ICoreShopSiteGroupService {

    /**
     * 创建应用默认套餐（create_by=app）
     */
    void createAppGroup(String addon);

    /**
     * 删除应用默认套餐
     */
    void deleteAppGroup(String addon);

    /**
     * 更新系统全应用套餐并同步 app 套餐插件列表
     */
    void updateSysGroup();
}
