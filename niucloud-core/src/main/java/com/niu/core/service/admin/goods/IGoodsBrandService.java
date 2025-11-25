package com.niu.core.service.admin.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.goods.param.EditGoodsBrandSortParam;
import com.niu.core.service.admin.goods.param.GoodsBrandParam;
import com.niu.core.service.admin.goods.param.GoodsBrandSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.admin.goods.vo.GoodsBrandInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsBrandListVo;

import java.util.List;

/**
 * 商品品牌服务接口
 */
public interface IGoodsBrandService {


    /**
     * 商品品牌列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsBrandListVo>
     */
    PageResult<GoodsBrandListVo> page(PageParam pageParam, GoodsBrandSearchParam searchParam);

    /**
     * 商品品牌列表
     *
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsBrandListVo>
     */
    List<GoodsBrandListVo> list(GoodsBrandSearchParam searchParam);

    /**
     * 商品品牌详情
     *
     * @param id 主键ID
     * @return ShopGoodsBrandInfoVo
     */
    GoodsBrandInfoVo info(Integer id);

    /**
     * 商品品牌添加
     *
     * @param addParam 添加参数
     */
    void add(GoodsBrandParam addParam);

    /**
     * 商品品牌编辑
     *
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, GoodsBrandParam editParam);

    /**
     * 商品品牌删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 修改品牌排序
     *
     * @param editParam
     */
    void modifySort(EditGoodsBrandSortParam editParam);

    /**
     * 店铺转移到平台
     * @param brandId
     */
    void move(Integer brandId);
}
