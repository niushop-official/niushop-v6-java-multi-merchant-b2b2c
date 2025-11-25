package com.niu.mall.service.core.goods.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MallGoodsStatVo {
    private Integer siteId;
    private Integer goodsId;
    private int saleNum;
    private int evaluateNum;
    private int cartNum;
    private int goodsVisitMemberCount;
    private int collectNum;
    private int refundNum;
    private int payNum;
    private int accessNum;
    private BigDecimal payMoney;
    private BigDecimal refundMoney;
}
