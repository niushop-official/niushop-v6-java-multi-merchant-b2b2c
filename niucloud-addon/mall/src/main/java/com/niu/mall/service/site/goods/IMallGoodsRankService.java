package com.niu.mall.service.site.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.goods.param.*;
import com.niu.mall.service.site.goods.vo.MallGoodsRankInfoVo;
import com.niu.mall.service.site.goods.vo.MallGoodsRankListVo;
import com.niu.mall.service.site.goods.vo.MallGoodsRankOptionData;
import com.niu.mall.service.site.goods.vo.MallGoodsRankSelectPageResult;
import com.niu.mall.service.core.goods.param.ShopGoodsRankConfigParam;
import com.niu.mall.service.core.goods.vo.ShopGoodsRankConfigVo;

/**
 * 商品排行榜服务接口
 */
public interface IMallGoodsRankService {

    /**
     * 商品排行榜列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsRankListVo>
     */
    PageResult<MallGoodsRankListVo> page(PageParam pageParam, MallGoodsRankSearchParam searchParam);

    /**
     * 商品排行榜详情
     *
     * @param id 主键ID
     * @return ShopGoodsRankInfoVo
     */
    MallGoodsRankInfoVo info(Integer id);

    /**
     * 商品排行榜添加
     *
     * @param addParam 添加参数
     * @return 榜单ID
     */
    Integer add(MallGoodsRankParam addParam);

    /**
     * 商品排行榜编辑
     *
     * @param id        主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MallGoodsRankParam editParam);

    MallGoodsRankOptionData getOptionData();

    /**
     * 商品排行榜删除
     *
     * @param id 主键ID
     */
    void del(Integer id);

    void setGoodsRankConfig(ShopGoodsRankConfigParam param);

    ShopGoodsRankConfigVo getGoodsRankConfig();

    void editSort(MallGoodsRankEditSortParam param);

    void batchDelete(MallGoodsRankBatchDeleteParam param);

    MallGoodsRankSelectPageResult getSelectPage(PageParam pageParam, MallGoodsRankSelectPageParam param);

    void modifyStatus(MallGoodsRankModifyStatusParam param);
}
