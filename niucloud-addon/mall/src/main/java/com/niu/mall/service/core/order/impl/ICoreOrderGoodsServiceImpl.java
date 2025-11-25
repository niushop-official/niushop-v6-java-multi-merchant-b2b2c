package com.niu.mall.service.core.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderDeliveryStatusEnum;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderGoodsService;
import com.niu.mall.service.core.order.param.OrderGoodsDeliveryParam;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ICoreOrderGoodsServiceImpl implements ICoreOrderGoodsService {

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Override
    public boolean cancelOrderGoodsDelivery(OrderGoodsDeliveryParam param) {
        MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().eq("order_no", param.getOrderNo()));

        if(ObjectUtil.isEmpty(order)) return true;

        MallOrderGoods updateOrderGoods = new MallOrderGoods();
        updateOrderGoods.setDeliveryStatus(OrderDeliveryStatusEnum.WAIT_DELIVERY.getStatus());
        QueryWrapper<MallOrderGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", param.getOrderNo());
        if(ObjectUtil.isNotEmpty(param.getOrderGoodsIds())) {
            queryWrapper.in("order_goods_id", param.getOrderGoodsIds());
        }
        mallOrderGoodsMapper.update(updateOrderGoods, queryWrapper);
        return true;
    }
}
