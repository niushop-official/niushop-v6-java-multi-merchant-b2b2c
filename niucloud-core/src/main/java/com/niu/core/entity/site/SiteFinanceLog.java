package com.niu.core.entity.site;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SiteFinanceLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发生主体id
     */
    @TableField("main_id")
    private Integer mainId;

    /**
     * 类型
     */
    @TableField("type")
    private String type;

    /**
     * 发生主体名称
     */
    @TableField("main_name")
    private String mainName;

    /**
     * 站点id
     */
    @TableField("site_id")
    private Integer siteId;

    /**
     * 变动账单金额
     */
    @TableField("value")
    private BigDecimal value;

    /**
     * 账户类型
     */
    @TableField("value_type")
    private String valueType;

    /**
     * 业务类型
     */
    @TableField("trade_type")
    private String tradeType;

    /**
     * 业务id
     */
    @TableField("trade_id")
    private String tradeId;

    /**
     * 业务单号
     */
    @TableField("trade_no")
    private String tradeNo;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 备注信息
     */
    @TableField("memo")
    private String memo;

    /**
     * 收支类型
     */
    @TableField("rae_type")
    private String raeType;
}
