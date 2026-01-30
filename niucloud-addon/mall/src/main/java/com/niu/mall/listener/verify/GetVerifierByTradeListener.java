package com.niu.mall.listener.verify;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.exception.CommonException;
import com.niu.core.enums.delivery.OrderDeliveryStatusEnum;
import com.niu.core.enums.delivery.OrderDeliveryTypeEnum;
import com.niu.core.event.verify.GetVerifyInfoByTradeEventDefiner;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@EventCallback("mall")
@Component
public class GetVerifierByTradeListener extends GetVerifyInfoByTradeEventDefiner {

    @Resource
    private MallOrderGoodsMapper mallOrderGoodsMapper;
    @Resource
    private MallOrderMapper mallOrderMapper;

    @Override
    public GetVerifyInfoByTradeResult handleCallback(GetVerifyInfoByTradeEvent event) {
        if("mall".equals( event.getTradeType())){
            MallOrder  order = mallOrderMapper.selectById( event.getTradeId());
            if(ObjectUtil.isEmpty( order)){
                throw new CommonException("核销信息不存在");
            }
            // 查询订单商品
            QueryWrapper<MallOrderGoods> orderGoodsWhere = new QueryWrapper<>();
            orderGoodsWhere.eq("order_id", order.getOrderId());
            orderGoodsWhere.eq("site_id", order.getSiteId());
            List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper.selectList(orderGoodsWhere);
            if(orderGoodsList.isEmpty()){
                throw new CommonException("核销信息不存在");
            }
            if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.VIRTUAL.getType()) &&  order.getStatus().equals(OrderStatusEnum.WAIT_TAKE.getStatus().toString())
                &&  orderGoodsList.size()  == 1){
                MallOrderGoods  orderGoods = orderGoodsList.get(0);
                if ( orderGoods.getIsVerify().equals(1) &&  orderGoods.getDeliveryStatus().equals(OrderDeliveryStatusEnum.DELIVERY_FINISH.getStatus())){
                    GetVerifyInfoByTradeResult  result = new GetVerifyInfoByTradeResult();
                    result.setType("shopVirtualGoods");
                    result.setData(Map.of("order_goods_id",  orderGoods.getOrderGoodsId()));
                    result.setMemberId(orderGoods.getMemberId());
                    return  result;
                }
            }
            if (order.getDeliveryType().equals(OrderDeliveryTypeEnum.STORE.getType()) &&  order.getStatus().equals(OrderStatusEnum.WAIT_DELIVERY.getStatus().toString())
                    &&  orderGoodsList.size()  == 1){
                GetVerifyInfoByTradeResult  result = new GetVerifyInfoByTradeResult();
                result.setType("shopPickUpOrder");
                result.setData(Map.of("order_id",  order.getOrderId()));
                result.setMemberId(order.getMemberId());
                return  result;
            }
        }
        return null;
    }
}

