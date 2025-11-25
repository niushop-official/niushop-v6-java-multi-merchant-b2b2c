package com.niu.mall.service.admin.goods.vo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 商城商铺商品信息
 */
@Data
public class MallSupplyGoodsInfoVo {

    private Integer goodsId;  // 商品id
    private String goodsName;  // 商品名称
    private Integer status;  // 商品状态（1.正常0下架）
    private Integer supplyGoodsId;
    private Integer supplyStatus;

}
