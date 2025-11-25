package com.niu.mall.service.site.supply_goods.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.niu.core.common.domain.BeanJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ShopGoods视图
 */
@Data
public class MallSupplyGoodsInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer goodsId;  // 商品id
    private Integer siteId;  // 站点id
    private String goodsName;  // 商品名称
    private String goodsType;  // 商品类型
    private String subTitle;  // 副标题
    private String goodsCover;  // 商品封面
    private String goodsImage;  // 商品图片
    private String goodsDesc;  // 商品介绍
    private Integer brandId;  // 商品品牌id
    private String serviceIds;  // 商品服务
    private String unit;  // 单位
    private Integer stock;  // 商品库存（总和）
    private Integer saleNum;  // 销量
    private Integer virtualSaleNum;  // 虚拟销量
    private Integer status;  // 商品状态（1.正常0下架）
    private Integer sort;  // 排序
    private String deliveryType;  // 支持的配送方式
    private Integer isFreeShipping;  // 是否免邮
    private String feeType;  // 运费设置，选择模板：template，固定运费：fixed
    private BigDecimal deliveryMoney;  // 固定运费
    private Integer deliveryTemplateId;  // 运费模板

    /** 商品商城分类 */
    private Integer goodsMallCategory;

    /** 审核状态 */
    private Integer auditStatus;

    /** 创建时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long createTime;

    /** 更新时间 */
    @JsonSerialize(using = BeanJsonSerializer.LongDateToStringSerializer.class)
    private Long updateTime;

    private String goodsTypeName;
    private String goodsEditPath;
    private String goodsCoverThumbSmall;
    private String statusName;

    private SiteInfo site;

    @Data
    public static class SiteInfo{
        private String siteName;
        private Integer siteId;
    }
}
