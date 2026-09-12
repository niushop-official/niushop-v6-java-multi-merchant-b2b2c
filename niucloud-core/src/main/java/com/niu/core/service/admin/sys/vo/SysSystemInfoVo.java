package com.niu.core.service.admin.sys.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 系统环境信息 VO（对齐 PHP SystemService::getSystemInfo）
 */
@Data
public class SysSystemInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 服务器信息：服务器系统 / 运行环境 / Java版本
     */
    private List<ServerItem> server;

    /**
     * 系统环境要求：Java版本 / MySQL版本
     */
    private List<ServerVersionItem> serverVersion;

    /**
     * 环境权限检测：Redis / Quartz / 目录权限 等
     */
    private List<VariableItem> systemVariables;

    /**
     * 服务器信息条目（server 列表元素）
     */
    @Data
    public static class ServerItem implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 名称，如 "服务器系统"、"运行环境" */
        private String name;
        /** 值，如 "Linux"、"Spring Boot / 3.4.1 + Tomcat" */
        private String server;

        public ServerItem() {}

        public ServerItem(String name, String server) {
            this.name = name;
            this.server = server;
        }
    }

    /**
     * 系统环境要求条目（server_version 列表元素）
     */
    @Data
    public static class ServerVersionItem implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 名称，如 "Java版本"、"MySQL版本" */
        private String name;
        /** 要求，如 "大于等于 17.0" */
        private String demand;
        /** 实际值，如 "17.0.10" */
        private String server;

        public ServerVersionItem() {}

        public ServerVersionItem(String name, String demand, String server) {
            this.name = name;
            this.demand = demand;
            this.server = server;
        }
    }

    /**
     * 环境权限检测条目（system_variables 列表元素）
     */
    @Data
    public static class VariableItem implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 名称，如 "Redis 缓存"、"Quartz 计划任务"、"/runtime" */
        private String name;
        /** 要求，如 "需开启"、"需可读写" */
        private String need;
        /** 状态，true 表示通过 */
        private boolean status;
        /** 可选：目录权限检测时记录实际路径 */
        private String path;

        public VariableItem() {}

        public VariableItem(String name, String need, boolean status) {
            this.name = name;
            this.need = need;
            this.status = status;
        }

        public VariableItem(String name, String need, boolean status, String path) {
            this.name = name;
            this.need = need;
            this.status = status;
            this.path = path;
        }
    }
}
