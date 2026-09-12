package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 操作日志实体
 */
@Data
//@TableName("sys_operation_log")
public class SysOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 操作人类型
     */
    private String mainType;

    /**
     * 操作人id
     */
    private Integer mainId;

    /**
     * 操作类型
     */
    private String type;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 请求参数JSON
     */
    private String paramJson;

    /**
     * 创建时间
     */
    private Long createTime;
}
