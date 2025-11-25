package com.niu.mall.service.admin.goods.param;

import lombok.Data;

@Data
public class BuyGoodsSelectParam {
    private String keyword;
    private String goodsCategory;
    private String goodsType;
    private Integer memberId;
}
