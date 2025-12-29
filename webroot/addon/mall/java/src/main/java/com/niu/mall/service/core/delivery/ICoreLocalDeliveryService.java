package com.niu.mall.service.core.delivery;

import com.niu.core.entity.site.Site;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;

import java.util.List;

/**
 * 配送服务接口
 */
public interface ICoreLocalDeliveryService {

    /**
     * 配送费用计算
     *
     * @return GoodsCategoryConfigVo
     */
    void calculate(OrderCreateDataVo vo);

    /**
     * 获取当前位置支持配送的站点id列表
     * @param siteList
     * @param latitude
     * @param longitude
     * @return
     */
    List<Integer> checkLocationInArea(List<Site> siteList, String latitude, String  longitude);
}
