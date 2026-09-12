package com.niu.core.service.api.vip_card.order.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.vip_card.MemberVipCardOrder;
import com.niu.core.entity.vip_card.MemberVipCardOrderItem;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.enums.vip_card.VipCardOrderStatusEnum;
import com.niu.core.mapper.vip_card.VipCardOrderItemMapper;
import com.niu.core.mapper.vip_card.VipCardOrderMapper;
import com.niu.core.service.api.vip_card.order.IApiVipCardOrderService;
import com.niu.core.service.api.vip_card.order.param.ApiVipCardOrderPageParam;
import com.niu.core.service.api.vip_card.order.vo.ApiVipCardOrderDetailVo;
import com.niu.core.service.api.vip_card.order.vo.ApiVipCardOrderPageVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApiVipCardOrderServiceImpl implements IApiVipCardOrderService {

    @Resource
    private VipCardOrderMapper orderMapper;

    @Resource
    private VipCardOrderItemMapper orderItemMapper;

    private Integer getCurrentMemberId() {
        return RequestUtils.memberId();
    }

    @Override
    public PageResult<ApiVipCardOrderPageVo> getPage(PageParam pageParam, ApiVipCardOrderPageParam param) {
        Integer memberId = getCurrentMemberId();
        if (memberId == null) {
            return PageResult.empty();
        }

        // 构建查询条件
        LambdaQueryWrapper<MemberVipCardOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberVipCardOrder::getMemberId, memberId)
                .eq(MemberVipCardOrder::getOrderType, "vip_card")
                .orderByDesc(MemberVipCardOrder::getCreateTime);

        if (StrUtil.isNotEmpty(param.getStatus())) {
            wrapper.eq(MemberVipCardOrder::getStatus, param.getStatus());
        }
        if (CollUtil.isNotEmpty(param.getCreateTime()) && param.getCreateTime().size() == 2) {
            QueryMapperUtils.buildByTime(wrapper, MemberVipCardOrder::getCreateTime, param.getCreateTime().toArray(new String[0]));
        }

        // 分页查询
        IPage<MemberVipCardOrder> page = orderMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()),
                wrapper
        );

        if (CollUtil.isEmpty(page.getRecords())) {
            return PageResult.empty();
        }

        // 批量查询订单项
        List<Long> orderIds = page.getRecords().stream().map(MemberVipCardOrder::getOrderId).collect(Collectors.toList());
        List<MemberVipCardOrderItem> allItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<MemberVipCardOrderItem>()
                        .in(MemberVipCardOrderItem::getOrderId, orderIds)
        );
        // order_id: 主表 Long，子表 Integer，统一按 long 关联（对齐 PHP hasOne，取首条）
        Map<Long, MemberVipCardOrderItem> itemMap = allItems.stream()
                .filter(item -> item.getOrderId() != null)
                .collect(Collectors.toMap(
                        item -> item.getOrderId().longValue(),
                        item -> item,
                        (a, b) -> a
                ));

        // 获取状态字典
        Map<Integer, Map<String, Object>> statusMap = getStatus();

        // 转换 VO
        List<ApiVipCardOrderPageVo> voList = page.getRecords().stream().map(order -> {
            ApiVipCardOrderPageVo vo = new ApiVipCardOrderPageVo();
            BeanUtil.copyProperties(order, vo);
            if (order.getOrderId() != null) {
                vo.setOrderId(order.getOrderId().intValue());
            }
            // 订单来源名称（假设通过枚举或常量转换）
            vo.setOrderFromName(ChannelEnum.getNameByCode(order.getOrderFrom()));
            // 状态信息
            vo.setStatusInfo(statusMap.get(order.getStatus()));
            // 订单项
            MemberVipCardOrderItem item = itemMap.get(order.getOrderId());
            if (item != null) {
                vo.setOrderItem(toItemVo(item));
            }
            return vo;
        }).collect(Collectors.toList());

        return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal(), voList);
    }

    @Override
    public ApiVipCardOrderDetailVo getDetail(Integer orderId) {
        Integer memberId = getCurrentMemberId();
        if (memberId == null) {
            return null;
        }

        MemberVipCardOrder order = orderMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardOrder>()
                        .eq(MemberVipCardOrder::getOrderId, orderId)
                        .eq(MemberVipCardOrder::getMemberId, memberId)
                        .eq(MemberVipCardOrder::getOrderType, "vip_card")
        );
        if (order == null) {
            return null;
        }

        MemberVipCardOrderItem item = orderItemMapper.selectOne(
                new LambdaQueryWrapper<MemberVipCardOrderItem>()
                        .eq(MemberVipCardOrderItem::getOrderId, orderId)
                        .select(MemberVipCardOrderItem::getOrderItemId, MemberVipCardOrderItem::getOrderId,
                                MemberVipCardOrderItem::getMemberId, MemberVipCardOrderItem::getCardId,
                                MemberVipCardOrderItem::getItemId, MemberVipCardOrderItem::getCardName,
                                MemberVipCardOrderItem::getItemName, MemberVipCardOrderItem::getPrice,
                                MemberVipCardOrderItem::getNum, MemberVipCardOrderItem::getItemMoney,
                                MemberVipCardOrderItem::getItemConfig, MemberVipCardOrderItem::getIsRenew,
                                MemberVipCardOrderItem::getIsTrial)
                        .last("limit 1")
        );

        ApiVipCardOrderDetailVo vo = new ApiVipCardOrderDetailVo();
        BeanUtil.copyProperties(order, vo);
        if (order.getOrderId() != null) {
            vo.setOrderId(order.getOrderId().intValue());
        }
        vo.setOrderFromName(ChannelEnum.getNameByCode(order.getOrderFrom()));
        vo.setStatusInfo(getStatus().get(order.getStatus()));
        if (item != null) {
            vo.setOrderItem(toItemVo(item));
        }
        return vo;
    }

    @Override
    public Map<Integer, Map<String, Object>> getStatus() {
        return VipCardOrderStatusEnum.getStatus();
    }

    private ApiVipCardOrderPageVo.VipCardOrderItemVo toItemVo(MemberVipCardOrderItem item) {
        ApiVipCardOrderPageVo.VipCardOrderItemVo itemVo = new ApiVipCardOrderPageVo.VipCardOrderItemVo();
        BeanUtil.copyProperties(item, itemVo);
        if (item.getOrderItemId() != null) {
            itemVo.setOrderItemId(item.getOrderItemId().intValue());
        }
        return itemVo;
    }
}
