package com.niu.core.service.api.cash.order.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.cash.CashOrder;
import com.niu.core.enums.cash.CashOrderStatusEnum;
import com.niu.core.enums.common.ChannelEnum;
import com.niu.core.mapper.cash.CashOrderMapper;
import com.niu.core.service.api.cash.order.IApiCashOrderService;
import com.niu.core.service.api.cash.order.param.ApiCashOrderPageParam;
import com.niu.core.service.api.cash.order.vo.ApiCashOrderDetailVo;
import com.niu.core.service.api.cash.order.vo.ApiCashOrderPageVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiCashOrderServiceImpl implements IApiCashOrderService {

    @Resource
    CashOrderMapper cashOrderMapper;

    @Override
    public PageResult<ApiCashOrderPageVo> getPage(PageParam pageParam, ApiCashOrderPageParam param) {
        Integer memberId = RequestUtils.memberId();
        if (memberId == null || memberId <= 0) {
            return PageResult.build(pageParam.getPage(), pageParam.getLimit(), 0L);
        }

        QueryWrapper<CashOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId);
        if (param != null) {
            if (StrUtil.isNotBlank(param.getStatus())) {
                queryWrapper.eq("status", param.getStatus());
            }
            if (param.getCreateTime() != null) {
                QueryMapperUtils.buildByTime(queryWrapper, "create_time", param.getCreateTime());
            }
        }
        queryWrapper.orderByDesc("create_time");

        IPage<CashOrder> page = cashOrderMapper.selectPage(
                new Page<>(pageParam.getPage(), pageParam.getLimit()),
                queryWrapper
        );

        List<ApiCashOrderPageVo> list = new ArrayList<>();
        for (CashOrder order : page.getRecords()) {
            list.add(toPageVo(order));
        }
        return PageResult.build(pageParam.getPage(), pageParam.getLimit(), page.getTotal()).setData(list);
    }

    @Override
    public ApiCashOrderDetailVo getDetail(Integer orderId) {
        Integer memberId = RequestUtils.memberId();
        CashOrder order = cashOrderMapper.selectOne(new QueryWrapper<CashOrder>()
                .eq("member_id", memberId)
                .eq("order_id", orderId));
        if (order == null) {
            return null;
        }
        ApiCashOrderDetailVo vo = new ApiCashOrderDetailVo();
        BeanUtils.copyProperties(toPageVo(order), vo);
        return vo;
    }

    @Override
    public Map<Integer, Map<String, Object>> getStatus() {
        return CashOrderStatusEnum.getMap();
    }

    private ApiCashOrderPageVo toPageVo(CashOrder order) {
        ApiCashOrderPageVo vo = new ApiCashOrderPageVo();
        BeanUtils.copyProperties(order, vo);
        vo.setOrderFromName(ChannelEnum.getNameByCode(order.getOrderFrom()));
        vo.setStatusInfo(new LinkedHashMap<>(CashOrderStatusEnum.getStatusInfo(order.getStatus())));
        return vo;
    }
}
