package com.niu.core.service.api.recharge.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.recharge.RechargeOrder;
import com.niu.core.entity.recharge.RechargeOrderItem;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.recharge.RechargeOrderItemMapper;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.api.recharge.IRechargeOrderService;
import com.niu.core.service.api.recharge.param.RechargeOrderSearchParam;
import com.niu.core.service.api.recharge.vo.RechargeOrderInfoVo;
import com.niu.core.service.api.recharge.vo.RechargeOrderListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class RechargeOrderServiceImpl implements IRechargeOrderService {

    @Resource
    RechargeOrderMapper rechargeOrderMapper;

    @Resource
    RechargeOrderItemMapper rechargeOrderItemMapper;

    @Resource
    MemberMapper memberMapper;

    /**
     * 订单列表
     *
     */
    @Override
    public PageResult<RechargeOrderListVo> list(PageParam pageParam, RechargeOrderSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<RechargeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_type","recharge");
        queryWrapper.eq("member_id", RequestUtils.memberId());
        queryWrapper.orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getOrderStatus())) queryWrapper.eq("order_status", searchParam.getOrderStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());


        IPage<RechargeOrder> iPage = rechargeOrderMapper.selectPage(new Page<RechargeOrder>(page, limit), queryWrapper);
        List<RechargeOrderListVo> list = new LinkedList<>();
        List<Integer> orderIds = iPage.getRecords().stream().map(RechargeOrder::getOrderId).toList();
        List<RechargeOrderItem> itemList = orderIds.isEmpty() ? new ArrayList<>() : rechargeOrderItemMapper.selectList(new QueryWrapper<RechargeOrderItem>().in("order_id", orderIds));

        for(RechargeOrder item : iPage.getRecords()) {
            RechargeOrderListVo vo = new RechargeOrderListVo();
            BeanUtils.copyProperties(item, vo);
            List<RechargeOrderItem> itemListThis = itemList.stream().filter(info -> info.getOrderId().equals(item.getOrderId())).toList();
            if(!itemListThis.isEmpty()){
                vo.setItem(itemListThis.get(0));
            }else{
                vo.setItem(new RechargeOrderItem());
            }
            list.add(vo);
        }

        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    /**
     * 充值订单详情
     *
     * @param orderId
     * @return
     */
    @Override
    public RechargeOrderInfoVo detail(Integer orderId) {
        QueryWrapper<RechargeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_type", "recharge");
        queryWrapper.eq("order_id", orderId);
        queryWrapper.eq("member_id", RequestUtils.memberId());
        RechargeOrder order = rechargeOrderMapper.selectOne(queryWrapper);
        if (order == null) return null;

        RechargeOrderInfoVo vo = new RechargeOrderInfoVo();
        BeanUtils.copyProperties(order, vo);

        List<RechargeOrderItem> itemList = rechargeOrderItemMapper.selectList(new QueryWrapper<RechargeOrderItem>().eq("order_id", order.getOrderId()));
        vo.setItem(itemList);
        return vo;
    }
}
