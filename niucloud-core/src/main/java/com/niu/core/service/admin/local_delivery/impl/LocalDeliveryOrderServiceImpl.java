package com.niu.core.service.admin.local_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrder;
import com.niu.core.entity.delivery.DeliveryLocalDeliveryOrderLog;
import com.niu.core.enums.local_delivery.LocalDeliveryStatusEnum;
import com.niu.core.enums.local_delivery.LocalDeliveryTypeEnum;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderLogMapper;
import com.niu.core.mapper.delivery.DeliveryLocalDeliveryOrderMapper;
import com.niu.core.service.admin.local_delivery.ILocalDeliveryOrderService;
import com.niu.core.service.admin.local_delivery.param.LocalDeliveryOrderSearchParam;
import com.niu.core.service.admin.local_delivery.vo.LocalDeliveryOrderInfoVo;
import com.niu.core.service.admin.local_delivery.vo.LocalDeliveryOrderListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 配送员实现
 */
@Service
public class LocalDeliveryOrderServiceImpl implements ILocalDeliveryOrderService {

    @Resource
    DeliveryLocalDeliveryOrderMapper deliveryLocalDeliveryOrderMapper;

    @Resource
    DeliveryLocalDeliveryOrderLogMapper deliveryLocalDeliveryOrderLogMapper;

    @Override
    public PageResult<LocalDeliveryOrderListVo> page(PageParam pageParam, LocalDeliveryOrderSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<DeliveryLocalDeliveryOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id, delivery_service, delivery_no, trade_id, trade_no, trade_type, delivery_money, delivery_start, delivery_start_lng, delivery_start_lat, delivery_end, delivery_end_lng, delivery_end_lat, delivery_distance, status, create_time, remark, body");
        queryWrapper.gt("id", 0);
        queryWrapper.like(ObjectUtil.isNotEmpty(searchParam.getDeliveryNo()), "delivery_no", searchParam.getDeliveryNo());
        queryWrapper.like(ObjectUtil.isNotEmpty(searchParam.getTradeNo()), "trade_no", searchParam.getTradeNo());
        queryWrapper.like(ObjectUtil.isNotEmpty(searchParam.getTradeNo()), "trade_no", searchParam.getTradeNo());
        queryWrapper.eq(ObjectUtil.isNotEmpty(searchParam.getTradeType()), "trade_type", searchParam.getTradeType());
        queryWrapper.eq(ObjectUtil.isNotEmpty(searchParam.getStatus()), "status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        }
        queryWrapper.orderByDesc("create_time");
        IPage<DeliveryLocalDeliveryOrder> iPage = deliveryLocalDeliveryOrderMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<LocalDeliveryOrderListVo> list = new LinkedList<>();
        JSONObject localDeliveryTypeJSON = LocalDeliveryTypeEnum.getType();
        for (DeliveryLocalDeliveryOrder item : iPage.getRecords()) {
            LocalDeliveryOrderListVo vo = new LocalDeliveryOrderListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setDeliveryServiceName(getName(item.getDeliveryService(), localDeliveryTypeJSON));
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public LocalDeliveryOrderInfoVo info(Integer id) {
        DeliveryLocalDeliveryOrder deliveryLocalDeliveryOrder = deliveryLocalDeliveryOrderMapper.selectById(id);

        List<DeliveryLocalDeliveryOrderLog> orderLogList = deliveryLocalDeliveryOrderLogMapper.selectList(new QueryWrapper<DeliveryLocalDeliveryOrderLog>()
                .eq("order_id", id));

        LocalDeliveryOrderInfoVo vo = new LocalDeliveryOrderInfoVo();
        BeanUtils.copyProperties(deliveryLocalDeliveryOrder, vo);
        List<LocalDeliveryOrderInfoVo.OrderLog> orderLogInfoList = new LinkedList<>();
        if(!orderLogList.isEmpty()){
            orderLogList.forEach(item -> {
                LocalDeliveryOrderInfoVo.OrderLog orderLog = new LocalDeliveryOrderInfoVo.OrderLog();
                BeanUtils.copyProperties(item, orderLog);
                orderLogInfoList.add(orderLog);
            });
            vo.setOrderLog(orderLogInfoList);
        }
        return vo;
    }

    public static String getName(String type,JSONObject jsonObject) {
        String name = "";
        if(!ObjectUtil.isEmpty(type)){
            JSONObject jsonObject1 = jsonObject.getJSONObject(type);
            if(jsonObject1 != null && jsonObject1.containsKey("name")){
                name = jsonObject1.getStr("name");
            }
        }
        return name;
    }
}
