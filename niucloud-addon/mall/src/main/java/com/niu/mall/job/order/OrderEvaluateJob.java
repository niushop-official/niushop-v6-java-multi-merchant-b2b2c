package com.niu.mall.job.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.job.AbstractJobProvider;
import com.niu.core.common.component.job.IJobProvider;
import com.niu.core.common.component.job.annotation.JobProvider;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.mall.entity.goods.MallGoodsEvaluate;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.goods.GoodsEvaluateEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.enums.trade.SettlementEnum;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.goods.ICoreGoodsEvaluateService;
import com.niu.mall.service.core.order.ICoreOrderSettlementService;
import org.quartz.JobExecutionContext;

import java.math.BigDecimal;
import java.util.List;

@JobProvider(key = "OrderEvaluateJob", name = "订单自动好评", describe = "订单自动好评", source = "mall")
public class OrderEvaluateJob extends AbstractJobProvider implements IJobProvider  {

    private MallOrderMapper mallOrderMapper() {
        return SpringContext.bean(MallOrderMapper.class);
    }
    private MallOrderGoodsMapper mallOrderGoodsMapper() {
        return SpringContext.bean(MallOrderGoodsMapper.class);
    }
    private MemberMapper memberMapper() {
        return SpringContext.bean(MemberMapper.class);
    }

    private static ICoreGoodsEvaluateService coreGoodsEvaluateService() {
        return SpringContext.bean(ICoreGoodsEvaluateService.class);
    }


    @Override
    public void exec(JobExecutionContext context) {
        QueryWrapper<MallOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_evaluate",0);
        queryWrapper.le("evaluate_timer", System.currentTimeMillis() / 1000);
        queryWrapper.gt("evaluate_timer", 0);
        List<MallOrder> orderList = mallOrderMapper().selectList(queryWrapper);
        if (!orderList.isEmpty()) {
            //查询订单商品
            List<Integer> orderIds = orderList.stream().map(MallOrder::getOrderId).toList();
            List<Integer> memberIds = orderList.stream().map(MallOrder::getMemberId).toList();
            List<MallOrderGoods> orderGoodsList = mallOrderGoodsMapper().selectList(new QueryWrapper<MallOrderGoods>().in("order_id", orderIds));
            List<Member> memberList = memberMapper().selectList(new QueryWrapper<Member>().in("member_id", memberIds));
            for (MallOrder order: orderList) {
                List<Member> member = memberList.stream().filter(item -> item.getMemberId().equals(order.getMemberId())).toList();
                List<MallOrderGoods> orderGoods = orderGoodsList.stream().filter(item -> item.getOrderId().equals(order.getOrderId())).toList();
                for (MallOrderGoods orderGoodsItem: orderGoods){
                    MallGoodsEvaluate mallGoodsEvaluate = new MallGoodsEvaluate();
                    mallGoodsEvaluate.setSiteId(order.getSiteId());
                    mallGoodsEvaluate.setOrderId(order.getOrderId());
                    mallGoodsEvaluate.setOrderGoodsId(orderGoodsItem.getOrderGoodsId());
                    mallGoodsEvaluate.setGoodsId(orderGoodsItem.getGoodsId());
                    mallGoodsEvaluate.setMemberId(order.getMemberId());
                    mallGoodsEvaluate.setMemberHead(member.isEmpty() ? "" : member.get(0).getHeadimg());
                    mallGoodsEvaluate.setMemberName(member.isEmpty() ? "" : member.get(0).getNickname());
                    mallGoodsEvaluate.setContent("系统默认好评");
                    mallGoodsEvaluate.setIsAnonymous(2);
                    mallGoodsEvaluate.setScores(5);
                    mallGoodsEvaluate.setQualityScore(new BigDecimal(5));
                    mallGoodsEvaluate.setServiceScore(new BigDecimal(5));
                    mallGoodsEvaluate.setLogisticsScore(new BigDecimal(5));
                    mallGoodsEvaluate.setIsAudit(GoodsEvaluateEnum.AUDIT_NO.getStatus());
                    coreGoodsEvaluateService().addEvaluate(mallGoodsEvaluate);
                }
            }
        }
    }

}
