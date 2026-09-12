package com.niu.core.service.admin.addon.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.utils.json.JacksonUtils;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.entity.addon.Addon;
import com.niu.core.entity.sys.SysMenu;
import com.niu.core.mapper.addon.AddonMapper;
import com.niu.core.mapper.sys.SysMenuMapper;
import com.niu.core.service.admin.addon.IAddonDevelopBuildService;
import jakarta.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 插件日志实现类
 */
@Service
public class AddonDevelopBuildServiceImpl implements IAddonDevelopBuildService {

    @Resource
    SysMenuMapper sysMenuMapper;

    @Resource
    AddonMapper addonMapper;

    private String addon;

    /** 编译目录 */
    private String addonPath;

    /**
     * 插件打包
     */
    @Override
    public void build(String addon) {
        if (!GlobalConfig.runActive.equals("dev")) throw new CommonException("只有在开发环境下才可以进行打包操作");

        if (!new File(WebAppEnvs.get().projectNiucloudAddon + addon).exists()) throw new CommonException("插件不存在");
        File infoFile = new File(WebAppEnvs.get().projectNiucloudAddon + addon + "/src/main/resources/info.json");
        if (!infoFile.exists()) throw new CommonException("插件不存在");

        this.addon = addon;
        this.addonPath = WebAppEnvs.get().webRootDownAddon + addon + "/";

        try {
            // 清空目录
            File[] files = new File(this.addonPath).listFiles();
            if (files != null) {
                for (File child : files) {
                    if (child.isDirectory() && !child.getName().equals("sql")) FileUtils.cleanDirectory(child);
                }
            }

            FileUtils.copyFile(infoFile, new File(this.addonPath + "info.json"));
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }

        this.admin();
        this.web();
        this.uniapp();
        this.siteuniapp();
        this.java();
        this.jar();
        this.resource();
        this.buildUniappLangJson();
        this.buildSiteUniappLangJson();
        this.buildUniappPagesJson();
        this.buildSiteUniappPagesJson();
        this.menu("site");
        this.menu("admin");
        if (this.isCircleAddonInstalled() && this.hasCircleMenuLoader()) {
            this.menu("circle");
        }
        // 生成压缩包
        this.compressor();
    }

    private void admin() {
        try {
            File sourcePath = new File(WebAppEnvs.get().projectRoot + "/admin/src/addon/" + this.addon + "/");
            if (!sourcePath.exists()) return;

            File destPath = new File(this.addonPath + "admin/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }

            FileUtils.copyDirectory(sourcePath, destPath);
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void uniapp() {
        try {
            File sourcePath = new File(WebAppEnvs.get().projectRoot + "/uni-app/src/addon/" + this.addon + "/");
            if (!sourcePath.exists()) return;

            File destPath = new File(this.addonPath + "uni-app/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }

            FileUtils.copyDirectory(sourcePath, destPath);
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void siteuniapp() {
        try {
            File sourcePath = new File(WebAppEnvs.get().projectRoot + "/site-uniapp/src/addon/" + this.addon + "/");
            if (!sourcePath.exists()) return;

            File destPath = new File(this.addonPath + "site-uniapp/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }

            FileUtils.copyDirectory(sourcePath, destPath);
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void web() {
        try {
            File sourcePath = new File(WebAppEnvs.get().projectRoot + "/web/addon/" + this.addon + "/");
            if (!sourcePath.exists()) return;

            File destPath = new File(this.addonPath + "web/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }

            FileUtils.copyDirectory(sourcePath, destPath);
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void java() {
        try {
            File sourcePath = new File(WebAppEnvs.get().projectNiucloudAddon + this.addon + "/");
            if (!sourcePath.exists()) return;

            File destPath = new File(this.addonPath + "java/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }

            String[] exclusionDir = {".mvm", "target"};
            FileUtils.copyDirectory(sourcePath, destPath, FileTools.createExclusionFilter(exclusionDir));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void jar() {
        File sourcePath = new File(WebAppEnvs.get().projectNiucloudAddon + this.addon + "/target/" + this.addon + ".jar");
        if (!sourcePath.exists()) throw new CommonException("请先通过maven打包好jar包");

        try {
            File destPath = new File(this.addonPath + "jar/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }
            FileUtils.copyFile(sourcePath, new File(destPath, this.addon + ".jar"));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void resource() {
        try {
            File sourcePath = new File(WebAppEnvs.get().webRoot + "resource/addon/" + this.addon + "/");
            if (!sourcePath.exists()) return;

            File destPath = new File(this.addonPath + "resource/");
            if (destPath.exists()) {
                FileUtils.cleanDirectory(destPath);
            } else {
                FileTools.createDirs(destPath.getPath());
            }

            FileUtils.copyDirectory(sourcePath, destPath);
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void buildUniappPagesJson() {
        File mainPages = new File(WebAppEnvs.get().projectRoot + "/uni-app/src/pages.json");
        if (!mainPages.exists()) return;

        try {
            String content = FileUtils.readFileToString(mainPages, "UTF-8");

            Pattern pattern = Pattern.compile("//\\s*"+ (this.addon.toUpperCase() + "_PAGE_BEGA?IN") +"(.*?)//\\s*" + (this.addon.toUpperCase() + "_PAGE_END"), Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String pagesContent = matcher.group(1).trim();
                String packageDir = this.addonPath + "package/";
                FileTools.createDirs(packageDir);
                FileUtils.writeStringToFile(new File(packageDir, "uni-app-pages.txt"), pagesContent, "UTF-8");
            }
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void buildSiteUniappPagesJson() {
        File mainPages = new File(WebAppEnvs.get().projectRoot + "/site-uniapp/src/pages.json");
        if (!mainPages.exists()) return;

        try {
            String content = FileUtils.readFileToString(mainPages, "UTF-8");

            Pattern pattern = Pattern.compile("//\\s*"+ (this.addon.toUpperCase() + "_PAGE_BEGA?IN") +"(.*?)//\\s*" + (this.addon.toUpperCase() + "_PAGE_END"), Pattern.DOTALL);
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String pagesContent = matcher.group(1).trim();
                String packageDir = this.addonPath + "package/";
                FileTools.createDirs(packageDir);
                FileUtils.writeStringToFile(new File(packageDir, "site-uniapp-pages.txt"), pagesContent, "UTF-8");
            }
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void buildUniappLangJson() {
        try {
            File zh = new File(WebAppEnvs.get().projectRoot + "/uni-app/src/locale/zh-Hans.json");
            File en = new File(WebAppEnvs.get().projectRoot + "/uni-app/src/locale/en.json");
            JSONObject zhJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(zh));
            JSONObject enJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(en));

            File addonZh = new File(this.addonPath +"uni-app/locale/zh-Hans.json");
            File addonEn = new File(this.addonPath + "uni-app/locale/en.json");

            JSONObject newZhJson = new JSONObject();
            for (String key : zhJson.keySet()) {
                if (key.indexOf(this.addon + ".") == 0) {
                    newZhJson.put(key.replace(this.addon + ".", ""), zhJson.getStr(key));
                }
            }
            JSONObject newEnJson = new JSONObject();
            for (String key : enJson.keySet()) {
                if (key.indexOf(this.addon + ".") == 0) {
                    newEnJson.put(key.replace(this.addon + ".", ""), zhJson.getStr(key));
                }
            }

            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newZhJson), addonZh);
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newEnJson), addonEn);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void buildSiteUniappLangJson() {
        try {
            File zh = new File(WebAppEnvs.get().projectRoot + "/site-uniapp/src/locale/zh-Hans.json");
            File en = new File(WebAppEnvs.get().projectRoot + "/site-uniapp/src/locale/en.json");
            if (!zh.exists()) return;
            JSONObject zhJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(zh));
            JSONObject enJson = en.exists() ? JSONUtil.parseObj(JsonLoadUtils.loadJsonString(en)) : new JSONObject();

            File addonZh = new File(this.addonPath + "site-uniapp/locale/zh-Hans.json");
            File addonEn = new File(this.addonPath + "site-uniapp/locale/en.json");

            JSONObject newZhJson = new JSONObject();
            for (String key : zhJson.keySet()) {
                if (key.indexOf(this.addon + ".") == 0) {
                    newZhJson.put(key.replace(this.addon + ".", ""), zhJson.getStr(key));
                }
            }
            JSONObject newEnJson = new JSONObject();
            for (String key : enJson.keySet()) {
                if (key.indexOf(this.addon + ".") == 0) {
                    newEnJson.put(key.replace(this.addon + ".", ""), enJson.getStr(key));
                }
            }

            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newZhJson), addonZh);
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newEnJson), addonEn);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void menu(String appType) {
        String menuDir = this.addonPath + "menu/";
        FileTools.createDirs(menuDir);

        File menuFile = new File(menuDir, appType + ".json");

        SysMenu model = new SysMenu();
        model.setSource("system");
        sysMenuMapper.update(model, new QueryWrapper<SysMenu>().ne("source", "system").eq("app_type", appType).eq("addon", this.addon));

        List<SysMenu> menuList = sysMenuMapper.selectList(new QueryWrapper<SysMenu>()
                .select("menu_name,menu_key,menu_short_name,parent_key,menu_type,icon,api_url,router_path,view_path,methods,sort,status,is_show,support_addon,mount_key")
                .eq("app_type", appType)
                .eq("addon", this.addon)
                .orderByDesc("sort"));

        if (menuList.isEmpty()) {
            JSONObject emptyMenuJson = new JSONObject();
            emptyMenuJson.put("menu", new JSONArray());
            emptyMenuJson.put("delete", this.loadDeleteFromLoader(appType));
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(emptyMenuJson), menuFile);
            return;
        }

        JSONArray flatMenus = JSONUtil.parseArray(JacksonUtils.toSnakeCaseJSONString(menuList));
        JSONArray menuTree = this.addonMenuToTree(flatMenus);

        JSONObject menuJson = new JSONObject();
        menuJson.put("menu", menuTree);
        menuJson.put("delete", this.loadDeleteFromLoader(appType));

        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(menuJson), menuFile);

        File addonMenuDir = new File(WebAppEnvs.get().projectNiucloudAddon + this.addon + "/src/main/resources/" + addon + "/loader/menu/");
        if (addonMenuDir.exists()) {
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(menuJson), new File(addonMenuDir, appType + ".json"));
        }
    }

    /**
     * 插件菜单转树：parent_key 不在本插件 menu_key 集合内时视为顶层根（挂载在核心菜单下）
     */
    private JSONArray addonMenuToTree(JSONArray flatMenus) {
        Set<String> menuKeys = new HashSet<>();
        for (Object item : flatMenus) {
            String menuKey = ((JSONObject) item).getStr("menu_key");
            if (menuKey != null) {
                menuKeys.add(menuKey);
            }
        }

        Map<String, JSONObject> nodeMap = new LinkedHashMap<>();
        for (Object item : flatMenus) {
            JSONObject node = JSONUtil.parseObj(item);
            node.set("children", new JSONArray());
            nodeMap.put(node.getStr("menu_key"), node);
        }

        JSONArray roots = new JSONArray();
        for (JSONObject node : nodeMap.values()) {
            String parentKey = node.getStr("parent_key");
            if (parentKey == null || parentKey.isEmpty() || !menuKeys.contains(parentKey)) {
                roots.add(node);
            } else {
                JSONObject parent = nodeMap.get(parentKey);
                if (parent != null) {
                    parent.getJSONArray("children").add(node);
                } else {
                    roots.add(node);
                }
            }
        }

        this.pruneEmptyChildren(roots);
        return roots;
    }

    private void pruneEmptyChildren(JSONArray nodes) {
        for (Object item : nodes) {
            JSONObject node = (JSONObject) item;
            JSONArray children = node.getJSONArray("children");
            if (children != null && !children.isEmpty()) {
                this.pruneEmptyChildren(children);
            } else {
                node.remove("children");
            }
        }
    }

    private JSONArray loadDeleteFromLoader(String appType) {
        File loaderMenuFile = new File(WebAppEnvs.get().projectNiucloudAddon + this.addon + "/src/main/resources/" + this.addon + "/loader/menu/" + appType + ".json");
        if (!loaderMenuFile.exists()) {
            return new JSONArray();
        }
        JSONObject loaderJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(loaderMenuFile));
        JSONArray delete = loaderJson.getJSONArray("delete");
        return delete != null ? delete : new JSONArray();
    }

    private boolean isCircleAddonInstalled() {
        return addonMapper.selectCount(new QueryWrapper<Addon>().eq("`key`", "circle")) > 0;
    }

    private boolean hasCircleMenuLoader() {
        return new File(WebAppEnvs.get().projectNiucloudAddon + this.addon + "/src/main/resources/" + this.addon + "/loader/menu/circle.json").exists();
    }

    private void compressor() {
        String tempDir = WebAppEnvs.get().webRootDownResource + "temp/" + this.addon + "/" + this.addon;
        FileTools.createDirs(tempDir);

        try {
            FileUtils.copyDirectory(new File(this.addonPath), new File(tempDir));

            String sourceDir = WebAppEnvs.get().webRootDownResource + "temp/" + this.addon;
            String zipFile =  WebAppEnvs.get().webRootDownResource + "temp/" + this.addon + ".zip";

            ZipUtil.zip(sourceDir, zipFile);
            FileUtils.deleteDirectory(new File(sourceDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 下载插件
     *
     * @param key
     * @return
     */
    public String download(String key) {
        File file = new File(WebAppEnvs.get().webRootDownResource + "temp/" + key + ".zip");
        if (!file.exists()) throw new CommonException("请先打包插件");
        return file.getPath().replace(WebAppEnvs.get().projectRoot, "");
    }
}

