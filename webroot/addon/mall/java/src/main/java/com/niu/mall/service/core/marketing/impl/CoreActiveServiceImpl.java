package com.niu.mall.service.core.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.CommonException;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.mall.entity.active.MallActive;
import com.niu.core.entity.active.MallActiveGoods;
import com.niu.mall.enums.active.ActiveStatusEnum;
import com.niu.mall.event.marketing.ActiveEndAfterEvent;
import com.niu.mall.event.marketing.ActiveStartAfterEvent;
import com.niu.core.mapper.active.MallActiveGoodsMapper;
import com.niu.mall.mapper.active.MallActiveMapper;
import com.niu.core.service.core.marketing.ICoreActiveGoodsService;
import com.niu.mall.service.core.marketing.ICoreActiveService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.List;

@Service
public class CoreActiveServiceImpl implements ICoreActiveService {

    @Resource
    private MallActiveGoodsMapper mallActiveGoodsMapper;

    @Resource
    private MallActiveMapper mallActiveMapper;

    @Resource
    private ICoreActiveGoodsService coreActiveGoodsService;

    @Override
    public Integer add(MallActive shopActive, List<MallActiveGoods> mallActiveGoodsList) {

        mallActiveMapper.insert(shopActive);

        if (ObjectUtil.isNotEmpty(mallActiveGoodsList)) {
            for (MallActiveGoods mallActiveGoods : mallActiveGoodsList) {
                mallActiveGoods.setActiveId(shopActive.getActiveId());
            }
            coreActiveGoodsService.insertAll(mallActiveGoodsList);
        }

        //活动开始时间小于当前时间
        if (ObjectUtil.isNotEmpty(shopActive.getStartTime()) && shopActive.getStartTime() != 0 && shopActive.getStartTime() < System.currentTimeMillis() / 1000) {
            this.start(shopActive.getActiveId());
        }
        //活动结束时间大于当前时间
        if (ObjectUtil.isNotEmpty(shopActive.getEndTime()) && shopActive.getEndTime() != 0 && shopActive.getEndTime() <= System.currentTimeMillis() / 1000) {
            this.end(shopActive.getActiveId());
        }

        return shopActive.getActiveId();
    }

    @Override
    public void edit(MallActive shopActive, List<MallActiveGoods> mallActiveGoodsList) {

        if (ObjectUtil.isNotEmpty(shopActive.getEndTime()) && shopActive.getEndTime() != 0 && shopActive.getEndTime() < System.currentTimeMillis() / 1000)
            throw new CommonException("活动已结束，无法编辑");

        mallActiveMapper.updateById(shopActive);

        if (ObjectUtil.isNotEmpty(mallActiveGoodsList)) {

            List<MallActiveGoods> activeGoodsList = mallActiveGoodsMapper.selectList(new QueryWrapper<MallActiveGoods>().eq("active_id", shopActive.getActiveId()));

            if (ObjectUtil.isNotEmpty(activeGoodsList)) {

                for (MallActiveGoods activeGoods : activeGoodsList) {
                    for (MallActiveGoods goods : mallActiveGoodsList) {

                        if (activeGoods.getGoodsId().equals(goods.getGoodsId())) {
                            activeGoods.setActiveGoodsOrderMoney(goods.getActiveGoodsOrderMoney());
                            activeGoods.setActiveGoodsOrderNum(goods.getActiveGoodsOrderNum());
                            activeGoods.setActiveGoodsMemberNum(goods.getActiveGoodsMemberNum());
                            activeGoods.setActiveGoodsSuccessNum(goods.getActiveGoodsSuccessNum());
                        }
                    }
                }

                mallActiveGoodsMapper.delete(new QueryWrapper<MallActiveGoods>().eq("active_id", shopActive.getActiveId()));
            }

            coreActiveGoodsService.insertAll(mallActiveGoodsList);

            //活动开始时间小于当前时间
            if (ObjectUtil.isNotEmpty(shopActive.getStartTime()) && shopActive.getStartTime() != 0 && shopActive.getStartTime() < System.currentTimeMillis() / 1000) {
                this.start(shopActive.getActiveId());
            }
            //活动结束时间大于当前时间
            if (ObjectUtil.isNotEmpty(shopActive.getEndTime()) && shopActive.getEndTime() != 0 && shopActive.getEndTime() <= System.currentTimeMillis() / 1000) {
                this.end(shopActive.getActiveId());
            }
        }
    }

    @Override
    public void del(Integer activeId, Integer siteId, Integer isForce) {

        MallActive shopActive = mallActiveMapper.selectOne(new QueryWrapper<MallActive>().eq("active_id", activeId).eq("site_id", siteId));
        if (ObjectUtil.isEmpty(shopActive)) throw new CommonException("活动不存在");

        if (isForce == 0 && shopActive.getActiveStatus().equals(ActiveStatusEnum.ACTIVE.getStatus()))
            throw new CommonException("活动正在进行中，无法删除");
        mallActiveMapper.deleteById(shopActive.getActiveId());
        mallActiveGoodsMapper.delete(new QueryWrapper<MallActiveGoods>().eq("active_id", shopActive.getActiveId()));
    }

    @Override
    public void close(Integer activeId, Integer siteId) {

        MallActive shopActive = new MallActive();
        shopActive.setActiveStatus(ActiveStatusEnum.CLOSE.getStatus());
        mallActiveMapper.update(shopActive, new QueryWrapper<MallActive>().eq("active_id", activeId).eq("site_id", siteId).eq("active_status", ActiveStatusEnum.ACTIVE.getStatus()));

        MallActiveGoods mallActiveGoods = new MallActiveGoods();
        mallActiveGoods.setActiveGoodsStatus(ActiveStatusEnum.CLOSE.getStatus());
        mallActiveGoodsMapper.update(mallActiveGoods, new QueryWrapper<MallActiveGoods>().eq("active_id", activeId).eq("site_id", siteId));
    }

    @Override
    public void start(Integer activeId) {

        MallActive shopActive = new MallActive();
        shopActive.setActiveStatus(ActiveStatusEnum.ACTIVE.getStatus());
        mallActiveMapper.update(shopActive, new QueryWrapper<MallActive>().eq("active_id", activeId).eq("active_status", ActiveStatusEnum.NOT_ACTIVE.getStatus()).le("start_time", System.currentTimeMillis() / 1000));

        MallActiveGoods mallActiveGoods = new MallActiveGoods();
        mallActiveGoods.setActiveGoodsStatus(ActiveStatusEnum.ACTIVE.getStatus());
        mallActiveGoodsMapper.update(mallActiveGoods, new QueryWrapper<MallActiveGoods>().eq("active_id", activeId));

        // 活动开启之后事件
        MallActive active = mallActiveMapper.selectById(activeId);
        ActiveStartAfterEvent event = new ActiveStartAfterEvent();
        event.setSiteId(active.getSiteId());
        event.addAppSign("mall");
        event.setName("ActiveStartAfterEvent");
        event.setActive(active);
        EventAndSubscribeOfPublisher.publishAll(event);
    }

    @Override
    public void end(Integer activeId) {

        MallActive shopActive = new MallActive();
        shopActive.setActiveStatus(ActiveStatusEnum.END.getStatus());
        mallActiveMapper.update(shopActive, new QueryWrapper<MallActive>().eq("active_id", activeId).eq("active_status", ActiveStatusEnum.ACTIVE.getStatus()).le("end_time", System.currentTimeMillis() / 1000));

        MallActiveGoods mallActiveGoods = new MallActiveGoods();
        mallActiveGoods.setActiveGoodsStatus(ActiveStatusEnum.END.getStatus());
        mallActiveGoodsMapper.update(mallActiveGoods, new QueryWrapper<MallActiveGoods>().eq("active_id", activeId));

        // 活动开启之后事件
        MallActive active = mallActiveMapper.selectById(activeId);
        ActiveEndAfterEvent event = new ActiveEndAfterEvent();
        event.setSiteId(active.getSiteId());
        event.addAppSign("mall");
        event.setName("ActiveEndAfterEvent");
        event.setActive(active);
        EventAndSubscribeOfPublisher.publishAll(event);
    }
}
