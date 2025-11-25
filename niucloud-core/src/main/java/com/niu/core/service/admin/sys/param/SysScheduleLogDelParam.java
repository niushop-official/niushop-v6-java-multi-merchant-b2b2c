package com.niu.core.service.admin.sys.param;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统任务参数
 */
@Data
public class SysScheduleLogDelParam implements Serializable {

    private Object ids;


    private Integer scheduleId;
}
