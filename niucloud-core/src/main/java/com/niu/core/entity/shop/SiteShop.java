package com.niu.core.entity.shop;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 店铺扩展表实体
 */
@Data
public class SiteShop implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /** 站点id */
    private Integer siteId;

    /** 是否推荐 */
    private Integer isRecommend;

    /** 是否自营 */
    private Integer isSelf;

    /** 佣金比率 */
    private BigDecimal serviceRate;

    /** 保证金 */
    private BigDecimal shopDeposit;

    /** 店铺关注数 */
    private Integer followNumber;

    /** 营业状态（0已打烊，1营业中） */
    private Integer businessStatus;

    /** 商城上架商品数 */
    private Integer mallUpNum;

    /** 商城商品销量 */
    private Integer mallSaleNum;

    /** 商品质量 */
    private BigDecimal qualityScore;

    /** 商家服务 */
    private BigDecimal serviceScore;

    /** 发货物流 */
    private BigDecimal logisticsScore;

    /** 评价次数 */
    private Integer evaluateNum;

    /** h5商户背景图 */
    private String backgroundH5;

    /** 商户类目id */
    private Integer categoryId;

    /** 商圈id */
    private Integer circleId;

    /** 子级行业类目(不涉及系统业务) */
    private Integer childCategoryId;

    /** 店铺关键词 */
    private String keywords;
}
