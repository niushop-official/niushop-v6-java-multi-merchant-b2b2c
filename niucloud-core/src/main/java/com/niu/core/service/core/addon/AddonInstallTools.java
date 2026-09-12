package com.niu.core.service.core.addon;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.common.utils.json.JsonLoadUtils;
import com.niu.core.common.utils.json.JsonModuleLoader;
import com.niu.core.entity.addon.Addon;
import com.niu.core.enums.addon.AddonStatusEnum;
import com.niu.core.mapper.addon.AddonMapper;
import jakarta.annotation.Resource;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import cn.hutool.core.io.FileUtil;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class AddonInstallTools extends CoreAddonBaseService {

    @Resource
    AddonMapper addonMapper;

    @Setter
    private String addon;

    /**
     * 安装插件代码
     */
    public void installDir(String addon) {
        this.addon = addon;
        if (new File(WebAppEnvs.get().projectRoot + "/admin").exists()) this.mergeAdmin(WebAppEnvs.get().projectRoot + "/admin/src/");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "admin").exists()) this.mergeAdmin(WebAppEnvs.get().webRootDownRuntime + "admin/src/");
        if (new File(WebAppEnvs.get().projectRoot + "/uni-app").exists()) this.mergeUniapp(WebAppEnvs.get().projectRoot + "/uni-app/src/addon/");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "uni-app").exists()) this.mergeUniapp(WebAppEnvs.get().webRootDownRuntime + "uni-app/src/addon/");
        if (new File(WebAppEnvs.get().projectRoot + "/site-uniapp").exists()) this.mergeSiteUniapp(WebAppEnvs.get().projectRoot + "/site-uniapp/src/addon/");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "site-uniapp").exists()) this.mergeSiteUniapp(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/src/addon/");
        if (new File(WebAppEnvs.get().projectRoot + "/web").exists()) this.mergeWeb(WebAppEnvs.get().projectRoot + "/web/");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "web").exists()) this.mergeWeb(WebAppEnvs.get().webRootDownRuntime + "web/");
        if (new File(WebAppEnvs.get().projectNiucloudAddon).exists()) this.mergeJava();
        this.mergeJar();
        this.mergeResource();
    }

    /**
     * 安装插件vue源码
     *
     * @param addon 插件
     */
    public void installVue(String addon) {
        this.addon = addon;
        if (new File(WebAppEnvs.get().projectRoot + "/admin").exists()) this.mergeAdmin(WebAppEnvs.get().projectRoot + "/admin/src/addon/");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "admin").exists()) this.mergeAdmin(WebAppEnvs.get().webRootDownRuntime + "admin/src/addon/");
        if (new File(WebAppEnvs.get().projectRoot + "/uni-app").exists()) this.mergeUniapp(WebAppEnvs.get().projectRoot + "/uni-app/src/addon/");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "uni-app").exists()) this.mergeUniapp(WebAppEnvs.get().webRootDownRuntime + "uni-app/src/addon/");
        if (new File(WebAppEnvs.get().projectRoot + "/site-uniapp").exists()) this.mergeUniapp(WebAppEnvs.get().projectRoot + "/site-uniapp/src/addon/");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "site-uniapp").exists()) this.mergeUniapp(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/src/addon/");
        if (new File(WebAppEnvs.get().projectRoot + "/web").exists()) this.mergeWeb(WebAppEnvs.get().projectRoot + "/web/addon/");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "web").exists()) this.mergeWeb(WebAppEnvs.get().webRootDownRuntime + "web/addon/");
    }

    /**
     * 卸载目录
     */
    public void uninstallDir(String addon) {
        this.addon = addon;
        if (new File(WebAppEnvs.get().projectRoot + "/admin/src/addon/" + this.addon + "/").exists()) this.removeAdmin(WebAppEnvs.get().projectRoot + "/admin/");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "admin/src/addon/" + this.addon + "/").exists()) this.removeAdmin(WebAppEnvs.get().webRootDownRuntime + "admin/");
        if (new File(WebAppEnvs.get().projectRoot + "/uni-app/src/addon/" + this.addon + "/").exists()) this.removeUniapp(WebAppEnvs.get().projectRoot + "/uni-app/");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "uni-app/src/addon/" + this.addon + "/").exists()) this.removeUniapp(WebAppEnvs.get().webRootDownRuntime + "uni-app/");
        if (new File(WebAppEnvs.get().projectRoot + "/site-uniapp/src/addon/" + this.addon + "/").exists()) this.removeSiteUniapp(WebAppEnvs.get().projectRoot + "/site-uniapp/");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/src/addon/" + this.addon + "/").exists()) this.removeSiteUniapp(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/");
        if (new File(WebAppEnvs.get().projectRoot + "/web/addon/" + this.addon + "/").exists()) this.removeWeb(WebAppEnvs.get().projectRoot + "/web/");
        if (new File(WebAppEnvs.get().projectNiucloudAddon + this.addon + "/").exists()) this.removeJava();
        if (new File(WebAppEnvs.get().webRootDownJar + "addon/" + this.addon + ".jar").exists()) this.removeJar();
        if (new File(WebAppEnvs.get().webRoot + "resource/addon/" + this.addon + "/").exists()) this.removeResource();
    }

    /**
     * 合并依赖
     */
    public void installDepend(String addon) {
        this.addon = addon;
        // 合并admin端依赖
        if (new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/package/admin-package.json").exists()) {
            if (new File(WebAppEnvs.get().projectRoot + "/admin/").exists()) this.mergeAdminDepend(WebAppEnvs.get().projectRoot + "/admin/");
            if (new File(WebAppEnvs.get().webRootDownRuntime + "admin/").exists()) this.mergeAdminDepend(WebAppEnvs.get().webRootDownRuntime + "admin/");
        }
        // 合并uniapp端依赖
        if (new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/package/uni-app-package.json").exists()) {
            if (new File(WebAppEnvs.get().projectRoot + "/uni-app/").exists()) this.mergeUniappDepend(WebAppEnvs.get().projectRoot + "/uni-app/");
            if (new File(WebAppEnvs.get().webRootDownRuntime + "uni-app/").exists()) this.mergeUniappDepend(WebAppEnvs.get().webRootDownRuntime + "uni-app/");
        }
        // 合并site-uniapp端依赖
        if (new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/package/site-uniapp-package.json").exists()) {
            if (new File(WebAppEnvs.get().projectRoot + "/site-uniapp/").exists()) this.mergeSiteUniappDepend(WebAppEnvs.get().projectRoot + "/site-uniapp/");
            if (new File(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/").exists()) this.mergeSiteUniappDepend(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/");
        }
        // 合并web端依赖
        if (new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/package/web-package.json").exists()) {
            if (new File(WebAppEnvs.get().projectRoot + "/web/").exists()) this.mergeWebDepend(WebAppEnvs.get().projectRoot + "/web/");
            if (new File(WebAppEnvs.get().webRootDownRuntime + "web/").exists()) this.mergeWebDepend(WebAppEnvs.get().webRootDownRuntime + "web/");
        }
    }

    /**
     * 处理uniapp
     * @param addon 插件
     */
    public void installUniapp(String addon) {
        this.addon = addon;

        List<Addon> addonList = addonMapper.selectList(new MPJQueryWrapper<Addon>().select("`key`").eq("status", AddonStatusEnum.ON.getCode()));

        List<String> addons = new LinkedList<>();
        for (Addon item : addonList) {
            addons.add(item.getKey());
        }
        addons.add(this.addon);

        // 处理uniapp pages.json
        if (new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/package/uni-app-pages.txt").exists()) {
            if (new File(WebAppEnvs.get().projectRoot + "/uni-app/").exists()) this.handlePagesJson(WebAppEnvs.get().projectRoot + "/uni-app/", addons);
            if (new File(WebAppEnvs.get().webRootDownRuntime + "uni-app/").exists()) this.handlePagesJson(WebAppEnvs.get().webRootDownRuntime + "uni-app/", addons);
        }

        // 处理uniapp组件
        if (new File(WebAppEnvs.get().projectRoot + "/uni-app/").exists()) this.handleUniappComponent(WebAppEnvs.get().projectRoot + "/uni-app/", addons);
        if (new File(WebAppEnvs.get().webRootDownRuntime + "uni-app/").exists()) this.handleUniappComponent(WebAppEnvs.get().webRootDownRuntime + "uni-app/", addons);

        // 处理uniapp语言包
        if (new File(WebAppEnvs.get().projectRoot + "/uni-app/").exists()) this.mergeUniappLocale(WebAppEnvs.get().projectRoot + "/uni-app/", "install");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "uni-app/").exists()) this.mergeUniappLocale(WebAppEnvs.get().webRootDownRuntime + "uni-app/", "install");

        // 处理site-uniapp pages.json
        if (new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/package/site-uniapp-pages.txt").exists()) {
            if (new File(WebAppEnvs.get().projectRoot + "/site-uniapp/").exists()) this.handleSiteUniappPagesJson(WebAppEnvs.get().projectRoot + "/site-uniapp/", addons);
            if (new File(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/").exists()) this.handleSiteUniappPagesJson(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/", addons);
        }

        // 处理site-uniapp语言包
        if (new File(WebAppEnvs.get().projectRoot + "/site-uniapp/").exists()) this.mergeSiteUniappLocale(WebAppEnvs.get().projectRoot + "/site-uniapp/", "install");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/").exists()) this.mergeSiteUniappLocale(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/", "install");
    }

    /**
     * 卸载插件后处理uniapp
     * @param addon 插件
     */
    public void uninstallUniapp(String addon) {
        this.addon = addon;

        List<Addon> addonList = addonMapper.selectList(new MPJQueryWrapper<Addon>().select("`key`").eq("status", AddonStatusEnum.ON.getCode()));

        List<String> addons = new LinkedList<>();
        for (Addon item : addonList) {
            if (this.addon.equals(item.getKey())) continue;
            addons.add(item.getKey());
        }

        // 处理uniapp pages.json
        if (new File(WebAppEnvs.get().projectRoot + "/uni-app/").exists()) this.handlePagesJson(WebAppEnvs.get().projectRoot + "/uni-app/", addons);
        if (new File(WebAppEnvs.get().webRootDownRuntime + "uni-app/").exists()) this.handlePagesJson(WebAppEnvs.get().webRootDownRuntime + "uni-app/", addons);

        // 处理uniapp组件
        if (new File(WebAppEnvs.get().projectRoot + "/uni-app/").exists()) this.handleUniappComponent(WebAppEnvs.get().projectRoot + "/uni-app/", addons);
        if (new File(WebAppEnvs.get().webRootDownRuntime + "uni-app/").exists()) this.handleUniappComponent(WebAppEnvs.get().webRootDownRuntime + "uni-app/", addons);

        // 处理uniapp语言包
        if (new File(WebAppEnvs.get().projectRoot + "/uni-app/").exists()) this.mergeUniappLocale(WebAppEnvs.get().projectRoot + "/uni-app/", "uninstall");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "uni-app/").exists()) this.mergeUniappLocale(WebAppEnvs.get().webRootDownRuntime + "uni-app/", "uninstall");

        // 处理site-uniapp pages.json
        if (new File(WebAppEnvs.get().projectRoot + "/site-uniapp/").exists()) this.handleSiteUniappPagesJson(WebAppEnvs.get().projectRoot + "/site-uniapp/", addons);
        if (new File(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/").exists()) this.handleSiteUniappPagesJson(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/", addons);

        // 处理site-uniapp语言包
        if (new File(WebAppEnvs.get().projectRoot + "/site-uniapp/").exists()) this.mergeSiteUniappLocale(WebAppEnvs.get().projectRoot + "/site-uniapp/", "uninstall");
        if (new File(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/").exists()) this.mergeSiteUniappLocale(WebAppEnvs.get().webRootDownRuntime + "site-uniapp/", "uninstall");
    }

    /**
     * 合并admin文件
     * @param dir admin文件夹
     */
    private void mergeAdmin(String dir) {
        try {
            // 判断目的文件是否存在
            FileTools.createDirs(dir + "addon/" + this.addon + "/");

            File source = new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/admin/");
            if (!source.exists()) return;

            FileUtils.copyDirectory(source, new File(dir + "addon/" + this.addon + "/"));

            // 判断图标目录是否存在
            File iconDir = new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/admin/icon/");
            if (iconDir.exists()) {
                File iconDestDir = new File(dir + "styles/icon/addon/" + this.addon);
                FileTools.createDirs(iconDestDir.getPath());
                FileUtils.copyDirectory(iconDir, iconDestDir);

                compileAdminIcon(dir);
            }
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void compileAdminIcon(String dir) {
        File addonIconDir = new File(dir + "styles/icon/addon/");
        List<String> content = new LinkedList<>();
        content.add("/* addon iconfont */");

        if (!addonIconDir.exists() && Objects.requireNonNull(addonIconDir.listFiles()).length > 0) {
            Arrays.stream(Objects.requireNonNull(addonIconDir.listFiles()))
                    .forEach(file -> content.add("@import \"addon/"+ file.getName() +"/iconfont.css\";"));
        }
        FileUtil.writeUtf8String(String.join("\n", content), new File(dir + "style/icon/addon-iconfont.css"));
    }

    private void removeAdmin(String dir) {
        try {
            FileUtils.deleteDirectory(new File(dir + "src/addon/" + this.addon + "/"));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 合并uniapp文件
     */
    private void mergeUniapp(String dir) {
        try {
            // 判断目的文件是否存在
            FileTools.createDirs(dir + this.addon + "/");

            File source = new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/uni-app/");
            if (!source.exists()) return;

            FileUtils.cleanDirectory(new File(dir + this.addon + "/"));
            FileUtils.copyDirectory(source, new File(dir + this.addon + "/"));

        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void removeUniapp(String dir) {
        try {
            FileUtils.deleteDirectory(new File(dir + "src/addon/" + this.addon + "/"));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 合并site-uniapp文件（商家管理端）
     */
    private void mergeSiteUniapp(String dir) {
        try {
            FileTools.createDirs(dir + this.addon + "/");

            File source = new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/site-uniapp/");
            if (!source.exists()) return;

            FileUtils.cleanDirectory(new File(dir + this.addon + "/"));
            FileUtils.copyDirectory(source, new File(dir + this.addon + "/"));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void removeSiteUniapp(String dir) {
        try {
            FileUtils.deleteDirectory(new File(dir + "src/addon/" + this.addon + "/"));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 合并web文件
     */
    private void mergeWeb(String dir) {
        try {
            // 判断目的文件是否存在
            FileTools.createDirs(dir + "addon/" + this.addon + "/");

            String source = WebAppEnvs.get().webRootDownAddon + this.addon + "/web/";
            if (!new File(source).exists()) return;

            FileUtils.cleanDirectory(new File(dir + "addon/" + this.addon + "/"));
            FileUtils.copyDirectory(new File(source), new File(dir + "addon/" + this.addon + "/"));

            File layoutFile = new File(source + "layouts/");
            if (layoutFile.exists()) {
                FileTools.createDirs(dir + "layouts/" + this.addon);
                FileUtils.copyDirectory(layoutFile, new File(dir + "layouts/" + this.addon));
            }
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 移除web端
     * @param dir web文件夹
     */
    private void removeWeb(String dir) {
        try {
            FileUtils.deleteDirectory(new File(dir + "/addon/" + this.addon + "/"));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    private void mergeResource() {
        try {
            File source = new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/resource/");
            if (!source.exists()) return;

            String dest = WebAppEnvs.get().webRoot + "resource/addon/" + this.addon + "/";
            FileTools.createDirs(dest);

            FileUtils.copyDirectory(source, new File(dest));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 移除resource
     */
    private void removeResource() {
        try {
            FileUtils.deleteDirectory(new File(WebAppEnvs.get().webRoot + "resource/addon/" + this.addon + "/"));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 合并jar
     */
    private void mergeJar() {
        try {
            File source = new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/jar/");
            if (!source.exists()) return;

            String dest = WebAppEnvs.get().webRootDownJar + "addon/";
            FileTools.createDirs(dest);

            FileUtils.copyDirectory(source, new File(dest));

            if (!new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/java/pom.xml").exists() && new File(WebAppEnvs.get().projectNiucloudAddon).exists()) {
                FileUtils.copyDirectory(source, new File(WebAppEnvs.get().projectNiucloudAddon));
            }
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 移除jar
     */
    private void removeJar() {
        try {
            new File(WebAppEnvs.get().webRootDownJar + "addon/" + this.addon + ".jar").deleteOnExit();
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 合并java文件
     */
    private void mergeJava() {
        try {
            File source = new File(WebAppEnvs.get().webRootDownAddon + this.addon + "/java/");
            if (!source.exists()) return;

            String dest = WebAppEnvs.get().projectNiucloudAddon + this.addon + "/";
            FileTools.createDirs(dest);

            FileUtils.copyDirectory(source, new File(dest));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 移除java目录
     */
    private void removeJava() {
        try {
            FileUtils.deleteDirectory(new File(WebAppEnvs.get().projectNiucloudAddon + this.addon + "/"));
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 合并admin端依赖
     * @param dir admin文件夹
     */
    private void mergeAdminDepend(String dir) {
        String addonPackage = WebAppEnvs.get().webRootDownAddon + this.addon + "/package/admin-package.json";
        String mainPackage = dir + "package.json";

        JSONObject original = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(new File(mainPackage)));
        JSONObject news = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(new File(addonPackage)));

        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(JsonModuleLoader.deepMerge(original, news)), new File(mainPackage));
    }

    /**
     * 合并uniapp端依赖
     * @param dir uniapp     * @param dir
     */
    private void mergeUniappDepend(String dir) {
        String addonPackage = WebAppEnvs.get().webRootDownAddon + this.addon + "/package/uni-app-package.json";
        String mainPackage = dir + "package.json";

        JSONObject original = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(new File(mainPackage)));
        JSONObject news = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(new File(addonPackage)));

        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(JsonModuleLoader.deepMerge(original, news)), new File(mainPackage));
    }

    /**
     * 合并web端依赖
     * @param dir web文件夹
     */
    private void mergeWebDepend(String dir) {
        String addonPackage = WebAppEnvs.get().webRootDownAddon + this.addon + "/package/web-package.json";
        String mainPackage = dir + "package.json";

        JSONObject original = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(new File(mainPackage)));
        JSONObject news = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(new File(addonPackage)));

        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(JsonModuleLoader.deepMerge(original, news)), new File(mainPackage));
    }

    /**
     * 合并pages.json
     * @param dir 文件夹
     */
    public void handlePagesJson(String dir, List<String> addonList) {
        this.buildPagesJson(dir, addonList, "uni-app-pages.txt");
    }

    /**
     * 合并 pages.json（uni-app 与 site-uniapp 共用，仅 pages 文件不同）
     * @param pagesFileName 插件包中的路由文件名，uni-app 为 uni-app-pages.txt，site-uniapp 为 site-uniapp-pages.txt
     */
    private void buildPagesJson(String dir, List<String> addonList, String pagesFileName) {
        try {
            File mainPages = new File(dir + "src/pages.json");
            String content = FileUtils.readFileToString(mainPages, "UTF-8");
            StringBuilder addon = new StringBuilder();

            for (String key : addonList) {
                String upperKey = key.toUpperCase();
                String beginMarker = "// " + upperKey + "_PAGE_BEGIN";
                String endMarker = "// " + upperKey + "_PAGE_END";
                // 兼容历史拼写：部分插件起始标记为 _PAGE_BEGAIN，正则 BEGA?IN 同时匹配 BEGIN / BEGAIN
                String beginRegex = "// " + Pattern.quote(upperKey) + "_PAGE_BEGA?IN";
                String endRegex = "// " + Pattern.quote(upperKey) + "_PAGE_END";
                File addonPages = new File(WebAppEnvs.get().webRootDownAddon + key + "/package/" + pagesFileName);

                if (addonPages.exists()) {
                    // pages.txt 存在，使用其内容
                    String pageContent = FileUtils.readFileToString(addonPages, "UTF-8");
                    // 替换 {{addon_name}} 为实际插件名（与 PHP 一致）
                    pageContent = pageContent.replace("{{addon_name}}", key).trim();
                    // 逗号兜底：保证每个插件块以逗号结尾，避免块之间缺少分隔（对齐 PHP preg_replace 补充逗号）
                    if (!pageContent.isEmpty() && !pageContent.endsWith(",")) {
                        pageContent = pageContent + ",";
                    }
                    addon.append("\t\t").append(beginMarker).append("\n")
                         .append(pageContent).append("\n")
                         .append("\t\t").append(endMarker).append("\n");
                } else {
                    // pages.txt 不存在但 pages.json 中已有该插件路由块，从原内容中提取保留，避免路由丢失
                    Pattern blockPattern = Pattern.compile(beginRegex + "[\\s\\S]*?" + endRegex);
                    Matcher blockMatcher = blockPattern.matcher(content);
                    if (blockMatcher.find()) {
                        addon.append("\t\t").append(blockMatcher.group()).append("\n");
                    }
                }
                // pages.txt 与已存在路由块都没有则跳过
            }

            Pattern pattern = Pattern.compile("// \\{\\{ PAGE_BEGAIN }}[\\s\\S]*?// \\{\\{ PAGE_END }}");
            Matcher matcher = pattern.matcher(content);
            // 使用 quoteReplacement 防止路由 JSON 中的 $、\ 被当作替换符/转义符
            String replacement = Matcher.quoteReplacement(
                "// {{ PAGE_BEGAIN }}\n" + addon + "\t// {{ PAGE_END }}");
            content = matcher.replaceAll(replacement);

            FileUtils.writeStringToFile(mainPages, content, "UTF-8");
        } catch (IOException e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     * 合并uniapp 组件
     * @param dir 文件夹
     */
    public void handleUniappComponent(String dir, List<String> addonList) {
        try {
            File diyGroup = new File(WebAppEnvs.get().webRoot + "resource/static/tmpl/diy-group.tpl");
            File fixedGroup = new File(WebAppEnvs.get().webRoot + "resource/static/tmpl/fixed-group.tpl");

            String diyGroupContent = FileUtils.readFileToString(diyGroup, "UTF-8");
            String fixedGroupContent = FileUtils.readFileToString(fixedGroup, "UTF-8");

            LinkedList<String> diyGroupImport = new LinkedList<>();
            LinkedList<String> fixedGroupImport = new LinkedList<>();
            LinkedList<String> diyGroupComponent = new LinkedList<>();
            LinkedList<String> fixedGroupComponent = new LinkedList<>();

            IOFileFilter vueFilter = new SuffixFileFilter("index.vue");
            IOFileFilter allFilesInVueDirs = FileFilterUtils.and(vueFilter, TrueFileFilter.INSTANCE);

            for(String key: addonList) {
                File diyComponentDir = new File(dir + "src/addon/"+ key +"/components/diy/");
                if (diyComponentDir.exists()) {
                    Collection<File> diyComponent = FileUtils.listFiles(diyComponentDir, allFilesInVueDirs, TrueFileFilter.INSTANCE);
                    for (File file : diyComponent) {
                        String name = new File(file.getParent()).getName();
                        String fileName = "diy-" + name;
                        String componentName = Arrays.stream(name.split("-")).map(s -> {
                            if (s.isEmpty()) return s;
                            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
                        }).collect(Collectors.joining(""));

                        String component = "            <template v-if=\"component.componentName == '" + componentName + "'\">\n";
                        component += "                <"+ fileName +" :component=\"component\" :global=\"data.global\" :index=\"index\" :scrollBool=\"diyGroup.componentsScrollBool."+ componentName +"\" @update:componentIsShow=\"component.componentIsShow = $event\" />\n";
                        component += "            </template>";

                        diyGroupComponent.add(component);
                        diyGroupImport.add("   import diy"+ componentName +" from '@/addon/" + key + "/components/diy/" + name + "/index.vue';");
                    }
                }

                File fixedComponentDir = new File(dir + "src/addon/"+ key +"/components/fixed/");
                if (fixedComponentDir.exists()) {
                    Collection<File> fixedComponent = FileUtils.listFiles(fixedComponentDir, allFilesInVueDirs, TrueFileFilter.INSTANCE);

                    for (File file : fixedComponent) {
                        String name = new File(file.getParent()).getName();
                        String fileName = "fixed-" + name;
                        String componentName = Arrays.stream(name.split("-")).map(s -> {
                            if (s.isEmpty()) return s;
                            return Character.toUpperCase(s.charAt(0)) + s.substring(1);
                        }).collect(Collectors.joining(""));

                        String component = "            <template v-if=\"props.data.global.component == '" + componentName + "'\">\n";
                        component += "                <"+ fileName +" :data=\"props.data\" :pullDownRefreshCount=\"props.pullDownRefreshCount\" />\n";
                        component += "            </template>";

                        diyGroupComponent.add(component);
                        diyGroupImport.add("   import fixed"+ componentName +" from '@/addon/" + key + "/components/fixed/" + name + "/index.vue';");
                    }
                }
            }

            diyGroupContent = diyGroupContent.replace("{{ component }}", String.join("\n", diyGroupComponent)).replace("{{ componentImport }}", String.join("\n", diyGroupImport));
            fixedGroupContent = fixedGroupContent.replace("{{ component }}", String.join("\n", fixedGroupComponent)).replace("{{ componentImport }}", String.join("\n", fixedGroupImport));

            FileUtils.writeStringToFile(new File(dir + "src/addon/components/diy/group/index.vue"), diyGroupContent, "UTF-8");
            FileUtils.writeStringToFile(new File(dir + "src/addon/components/fixed/group/index.vue"), fixedGroupContent, "UTF-8");
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    /**
     *
     * @param dir 文件夹
     * @param mode 模式
     */
    public void mergeUniappLocale(String dir, String mode) {
        try {
            File zh = new File(dir + "src/locale/zh-Hans.json");
            File en = new File(dir + "src/locale/en.json");
            JSONObject zhJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(zh));
            JSONObject enJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(en));

            File addonZh = new File(dir + "src/addon/"+ this.addon +"/locale/zh-Hans.json");
            File addonEn = new File(dir + "src/addon/"+ this.addon +"/locale/en.json");

            if (mode.equals("install")) {
                if (addonZh.exists()) {
                    JSONObject addonZhJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(addonZh));
                    for (String key : addonZhJson.keySet()) {
                        zhJson.set(this.addon + "." + key, addonZhJson.getStr(key));
                    }
                    JSONObject addonEnJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(addonEn));
                    for (String key : addonEnJson.keySet()) {
                        enJson.set(this.addon + "." + key, addonEnJson.getStr(key));
                    }
                }
            } else {
                JSONObject newZhJson = new JSONObject();
                for (String key : zhJson.keySet()) {
                    if (!key.contains(this.addon + ".")) {
                        newZhJson.set(key, zhJson.getStr(key));
                    }
                }
                zhJson = newZhJson;
                JSONObject newEnJson = new JSONObject();
                for (String key : enJson.keySet()) {
                    if (!key.contains(this.addon + ".")) {
                        newEnJson.set(key, newEnJson.getStr(key));
                    }
                }
                enJson = newEnJson;
            }

            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(zhJson), zh);
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(enJson), en);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    public void mergeUniappManifest(String dir, JSONObject data) {
        try {
            File manifest = new File(dir + "src/manifest.json");
            JSONObject manifestJson = JSONUtil.parseObj(removeComments(FileUtil.readUtf8String(manifest)));
            manifestJson = JsonModuleLoader.deepMerge(manifestJson, data);
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(manifestJson), manifest);
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }

    private String removeComments(String json) {
        // 匹配多行注释的正则表达式
        Pattern pattern = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(json);
        // 将匹配到的注释替换为空字符串
        return matcher.replaceAll("");
    }

    /**
     * 合并site-uniapp端依赖
     */
    private void mergeSiteUniappDepend(String dir) {
        String addonPackage = WebAppEnvs.get().webRootDownAddon + this.addon + "/package/site-uniapp-package.json";
        String mainPackage = dir + "package.json";

        JSONObject original = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(new File(mainPackage)));
        JSONObject news = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(new File(addonPackage)));

        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(JsonModuleLoader.deepMerge(original, news)), new File(mainPackage));
    }

    /**
     * 合并site-uniapp pages.json（复用 uniapp 逻辑，仅 pages 文件不同）
     */
    public void handleSiteUniappPagesJson(String dir, List<String> addonList) {
        this.buildPagesJson(dir, addonList, "site-uniapp-pages.txt");
    }

    /**
     * 合并site-uniapp语言包
     */
    public void mergeSiteUniappLocale(String dir, String mode) {
        try {
            File zh = new File(dir + "src/locale/zh-Hans.json");
            File en = new File(dir + "src/locale/en.json");
            if (!zh.exists()) return;
            JSONObject zhJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(zh));
            JSONObject enJson = en.exists() ? JSONUtil.parseObj(JsonLoadUtils.loadJsonString(en)) : new JSONObject();

            File addonZh = new File(dir + "src/addon/" + this.addon + "/locale/zh-Hans.json");
            File addonEn = new File(dir + "src/addon/" + this.addon + "/locale/en.json");

            if ("install".equals(mode)) {
                if (addonZh.exists()) {
                    JSONObject addonZhJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(addonZh));
                    for (String key : addonZhJson.keySet()) {
                        zhJson.set(this.addon + "." + key, addonZhJson.getStr(key));
                    }
                }
                if (addonEn.exists()) {
                    JSONObject addonEnJson = JSONUtil.parseObj(JsonLoadUtils.loadJsonString(addonEn));
                    for (String key : addonEnJson.keySet()) {
                        enJson.set(this.addon + "." + key, addonEnJson.getStr(key));
                    }
                }
            } else {
                JSONObject newZhJson = new JSONObject();
                for (String key : zhJson.keySet()) {
                    if (!key.contains(this.addon + ".")) {
                        newZhJson.set(key, zhJson.getStr(key));
                    }
                }
                zhJson = newZhJson;
                JSONObject newEnJson = new JSONObject();
                for (String key : enJson.keySet()) {
                    if (!key.contains(this.addon + ".")) {
                        newEnJson.set(key, enJson.getStr(key));
                    }
                }
                enJson = newEnJson;
            }

            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(zhJson), zh);
            if (en.exists()) {
                FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(enJson), en);
            }
        } catch (Exception e) {
            throw new CommonException(e.getMessage());
        }
    }
}


