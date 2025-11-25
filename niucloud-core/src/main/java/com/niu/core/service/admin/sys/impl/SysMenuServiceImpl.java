package com.niu.core.service.admin.sys.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.BaseException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.TreeUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.enums.sys.CacheTagEnum;
import com.niu.core.enums.sys.MenuSourceEnum;
import com.niu.core.mapper.sys.SysMenuMapper;
import com.niu.core.service.admin.sys.ISysMenuService;
import com.niu.core.service.admin.sys.param.SysMenuParam;
import com.niu.core.service.admin.sys.vo.SysMenuInfoVo;
import com.niu.core.service.admin.sys.vo.SysMenuTreeVo;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * 菜单实现类
 */
@Service
public class SysMenuServiceImpl implements ISysMenuService {

    /** 菜单tag */
    public static String cacheTagName = CacheTagEnum.MENU_CACHE.getTagName();

    /** 是否使用缓存 */
    static final boolean useCache = true;

    @Resource
    SysMenuMapper sysMenuMapper;

    @Resource
    ICoreSiteService coreSiteService;

    @Resource
    ICoreAddonService coreAddonService;

    @Resource
    private Cached cached;

    /**
     * 菜单详情
     *
     * @param id 主键参数
     * @return SysMenu
     */
    @Override
    public SysMenuInfoVo info(Integer id) {
        SysMenu model = sysMenuMapper.selectOne(
                new QueryWrapper<SysMenu>()
                        .eq("id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        SysMenuInfoVo vo = new SysMenuInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 菜单详情
     *
     * @param appType
     * @param menuKey
     * @return
     */
    @Override
    public SysMenuInfoVo get(String appType, String menuKey) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_type", appType);
        queryWrapper.eq("menu_key", menuKey);
        SysMenu sysMenu = sysMenuMapper.selectOne(queryWrapper);
        Assert.notNull(sysMenu, "菜单数据不存在");
        SysMenuInfoVo sysMenuInfoVo = new SysMenuInfoVo();
        BeanUtil.copyProperties(sysMenu, sysMenuInfoVo);
        return sysMenuInfoVo;
    }

    /**
     * 菜单新增
     *
     * @param addParam 参数
     */
    @Override
    @Transactional
    public void add(SysMenuParam addParam) {
        SysMenu sysMenu = this.find(addParam.getMenuKey(), addParam.getAppType());
        if (ObjectUtil.isNotNull(sysMenu)) {
            throw new AdminException("validate_menu.exit_menu_key");
        }

        SysMenu model = new SysMenu();
        BeanUtil.copyProperties(addParam, model);
        model.setCreateTime(DateUtils.currTime());
        model.setSource(MenuSourceEnum.CREATE.getCode());
        sysMenuMapper.insert(model);
        /** 清理缓存 */
        cached.tag(cacheTagName).clear();
    }

    /**
     * 编辑菜单
     *
     * @param appType
     * @param menuKey
     * @param editParam
     */
    @Override
    public void edit(String appType, String menuKey, SysMenuParam editParam) {
        SysMenu model = sysMenuMapper.selectOne(
                new QueryWrapper<SysMenu>()
                        .eq("app_type", appType)
                        .eq("menu_key", menuKey)
                        .last("limit 1"));
        Assert.notNull(model, "数据不存在!");
        BeanUtil.copyProperties(editParam, model);
        sysMenuMapper.updateById(model);
        /** 清理缓存 */
        cached.tag(cacheTagName).clear();
    }

    /**
     * 删除菜单
     *
     * @param appType
     * @param menuKey
     */
    @Override
    public void del(String appType, String menuKey) {
        Long keyCount = sysMenuMapper.selectCount(new QueryWrapper<SysMenu>().eq("parent_key", menuKey).eq("app_type", appType));
        if (keyCount > 0) {
            throw new AdminException("MENU_NOT_ALLOW_DELETE");
        }
        sysMenuMapper.delete(new QueryWrapper<SysMenu>().eq("app_type", appType).eq("menu_key", menuKey));
        /** 清理缓存 */
        cached.tag(cacheTagName).clear();
    }

    @Override
    public JSONArray menuTree() {

        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("menuTree"), uniqueKey -> {

            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_type", "site");
            queryWrapper.orderByDesc(Arrays.asList("sort", "id"));
            List<SysMenu> sysMenuList = sysMenuMapper.selectList(queryWrapper);
            JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(sysMenuList));
            return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
        });
    }

    @Override
    public Map<String, List<String>> getAllApiList(String app_type, Integer status) {

        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("getAllApiList", app_type, status), uniqueKey -> {
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.ne("api_url", "");
            queryWrapper.eq("app_type", app_type);
            if (status != 100) {
                queryWrapper.eq("status", status);
            }
            queryWrapper.orderByDesc("sort");
            List<SysMenu> list = sysMenuMapper.selectList(queryWrapper);
            Map<String, List<String>> apiMap = new HashMap<>();
            for (SysMenu sysMenu : list) {
                if (!apiMap.containsKey(sysMenu.getMethods())) {
                    apiMap.put(sysMenu.getMethods(), new ArrayList<String>());
                }
                apiMap.get(sysMenu.getMethods()).add(sysMenu.getApiUrl());
            }
            return apiMap;
        });
    }

    /**
     * 获取系统菜单(站点权限api)
     *
     * @param appType
     * @param addons
     * @return
     */
    @Override
    public Map<String, List<String>> getApiListBySystem(String appType, List<String> addons) {

        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("getApiListBySystem", appType, addons), uniqueKey -> {
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            if (addons.size() > 0) {
                queryWrapper.in("addon", addons);
                if (!addons.contains("supply")) {
                    queryWrapper.notLikeRight("menu_key", "supply_");
                }
            }
            if (ObjectUtil.isNotEmpty(appType)) {
                queryWrapper.eq("app_type", appType);
            }

            queryWrapper.orderByDesc("sort");
            List<SysMenu> list = sysMenuMapper.selectList(queryWrapper);

            if (!addons.contains("mall")) {
                list.removeIf(menu -> menu.getMenuKey().equals("mall_account_bill_list"));
            }
            if (!addons.contains("supply")) {
                list.removeIf(menu -> menu.getMenuKey().equals("supply_account_bill_list"));
            }
            Map<String, List<String>> apiMap = new HashMap<>();
            for (SysMenu sysMenu : list) {
                if (!apiMap.containsKey(sysMenu.getMethods())) {
                    apiMap.put(sysMenu.getMethods(), new ArrayList<String>());
                }
                apiMap.get(sysMenu.getMethods()).add(sysMenu.getApiUrl());
            }
            return apiMap;
        });
    }

    /**
     * 通过菜单menu_key 获取接口数据
     *
     * @param appType
     * @param menuKeys
     * @return
     */
    @Override
    public Map<String, List<String>> getApiListByMenuKeys(String appType, List<String> menuKeys) {
        return cached.rememberObject(useCache, cacheTagName, Arrays.asList("getApiListByMenuKeys", appType, menuKeys), uniqueKey -> {
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            if (menuKeys.size() > 0) {
                queryWrapper.in("menu_key", menuKeys);
            } else {
                queryWrapper.in("menu_key", " ");
            }

            if (ObjectUtil.isNotEmpty(appType)) {
                queryWrapper.eq("app_type", appType);
            }

            //站点相关插件
            Integer siteId = RequestUtils.siteId();
            List<String> addonList = coreSiteService.getAddonKeysBySiteId(siteId);
            if (!RequestUtils.defaultSiteId().equals(siteId)) {

                if (addonList.size() > 0) {
                    addonList.add("");
                    queryWrapper.in("addon", addonList);
                    if (!addonList.contains("supply")) {
                        queryWrapper.notLikeRight("menu_key", "supply_");
                    }

                }
            }
            queryWrapper.orderByDesc("sort");
            List<SysMenu> list = sysMenuMapper.selectList(queryWrapper);

            if (!addonList.contains("mall")) {
                list.removeIf(menu -> menu.getMenuKey().equals("mall_account_bill_list"));
            }
            if (!addonList.contains("supply")) {
                list.removeIf(menu -> menu.getMenuKey().equals("supply_account_bill_list"));
            }
            Map<String, List<String>> apiMap = new HashMap<>();
            for (SysMenu sysMenu : list) {
                if (!apiMap.containsKey(sysMenu.getMethods())) {
                    apiMap.put(sysMenu.getMethods(), new ArrayList<String>());
                }
                apiMap.get(sysMenu.getMethods()).add(sysMenu.getApiUrl());
            }
            return apiMap;
        });
    }

    @Override
    public List<SysMenu> getAdminMenuListByMenuKeys(String appType, Integer siteId, Integer status, Integer isButton, List<String> menuKeys, String addon) {
        return cached.remember(useCache, cacheTagName, Arrays.asList("getSiteMenuListByMenuKeys", appType, siteId, status, isButton, menuKeys, addon), uniqueKey -> {
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            if (ObjectUtil.isNotEmpty(appType)) {
                queryWrapper.eq("app_type", appType);
            }
            List<String> addonList = coreAddonService.getAddonCache();
            if (addonList.size() > 0) {
                addonList.add("");
                queryWrapper.in("addon", addonList);
            }
            if (status != 100) {
                queryWrapper.eq("status", status);
            }
            if (menuKeys.size() > 0) {
                queryWrapper.in("menu_key", menuKeys);
            }
            if (isButton == 0) {
                queryWrapper.ne("menu_type", 2);
            }
            queryWrapper.orderByDesc("sort");

            //排除菜单 后期处理

            List<SysMenu> menuList = sysMenuMapper.selectList(queryWrapper);
            return menuList;
        });
    }

    @Override
    public List<SysMenu> getSiteMenuListByMenuKeys(String appType, Integer siteId, Integer status, Integer isButton, List<String> menuKeys, String addon) {
        return cached.remember(useCache, cacheTagName, Arrays.asList("getSiteMenuListByMenuKeys", appType, siteId, status, isButton, menuKeys, addon), uniqueKey -> {
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            if (ObjectUtil.isNotEmpty(appType)) {
                queryWrapper.eq("app_type", appType);
            }
            if (siteId != 0) {
                List<String> addonList = coreSiteService.getAddonKeysBySiteId(siteId);
                if (addonList.size() > 0) {
                    addonList.add("");
                    queryWrapper.in("addon", addonList);
                } else {
                    queryWrapper.in("addon", "");
                }
            }
            if (status != 100) {
                queryWrapper.eq("status", status);
            }
            if (menuKeys.size() > 0) {
                queryWrapper.in("menu_key", menuKeys);
            }
            if (isButton == 0) {
                queryWrapper.ne("menu_type", 2);
            }
            queryWrapper.orderByDesc("sort");

            //排除菜单 后期处理

            List<SysMenu> menuList = sysMenuMapper.selectList(queryWrapper);
            return menuList;
        });
    }

    @Override
    public List<SysMenu> getAdminAllMenuList(String appType, Integer status, Boolean isButton) {
        return cached.remember(useCache, cacheTagName, Arrays.asList("getAdminAllMenuList", appType, status, isButton), uniqueKey -> {
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            if (ObjectUtil.isNotEmpty(appType)) {
                queryWrapper.eq("app_type", appType);
            }
            List<String> addonList = coreAddonService.getAddonCache();
            if (addonList.size() > 0) {
                addonList.add("");
                queryWrapper.in("addon", addonList);
            }
            if (status != 100) {
                queryWrapper.eq("status", status);
            }
            if (!isButton) {
                queryWrapper.ne("menu_type", 2);
            }

            queryWrapper.orderByDesc("sort");

            //排除菜单 后期处理

            List<SysMenu> menuList = sysMenuMapper.selectList(queryWrapper);
            return menuList;
        });
    }

    @Override
    public List<SysMenu> getSiteAllMenuList(String appType, Integer siteId, Integer status, Boolean isButton) {
        return cached.remember(useCache, cacheTagName, Arrays.asList("getSiteAllMenuList", appType, siteId, status, isButton), uniqueKey -> {
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            if (ObjectUtil.isNotEmpty(appType)) {
                queryWrapper.eq("app_type", appType);
            }
            if (siteId != 0) {
                List<String> addonList = coreSiteService.getAddonKeysBySiteId(siteId);
                if (addonList.size() > 0) {
                    addonList.add("");
                    queryWrapper.in("addon", addonList);
                } else {
                    queryWrapper.in("addon", "");
                }
            }
            if (status != 100) {
                queryWrapper.eq("status", status);
            }
            if (!isButton) {
                queryWrapper.ne("menu_type", 2);
            }
            queryWrapper.orderByDesc("sort");

            //排除菜单 后期处理

            List<SysMenu> menuList = sysMenuMapper.selectList(queryWrapper);
            return menuList;
        });
    }

    /**
     * 通过条件查询菜单
     *
     * @param appType
     * @param siteId
     * @param status
     * @param isButton
     * @param menuKeys
     * @param addon
     * @return
     */
    @Override
    public List<SysMenu> getMenuListByCondition(String appType, Integer siteId, Integer status, Integer isButton, List<String> menuKeys, String addon) {
        return cached.remember(useCache, cacheTagName, Arrays.asList("getMenuListByCondition", appType, siteId, status, isButton, menuKeys, addon), uniqueKey -> {
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            if (ObjectUtil.isNotEmpty(appType)) {
                queryWrapper.eq("app_type", appType);
            }
            if (siteId != 0) {
                List<String> addonList = coreSiteService.getAddonKeysBySiteId(siteId);
                if (addonList.size() > 0) {
                    addonList.add("");
                    queryWrapper.in("addon", addonList);
                } else {
                    queryWrapper.in("addon", "");
                }
            }
            if (status != 100) {
                queryWrapper.eq("status", status);
            }
            if (menuKeys.size() > 0) {
                queryWrapper.in("menu_key", menuKeys);
            }
            if (isButton == 0) {
                queryWrapper.ne("menu_type", 2);
            }
            queryWrapper.orderByDesc("sort");

            //排除菜单 后期处理

            List<SysMenu> menuList = sysMenuMapper.selectList(queryWrapper);
            return menuList;
        });
    }

    /**
     * 获取所有接口菜单
     *
     * @param appType
     * @param status
     * @return
     */
    @Override
    public JSONArray getAllMenuList(String appType, Integer status) {
        //站点id
        Integer siteId = RequestUtils.siteId();

        // PHP逻辑: if ($site_id > 0) return [];
        // 平台端才能访问菜单列表，站点端直接返回空
        if (siteId > 0) {
            return new JSONArray();
        }

        // 缓存Key值计算参数
        String key = "menuList_" + appType + "_" + status + siteId.toString();

        // 检查缓存中是否有数据
        Object cachedData = cached.tag(cacheTagName).get(key);
        if (cachedData != null) {
            if (cachedData instanceof List) {
                // 如果缓存中有空数据，清理缓存重新查询
                if (((List<?>) cachedData).size() == 0) {
                    cached.tag(cacheTagName).remove(key);
                }
            }
        }

        List<SysMenu> outterMenuList = cached.cache(useCache, cacheTagName, key, uniqueKey -> {
            List<SysMenu> menuList = new ArrayList<>();
            try {
                QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
                if (ObjectUtil.isNotEmpty(appType)) {
                    // 如果appType是system_menu，应该查询admin类型的菜单
                    String actualAppType = "system_menu".equals(appType) ? "admin" : appType;
                    queryWrapper.eq("app_type", actualAppType);
                }

                //站点相关插件
                List<String> addonList = coreSiteService.getAddonKeysBySiteId(siteId);

                if (!RequestUtils.defaultSiteId().equals(siteId)) {
                    if (addonList.size() > 0) {
                        addonList.add("");
                        queryWrapper.in("addon", addonList);
                        if (!addonList.contains("supply")) {
                            queryWrapper.notLikeRight("menu_key", "supply_");
                        }
                    }
                }

                if (status != 100) {
                    queryWrapper.eq("status", status);
                }
                queryWrapper.orderByDesc("sort");

                // 先查询所有app_type，看看数据库中有哪些类型
                QueryWrapper<SysMenu> debugWrapper = new QueryWrapper<>();
                debugWrapper.select("DISTINCT app_type");
                List<Map<String, Object>> appTypes = sysMenuMapper.selectMaps(debugWrapper);

                //排除菜单
                menuList = sysMenuMapper.selectList(queryWrapper);

                if (!addonList.contains("mall")) {
                    menuList.removeIf(menu -> menu.getMenuKey().equals("mall_account_bill_list"));
                }
                if (!addonList.contains("supply")) {
                    menuList.removeIf(menu -> menu.getMenuKey().equals("supply_account_bill_list"));
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new BaseException("查询菜单错误");
            }
            return menuList;
        });

        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(outterMenuList));
        return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
    }

    /**
     * 查询菜单
     *
     * @param menuKey
     * @param appType
     * @return
     */
    @Override
    public SysMenu find(String menuKey, String appType) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNotNull(menuKey) && ObjectUtil.isNotEmpty(menuKey)) {
            queryWrapper.eq("menu_key", menuKey);
        }
        if (ObjectUtil.isNotNull(appType) && ObjectUtil.isNotEmpty(appType)) {
            queryWrapper.eq("app_type", appType);
        }
        SysMenu sysMenu = sysMenuMapper.selectOne(queryWrapper);
        return sysMenu;
    }

    @Override
    public JSONArray getMenuByTypeDir(String addon) {
        List<SysMenu> outterMenuList = cached.remember(useCache, cacheTagName, Arrays.asList("getMenuByTypeDir", addon), uniqueKey -> {
            QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_type", "site");
            queryWrapper.eq("menu_type", "0");
            queryWrapper.eq("addon", addon.equals("system") ? "" : addon);
            queryWrapper.orderByDesc("sort");
            return sysMenuMapper.selectList(queryWrapper);
        });

        //暂无多语言设计
        JSONArray jsonArray = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(outterMenuList));
        return TreeUtils.listToTree(jsonArray, "menu_key", "parent_key", "children");
    }

    /**
     * 查询插件跳转地址
     * @param parentKey
     * @param addon
     * @param appType
     * @return
     */
    @Override
    public List<SysMenu> getMenuListByParentKeyAndAddonAndAppType(String parentKey, String addon, String appType) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_key", parentKey);
        queryWrapper.eq("addon", addon);
        queryWrapper.eq("app_type", appType);
        return sysMenuMapper.selectList(queryWrapper);
    }

    @Override
    public List<SysMenuTreeVo> getAddonMenu(String appKey, String status, Integer isTree, Integer isButton) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();

        if (isButton == 0) {
            queryWrapper.in("menu_type", Arrays.asList(0, 1));
        }

        if (!"all".equals(status)) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.eq("addon", appKey);

        List<SysMenu> menuList = sysMenuMapper.selectList(queryWrapper);

        // 转换为VO
        List<SysMenuTreeVo> voList = new ArrayList<>();
        for (SysMenu menu : menuList) {
            SysMenuTreeVo vo = new SysMenuTreeVo();
            BeanUtils.copyProperties(menu, vo);
            voList.add(vo);
        }

        return isTree == 1 ? menuToTree(voList, "menu_key", "parent_key", "children", "auth", "", isButton) : voList;
    }

    /**
     * 获取系统菜单（不包含插件菜单）
     * 对应 PHP: public function getSystemMenu($status = 'all', $is_tree = 0, $is_button = 0)
     * @param status 状态过滤
     * @param isTree 是否返回树形结构
     * @param isButton 是否包含按钮
     * @return
     */
    @Override
    public List<SysMenuTreeVo> getSystemMenu(String status, Integer isTree, Integer isButton) {
        QueryWrapper<SysMenu> queryWrapper = new QueryWrapper<>();

        if (isButton == 0) {
            queryWrapper.in("menu_type", Arrays.asList(0, 1));
        }

        if (!"all".equals(status)) {
            queryWrapper.eq("status", status);
        }

        queryWrapper.eq("addon", "");

        queryWrapper.orderByDesc("sort");

        List<SysMenu> menuList = sysMenuMapper.selectList(queryWrapper);

        // 转换为VO
        List<SysMenuTreeVo> voList = new ArrayList<>();
        for (SysMenu menu : menuList) {
            SysMenuTreeVo vo = new SysMenuTreeVo();
            BeanUtils.copyProperties(menu, vo);


            voList.add(vo);
        }

        return isTree == 1 ? menuToTree(voList, "menu_key", "parent_key", "children", "auth", "", isButton) : voList;
    }

    /**
     * 将菜单列表转换为树形结构，严格按照PHP版本实现
     * @param list 菜单列表
     * @param pk 主键字段名
     * @param pid 父级字段名
     * @param child 子级字段名
     * @param buttonName 按钮字段名
     * @param root 根节点值
     * @param isButton 是否包含按钮
     * @return 树形结构菜单
     */
    public List<SysMenuTreeVo> menuToTree(List<SysMenuTreeVo> list, String pk, String pid, String child, String buttonName, String root, Integer isButton) {
        List<SysMenuTreeVo> tree = new ArrayList<>();

        if (list != null && !list.isEmpty()) {
            Map<String, SysMenuTreeVo> refer = new HashMap<>();
            for (SysMenuTreeVo menu : list) {
                refer.put(menu.getMenuKey(), menu);
            }

            for (SysMenuTreeVo menu : list) {
                String parentId = menu.getParentKey();
                if (root.equals(parentId)) {
                    tree.add(menu);
                } else {
                    SysMenuTreeVo parent = refer.get(parentId);
                    if (parent != null) {
                        if (menu.getMenuType() == 2 && isButton == 1) {
                            if (parent.getAuth() == null) {
                                parent.setAuth(new ArrayList<>());
                            }
                            parent.getAuth().add(menu.getMenuKey());
                        } else {
                            if (parent.getChildren() == null) {
                                parent.setChildren(new ArrayList<>());
                            }
                            parent.getChildren().add(menu);
                        }
                    }
                }
            }
        }

        return tree;
    }

}
