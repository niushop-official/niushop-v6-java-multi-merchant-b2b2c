package com.niu.mall.service.core.order;

import com.niu.mall.entity.order.config.MallOrderDelaySpear;
import com.niu.mall.entity.order.config.MallOrderEvaluate;
import com.niu.mall.entity.order.config.MallOrderFinish;
import com.niu.mall.entity.order.config.MallOrderInvoice;
import com.niu.mall.entity.order.config.MallOrderRefund;
import com.niu.mall.service.core.order.param.OrderConfigParam;
import com.niu.mall.service.core.order.vo.OrderConfigVo;
import com.niu.mall.service.core.order.vo.MallOrderCloseVo;

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
}
