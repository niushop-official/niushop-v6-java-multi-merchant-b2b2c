package com.niu.mall.listener.verify;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.verify.VerifyCheckEventDefiner;
import com.niu.core.entity.goods.MallGoods;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderDeliveryStatusEnum;
import com.niu.mall.enums.order.OrderGoodsRefundEnum;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EventCallback("mall")
@Component
public class VerifyCheckListener extends VerifyCheckEventDefiner {

    @Resource
    private MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    private MallGoodsMapper mallGoodsMapper;

    @Override
    @SuppressWarnings("unchecked")
    public VerifyCheckResult handleCallback(VerifyCheckEvent event) {
        Map<String, Object> params = event.getVerifyData();
        
        // 处理 site_id 字段类型
        Object siteIdObj = params.get("site_id");
        Integer siteId;
        if (siteIdObj instanceof Integer) {
            siteId = (Integer) siteIdObj;
        } else if (siteIdObj instanceof String) {
            siteId = Integer.parseInt((String) siteIdObj);
        } else {
            siteId = Integer.parseInt(siteIdObj.toString());
        }
        
        params.put("un_use_msg", new ArrayList<String>());
        params.put("is_can_use", 1);
        
        // 处理 data 字段，可能是 Map 也可能是 String
        Object dataObj = params.get("data");
        Map<String, Object> data;
        if (dataObj instanceof String) {
            data = JSONUtil.parseObj((String) dataObj);
        } else if (dataObj instanceof Map) {
            data = (Map<String, Object>) dataObj;
        } else {
            return new VerifyCheckResult().setProcessedData(params);
        }

        // 虚拟商品
        if ("shopVirtualGoods".equals(params.get("type"))) {
            // 处理 order_goods_id 字段类型
            Object orderGoodsIdObj = data.get("order_goods_id");
            Integer orderGoodsId = 0;
            if (orderGoodsIdObj instanceof Integer) {
                orderGoodsId = (Integer) orderGoodsIdObj;
            } else if (orderGoodsIdObj instanceof String) {
                orderGoodsId = Integer.parseInt((String) orderGoodsIdObj);
            } else if (orderGoodsIdObj != null) {
                orderGoodsId = Integer.parseInt(orderGoodsIdObj.toString());
            }
            MallOrderGoods orderGoodsInfo = mallOrderGoodsMapper.selectOne(
                    new QueryWrapper<MallOrderGoods>()
                            .eq("site_id", siteId)
                            .eq("order_goods_id", orderGoodsId)
            );

            String unUseMsg = "";
            if (orderGoodsInfo != null) {
                if (OrderDeliveryStatusEnum.TAKED.getStatus().equals(orderGoodsInfo.getDeliveryStatus())
                        || OrderDeliveryStatusEnum.EXPIRE.getStatus().equals(orderGoodsInfo.getDeliveryStatus())) {
                    ((List<String>) params.get("un_use_msg")).add("商品已核销或已过期");
                    params.put("is_can_use", 0);
                    unUseMsg = "商品已核销或已过期";
                } else if (OrderGoodsRefundEnum.REFUNDING.getStatus().equals(orderGoodsInfo.getStatus())
                        || OrderGoodsRefundEnum.REFUND_FINISH.getStatus().equals(orderGoodsInfo.getStatus())) {
                    ((List<String>) params.get("un_use_msg")).add("商品正在退款或已退款");
                    params.put("is_can_use", 0);
                    unUseMsg = "商品正在退款或已退款";
                } else {
                    MallGoods goods = mallGoodsMapper.selectOne(
                            new QueryWrapper<MallGoods>()
                                    .select("goods_id", "status")
                                    .eq("goods_id", orderGoodsInfo.getGoodsId())
                    );
                    if (goods != null && goods.getStatus() != 1) {
                        ((List<String>) params.get("un_use_msg")).add("商品已下架");
                        params.put("is_can_use", 0);
                        unUseMsg = "商品已下架";
                    }
                }
            }
            
            // 处理 value 字段，可能是 Map 也可能是 String
            Object valueObj = params.get("value");
            if (valueObj != null) {
                Map<String, Object> valueMap;
                if (valueObj instanceof String) {
                    valueMap = JSONUtil.parseObj((String) valueObj);
                } else if (valueObj instanceof Map) {
                    valueMap = (Map<String, Object>) valueObj;
                } else {
                    return new VerifyCheckResult().setProcessedData(params);
                }
                
                List<Map<String, Object>> list = (List<Map<String, Object>>) valueMap.get("list");
                if (list != null && !list.isEmpty()) {
                    list.get(0).put("un_use_msg", unUseMsg);
                }
            }
            return new VerifyCheckResult().setProcessedData(params);
        } else if ("shopPickUpOrder".equals(params.get("type"))) { // 自提订单
            // 处理 order_id 字段类型
            Object orderIdObj = data.get("order_id");
            Integer orderId = 0;
            if (orderIdObj instanceof Integer) {
                orderId = (Integer) orderIdObj;
            } else if (orderIdObj instanceof String) {
                orderId = Integer.parseInt((String) orderIdObj);
            } else if (orderIdObj != null) {
                orderId = Integer.parseInt(orderIdObj.toString());
            }
            List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper.selectList(
                    new QueryWrapper<MallOrderGoods>()
                            .eq("site_id", siteId)
                            .eq("order_id", orderId)
            );

            // 处理 value 字段，可能是 Map 也可能是 String
            Object valueObj = params.get("value");
            if (valueObj == null) {
                return new VerifyCheckResult().setProcessedData(params);
            }
            
            Map<String, Object> valueMap;
            if (valueObj instanceof String) {
                valueMap = JSONUtil.parseObj((String) valueObj);
            } else if (valueObj instanceof Map) {
                valueMap = (Map<String, Object>) valueObj;
            } else {
                return new VerifyCheckResult().setProcessedData(params);
            }
            
            List<Map<String, Object>> verifyList = (List<Map<String, Object>>) valueMap.get("list");
            if (verifyList == null) {
                return new VerifyCheckResult().setProcessedData(params);
            }
            
            for (Map<String, Object> value : verifyList) {
                value.put("un_use_msg", "");
                for (MallOrderGoods orderGoodsInfo : orderGoodsList) {
                    if (value.get("order_goods_id").equals(orderGoodsInfo.getOrderGoodsId())) {
                        if (OrderGoodsRefundEnum.REFUNDING.getStatus().equals(orderGoodsInfo.getStatus())
                                || OrderGoodsRefundEnum.REFUND_FINISH.getStatus().equals(orderGoodsInfo.getStatus())) {
                            ((List<String>) params.get("un_use_msg")).add("商品正在退款或已退款");
                            params.put("is_can_use", 0);
                            value.put("un_use_msg", "商品正在退款或已退款");
                            continue;
                        }
                        MallGoods goods = mallGoodsMapper.selectOne(
                                new QueryWrapper<MallGoods>()
                                        .select("goods_id", "status", "goods_name")
                                        .eq("goods_id", orderGoodsInfo.getGoodsId())
                        );
                        if (goods != null && goods.getStatus() != 1) {
                            ((List<String>) params.get("un_use_msg")).add(goods.getGoodsName() + "已下架");
                            params.put("is_can_use", 0);
                            value.put("un_use_msg", "商品已下架");
                        }
                    }
                }
            }
            valueMap.put("list", verifyList);
            params.put("value", valueMap);
            return new VerifyCheckResult().setProcessedData(params);
        }

        return new VerifyCheckResult().setProcessedData(params);
    }
}
