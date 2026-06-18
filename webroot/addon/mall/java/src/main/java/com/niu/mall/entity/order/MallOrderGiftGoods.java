package com.niu.mall.entity.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单赠品项表
 */
@Data
public class MallOrderGiftGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_gift_id", type = IdType.AUTO)
    /** 赠品项ID */
    private Integer orderGiftId;
    /** 站点ID */
    private Integer siteId;
    /** 订单ID */
    private Integer orderId;
    /** 会员ID */
    private Integer memberId;
    /** 商品ID */
    private Integer goodsId;
    /** SKU ID */
    private Integer skuId;
    /** 商品名称 */
    private String goodsName;
    /** 规格名称 */
    private String skuName;
    /** 商品图片 */
    private String goodsImage;
    /** 规格图片 */
    private String skuImage;
    /** 商品单价 */
    private BigDecimal price;
    /** 购买数量 */
    private Integer num;
    /** 商品总价 */
    private BigDecimal goodsMoney;
    /** 商品类型 */
    private String goodsType;
    /** 商品原价 */
    private BigDecimal originalPrice;
    /** 供货商商品ID */
    private Integer supplyGoodsId;
    /** 供货商ID */
    private Integer supplyId;
    /** 供货商SKU ID */
    private Integer supplySkuId;
    /** 商品成本总额 */
    private BigDecimal costGoodsMoney;
}
