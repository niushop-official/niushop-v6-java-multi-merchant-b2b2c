package com.niu.mall.service.core.goods.helper;

import com.niu.core.entity.goods.MallGoods;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.service.core.goods.ICoreGoodsActivePriceService;
import com.niu.mall.service.core.goods.param.GetShowPriceParam;
import com.niu.mall.service.core.goods.vo.ShowPriceVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 海报商品展示价辅助
 */
@Component
public class PosterShowPriceHelper {

    @Resource
    private ICoreGoodsActivePriceService coreGoodsActivePriceService;

    public String formatGoodsPrice(MallGoodsSku sku, MallGoods goods, Integer memberId) {
        return "￥" + formatPriceValue(resolveShowPrice(sku, goods, memberId));
    }

    public BigDecimal resolveShowPrice(MallGoodsSku sku, MallGoods goods, Integer memberId) {
        if (memberId == null || memberId <= 0) {
            return sku.getSalePrice() != null ? sku.getSalePrice() : sku.getPrice();
        }
        ShowPriceVo showPrice = coreGoodsActivePriceService.getShowPrice(GetShowPriceParam.builder()
                .goodsId(sku.getGoodsId())
                .skuId(sku.getSkuId())
                .price(sku.getPrice())
                .salePrice(sku.getSalePrice())
                .memberDiscount(goods.getMemberDiscount())
                .vipCardDiscount(goods.getVipCardDiscount())
                .memberPrice(sku.getMemberPrice())
                .svipDiscount(sku.getSvipDiscount())
                .svipPrice(sku.getSvipPrice())
                .memberId(memberId)
                .build());
        return showPrice.getShowPrice();
    }

    private String formatPriceValue(BigDecimal price) {
        if (price == null) {
            return "0";
        }
        return price.stripTrailingZeros().toPlainString();
    }
}
