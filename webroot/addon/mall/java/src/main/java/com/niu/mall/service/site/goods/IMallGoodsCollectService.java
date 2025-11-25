package com.niu.mall.service.site.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.goods.param.MallGoodsCollectParam;
import com.niu.mall.service.site.goods.param.MallGoodsCollectSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.mall.service.site.goods.vo.MallGoodsCollectInfoVo;
import com.niu.mall.service.site.goods.vo.MallGoodsCollectListVo;

/**
 * 商品收藏记录服务接口
 */
public interface IMallGoodsCollectService {


    /**
     * 商品收藏记录列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsCollectListVo>
     */
    PageResult<MallGoodsCollectListVo> list(PageParam pageParam, MallGoodsCollectSearchParam searchParam);

    /**
     * 商品收藏记录详情
     * @param id 主键ID
     * @return ShopGoodsCollectInfoVo
     */
    MallGoodsCollectInfoVo info(Integer id);

    /**
     * 商品收藏记录添加
     * @param addParam 添加参数
     */
    void add(MallGoodsCollectParam addParam);

    /**
     * 商品收藏记录编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MallGoodsCollectParam editParam);

    /**
     * 商品收藏记录删除
     * @param id 主键ID
     */
    void del(Integer id);

}
