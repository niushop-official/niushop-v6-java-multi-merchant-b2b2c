package com.niu.mall.service.admin.goods.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.niu.mall.entity.goods.MallGoodsSpec;
import com.niu.mall.mapper.goods.MallGoodsSpecMapper;
import com.niu.mall.service.admin.goods.IMallGoodsSpecService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.util.List;


/**
 * 商品规格项/值实现
 */
@Service
public class MallGoodsSpecServiceImpl extends ServiceImpl<MallGoodsSpecMapper, MallGoodsSpec> implements IMallGoodsSpecService {
        
    @Resource
    MallGoodsSpecMapper mallGoodsSpecMapper;

    @Override
    public void insertAll(List<MallGoodsSpec> list) {
        super.saveOrUpdateBatch(list);
    }
}
