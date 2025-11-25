package com.niu.mall.service.site.goods;

import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.entity.goods.MallGoods;
import com.niu.mall.service.site.goods.param.*;
import com.niu.mall.service.site.goods.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 商品服务接口
 */
public interface IMallGoodsService {


    /**
     * 商品列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsListVo>
     */
    PageResult<MallGoodsListVo> page(PageParam pageParam, MallGoodsSearchParam searchParam);

    /**
     * 商品详情
     *
     * @param id 主键ID
     * @return ShopGoodsInfoVo
     */
    MallGoodsInfoVo info(Integer id);

    /**
     * 商品添加
     *
     * @param addParam 添加参数
     */
    void add(MallGoodsParam addParam);

    /**
     * 商品编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MallGoodsParam editParam);

    /**
     * 商品删除
     *
     * @param param
     */
    void del(GetGoodsIdsParam param);

    /**
     * 恢复商品
     *
     * @param param
     */
    void recycle(GetGoodsIdsParam param);

    /**
     * 商品回收站
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsListVo>
     */
    PageResult<MallGoodsListVo> recyclePage(PageParam pageParam, MallGoodsSearchParam searchParam);

    /**
     * 查询商品状态
     *
     */
    Object getStatus();

    /**
     * 修改商品上下架状态
     *
     * @param param
     */
    void editStatus(EditGoodsStatusParam param);

    /**
     * 修改商品排序号
     *
     * @param param
     */
    void editSort(EditGoodsSortParam param);

    /**
     * 获取商品添加/编辑数据
     *
     * @param goodsId
     * @return
     */
    MallGoodsInitVo getInit(Integer goodsId);

    /**
     * 查询商品参与营销活动的数量
     *
     * @param goodsId
     * @return
     */
    Long getActiveGoodsCount(Integer goodsId);

    /**
     * 查询商品参与营销活动的数量
     *
     * @param goodsIds
     * @return
     */
    Long getActiveGoodsCount(Integer[] goodsIds);

    /**
     * 查询商品正在交易中的数量
     *
     * @param skuIds
     * @return
     */
    Long getSkuTransactionCount(Integer[] skuIds);

    /**
     * 查询商品正在交易中的数量
     *
     * @param goodsId
     * @return
     */
    Long getGoodsTransactionCount(Integer goodsId);

    /**
     * 复制商品
     *
     * @param goodsId
     */
    void copy(Integer goodsId);

    /**
     * 商品选择分页列表
     *
     * @param pageParam
     * @param searchParam
     */
    MallSelectGoodsListVo getSelectPage(PageParam pageParam, MallGoodsSelectSearchParam searchParam);

    /**
     * 获取商品选择列表带ku
     */
    List<MallSelectGoodsListVo.ListVo> getSelectGoodsSku(MallGoodsSelectSearchParam searchParam);

    /**
     * 查询商品SKU规格列表
     *
     * @param goodsId
     * @return
     */
    List<MallGoodsSkuListVo> getSkuList(Integer goodsId);

    /**
     * 编辑商品规格列表库存
     *
     * @param param
     */
    void editGoodsListStock(EditGoodsListStockParam param);

    /**
     * 编辑商品规格列表价格
     *
     * @param param
     */
    void editGoodsListPrice(EditGoodsListPriceParam param);

    /**
     * 编辑商品规格列表会员价
     *
     * @param param
     */
    void editGoodsListMemberPrice(EditGoodsListMemberPriceParam param);

    PageResult<GetBuyGoodsSelectVo> getBuyGoodsSelect(PageParam pageParam, BuyGoodsSelectParam param);

    GetBuySkuSelectVo getBuySkuSelect(BuySkuSelectParam param);

    List<GetBuyGoodsSelectedVo> getBuyGoodsSelected(BuyGoodsSelectedParam param);

    /**
     * 获取商品来源列表
     * @return 商品来源列表
     */
    List<Map<String, Object>> getSource();

    /**
     * 批量设置商品
     * @param param 批量设置参数
     */
    void batchSet(GoodsBatchSetParam param);

    /**
     * 获取批量设置字典
     * @return 批量设置类型字典
     */
    Map<String, String> getBatchSetDict();

    MPJQueryWrapper<MallGoods> getBatchAllQuery(Map<String, Object> where, List<Integer> goodsIds);

    Map<String, Object>  sync(Integer goodsId,Integer supplySync);
}
