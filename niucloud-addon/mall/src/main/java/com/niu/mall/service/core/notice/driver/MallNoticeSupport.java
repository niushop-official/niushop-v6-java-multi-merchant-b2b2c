package com.niu.mall.service.core.notice.driver;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.service.core.sys.ICoreSysConfigService;
import com.niu.core.service.core.user.ICoreUserService;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderRefund;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.mapper.order.MallOrderRefundMapper;
import com.niu.mall.service.core.order.ICoreOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 商城消息模板公共依赖
 */
@Component
public class MallNoticeSupport {

    @Resource
    private ICoreOrderService coreOrderService;
    @Resource
    private MallOrderMapper mallOrderMapper;
    @Resource
    private MallOrderRefundMapper mallOrderRefundMapper;
    @Resource
    private ICoreSysConfigService coreSysConfigService;
    @Resource
    private ICoreUserService coreUserService;

    public MallOrder resolveOrder(Object value) {
        if (value instanceof MallOrder order) {
            return order;
        }
        if (value == null) {
            return null;
        }
        Integer orderId = parseInt(value);
        if (orderId == null || orderId <= 0) {
            return null;
        }
        return coreOrderService.orderInfo(orderId);
    }

    public MallOrderRefund resolveRefund(Object refundValue, Object refundIdValue, Object refundNoValue) {
        if (refundValue instanceof MallOrderRefund refund) {
            return refund;
        }
        if (refundNoValue != null && StrUtil.isNotBlank(refundNoValue.toString())) {
            return mallOrderRefundMapper.selectOne(new QueryWrapper<MallOrderRefund>()
                    .eq("order_refund_no", refundNoValue.toString())
                    .last("limit 1"));
        }
        Integer refundId = parseInt(refundIdValue);
        if (refundId != null && refundId > 0) {
            return mallOrderRefundMapper.selectById(refundId);
        }
        return null;
    }

    public MallOrder loadOrderByRefund(MallOrderRefund refund) {
        if (refund == null || refund.getOrderId() == null) {
            return null;
        }
        return mallOrderMapper.selectById(refund.getOrderId());
    }

    public Integer getNoticeUid(Integer siteId) {
        return coreUserService.getNoticeUid(siteId);
    }

    public ICoreSysConfigService getCoreSysConfigService() {
        return coreSysConfigService;
    }

    private Integer parseInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
