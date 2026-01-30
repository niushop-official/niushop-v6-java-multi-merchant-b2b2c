package com.niu.mall.listener.invoice;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.event.order.OrderInvoiceEventDefiner;
import com.niu.core.service.api.order.vo.InvoiceListVo;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@EventCallback("mall")
public class MallInvoiceTradeInfoListener extends OrderInvoiceEventDefiner {

    @Resource
    MallOrderMapper mallOrderMapper;
    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Override
    public OrderInvoiceResult handleCallback(OrderInvoiceEvent event) {
        OrderInvoiceResult result = new OrderInvoiceResult();
        if("mall".equals(event.getTradeType())){
            MPJQueryWrapper<MallOrder> queryWrapper = new MPJQueryWrapper<>();
            queryWrapper.setAlias("o")
                    .leftJoin("?_pay p ON o.out_trade_no = p.out_trade_no".replace("?_", GlobalConfig.tablePrefix));
            queryWrapper.select("o.*");
            queryWrapper.select("p.out_trade_no,p.type,p.pay_time");
            queryWrapper.eq("o.order_id", event.getTradeId());
            List<InvoiceListVo.OrderInfo> list = mallOrderMapper.selectJoinList(InvoiceListVo.OrderInfo.class, queryWrapper);
            for (InvoiceListVo.OrderInfo item : list) {
                item.setStatusName(OrderStatusEnum.getDeliveryTypeMapByStatus(Integer.valueOf(item.getStatus()),item.getDeliveryType(),item.getOrderType()));
                InvoiceListVo.OrderInfo.Pay pay = new InvoiceListVo.OrderInfo.Pay();
                BeanUtils.copyProperties(item, pay);
                item.setPay(pay);
                QueryWrapper<MallOrderGoods> orderGoodsWra = new QueryWrapper<>();
                orderGoodsWra.select("site_id,order_id,goods_name,sku_name,sku_image,price,num");
                orderGoodsWra.eq("order_id", item.getOrderId());
                List<MallOrderGoods> mallOrderGoods = mallOrderGoodsMapper.selectList(orderGoodsWra);

                List<InvoiceListVo.OrderInfo.OrderGoods> orderGoods = new ArrayList<>(mallOrderGoods.size());
                for (MallOrderGoods item1 : mallOrderGoods) {
                    InvoiceListVo.OrderInfo.OrderGoods orderGoodsInfo = new InvoiceListVo.OrderInfo.OrderGoods();
                    BeanUtils.copyProperties(item1, orderGoodsInfo);
                    orderGoods.add(orderGoodsInfo);
                }
                item.setOrderGoods(orderGoods);
            }
            if(list.isEmpty()){
                return null;
            }else{
                result.setOrderList(list.get(0));
            }
            return result;
        }
        return null;
    }
}
