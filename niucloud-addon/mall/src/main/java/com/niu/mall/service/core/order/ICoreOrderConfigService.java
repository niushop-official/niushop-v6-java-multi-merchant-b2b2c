package com.niu.mall.service.core.order;

import com.niu.mall.entity.order.config.MallOrderDelaySpear;
import com.niu.mall.entity.order.config.MallOrderEvaluate;
import com.niu.mall.entity.order.config.MallOrderFinish;
import com.niu.mall.entity.order.config.MallOrderRefundInfo;
import com.niu.mall.service.core.order.param.OrderConfigParam;
import com.niu.mall.service.core.order.vo.MallOrderCloseVo;
import com.niu.mall.service.core.order.vo.OrderConfigVo;

/**
 * 订单配置接口
 */
public interface ICoreOrderConfigService {

    /**
     * 订单设置
     *
     * @param orderConfigParam
     */
    void setConfig(OrderConfigParam orderConfigParam);

    /**
     * 获取订单设置
     *
     * @return
     */
    OrderConfigVo getConfig();

    /**
     * 订单完成配置
     *
     * @return
     */
    MallOrderFinish orderConfirm();

    MallOrderEvaluate getEvaluateConfig();


    MallOrderCloseVo orderClose();

    MallOrderDelaySpear orderDelaySpear();

    /**
     * 获取快捷退款配置
     *
     * @return
     */
    MallOrderRefundInfo refundAudit();
}
