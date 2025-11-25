package com.niu.mall.service.core.order;

import com.niu.mall.entity.order.config.MallOrderEvaluate;
import com.niu.mall.entity.order.config.MallOrderFinish;
import com.niu.mall.entity.order.config.MallOrderRefund;
import com.niu.mall.service.core.order.param.OrderConfigParam;
import com.niu.mall.service.core.order.param.SiteOrderConfigParam;
import com.niu.mall.service.core.order.vo.MallOrderCloseVo;
import com.niu.mall.service.core.order.vo.OrderConfigVo;
import com.niu.mall.service.core.order.vo.SiteOrderConfigVo;

/**
 * 订单配置接口
 */
public interface ICoreSiteOrderConfigService {

    /**
     * 订单设置
     *
     * @param orderConfigParam
     */
    void setConfig(SiteOrderConfigParam orderConfigParam);

    /**
     * 获取订单设置
     *
     * @return
     */
    SiteOrderConfigVo getConfig(Integer siteId);
}
