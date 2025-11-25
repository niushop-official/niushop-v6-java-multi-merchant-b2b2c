package com.niu.mall.service.site.goods.vo;

import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.entity.goods.MallGoodsSku;
import com.niu.mall.service.admin.goods.vo.MallManjianRuleContentVo;
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
