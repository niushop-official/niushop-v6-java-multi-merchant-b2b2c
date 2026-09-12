package com.niu.core.service.admin.local_delivery.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.component.context.SpringContext;
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

import java.math.BigDecimal;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;


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
        queryWrapper.eq(ObjectUtil.isNotEmpty(searchParam.getTradeType()) && !"all".equals(String.valueOf(searchParam.getTradeType())), "trade_type", searchParam.getTradeType());
        queryWrapper.eq(ObjectUtil.isNotEmpty(searchParam.getStatus()) && !"all".equals(String.valueOf(searchParam.getStatus())), "status", searchParam.getStatus());
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "create_time", searchParam.getCreateTime());
        }
        queryWrapper.orderByDesc("create_time");
        IPage<DeliveryLocalDeliveryOrder> iPage = deliveryLocalDeliveryOrderMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<LocalDeliveryOrderListVo> list = new LinkedList<>();
        JSONObject localDeliveryTypeJSON = LocalDeliveryTypeEnum.getType();
        Map<Integer, FrontWarehouseOrderFallback> frontWarehouseOrders = loadFrontWarehouseOrders(iPage.getRecords());
        for (DeliveryLocalDeliveryOrder item : iPage.getRecords()) {
            LocalDeliveryOrderListVo vo = new LocalDeliveryOrderListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setDeliveryServiceName(getName(item.getDeliveryService(), localDeliveryTypeJSON));
            FrontWarehouseOrderFallback fallback = frontWarehouseOrders.get(item.getTradeId());
            if (StrUtil.isBlank(vo.getBody()) && "front_warehouse".equals(item.getTradeType())) {
                vo.setBody(fallback == null ? "" : fallback.body());
            }
            if (isMerchantFrontWarehouseOrder(item) && isZero(item.getDeliveryMoney()) && fallback != null) {
                vo.setDeliveryMoney(fallback.deliveryMoney());
            }
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
        if (StrUtil.isBlank(vo.getBody()) && "front_warehouse".equals(deliveryLocalDeliveryOrder.getTradeType())) {
            FrontWarehouseOrderFallback fallback = loadFrontWarehouseOrders(Collections.singletonList(deliveryLocalDeliveryOrder))
                    .get(deliveryLocalDeliveryOrder.getTradeId());
            vo.setBody(fallback == null ? "" : fallback.body());
        }
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

    /**
     * Older front-warehouse delivery records may not have copied the order body.
     * Resolve the visible description in two batch queries without making core depend on the addon at compile time.
     */
    private Map<Integer, FrontWarehouseOrderFallback> loadFrontWarehouseOrders(List<DeliveryLocalDeliveryOrder> deliveryOrders) {
        Set<Integer> orderIds = deliveryOrders.stream()
                .filter(item -> "front_warehouse".equals(item.getTradeType())
                        && (StrUtil.isBlank(item.getBody()) || isMerchantFrontWarehouseOrder(item) && isZero(item.getDeliveryMoney())))
                .map(DeliveryLocalDeliveryOrder::getTradeId)
                .filter(Objects::nonNull)
                .filter(id -> id > 0)
                .collect(Collectors.toSet());
        if (orderIds.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, FrontWarehouseOrderFallback> result = new HashMap<>();
        try {
            List<?> orders = selectByOrderIds("com.niu.front_warehouse.mapper.FrontWarehouseOrderMapper", orderIds);
            Set<Integer> missingBodyOrderIds = new HashSet<>(orderIds);
            for (Object order : orders) {
                Integer orderId = readIntegerProperty(order, "getOrderId");
                String body = readStringProperty(order, "getBody");
                BigDecimal deliveryMoney = readBigDecimalProperty(order, "getDeliveryMoney");
                if (orderId != null) {
                    result.put(orderId, new FrontWarehouseOrderFallback(body, deliveryMoney));
                }
                if (orderId != null && StrUtil.isNotBlank(body)) {
                    missingBodyOrderIds.remove(orderId);
                }
            }
            if (missingBodyOrderIds.isEmpty()) {
                return result;
            }

            List<?> goodsList = selectByOrderIds("com.niu.front_warehouse.mapper.FrontWarehouseOrderGoodsMapper", missingBodyOrderIds);
            Map<Integer, StringJoiner> goodsBodies = new HashMap<>();
            for (Object goods : goodsList) {
                Integer orderId = readIntegerProperty(goods, "getOrderId");
                String goodsName = readStringProperty(goods, "getGoodsName");
                if (orderId != null && StrUtil.isNotBlank(goodsName)) {
                    goodsBodies.computeIfAbsent(orderId, ignored -> new StringJoiner("\u3001")).add(goodsName);
                }
            }
            goodsBodies.forEach((orderId, body) -> {
                FrontWarehouseOrderFallback fallback = result.get(orderId);
                BigDecimal deliveryMoney = fallback == null ? null : fallback.deliveryMoney();
                result.put(orderId, new FrontWarehouseOrderFallback(body.toString(), deliveryMoney));
            });
        } catch (Exception ignored) {
            // The front_warehouse addon may be unavailable in a platform-only deployment.
        }
        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private List<?> selectByOrderIds(String mapperClassName, Set<Integer> orderIds) throws ClassNotFoundException {
        Object mapper = SpringContext.bean(Class.forName(mapperClassName));
        if (!(mapper instanceof BaseMapper baseMapper)) {
            return Collections.emptyList();
        }
        return baseMapper.selectList(new QueryWrapper<>().in("order_id", orderIds));
    }

    private Integer readIntegerProperty(Object target, String getterName) {
        Object value = readProperty(target, getterName);
        return value instanceof Number number ? number.intValue() : null;
    }

    private String readStringProperty(Object target, String getterName) {
        Object value = readProperty(target, getterName);
        return value == null ? "" : String.valueOf(value);
    }

    private BigDecimal readBigDecimalProperty(Object target, String getterName) {
        Object value = readProperty(target, getterName);
        if (value instanceof BigDecimal money) {
            return money;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        try {
            return value == null ? null : new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static boolean isMerchantFrontWarehouseOrder(DeliveryLocalDeliveryOrder order) {
        return "front_warehouse".equals(order.getTradeType()) && "merchant".equals(order.getDeliveryService());
    }

    private static boolean isZero(BigDecimal value) {
        return value == null || value.compareTo(BigDecimal.ZERO) == 0;
    }

    private Object readProperty(Object target, String getterName) {
        if (target == null) {
            return null;
        }
        try {
            Method getter = target.getClass().getMethod(getterName);
            return getter.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private record FrontWarehouseOrderFallback(String body, BigDecimal deliveryMoney) {
    }
}
