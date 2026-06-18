package com.niu.core.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

/**
 * 导出报表实体
 */
@Data
public class SysExport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 站点ID */
    private Integer siteId;

    /** 主题关键字 */
    private String exportKey;

    /** 导出数据数量 */
    private Integer exportNum;

    /** 文件存储路径 */
    private String filePath;

    /** 文件大小 */
    private String fileSize;

    /** 导出状态 */
    private Integer exportStatus;

    /** 导出成功数量 */
    private Integer successNum;

    /** 导出失败数量 */
    private Integer failNum;

    /** 失败原因 */
    private String failReason;

    /** 导出查询条件 */
    private String searchCondition;

    /** 导出数据列 */
    private String dataColumn;

    /** 导出时间 */
    private Long createTime;

}