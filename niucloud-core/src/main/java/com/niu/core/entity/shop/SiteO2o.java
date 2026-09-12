package com.niu.core.entity.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * O2O站点业务表 实体类
 */
@Data
//@TableName("ns_site_o2o")
public class SiteO2o {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 系统站点ID（关联ns_site）
     */
    private Integer siteId;

    /**
     * 主体类型 1个人 2个体 3企业
     */
    private Integer businessType;

    /**
     * 营业状态（0已打烊，1营业中）
     */
    private Integer businessStatus;

    /**
     * 佣金比率
     */
    private BigDecimal serviceRate;

    /**
     * 法人姓名
     */
    private String idCardName;

    /**
     * 身份证正面
     */
    private String idCardFront;

    /**
     * 身份证反面
     */
    private String idCardBack;

    /**
     * 创建时间（时间戳，Long类型）
     */
    private Long createTime;

    /**
     * 更新时间（时间戳，Long类型）
     */
    private Long updateTime;

    /**
     * 主营一级行业ID
     */
    private Integer categoryId;

    /**
     * 主营二级行业ID（多个用逗号分隔）
     */
    private String childCategoryId;

    /**
     * 店铺评分（0.00~5.00）
     */
    private BigDecimal siteScore;

    /**
     * 人均消费
     */
    private BigDecimal perCapita;

    /**
     * 设施服务，逗号分隔
     */
    private String facilityService;

    /**
     * 店铺关键词，用于搜索
     */
    private String keywords;

    /**
     * 评价次数
     */
    private Integer evaluateNum;

    /**
     * 门头照，多张用逗号分隔
     */
    private String storePhoto;

    /**
     * 销量
     */
    private Integer saleNum;

    /**
     * 商圈id
     */
    private Integer circleId;
}