package com.niu.core.service.site.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.goods.param.GoodsServiceParam;
import com.niu.core.service.site.goods.param.GoodsServiceSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.core.service.site.goods.vo.GoodsServiceInfoVo;
import com.niu.core.service.site.goods.vo.GoodsServiceListVo;

import java.util.List;

/**
 * 商品服务服务接口
 */
public interface IGoodsServiceService {

    /**
     * 商品服务分页列表
     *
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsServiceListVo>
     */
    PageResult<GoodsServiceListVo> page(PageParam pageParam, GoodsServiceSearchParam searchParam);


    /**
     * 商品服务列表
     *
     * @param searchParam 搜索参数
     * @return List<ShopGoodsServiceListVo>
     */
    List<GoodsServiceListVo> list(GoodsServiceSearchParam searchParam);

    /**
     * 商品服务详情
     * @param id 主键ID
     * @return ShopGoodsServiceInfoVo
     */
    GoodsServiceInfoVo info(Integer id);

    /**
     * 商品服务添加
     * @param addParam 添加参数
     */
    void add(GoodsServiceParam addParam);

    /**
     * 商品服务编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, GoodsServiceParam editParam);

    /**
     * 商品服务删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 获取符合条件的插件多个数据
     *
     * @param addon 插件标识
     * @return 商品服务列表
     */
    List<GoodsServiceListVo> getAddonServiceList(String addon);

}
