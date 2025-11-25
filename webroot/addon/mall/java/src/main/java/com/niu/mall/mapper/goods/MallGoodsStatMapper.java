package com.niu.mall.mapper.goods;

import com.github.yulichang.base.MPJBaseMapper;
import com.niu.mall.entity.goods.MallGoodsStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品数据统计 Mapper
 */
@Mapper
public interface MallGoodsStatMapper extends MPJBaseMapper<MallGoodsStat> {
    void insertBatchSomeColumn(@Param("list") List<MallGoodsStat> batchList);
}
