package com.niu.core.service.admin.sys.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.component.context.queue.AsyncTask;
import com.niu.core.common.component.context.queue.AsyncTaskManager;
import com.niu.core.common.component.context.queue.AsyncTaskQueueManager;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.file.FileTools;
import com.niu.core.service.core.app.ICoreQueueCheckService;
import com.niu.core.service.admin.sys.ISystemService;
import com.niu.core.service.admin.sys.vo.SysQrcodeVo;
import com.niu.core.service.admin.sys.vo.SysSystemInfoVo;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.common.component.job.quartz.QuartzManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class SystemServiceImpl implements ISystemService {


    @Resource
    private Cached cached;

    @Resource
    private ICoreConfigService coreConfigService;

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取系统环境信息（对齐 PHP SystemService::getSystemInfo）
     * 返回 server、server_version、system_variables，不返回 process（Java 无 php think queue:listen 进程模型）
     */
    @Override
    public SysSystemInfoVo getSystemInfo() {
        SysSystemInfoVo result = new SysSystemInfoVo();

        // 1. 服务器信息：对齐 PHP $server
        List<SysSystemInfoVo.ServerItem> server = new ArrayList<>();
        server.add(new SysSystemInfoVo.ServerItem("服务器系统", System.getProperty("os.name", "Unknown")));

        String runtimeEnv;
        try {
            runtimeEnv = "Spring Boot / " + (org.springframework.boot.SpringBootVersion.getVersion() != null
                    ? org.springframework.boot.SpringBootVersion.getVersion() : "Unknown");
            String tomcatHome = System.getProperty("catalina.home");
            if (tomcatHome != null) runtimeEnv += " + Tomcat";
        } catch (Exception e) {
            runtimeEnv = "Spring Boot";
        }
        server.add(new SysSystemInfoVo.ServerItem("运行环境", runtimeEnv));
        server.add(new SysSystemInfoVo.ServerItem("Java版本", System.getProperty("java.version", "Unknown")));
        result.setServer(server);

        // 2. 系统环境要求：对齐 PHP $server_version
        List<SysSystemInfoVo.ServerVersionItem> serverVersion = new ArrayList<>();
        serverVersion.add(new SysSystemInfoVo.ServerVersionItem("Java版本", "大于等于 17.0",
                System.getProperty("java.version", "Unknown")));
        serverVersion.add(new SysSystemInfoVo.ServerVersionItem("MySQL版本", "大于等于 5.7", getMysqlVersion()));
        result.setServerVersion(serverVersion);

        // 3. 环境权限检测：对齐 PHP $system_variables
        // PHP 中 pdo/curl/openssl/GD/fileinfo 等是 PHP 扩展，Java 无此概念；
        // Java 检测关键目录的读写权限（runtime、upload），以及 Redis/Quartz 是否可用
        List<SysSystemInfoVo.VariableItem> systemVariables = new ArrayList<>();
        systemVariables.add(new SysSystemInfoVo.VariableItem("Redis 缓存", "需开启", cached != null));
        systemVariables.add(new SysSystemInfoVo.VariableItem("Quartz 计划任务", "需开启", checkQuartzAvailable()));

        String runtimeDir = WebAppEnvs.get().webRootDownRuntime;
        File runtime = new File(runtimeDir);
        boolean runtimeOk = (runtime.exists() || runtime.mkdirs()) && runtime.canRead() && runtime.canWrite();
        systemVariables.add(new SysSystemInfoVo.VariableItem("/runtime", "需可读写", runtimeOk, runtimeDir));

        String uploadDir = WebAppEnvs.get().webRoot + "upload";
        File upload = new File(uploadDir);
        boolean uploadOk = (upload.exists() || upload.mkdirs()) && upload.canRead() && upload.canWrite();
        systemVariables.add(new SysSystemInfoVo.VariableItem("/upload", "需可读写", uploadOk, uploadDir));
        result.setSystemVariables(systemVariables);

        return result;
    }

    private String getMysqlVersion() {
        try {
            String version = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            return version != null ? version : "Unknown";
        } catch (Exception e) {
            log.warn("获取MySQL版本失败: {}", e.getMessage());
            return "Unknown";
        }
    }

    private boolean checkQuartzAvailable() {
        try {
            if (QuartzManager.schedulerFactory() == null) return false;
            org.quartz.Scheduler scheduler = QuartzManager.schedulerFactory().getScheduler();
            return scheduler.isStarted() && !scheduler.isShutdown();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void clearCache() {
        cached.getAllKeys().stream().forEach(key -> cached.remove(key));;
    }

    @Override
    public boolean checkJob() {
        String secret = UUID.randomUUID().toString().replace("-", "");
        String runtimeDir = WebAppEnvs.get().webRootDownRuntime;
        FileTools.createDirs(runtimeDir);
        String file = runtimeDir + secret + ".job";
        try {
            AsyncTask asyncTask = AsyncTaskManager.build(ICoreQueueCheckService.class)
                    .setMethodName("markQueueCheckFile")
                    .addMethodParameter("file", file);
            if (!AsyncTaskQueueManager.delayExecute(asyncTask)) {
                return false;
            }
        } catch (Exception e) {
            log.error("校验消息队列异常", e);
            return false;
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        File marker = new File(file);
        if (marker.exists()) {
            FileTools.deleteFile(file);
            return true;
        }
        return false;
    }

    @Override
    public SysQrcodeVo getQrcode(String folder, String page, Map<String, String> allParams) {
        SysQrcodeVo result = new SysQrcodeVo();
        result.setH5Path("");
        result.setWeappPath("");
        Integer siteId = RequestUtils.siteId();
        List<Map<String,Object>> paramList = new ArrayList<>();

        if(!allParams.isEmpty() && containsKeyWithParam(allParams,"params")){
            //提取数字
            List<String> params = allParams.keySet().stream().filter(param -> param.contains("params")).map(param -> param.substring(7,8)).distinct().toList();
            params.forEach(param -> {
                Map<String,Object> paramMap = new HashMap<>();
                paramMap.put("key",containsKeyText(allParams,param,"column_name"));
                paramMap.put("value",containsKeyText(allParams,param,"column_value"));
                paramList.add(paramMap);
            });
        }
        if(!paramList.isEmpty()){
            String dir = "upload/"+siteId+"/"+ folder+"_qrcode";
            try {
                String path = QrcodeUtils.qrcodeToFile(RequestUtils.siteId(),"weapp", "", page, paramList.get(0), dir);
                result.setWeappPath(path);
            } catch (Exception e) {
                log.error("获取推广微信小程序二维码error: {}, 错误文件: {}, 错误行号: {} , params: {}" , e.getMessage() ,e.getStackTrace()[0].getFileName() ,e.getStackTrace()[0].getLineNumber() , JSONUtil.parse(paramList.get(0)).toString());
                throw new AdminException("小程序未配置完善");
            }
        }
        return result;
    }

    public static boolean containsKeyWithParam(Map<String, String> map, String param) {
        for (String key : map.keySet()) {
            if (key.contains(param)) {
                return true;
            }
        }
        return false;
    }

    public static String containsKeyText(Map<String, String> map, String index, String keyText) {
        for (String key : map.keySet()) {
            if (key.contains(index) && key.contains(keyText)) {
                return map.get(key);
            }
        }
        return "";
    }

    /**
     * 校验计划任务是否正常运行
     */
    @Override
    public boolean checkSchedule() {
        try {
            if (QuartzManager.schedulerFactory() == null) {
                return false;
            }
            org.quartz.Scheduler scheduler = QuartzManager.schedulerFactory().getScheduler();
            return scheduler.isStarted() && !scheduler.isShutdown();
        } catch (Exception e) {
            log.error("校验计划任务异常", e);
            return false;
        }
    }

    /**
     * 清理表缓存
     */
    @Override
    public void schemaCache() {
        // Java 项目使用 MyBatis-Plus，无 PHP 的 schema 缓存目录
        // 清理 Redis 缓存中与数据库表结构相关的缓存键
        cached.getAllKeys().stream()
                .filter(key -> key.contains("schema") || key.contains("table") || key.contains("column"))
                .forEach(key -> cached.remove(key));
    }

    /**
     * 获取环境变量
     */
    @Override
    public Map<String, Object> getEnvInfo() {
        Map<String, Object> result = new HashMap<>();
        result.put("app_debug", Boolean.parseBoolean(System.getProperty("app.debug", "false")));
        result.put("java_version", System.getProperty("java.version"));
        result.put("os_name", System.getProperty("os.name"));
        result.put("spring_profiles_active", System.getProperty("spring.profiles.active", ""));
        return result;
    }

    /**
     * 获取布局列表
     * 对应 PHP: event('SiteLayout')
     */
    @Override
    public JSONArray getLayoutList() {
        // Java 项目中布局通过 LAYOUT_SETTING 配置项管理，不像 PHP 有事件机制
        // 返回当前布局配置
        JSONArray layouts = new JSONArray();
        JSONObject currentLayout = coreConfigService.getConfigValue(RequestUtils.siteId(), "LAYOUT_SETTING");
        if (currentLayout != null && !currentLayout.isEmpty()) {
            JSONObject item = new JSONObject();
            item.set("key", currentLayout.getStr("key", ""));
            item.set("value", currentLayout.getStr("value", ""));
            item.set("sort", 1);
            layouts.add(item);
        }
        return layouts;
    }

    /**
     * 设置布局
     * 对应 PHP: SystemService.setLayout(key)
     */
    @Override
    public Boolean setLayout(String key) {
        JSONObject config = new JSONObject();
        config.set("key", key);
        config.set("value", key);
        coreConfigService.setConfig(RequestUtils.siteId(), "LAYOUT_SETTING", config);
        return true;
    }
}
