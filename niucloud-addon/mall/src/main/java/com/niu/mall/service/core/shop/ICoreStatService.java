package com.niu.mall.service.core.shop;

import com.niu.mall.service.core.shop.param.AddStatParam;
import com.niu.mall.service.core.shop.param.DelStatParam;
import com.niu.mall.service.core.shop.vo.StatDataVo;
import com.niu.mall.service.core.shop.vo.StatVo;

public interface ICoreStatService {

    /**
     * 添加统计数据
     *
     * @param siteId
     * @param data
     */
    void addStat(Integer siteId, AddStatParam data);

    /**
     * 删除统计数据
     *
     * @param siteId
     * @param data
     */
    void delStat(Integer siteId, DelStatParam data);

    /**
     * 查询统计总和
     * @param siteId
     * @return
     */
    StatVo getStat(Integer siteId);

    /**
     * 获取时间区间内天统计数据
     *
     * @param siteId
     * @param startTime
     * @param endtTime
     * @return
     */
    StatDataVo getStatData(Integer siteId, String startTime, String endtTime);
    /**
     * 获取时间区间内天统计数据
     *
     * @param siteId
     * @param startTime
     * @param endtTime
     * @return
     */
    StatVo getStatDate(Integer siteId, Long startTime, Long endtTime);
}
