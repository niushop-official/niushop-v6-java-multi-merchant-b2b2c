package com.niu.mall.service.core.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderGoodsRefundEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.service.core.goods.ICoreGoodsLimitBuyService;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CoreGoodsLimitBuyServiceImpl implements ICoreGoodsLimitBuyService {

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Override
    public String checkGoodsLimitBuy(Integer memberId, OrderCreateDataVo vo) {
        Map<String, OrderCreateDataVo.LimitBuy> limitList = getGoodsLimit(vo);
        for (OrderCreateDataVo.LimitBuy item : limitList.values()) {
            if (item.getMinBuy() != null && item.getMinBuy() > 0) {
                if (item.getStock() < item.getMinBuy()) {
                    String error = "商品【" + item.getGoodsName() + "】" + "库存数量小于起购数量";
                    setGoodsError(error, vo, item.getSkuIds());
                }
                if (item.getNum() < item.getMinBuy()) {
                    String error = "商品【" + item.getGoodsName() + "】" + item.getMinBuy() + "件起购";
                    setGoodsError(error, vo, item.getSkuIds());
                }
            }
            if (item.getIsLimit() != null && item.getMaxBuy() != null) {
                if (item.getIsLimit() == 1 && item.getMaxBuy() > 0) {
                    if (item.getLimitType() == 1) { // 单次限购
                        if (item.getNum() > item.getMaxBuy()) {
                            String error = "商品【" + item.getGoodsName() + "】" + "单次限购" + item.getMaxBuy() + "件";
                            setGoodsError(error, vo, item.getSkuIds());
                        }
                    } else if (item.getLimitType() == 2) { // 长期限购
                        int sum = getGoodsHasBuyNumber(item.getSiteId(), memberId, item.getGoodsId());
                        if (sum + item.getNum() > item.getMaxBuy()) {
                            String error = "商品【" + item.getGoodsName() + "】" + "每人限购" + item.getMaxBuy() + "件" + (sum > 0 ? "，您已购买" + sum + "件" : "");
                            setGoodsError(error, vo, item.getSkuIds());
                        }
                    }
                }
            }
        }
        return "";
    }

    @Override
    public Integer getGoodsHasBuyNumber(Integer siteId, Integer memberId, Integer goodsId) {

        MPJQueryWrapper<MallOrderGoods> queryWrapper = new MPJQueryWrapper<>();

        //sql语句
        queryWrapper.select("IFNULL(SUM(nsog.num), 0) as sum_num")
                .setAlias("nsog")
                .innerJoin("?_mall_order nso ON nsog.order_id = nso.order_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("nso.site_id", siteId)
                .eq("nsog.member_id", memberId)
                .eq("nsog.goods_id", goodsId)
                .ne("nso.status", OrderStatusEnum.CLOSE.getStatus())
                .ne("nsog.status", OrderGoodsRefundEnum.REFUND_FINISH.getStatus());
        return mallOrderGoodsMapper.selectObjs(queryWrapper).stream()
                .map(o -> new BigDecimal(o.toString()).intValue())
                .findFirst().orElse(0);
    }

    public Map<String, OrderCreateDataVo.LimitBuy> getGoodsLimit(OrderCreateDataVo vo) {
        if (vo.getLimitBuy() == null) {
            vo.setLimitBuy(new HashMap<>());
        }
        if (!vo.getLimitBuy().isEmpty()) {
            return vo.getLimitBuy();
        }
        for (OrderCreateDataVo.Sku value : vo.getGoodsData().values()) {
            Integer goodsId = value.getGoodsId();
            String goodsKey = String.valueOf(goodsId);
            String mergeKey = "goods_" + goodsId;
            int num = ObjectUtil.defaultIfNull(value.getNum(), 0);
            int skuId = value.getSkuId();

            if (vo.getLimitBuy().containsKey(goodsKey) || vo.getLimitBuy().containsKey(mergeKey)) {
                OrderCreateDataVo.LimitBuy limitBuy = vo.getLimitBuy().get(mergeKey);
                if (limitBuy == null) {
                    limitBuy = vo.getLimitBuy().remove(goodsKey);
                }
                limitBuy.setNum(ObjectUtil.defaultIfNull(limitBuy.getNum(), 0) + num);
                List<Integer> skuIds = new ArrayList<>(ObjectUtil.defaultIfNull(limitBuy.getSkuIds(), new ArrayList<>()));
                if (!skuIds.contains(skuId)) {
                    skuIds.add(skuId);
                }
                limitBuy.setSkuIds(skuIds);
                vo.getLimitBuy().put(mergeKey, limitBuy);
            } else {
                OrderCreateDataVo.LimitBuy limitBuy = new OrderCreateDataVo.LimitBuy();
                limitBuy.setGoodsId(goodsId);
                limitBuy.setSiteId(value.getSiteId());
                limitBuy.setGoodsName(value.getGoods().getGoodsName());
                limitBuy.setStock(value.getStock());
                limitBuy.setNum(num);
                limitBuy.setIsLimit(value.getGoods().getIsLimit());
                limitBuy.setLimitType(value.getGoods().getLimitType());
                limitBuy.setMaxBuy(value.getMaxBuy());
                limitBuy.setMinBuy(value.getMinBuy());
                limitBuy.setSkuIds(new ArrayList<>(List.of(skuId)));
                vo.getLimitBuy().put(goodsKey, limitBuy);
            }
        }
        return vo.getLimitBuy();
    }

    public void setGoodsError(String error, OrderCreateDataVo vo, List<Integer> skuIds) {
        for (Integer key : vo.getGoodsData().keySet()) {
            Integer skuId = vo.getGoodsData().get(key).getSkuId();
            if (skuIds.contains(skuId)) {
                vo.getGoodsData().get(key).setError(error);
            }
        }
        vo.getError().add(error);
    }
}
