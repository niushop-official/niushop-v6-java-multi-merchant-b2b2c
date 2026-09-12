package com.niu.mall.service.admin.goods.vo;

import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetBuyGoodsSelectedVo extends MallGoodsSku {
    private String goodsCoverThumbSmall;
    private String goodsCoverThumbMid;
    private Integer hasBuy;
    private MallGoods goods;
    private MallManjianRuleContentVo manjianInfo;
}
