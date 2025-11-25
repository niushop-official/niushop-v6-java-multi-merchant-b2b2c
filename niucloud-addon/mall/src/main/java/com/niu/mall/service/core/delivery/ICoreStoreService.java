package com.niu.mall.service.core.delivery;

import com.niu.core.service.core.delivery.param.LatlngParam;
import com.niu.core.service.core.delivery.vo.StoreListVo;
import com.niu.mall.service.core.order.vo.OrderCreateDataVo;

import java.util.List;

/**
 * 配送服务接口
 */
public interface ICoreStoreService {

    /**
     * 配送费用计算
     *
     * @return GoodsCategoryConfigVo
     */
    void calculate(OrderCreateDataVo vo);

    /**
     * 获取自提点列表
     *
     */
    List<StoreListVo> getStoreList(Integer siteId, LatlngParam latlng);
}
