package com.niu.mall.service.admin.stat;

import com.niu.mall.service.admin.stat.vo.GoodsStatVo;
import com.niu.mall.service.admin.stat.vo.OrderStatVo;
import com.niu.mall.service.core.shop.vo.StatDataVo;
import com.niu.mall.service.core.shop.vo.StatVo;

/**
 * 【请填写功能名称】服务接口
 */
public interface IMallStatService {

    /**
     * 查询统计总和
     *
     * @return
     */
    StatVo getStat();

    /**
     * 查询统计总和
     *
     * @param startTime
     * @param endtTime
     * @return
     */
    StatVo getStat(Long startTime, Long endtTime);

    /**
     * 获取时间区间内天统计数据
     *
     * @param startTime
     * @param endtTime
     * @return
     */
    StatDataVo getStatData(String startTime, String endtTime);

    /**
     * 查询订单统计
     *
     * @return
     */
    OrderStatVo orderStat();

    /**
     * 查询商品统计
     *
     * @return
     */
    GoodsStatVo goodsStat();
}
