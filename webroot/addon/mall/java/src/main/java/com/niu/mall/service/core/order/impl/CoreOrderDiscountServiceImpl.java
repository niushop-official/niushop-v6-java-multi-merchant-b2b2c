package com.niu.mall.service.core.order.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.mall.entity.order.MallOrderDiscount;
import com.niu.mall.enums.order.OrderDiscountTypeEnum;
import com.niu.mall.mapper.order.MallOrderDiscountMapper;
import com.niu.mall.service.core.order.ICoreOrderDiscountService;
import com.niu.mall.service.core.order.vo.OrderDiscountDetailsVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CoreOrderDiscountServiceImpl extends ServiceImpl<MallOrderDiscountMapper, MallOrderDiscount> implements ICoreOrderDiscountService {

    @Override
    public void inserAll(List<MallOrderDiscount> list) {
        super.saveBatch(list);
    }

    @Override
    public List<MallOrderDiscount> getDiscountRecords(Integer orderId, List<String> type, List<String> discountType, Integer siteId, Integer status) {
        LambdaQueryWrapper<MallOrderDiscount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MallOrderDiscount::getOrderId, orderId);
        if (siteId != null && siteId > 0) {
            wrapper.eq(MallOrderDiscount::getSiteId, siteId);
        }
        if (!CollectionUtils.isEmpty(type)) {
            wrapper.in(MallOrderDiscount::getType, type);
        }
        if (!CollectionUtils.isEmpty(discountType)) {
            wrapper.in(MallOrderDiscount::getDiscountType, discountType);
        }
        if (status != null) {
            wrapper.eq(MallOrderDiscount::getStatus, status);
        }
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<MallOrderDiscount> batchGetDiscountRecords(List<Integer> orderIds, List<String> type, List<String> discountType, Integer siteId, Integer status) {
        if (CollectionUtils.isEmpty(orderIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<MallOrderDiscount> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(MallOrderDiscount::getOrderId, orderIds);
        if (siteId != null && siteId > 0) {
            wrapper.eq(MallOrderDiscount::getSiteId, siteId);
        }
        if (!CollectionUtils.isEmpty(type)) {
            wrapper.in(MallOrderDiscount::getType, type);
        }
        if (!CollectionUtils.isEmpty(discountType)) {
            wrapper.in(MallOrderDiscount::getDiscountType, discountType);
        }
        if (status != null) {
            wrapper.eq(MallOrderDiscount::getStatus, status);
        }
        return baseMapper.selectList(wrapper);
    }

    @Override
    public OrderDiscountDetailsVo getOrderDiscountDetails(Integer orderId, BigDecimal discountMoney, BigDecimal mallCouponMoney) {
        return getOrderDiscountDetails(orderId, discountMoney, mallCouponMoney, null, null);
    }

    @Override
    public OrderDiscountDetailsVo getOrderDiscountDetails(Integer orderId, BigDecimal discountMoney, BigDecimal mallCouponMoney,
                                                          BigDecimal pointDeductMoney, Integer usePoint) {
        BigDecimal shopDiscountMoney = discountMoney != null ? discountMoney : BigDecimal.ZERO;
        BigDecimal platformCouponMoney = mallCouponMoney != null ? mallCouponMoney : BigDecimal.ZERO;
        BigDecimal orderPointDeductMoney = pointDeductMoney != null ? pointDeductMoney : BigDecimal.ZERO;
        Integer orderUsePoint = usePoint != null ? usePoint : 0;

        OrderDiscountDetailsVo result = new OrderDiscountDetailsVo();
        result.setShopDiscount(buildShopDiscount(orderId, shopDiscountMoney));
        result.setPlatformDiscount(buildPlatformDiscount(orderId, platformCouponMoney, orderPointDeductMoney, orderUsePoint));
        return result;
    }

    private OrderDiscountDetailsVo.DiscountSectionVo buildShopDiscount(Integer orderId, BigDecimal discountMoney) {
        OrderDiscountDetailsVo.DiscountSectionVo shopDiscount = new OrderDiscountDetailsVo.DiscountSectionVo();

        OrderDiscountDetailsVo.DiscountTotalVo shopTotal = new OrderDiscountDetailsVo.DiscountTotalVo();
        shopTotal.setDiscountTypeName("店铺优惠");
        shopTotal.setDiscountMoney(BigDecimal.ZERO);
        shopDiscount.setDiscountTotal(shopTotal);

        List<OrderDiscountDetailsVo.DiscountItemVo> shopList = new ArrayList<>();
        List<MallOrderDiscount> shopRecords = getDiscountRecords(orderId, null,
                OrderDiscountTypeEnum.getShopDiscountType(), null, 1);
        BigDecimal fullReductionMoney = BigDecimal.ZERO;

        for (MallOrderDiscount item : shopRecords) {
            if (OrderDiscountTypeEnum.COUPON.getType().equals(item.getDiscountType())) {
                continue;
            }
            OrderDiscountDetailsVo.DiscountItemVo row = new OrderDiscountDetailsVo.DiscountItemVo();
            row.setDiscountTypeName(OrderDiscountTypeEnum.getNameByType(item.getDiscountType()));
            row.setDiscountMoney(item.getMoney());
            shopList.add(row);
            if (OrderDiscountTypeEnum.FULL_REDUCTION.getType().equals(item.getDiscountType())) {
                fullReductionMoney = item.getMoney();
            }
        }

        OrderDiscountDetailsVo.DiscountItemVo shopCouponRow = new OrderDiscountDetailsVo.DiscountItemVo();
        shopCouponRow.setDiscountTypeName("店铺优惠券");
        shopCouponRow.setDiscountMoney(discountMoney.subtract(fullReductionMoney));
        shopList.add(shopCouponRow);

        shopTotal.setDiscountMoney(discountMoney);
        shopDiscount.setDiscountList(shopList);
        return shopDiscount;
    }

    private OrderDiscountDetailsVo.DiscountSectionVo buildPlatformDiscount(Integer orderId, BigDecimal platformCouponMoney,
                                                                           BigDecimal pointDeductMoney, Integer usePoint) {
        OrderDiscountDetailsVo.DiscountSectionVo platformDiscount = new OrderDiscountDetailsVo.DiscountSectionVo();

        OrderDiscountDetailsVo.DiscountTotalVo platformTotal = new OrderDiscountDetailsVo.DiscountTotalVo();
        platformTotal.setDiscountTypeName("平台优惠");
        platformTotal.setDiscountMoney(BigDecimal.ZERO);
        platformDiscount.setDiscountTotal(platformTotal);

        List<OrderDiscountDetailsVo.DiscountItemVo> platformList = new ArrayList<>();
        List<MallOrderDiscount> platformRecords = getDiscountRecords(orderId, null,
                OrderDiscountTypeEnum.getPlatFormDiscountType(), null, 1);
        BigDecimal platformTotalMoney = BigDecimal.ZERO;
        boolean hasPointDeductRow = false;

        for (MallOrderDiscount item : platformRecords) {
            OrderDiscountDetailsVo.DiscountItemVo row = new OrderDiscountDetailsVo.DiscountItemVo();
            row.setDiscountTypeName(OrderDiscountTypeEnum.getNameByType(item.getDiscountType()));
            row.setDiscountMoney(item.getMoney());
            platformList.add(row);
            platformTotalMoney = platformTotalMoney.add(item.getMoney());
            if (OrderDiscountTypeEnum.POINT_DEDUCT.getType().equals(item.getDiscountType())) {
                hasPointDeductRow = true;
            }
        }

        if (!hasPointDeductRow && pointDeductMoney.compareTo(BigDecimal.ZERO) > 0) {
            OrderDiscountDetailsVo.DiscountItemVo pointRow = new OrderDiscountDetailsVo.DiscountItemVo();
            pointRow.setDiscountTypeName(OrderDiscountTypeEnum.POINT_DEDUCT.getName());
            pointRow.setDiscountMoney(pointDeductMoney);
            platformList.add(0, pointRow);
            platformTotalMoney = platformTotalMoney.add(pointDeductMoney);
        }

        OrderDiscountDetailsVo.DiscountItemVo platformCouponRow = new OrderDiscountDetailsVo.DiscountItemVo();
        platformCouponRow.setDiscountTypeName("平台优惠券");
        platformCouponRow.setDiscountMoney(platformCouponMoney);
        platformList.add(platformCouponRow);
        platformTotalMoney = platformTotalMoney.add(platformCouponMoney);

        platformTotal.setDiscountMoney(platformTotalMoney);
        platformDiscount.setDiscountList(platformList);
        return platformDiscount;
    }
}
