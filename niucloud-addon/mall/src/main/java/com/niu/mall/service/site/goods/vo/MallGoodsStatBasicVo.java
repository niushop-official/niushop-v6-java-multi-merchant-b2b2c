package com.niu.mall.service.site.goods.vo;

import lombok.Data;

@Data
public class MallGoodsStatBasicVo {
    private int accessGoodNum;
    private int saleGoodNum;
    private int cartNum;
    private int saleNum;
    private int payNum;
    private int payMoney;
    private int refundNum;
    private int refundMoney;
    private int accessNum;
    private int collectNum;
    private int evaluateNum;
    private int goodsVisitMemberCount;
}
