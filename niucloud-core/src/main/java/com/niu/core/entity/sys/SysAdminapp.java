package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户手机管理端偏好设置实体
 */
@Data
public class SysAdminapp implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 用户ID */
    private Integer uid;

    /** 站点ID */
    private Integer siteId;

    /** 设置项类型 app应用 stat统计 todo待办事项 */
    private String type;

    /** 配置数据（JSON格式） */
    private String value;

    /** 添加时间 */
    private Long createTime;

    /** 更新时间 */
    private Long updateTime;
}
