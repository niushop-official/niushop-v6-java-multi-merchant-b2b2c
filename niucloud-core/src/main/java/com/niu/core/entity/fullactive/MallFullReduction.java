package com.niu.core.entity.fullactive;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 满减活动实体
 */
@Data
public class MallFullReduction implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    /** 活动ID */
    private Integer id;

    /**
     * 站点ID
     */
    private Integer siteId;

    /**
     * 活动名称
     */
    private String title;

    /** 业务应用标识，例如 front_warehouse。 */
    private String addon;

    /**
     * 活动类型：amount/num
     */
    private String type;

    /**
     * discount 折扣 / reduction 减价
     */
    private String promotionType;

    /**
     * 活动状态
     */
    private String status;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 适用范围类型
     */
    private String goodsType;

    /**
     * 规则类型
     */
    private String ruleType;

    /**
     * 会员规则
     */
    private String memberType;

    /**
     * 会员规则内容
     */
    private String memberTypeJson;

    /**
     * 规则配置JSON
     */
    private String ruleJson;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 更新时间
     */
    private Long updateTime;

}
