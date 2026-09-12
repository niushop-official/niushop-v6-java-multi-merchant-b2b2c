package com.niu.mall.job.order;

import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.common.config.GlobalConfig;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.core.enums.goods.GoodsTypeEnum;
import com.niu.mall.enums.order.OrderLogMainTypeEnum;
import com.niu.mall.enums.order.OrderRefundSourceEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.refund.ICoreRefundActionService;
import com.niu.mall.service.core.refund.impl.CoreRefundActionServiceImpl;
import com.niu.mall.service.core.refund.param.ActiveRefundParam;
import org.quartz.JobExecutionContext;

import java.util.List;

@JobProvider(key = "OrderGoodsExpiredJob", name = "订单商品核销过期自动退款", describe = "订单商品核销过期自动退款", source = "mall")
public class OrderGoodsExpiredJob extends AbstractJobProvider implements IJobProvider {

    private MallOrderMapper shopOrderMapper() {
        return SpringContext.bean(MallOrderMapper.class);
    }

    private MallOrderGoodsMapper mallOrderGoodsMapper() {
        return SpringContext.bean(MallOrderGoodsMapper.class);
    }

    private ICoreRefundActionService coreRefundActionService() {
        return SpringContext.bean(CoreRefundActionServiceImpl.class);
    }


    @Override
    public void exec(JobExecutionContext context) {
        List<MallOrderGoods> goodsList = mallOrderGoodsMapper().selectJoinList(MallOrderGoods.class, new MPJQueryWrapper<MallOrderGoods>()
                .setAlias("g")
                .selectAll(MallOrderGoods.class)
                .leftJoin("?_mall_order o ON g.order_id = o.order_id".replace("?_", GlobalConfig.tablePrefix))
                .eq("g.goods_type", GoodsTypeEnum.VIRTUAL.getType())
                .eq("g.is_verify", 1)
                .gt("g.verify_expire_time", 0)
                .le("g.verify_expire_time", System.currentTimeMillis() / 1000)
                .eq("g.virtual_refund_type", 2)
                .eq("o.status", OrderStatusEnum.WAIT_TAKE.getStatus())
        );
        if (!goodsList.isEmpty()) {
            for (MallOrderGoods goods : goodsList) {
                ActiveRefundParam param = new ActiveRefundParam();
                param.setSiteId(goods.getSiteId());
                param.setOrderId(goods.getOrderId());
                param.setRemark("订单商品核销过期自动退款");
                param.setSource(String.valueOf(OrderRefundSourceEnum.EXPIRE_AUTO_REFUND.getType()));
                param.setMainType(OrderLogMainTypeEnum.SYSTEM.getType());
                param.setMainId(0);
                coreRefundActionService().shopActiveRefund(param);
            }
        }
    }
}
