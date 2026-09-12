package com.niu.mall.service.site.goods;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.core.entity.goods.MallGoodsSku;
import com.niu.mall.service.site.goods.param.MallGoodsSkuSearchParam;
import com.niu.mall.service.api.goods.vo.MallGoodsSkuVo;

import java.util.List;

/**
 * 商品规格服务接口
 */
public interface IMallGoodsSkuService extends IService<MallGoodsSku> {

    void insertAll(List<MallGoodsSku> list);

    List<MallGoodsSkuVo> list(MallGoodsSkuSearchParam searchParam);
}
