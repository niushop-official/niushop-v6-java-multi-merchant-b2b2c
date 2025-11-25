package com.niu.mall.service.core.marketing;


import com.niu.mall.entity.active.MallActive;
import com.niu.mall.entity.active.MallActiveGoods;

import java.util.List;

/**
 * 营销活动接口
 */
public interface ICoreActiveService {

    /**
     * 添加营销活动
     * @param shopActive
     * @param mallActiveGoodsList
     */
    Integer add(MallActive shopActive, List<MallActiveGoods> mallActiveGoodsList);

    /**
     * 编辑营销活动
     * @param shopActive
     * @param mallActiveGoodsList
     */
    void edit(MallActive shopActive, List<MallActiveGoods> mallActiveGoodsList);

    /**
     * 删除营销活动
     * @param activeId
     * @param siteId
     * @param isForce
     */
    void del(Integer activeId, Integer siteId, Integer isForce);

    /**
     * 活动关闭
     * @param activeId
     * @param siteId
     */
    void close(Integer activeId, Integer siteId);

    /**
     * 活动开启
     * @param activeId
     */
    void start(Integer activeId);

    /**
     * 活动关闭
     * @param activeId
     */
    void end(Integer activeId);


}
