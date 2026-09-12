package com.niu.core.service.admin.recharge.impl;

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
import com.niu.core.enums.pay.OrderStatusEnum;
import com.niu.core.enums.recharge.RechargeOrderStatusEnum;
import com.niu.core.enums.recharge.RechargeRefundStatusEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.recharge.RechargeOrderItemMapper;
import com.niu.core.mapper.recharge.RechargeOrderMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.admin.recharge.IRechargeOrderService;
import com.niu.core.service.admin.recharge.param.RechargeOrderSearchParam;
import com.niu.core.service.admin.recharge.vo.RechargeOrderInfoVo;
import com.niu.core.service.admin.recharge.vo.RechargeOrderListVo;
import com.niu.core.service.admin.recharge.vo.RechargeOrderStatVo;
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
     * @param pageParam
     * @param searchParam
     * @return
     */
    @Override
    public PageResult<RechargeOrderListVo> list(PageParam pageParam, RechargeOrderSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<RechargeOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_type","recharge");
        queryWrapper.orderByDesc("create_time");

        if (ObjectUtil.isNotEmpty(searchParam.getOrderNo())) queryWrapper.eq("order_no", searchParam.getOrderNo());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderStatus()) && !"all".equals(String.valueOf(searchParam.getOrderStatus()))) queryWrapper.eq("order_status", searchParam.getOrderStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getOrderFrom()) && !"all".equals(searchParam.getOrderFrom())) queryWrapper.eq("order_from", searchParam.getOrderFrom());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        if (ObjectUtil.isNotEmpty(searchParam.getPayTime())) QueryMapperUtils.buildByTime(queryWrapper, "pay_time", searchParam.getPayTime());
        if (searchParam.getStartMoney().compareTo(BigDecimal.ZERO) > 0) queryWrapper.ge("order_money", searchParam.getStartMoney());
        if (searchParam.getEndMoney().compareTo(BigDecimal.ZERO) > 0) queryWrapper.le("order_money", searchParam.getEndMoney());

        IPage<RechargeOrder> iPage = rechargeOrderMapper.selectPage(new Page<RechargeOrder>(page, limit), queryWrapper);
        List<RechargeOrderListVo> list = new LinkedList<>();
        List<Integer> orderIds = iPage.getRecords().stream().map(RechargeOrder::getOrderId).toList();
        List<Integer> memberIds = iPage.getRecords().stream().map(RechargeOrder::getMemberId).toList();
        List<RechargeOrderItem> itemList = orderIds.isEmpty() ? new ArrayList<>() : rechargeOrderItemMapper.selectList(new QueryWrapper<RechargeOrderItem>().in("order_id", orderIds));
        List<Member> memberList = memberIds.isEmpty() ? new ArrayList<>() : memberMapper.selectList(new QueryWrapper<Member>().in("member_id", orderIds));
        for(RechargeOrder item : iPage.getRecords()) {
            RechargeOrderListVo vo = new RechargeOrderListVo();
            BeanUtils.copyProperties(item, vo);
            MemberBriefInfoVo memberInfo = new MemberBriefInfoVo();
            List<Member> memberThis = memberList.stream().filter(info -> info.getMemberId().equals(item.getMemberId())).toList();
            if (!memberThis.isEmpty() ) BeanUtils.copyProperties(memberThis.get(0), memberInfo);
            vo.setMember(memberInfo);
            List<RechargeOrderItem> itemListThis = itemList.stream().filter(info -> info.getOrderId().equals(item.getOrderId())).toList();
            vo.setItem(itemListThis);
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

        RechargeOrder order = rechargeOrderMapper.selectOne(queryWrapper);
        if (order == null) return null;

        RechargeOrderInfoVo vo = new RechargeOrderInfoVo();
        BeanUtils.copyProperties(order, vo);

        MemberBriefInfoVo memberInfo = new MemberBriefInfoVo();
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", order.getMemberId()));
        if (member != null) BeanUtils.copyProperties(member, memberInfo);
        vo.setMember(memberInfo);

        List<RechargeOrderItem> itemList = rechargeOrderItemMapper.selectList(new QueryWrapper<RechargeOrderItem>().eq("order_id", order.getOrderId()));
        vo.setItem(itemList);

        return vo;
    }

    /**
     * 统计
     *
     * @param searchParam
     * @return
     */
    public RechargeOrderStatVo stat(RechargeOrderSearchParam searchParam) {
        RechargeOrderStatVo vo = new RechargeOrderStatVo();

        QueryWrapper<RechargeOrder> rechargeMoneyQueryWrapper = new QueryWrapper<RechargeOrder>();
        rechargeMoneyQueryWrapper.select("SUM(order_money) AS order_money");
        rechargeMoneyQueryWrapper.eq("order_type", "recharge");
        rechargeMoneyQueryWrapper.eq("order_status", RechargeOrderStatusEnum.FINISH.getStatus());
        if (searchParam.getMemberId() > 0) rechargeMoneyQueryWrapper.eq("member_id", searchParam.getMemberId());
        rechargeMoneyQueryWrapper.last("limit 1");
        RechargeOrder rechargeMoney = rechargeOrderMapper.selectOne(rechargeMoneyQueryWrapper);
        if (rechargeMoney != null) vo.setRechargeMoney(rechargeMoney.getOrderMoney());

        QueryWrapper<RechargeOrder> refundMoneyQueryWrapper = new QueryWrapper<RechargeOrder>();
        refundMoneyQueryWrapper.select("SUM(order_money) AS order_money");
        refundMoneyQueryWrapper.eq("order_type", "recharge");
        refundMoneyQueryWrapper.eq("refund_status", RechargeRefundStatusEnum.REFUND_COMPLETED.getStatus());
        if (searchParam.getMemberId() > 0) refundMoneyQueryWrapper.eq("member_id", searchParam.getMemberId());
        refundMoneyQueryWrapper.last("limit 1");
        RechargeOrder refundeMoney = rechargeOrderMapper.selectOne(refundMoneyQueryWrapper);
        if (refundeMoney != null) vo.setRechargeRefundMoney(refundeMoney.getOrderMoney());

        return vo;
    }
}
