package com.niu.core.service.api.shop.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
* 站点分类详情
*/
@Data
public class SiteShopCategoryInfoVo implements Serializable {

private static final long serialVersionUID = 1L;


    /** 分类id */
    private Integer categoryId;


    /** 分类名称 */
    private String categoryName;


    /** 手续费比率 */
    private BigDecimal commissionRate;


    /** 添加时间 */
    private Long createTime;


    /** 分类图 */
    private String image;


    /** 排序号 */
    private Integer sort;


    /** 更新时间 */
    private Long updateTime;

}