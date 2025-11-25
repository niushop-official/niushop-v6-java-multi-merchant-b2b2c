package com.niu.mall.service.site.order;

import com.niu.mall.service.core.order.param.OrderConfigParam;
import com.niu.mall.service.core.order.param.SiteOrderConfigParam;
import com.niu.mall.service.core.order.vo.OrderConfigVo;
import com.niu.mall.service.core.order.vo.SiteOrderConfigVo;

/**
 * 订单设置接口
 */
public interface IOrderConfigService {

    void setConfig(SiteOrderConfigParam orderConfigParam);

    SiteOrderConfigVo getConfig();

}
