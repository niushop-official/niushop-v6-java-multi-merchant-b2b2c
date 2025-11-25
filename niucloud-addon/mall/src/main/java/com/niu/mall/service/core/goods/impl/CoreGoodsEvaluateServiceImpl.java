package com.niu.mall.service.core.goods.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.mall.entity.goods.MallGoodsEvaluate;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.mapper.goods.MallGoodsEvaluateMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.goods.ICoreGoodsEvaluateService;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;

@Service
public class CoreGoodsEvaluateServiceImpl implements ICoreGoodsEvaluateService {

    @Resource
    private MallGoodsEvaluateMapper mallGoodsEvaluateMapper;

    @Resource
    private MallOrderMapper mallOrderMapper;

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Override
    public Integer addEvaluate(MallGoodsEvaluate mallGoodsEvaluate) {
        mallGoodsEvaluateMapper.insert(mallGoodsEvaluate);

        if (mallGoodsEvaluate.getOrderId() > 0) {
            MallOrder mallOrder = new MallOrder();
            mallOrder.setIsEvaluate(1);
            mallOrderMapper.update(mallOrder, new QueryWrapper<MallOrder>().eq("order_id", mallGoodsEvaluate.getOrderId()));
        }

        if (mallGoodsEvaluate.getIsAudit() == 2) {
            GoodsAddStatParam addStatParam = new GoodsAddStatParam();
            addStatParam.setGoodsId(mallGoodsEvaluate.getGoodsId());
            addStatParam.setSiteId(mallGoodsEvaluate.getSiteId());
            addStatParam.setEvaluateNum(1);
            coreGoodsStatService.addStat(addStatParam);
        }

        return mallGoodsEvaluate.getEvaluateId();
    }
}
