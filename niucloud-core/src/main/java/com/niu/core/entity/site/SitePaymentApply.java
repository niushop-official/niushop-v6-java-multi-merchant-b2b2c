package com.niu.core.entity.site;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.Data;

import java.util.Map;

@Data
public class SitePaymentApply {
    private static final long serialVersionUID = 1L;
    /**
     * 主键 id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 站点 ID
     */
    @TableField("site_id")
    private Integer siteId;

    /**
     * 支付类型（如：alipay、wechat、huifu）
     */
    private String type;

    /**
     * 申请状态：草稿 / 待审核 / 已通过 / 失败 / 取消
     */
    private String status;

    /**
     * 进件信息（JSON格式解析为Map）
     */
    @TableField("infos")
    @JsonRawValue  // 标记原始 JSON 值
    private Object infos;

    /**
     * 创建时间（时间戳）
     */
    private Integer createTime;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 审核时间（时间戳）
     */
    private Integer auditTime;

    /**
     * 申请时间（时间戳）
     */
    private Integer applyTime;

    /**
     * 扩展编号
     */
    private String extendNo;

} 