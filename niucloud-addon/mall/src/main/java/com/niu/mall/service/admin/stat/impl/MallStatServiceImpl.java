package com.niu.mall.service.admin.stat.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.goods.MallGoods;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.enums.order.OrderGoodsRefundEnum;
import com.niu.mall.enums.order.OrderStatusEnum;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.admin.stat.IMallStatService;
import com.niu.mall.service.admin.stat.vo.GoodsStatVo;
import com.niu.mall.service.admin.stat.vo.OrderStatVo;
import com.niu.mall.service.core.shop.ICoreStatService;
import com.niu.mall.service.core.shop.vo.StatDataVo;
import com.niu.mall.service.core.shop.vo.StatVo;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;


/**
 * 【请填写功能名称】实现
 */
@Service
public class MallStatServiceImpl implements IMallStatService {
        
    @Resource
    ICoreStatService coreStatService;

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    MallGoodsMapper mallGoodsMapper;

    /**
     * 查询统计总和
     *
     * @return
     */
    @Override
    public StatVo getStat() {
        return coreStatService.getStat(RequestUtils.siteId());
    }

    /**
     * 查询统计总和
     *
     * @param startTime
     * @param endtTime
     * @return
     */
    @Override
    public StatVo getStat(Long startTime, Long endtTime) {
        return coreStatService.getStat(RequestUtils.siteId());
    }

    /**
     * 获取时间区间内天统计数据
     *
     * @param startTime
     * @param endtTime
     * @return
     */
    @Override
    public StatDataVo getStatData(String startTime, String endtTime) {
        return coreStatService.getStatData(RequestUtils.siteId(), startTime, endtTime);
    }

    /**
     * 查询订单统计
     *
     * @return
     */
    @Override
    public OrderStatVo orderStat() {
        Integer siteId = RequestUtils.siteId();
        OrderStatVo vo = new OrderStatVo();

        vo.setWaitPayOrder(ObjectUtil.defaultIfNull(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                .eq("site_id", siteId)
                .eq("status", OrderStatusEnum.WAIT_PAY.getStatus())
        ), 0L));

        vo.setWaitDeliveryOrder(ObjectUtil.defaultIfNull(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                .eq("site_id", siteId)
                .eq("status", OrderStatusEnum.WAIT_DELIVERY.getStatus())
        ), 0L));

        vo.setWaitTakeOrder(ObjectUtil.defaultIfNull(mallOrderMapper.selectCount(new QueryWrapper<MallOrder>()
                .eq("site_id", siteId)
                .eq("status", OrderStatusEnum.WAIT_TAKE.getStatus())
        ), 0L));

        vo.setRefundOrder(ObjectUtil.defaultIfNull(mallOrderGoodsMapper.selectCount(new QueryWrapper<MallOrderGoods>()
                .eq("site_id", siteId)
                .eq("status", OrderGoodsRefundEnum.REFUNDING.getStatus())
        ), 0L));

        return vo;
    }

    /**
     * 查询商品统计
     *
     * @return
     */
    @Override
    public GoodsStatVo goodsStat() {
        Integer siteId = RequestUtils.siteId();
        GoodsStatVo vo = new GoodsStatVo();

        vo.setSaleGoodsNum(ObjectUtil.defaultIfNull(mallGoodsMapper.selectCount(new QueryWrapper<MallGoods>()
                .eq("site_id", siteId)
                .eq("status", 1)
        ), 0L));

        vo.setWarehouseGoodsNum(ObjectUtil.defaultIfNull(mallGoodsMapper.selectCount(new QueryWrapper<MallGoods>()
                .eq("site_id", siteId)
                .eq("status", 0)
        ), 0L));

        return vo;
    }
}
