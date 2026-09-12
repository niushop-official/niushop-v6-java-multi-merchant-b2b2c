package com.niu.core.service.admin.upgrade.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data
public class UpgradeTaskVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String envType;

    private String upgradeTime;

    private String addon;

    private String key;

    private Map<String, Object> upgrade;

    private String step;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private LinkedList<String> executed = new LinkedList<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private LinkedList<String> log = new LinkedList<>();

    private Map<String, Object> params;

    private UpgradeContentVo upgradeContent;

    private String status;

    /**
     * 对齐 PHP：error 仅在有错误时才存在于返回结果中，
     * 空列表不序列化，避免前端 if (data.error) 误判为有错误而清除计时器。
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private LinkedList<String> error = new LinkedList<>();

    private String version;

    private String upgradeVersion;

    private LinkedHashMap<String, Step> steps = new LinkedHashMap<>();


    public UpgradeTaskVo() {
        this.steps = new LinkedHashMap<>();
        steps.put("requestUpgrade", new Step("requestUpgrade", "请求升级"));
        steps.put("downloadFile", new Step("downloadFile", "下载更新文件"));
        steps.put("backupCode", new Step("backupCode", "备份源码"));
        steps.put("backupSql", new Step("backupSql", "备份数据库"));
        steps.put("coverCode", new Step("coverCode", "合并更新文件"));
        steps.put("handleUpgrade", new Step("coverCode", "执行升级sql和升级方法"));
        steps.put("handleVue", new Step("handleVue", "处理前端源码"));
        steps.put("refreshMenu", new Step("refreshMenu", "刷新菜单"));
        steps.put("installSchedule", new Step("installSchedule", "安装计划任务"));
        steps.put("cloudBuild", new Step("cloudBuild", "开始云编译"));
        steps.put("gteCloudBuildLog", new Step("gteCloudBuildLog", ""));
        steps.put("upgradeComplete", new Step("upgradeComplete", "升级完成"));
    }

    @Data
    @AllArgsConstructor
    public static class Step {
        private String step;

        private String title;
    }

    public List<String> getUpgradeApps() {
        return upgradeContent == null ? new LinkedList<>() : upgradeContent.getUpgradeApps();
    }
}
