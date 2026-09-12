package com.niu.mall.service.core.order.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.pay.ICorePayService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.MallOrderGroup;
import com.niu.mall.enums.order.OrderGroupEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.mall.event.order.OrderGroupCloseAfterEvent;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderGroupMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.order.ICoreOrderGroupService;
import com.niu.mall.service.core.order.param.MallOrderGroupParam;
import com.niu.mall.service.core.order.vo.MallOrderGroupCloseVo;
import com.niu.mall.service.core.order.vo.MallOrderGroupDataVo;
import com.niu.mall.service.core.order.vo.MallOrderGroupVo;
import com.niu.mall.service.core.order.vo.MallOrderListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CoreOrderGroupServiceImpl implements ICoreOrderGroupService {

    @Resource
    MallOrderGroupMapper mallOrderGroupMapper;

    @Resource
    MallOrderMapper orderMapper;

    @Resource
    SiteMapper siteMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    ICorePayService corePayService;


    @Override
    public MallOrderGroupVo getInfo(Integer groupId) {
        MallOrderGroup orderGroup = mallOrderGroupMapper.selectById(groupId);
        MallOrderGroupVo vo = new MallOrderGroupVo();
        if (ObjectUtil.isNotEmpty(orderGroup)) {
            BeanUtils.copyProperties(orderGroup, vo);
        }
        return vo;
    }

    @Override
    public MallOrderGroupDataVo getGroupByOrderId(Integer orderId) {
        MallOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new CommonException("订单信息不存在");
        }
        // 假设这里有状态常量
        if (!Arrays.asList(OrderStatusEnum.WAIT_PAY.getStatus(), OrderStatusEnum.CLOSE.getStatus()).contains(Integer.parseInt(order.getStatus()))) {
            throw new CommonException("订单状态异常");
        }
        Integer groupId = order.getGroupId();
        MallOrderGroupDataVo data = new MallOrderGroupDataVo();
        boolean isAllowPay = true;
        if (groupId != null && groupId > 0) {
            MallOrderGroup group = mallOrderGroupMapper.selectById(groupId);
            if (group != null) {
                if (group.getIsBundlePay() == 1) {
                    isAllowPay = false;
                    data.setError("当前订单参与了跨店优惠,单项订单关闭受限");
                    List<MallOrder> orderList = orderMapper.selectList(new QueryWrapper<MallOrder>().eq("group_id", groupId));

                    List<MallOrderListVo> mallOrderListVoList = new ArrayList<>();
                    for (MallOrder mallOrder : orderList) {

                        MallOrderListVo vo = new MallOrderListVo();
                        BeanUtils.copyProperties(mallOrder, vo);
                        List<MallOrderListVo.OrderGoods> orderGoods = CollectUtils.convert(mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("order_id", vo.getOrderId())), MallOrderListVo.OrderGoods.class);
                        vo.setOrderGoods(orderGoods);
                        mallOrderListVoList.add(vo);
                    }
                    data.setOrderList(mallOrderListVoList);
                    data.setGroupId(groupId);
                } else {
                    corePayService.closeByTrade("mall", groupId);
                    orderMapper.update(null, new UpdateWrapper<MallOrder>().eq("group_id", groupId).set("group_id", 0));
                    mallOrderGroupMapper.deleteById(groupId);
                }
            }
        }
        if (isAllowPay) {
            //创建新的订单组
            MallOrderGroupParam param = new MallOrderGroupParam();
            param.setMemberId(order.getMemberId());
            param.setGoodsMoney(order.getGoodsMoney());
            param.setDeliveryMoney(order.getDeliveryMoney());
            param.setShopDiscountMoney(order.getDiscountMoney());
            param.setOrderMoney(order.getOrderMoney());
            param.setShopMoney(order.getShopMoney());
            param.setMallMoney(order.getMallMoney());
            param.setBody(order.getBody());
            param.setSiteId(order.getOrderId());
            param.setTakerFullAddress(order.getTakerFullAddress());
            MallOrderGroup mallOrderGroup = new MallOrderGroup();
            BeanUtils.copyProperties(param, mallOrderGroup);
            mallOrderGroupMapper.insert(mallOrderGroup);
            order.setGroupId(mallOrderGroup.getGroupId());
            orderMapper.updateById(order);
            groupId = mallOrderGroup.getGroupId();
        }
        data.setIsAllowPay(isAllowPay);
        data.setGroupId(groupId);
        data.setTakerFullAddress(order.getTakerFullAddress());
        return data;
    }

    @Override
    public void add(MallOrderGroupParam addParam) {

        MallOrderGroup mallOrderGroup = new MallOrderGroup();
        BeanUtils.copyProperties(addParam, mallOrderGroup);
        mallOrderGroupMapper.insert(mallOrderGroup);
    }

    @Override
    public void cancel(Integer groupId) {
        if (groupId != null && groupId > 0) {
            MallOrderGroup group = mallOrderGroupMapper.selectById(groupId);
            if (group != null && group.getStatus().equals(OrderGroupEnum.WAIT_PAY.getStatus())) {
                if (group.getIsBundlePay() == 1) {
                    throw new CommonException("当前订单参与了跨店优惠,单项订单关闭受限");
                } else {
                    corePayService.closeByTrade("mall", groupId);
                    orderMapper.update(null, new UpdateWrapper<MallOrder>().eq("group_id", groupId).set("group_id", 0));
                    mallOrderGroupMapper.deleteById(groupId);
                }
            }
        }
    }

    @Override
    public MallOrderGroupCloseVo checkCloseByOrderId(Integer orderId) {
        MallOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!Arrays.asList(OrderStatusEnum.WAIT_PAY.getStatus(), OrderStatusEnum.CLOSE.getStatus()).contains(Integer.valueOf(order.getStatus()))) {
            throw new RuntimeException("订单已支付");
        }
        Integer groupId = order.getGroupId();
        MallOrderGroupCloseVo data = new MallOrderGroupCloseVo();
        boolean isAllowClose = true;
        if (groupId != null && groupId > 0) {
            MallOrderGroup group = mallOrderGroupMapper.selectById(groupId);
            if (group != null) {
                if (group.getIsBundlePay() == 1) {
                    isAllowClose = false;
                    data.setError("当前订单参与了跨店优惠,单项订单关闭受限");
                    MPJQueryWrapper<MallOrder> queryWrapper = new MPJQueryWrapper<>();
                    queryWrapper.setAlias("o")
                            .leftJoin("?_member m ON o.member_id = m.member_id".replace("?_", GlobalConfig.tablePrefix))
                            .leftJoin("?_pay p ON o.out_trade_no = p.out_trade_no".replace("?_", GlobalConfig.tablePrefix));
                    queryWrapper.select("o.mall_money,o.shop_money,o.site_id,order_id,order_no,order_type,order_from,o.out_trade_no,o.status,o.member_id,ip,goods_money,delivery_money,order_money,o.create_time,o.pay_time,delivery_type,taker_name,taker_mobile,taker_full_address,take_store_id,is_enable_refund,member_remark,shop_remark,close_remark");
                    queryWrapper.eq("o.group_id", groupId);
                    queryWrapper.ne("o.order_id", orderId);
                    queryWrapper.orderByDesc("o.create_time");
                    List<MallOrderListVo> orderList = orderMapper.selectJoinList(MallOrderListVo.class, queryWrapper);
                    for (MallOrderListVo vo : orderList) {
                        List<MallOrderListVo.OrderGoods> orderGoods = CollectUtils.convert(mallOrderGoodsMapper.selectList(new QueryWrapper<MallOrderGoods>().eq("order_id", vo.getOrderId())), MallOrderListVo.OrderGoods.class);
                        vo.setOrderGoods(orderGoods);
                        Site siteInfo = siteMapper.selectById(vo.getSiteId());
                        MallOrderListVo.SiteInfo site = new MallOrderListVo.SiteInfo();
                        site.setSiteId(vo.getSiteId());
                        site.setSiteName(siteInfo.getSiteName());
                        vo.setSite(site);
                    }
                    data.setOrderList(orderList);
                    data.setGroupId(groupId);
                } else {
                    corePayService.closeByTrade("mall", groupId);
                    orderMapper.update(null, new UpdateWrapper<MallOrder>().eq("group_id", groupId).set("group_id", 0));
                    mallOrderGroupMapper.deleteById(groupId);
                }
            }
        }
        data.setIsAllowClose(isAllowClose);
        data.setGroupId(groupId);
        return data;
    }

    /**
     * 根据订单ID关闭所属订单组（如果组内所有订单都已关闭）
     *
     * @param orderId 订单ID（简化参数，实际可接收 Map）
     */
    public void closeOfAllOrderClose(Integer orderId) {
        // 查询订单
        MallOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<MallOrder>()
                        .eq(MallOrder::getOrderId, orderId)
        );
        if (order == null) {
            throw new CommonException("订单不存在");
        }

        // 状态判断：只有待支付(WAIT_PAY) 或 已关闭(CLOSE) 才能参与关闭
        List<String> validStatuses = Arrays.asList(
                OrderStatusEnum.WAIT_PAY.getStatus().toString(),
                OrderStatusEnum.CLOSE.getStatus().toString()
        );
        if (!validStatuses.contains(order.getStatus())) {
            throw new CommonException("订单已支付或已关闭");
        }

        Integer groupId = order.getGroupId();
        if (groupId != null && groupId > 0) {
            // 查询同组内是否存在状态不是 CLOSE 的订单
            long ingOrderCount = orderMapper.selectCount(
                    new LambdaQueryWrapper<MallOrder>()
                            .eq(MallOrder::getGroupId, groupId)
                            .ne(MallOrder::getStatus, OrderStatusEnum.CLOSE.getStatus().toString())
            );
            if (ingOrderCount == 0) {
                // 全部已关闭，关闭订单组
                closeGroup(groupId);
            }
        }
    }

    /**
     * 关闭订单组（更新状态、触发事件）
     *
     * @param groupId 订单组ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeGroup(Integer groupId) {
        // 查询订单组
        MallOrderGroup group = mallOrderGroupMapper.selectById(groupId);
        if (group == null) {
            throw new CommonException("订单组不存在");
        }
        if (OrderGroupEnum.CLOSE.getStatus().equals(group.getStatus())) {
            throw new CommonException("订单组不能重复关闭");
        }

        // 更新状态
        MallOrderGroup update = new MallOrderGroup();
        update.setGroupId(groupId);
        update.setStatus(OrderGroupEnum.CLOSE.getStatus());
        mallOrderGroupMapper.updateById(update);

        // 重新获取最新数据用于事件
        MallOrderGroup closedGroup = mallOrderGroupMapper.selectById(groupId);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishOrderGroupCloseAfterEvent(closedGroup);
                }
            });
        } else {
            publishOrderGroupCloseAfterEvent(closedGroup);
        }
    }

    private void publishOrderGroupCloseAfterEvent(MallOrderGroup closedGroup) {
        OrderGroupCloseAfterEvent event = new OrderGroupCloseAfterEvent();
        event.addAppSign("mall");
        event.setName("OrderGroupCloseAfterEvent");
        event.setOrderGroup(closedGroup);
        EventAndSubscribeOfPublisher.publishAll(event);
    }
}
