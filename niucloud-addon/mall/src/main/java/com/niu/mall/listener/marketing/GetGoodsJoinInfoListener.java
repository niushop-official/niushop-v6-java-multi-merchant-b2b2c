

package com.niu.mall.listener.marketing;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.context.listener.AbstractListener;
import com.niu.mall.entity.active.MallActive;
import com.niu.mall.entity.active.MallActiveGoods;
import com.niu.mall.entity.goods.MallGoods;

import com.niu.mall.enums.active.ActiveClassEnum;
import com.niu.mall.enums.active.ActiveGoodsTypeEnum;
import com.niu.mall.enums.common.CommonActiveEnum;
import com.niu.mall.event.marketing.GoodsJoinInfoEventDefiner;

import com.niu.mall.mapper.active.MallActiveGoodsMapper;
import com.niu.mall.mapper.active.MallActiveMapper;
import com.niu.mall.mapper.goods.MallGoodsMapper;
import com.niu.mall.service.site.goods.IMallGoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@EventCallback("mall")
@Component
public class GetGoodsJoinInfoListener extends GoodsJoinInfoEventDefiner {
    @Resource
    MallGoodsMapper mallGoodsMapper;
    private IMallGoodsService siteMallGoods() {
        return SpringContext.bean(IMallGoodsService.class);
    }

    private Map<String, Object> getCommonActiveDict() {
        @SuppressWarnings("unchecked")
        Map<String, Object> dict = (Map<String, Object>) CommonActiveEnum.getActiveShort("");
        return dict;
    }

    @Override
    public GoodsJoinInfoResult handleCallback(GoodsJoinInfoEvent event) {
        GoodsJoinInfoResult result = new GoodsJoinInfoResult();

        List<Integer> goodsIds = event.getGoodsIds();
        Integer siteId = event.getSiteId();
        Integer isGetCount = event.getIsGetCount() != null ? event.getIsGetCount() : 0;
        Integer isAll = event.getIsAll() != null ? event.getIsAll() : 0;

        List<Integer> conditionGoodsIds;
        if (isAll == 1) {
            if (event.getWhere() != null && !event.getWhere().isEmpty()) {
                MPJQueryWrapper<MallGoods> batchAllQuery = siteMallGoods().getBatchAllQuery(event.getWhere(), goodsIds);
                List<MallGoods> goodsList = mallGoodsMapper.selectList(batchAllQuery);
                conditionGoodsIds = goodsList.stream()
                        .map(MallGoods::getGoodsId)
                        .collect(Collectors.toList());
            } else {
                conditionGoodsIds = goodsIds;
            }
        } else {
            conditionGoodsIds = goodsIds;
        }

        MallActiveGoodsMapper activeGoodsMapper = SpringContext.bean(MallActiveGoodsMapper.class);
        MallActiveMapper activeMapper = SpringContext.bean(MallActiveMapper.class);

        QueryWrapper<MallActiveGoods> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.eq("active_goods_status", "active");
        queryWrapper.in("active_goods_type", ActiveGoodsTypeEnum.GOODS_SINGLE.getType(), ActiveGoodsTypeEnum.GOODS_INDEPENDENT.getType());
        queryWrapper.eq("active_class", ActiveClassEnum.DISCOUNT.getType());
        if(!conditionGoodsIds.isEmpty()){
            queryWrapper.in("goods_id", conditionGoodsIds);
        }

        if (isGetCount == 1) {
            // 如果只需要获取数量，返回计数
            result.setCount(activeGoodsMapper.selectCount(queryWrapper).intValue());
            return result;
        }

        // 查询活动商品列表
        List<MallActiveGoods> activeGoodsList = activeGoodsMapper.selectList(queryWrapper);

        // 获取活动信息
        Map<Integer, MallActive> activeMap = new HashMap<>();
        if (!activeGoodsList.isEmpty()) {
            List<Integer> activeIds = activeGoodsList.stream()
                    .map(MallActiveGoods::getActiveId)
                    .distinct()
                    .collect(Collectors.toList());

            QueryWrapper<MallActive> activeQueryWrapper = new QueryWrapper<>();
            activeQueryWrapper.select("active_id,active_name,active_desc,start_time,end_time");
            activeQueryWrapper.in("active_id", activeIds);
            List<MallActive> activeList = activeMapper.selectList(activeQueryWrapper);

            activeMap = activeList.stream()
                    .collect(Collectors.toMap(MallActive::getActiveId, active -> active));
        }

        // 构建返回结果
        Map<Integer, Map<String, Object>> returnMap = new HashMap<>();

        // 获取活动字典信息（这里需要根据实际的字典服务来获取）
        Map<String, Object> commonActiveDict = getCommonActiveDict();

        for (MallActiveGoods activeGoods : activeGoodsList) {
            Integer goodsId = activeGoods.getGoodsId();
            Integer activeId = activeGoods.getActiveId();

            if (!returnMap.containsKey(goodsId)) {
                returnMap.put(goodsId, new HashMap<>());
            }

            MallActive active = activeMap.get(activeId);
            String activeName = active != null ? active.getActiveName() : "限时折扣";

            Map<String, Object> activeInfo = new HashMap<>();
            activeInfo.put("join_id", activeId);
            activeInfo.put("join_type", "active_" + activeGoods.getActiveClass());
            activeInfo.put("short", commonActiveDict.get(activeGoods.getActiveClass()));
            activeInfo.put("name", activeName);

            returnMap.get(goodsId).put("active_" + activeId, activeInfo);
        }
        result.setInfo(returnMap);
        return result;
    }
}
