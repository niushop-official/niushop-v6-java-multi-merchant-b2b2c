package com.niu.mall.service.site.marketing.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.mall.entity.active.MallActiveGoods;
import com.niu.mall.mapper.active.MallActiveGoodsMapper;
import com.niu.mall.service.site.marketing.IActiveGoodsService;
import org.springframework.stereotype.Service;
import java.util.List;


/**
 * 店铺营销活动实现
 */
@Service
public class ActiveGoodsServiceImpl extends ServiceImpl<MallActiveGoodsMapper, MallActiveGoods> implements IActiveGoodsService {


    @Override
    public void insertAll(List<MallActiveGoods> list) {
            super.saveOrUpdateBatch(list);
    }
}
