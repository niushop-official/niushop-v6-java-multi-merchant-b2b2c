package com.niu.mall.listener.marketing;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.utils.StringUtils;
import com.niu.mall.entity.active.MallActive;
import com.niu.mall.entity.active.MallActiveGoods;
import com.niu.mall.enums.active.ActiveClassEnum;
import com.niu.mall.enums.active.ActiveStatusEnum;
import com.niu.mall.enums.goods.GoodsPriceTypeEnum;
import com.niu.mall.event.marketing.GoodsMarketCalculateEventDefiner;
import com.niu.mall.mapper.active.MallActiveGoodsMapper;
import com.niu.mall.mapper.active.MallActiveMapper;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@EventCallback("mall")
@Component
public class DiscountCalculateListener extends GoodsMarketCalculateEventDefiner {

    @Resource
    MallActiveGoodsMapper mallActiveGoodsMapper;
    @Resource
    MallActiveMapper mallActiveMapper;

    @Override
    public GoodsMarketCalculateResult handleCallback(GoodsMarketCalculateEvent event) {
        OrderCreateDataVo vo = event.getVo();
        OrderCreateDataVo.Sku skuInfo = event.getSkuInfo();
        GoodsMarketCalculateResult result = new GoodsMarketCalculateResult();
        if (skuInfo.getGoods().getIsDiscount().equals(1) && vo.getOrderType().equals("mall")) {
            MallActiveGoods discountGoods = mallActiveGoodsMapper.selectOne(new MPJQueryWrapper<MallActiveGoods>()
                    .setAlias("g").select("g.*")
                    .innerJoin("?_mall_active a ON g.active_id = a.active_id ".replace("?_", GlobalConfig.tablePrefix))
                    .eq("g.goods_id", skuInfo.getGoodsId())
                    .eq("a.active_status", ActiveStatusEnum.ACTIVE.getStatus())
                    .last("limit 1")
            );
            //查询限时活动
            MallActive active = mallActiveMapper.selectById(discountGoods.getActiveId());
            JSONArray activeGoodsValue = StringUtils.isNotEmpty(discountGoods.getActiveGoodsValue()) ? JSONUtil.parseArray(discountGoods.getActiveGoodsValue())  : new JSONArray();
            Map<Integer, JSONObject> map = new HashMap<>();
            if(!activeGoodsValue.isEmpty()){
                for (int i = 0; i < activeGoodsValue.size(); i++) {
                    JSONObject jsonObject = activeGoodsValue.getJSONObject(i);
                    map.put(jsonObject.getInt("sku_id"), jsonObject);
                }
                if (map.containsKey(skuInfo.getSkuId())){
                    JSONObject jsonObject = map.get(skuInfo.getSkuId());
                    if(jsonObject.getInt("is_enabled") != null && jsonObject.getInt("is_enabled") > 0){
                        BigDecimal comparison = (skuInfo.getPrice().subtract(skuInfo.getSalePrice())).max(BigDecimal.ZERO);
                        if (comparison.compareTo(BigDecimal.ZERO) > 0){
                            skuInfo.setPrice(skuInfo.getSalePrice());
                            skuInfo.setShowType(GoodsPriceTypeEnum.DISCOUNT_PRICE.getType());
                        }
                        skuInfo.setGoodsMoney((skuInfo.getPrice().multiply(new BigDecimal(skuInfo.getNum()))).setScale(2, RoundingMode.HALF_UP));
                        if (GoodsPriceTypeEnum.DISCOUNT_PRICE.getType().equals(skuInfo.getShowType())){
                            OrderCreateDataVo.Discount discount = new OrderCreateDataVo.Discount();
                            discount.setMatchGoodsIds(new Integer[]{ skuInfo.getSkuId() });
                            discount.setNum(skuInfo.getNum());
                            discount.setType("discount");
                            discount.setMoney(skuInfo.getOriginalPrice().subtract(skuInfo.getPrice()));
                            discount.setDiscountType("discount");
                            discount.setDiscountTypeId(discountGoods.getActiveId());
                            discount.setTitle("限时折扣");
                            discount.setContent(ObjectUtil.isNotEmpty(active.getActiveDesc()) ? active.getActiveDesc() :"");
                            vo.getDiscount().put("discount", discount);
                        }
                        result.setSkuInfo(skuInfo);
                        return result;
                    }
                }
            }
        }
        return result;
    }
}
