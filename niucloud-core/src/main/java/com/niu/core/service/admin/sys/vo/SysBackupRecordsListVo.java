package com.niu.core.service.admin.sys.vo;

import lombok.Data;

@Data
public class SysBackupRecordsListVo {
    /**
     * 备份记录id
     */
    private Integer id;
    /**
     * 备份版本号
     */
    private String version;
    /**
     * 备份标识
     */
    private String backupKey;

    private String backupDir;
    /**
     * 备份内容
     */
    private Object content;
    /**
     * 状态
     */
    private String status;

    private String statusName;
    /**
     * 失败原因
     */
    private String failReason;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 完成时间
     */
    private String completeTime;
}
