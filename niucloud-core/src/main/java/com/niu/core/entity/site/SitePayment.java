package com.niu.core.entity.site;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;

import java.util.Map;

@Data
public class SitePayment {
    private static final long serialVersionUID = 1L;
    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.AUTO) // 数据库主键字段，自增
    private Long id;

    /**
     * 站点 ID
     */
    @TableField("site_id")
    private Integer siteId;

    /**
     * 聚合支付类型
     */
    @TableField("type")
    private String type;

    /**
     * 状态: 启用/禁用 (on/off)
     */
    @TableField("status")
    private String status;

    /**
     * 进件信息（JSON格式）
     */
    @TableField("infos")
    @JsonRawValue  // 标记原始 JSON 值
    private Object infos;

    /**
     * 创建时间（时间戳）
     */
    @TableField("create_time")
    private Integer createTime;

    /**
     * 扩展编号
     */
    @TableField("extend_no")
    private String extendNo;

    /**
     * 是否结算（0:否, 1:是）
     */
    @TableField("is_settle")
    private Integer isSettle;

    /**
     * 取现编号
     */
    @TableField("withdrawal_no")
    private String withdrawalNo;

    /**
     * 结算开通情况描述
     */
    @TableField("settle_desc")
    private String settleDesc;




}
