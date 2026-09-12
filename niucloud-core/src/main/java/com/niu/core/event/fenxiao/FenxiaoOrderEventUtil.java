package com.niu.core.event.fenxiao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 构建分销订单事件工具
 */
public final class FenxiaoOrderEventUtil {

    private FenxiaoOrderEventUtil() {
    }

    /**
     * 构建通用订单数据
     *
     * @param siteId     站点ID
     * @param orderId    订单ID
     * @param orderNo    订单编号
     * @param memberId   会员ID
     * @param orderMoney 订单金额
     * @return FenxiaoOrderData
     */
    public static FenxiaoOrderData buildOrderData(Integer siteId, Integer orderId, String orderNo,
                                                  Integer memberId, BigDecimal orderMoney) {
        FenxiaoOrderData data = new FenxiaoOrderData();
        data.setSiteId(siteId);
        data.setOrderId(orderId);
        data.setOrderNo(orderNo);
        data.setMemberId(memberId);
        data.setOrderMoney(orderMoney);
        return data;
    }

    /**
     * 构建通用订单项数据
     *
     * @param orderGoodsId    订单项ID
     * @param goodsId         商品ID
     * @param skuId           规格ID
     * @param num             数量
     * @param orderGoodsMoney 订单项金额
     * @param supplyOrderId   供应订单ID
     * @param costGoodsMoney  成本金额
     * @return FenxiaoOrderGoodsItem
     */
    public static FenxiaoOrderGoodsItem buildGoodsItem(Integer orderGoodsId, Integer goodsId, Integer skuId,
                                                       Integer num, BigDecimal orderGoodsMoney,
                                                       Integer supplyOrderId, BigDecimal costGoodsMoney) {
        FenxiaoOrderGoodsItem item = new FenxiaoOrderGoodsItem();
        item.setOrderGoodsId(orderGoodsId);
        item.setGoodsId(goodsId);
        item.setSkuId(skuId);
        item.setNum(num);
        item.setOrderGoodsMoney(orderGoodsMoney);
        item.setSupplyOrderId(supplyOrderId);
        item.setCostGoodsMoney(costGoodsMoney);
        return item;
    }

    /**
     * 构建分销订单创建事件
     *
     * @param tradeType  业务类型
     * @param orderData  订单数据
     * @param goodsItems 订单项列表
     * @return FenxiaoOrderCreateEvent
     */
    public static FenxiaoOrderCreateEvent buildCreateEvent(String tradeType, FenxiaoOrderData orderData,
                                                           List<FenxiaoOrderGoodsItem> goodsItems) {
        FenxiaoOrderCreateEvent event = new FenxiaoOrderCreateEvent();
        event.setTradeType(tradeType);
        event.setOrderData(orderData);
        event.setOrderGoods(goodsItems != null ? goodsItems : new ArrayList<>());
        if (orderData != null && orderData.getSiteId() != null) {
            event.setSiteId(orderData.getSiteId());
        }
        return event;
    }

    /**
     * 构建分销订单结算事件
     *
     * @param tradeType 业务类型
     * @param orderData 订单数据
     * @return FenxiaoOrderSettlementEvent
     */
    public static FenxiaoOrderSettlementEvent buildSettlementEvent(String tradeType, FenxiaoOrderData orderData) {
        FenxiaoOrderSettlementEvent event = new FenxiaoOrderSettlementEvent();
        event.setTradeType(tradeType);
        event.setOrderData(orderData);
        if (orderData != null && orderData.getSiteId() != null) {
            event.setSiteId(orderData.getSiteId());
        }
        return event;
    }
}
