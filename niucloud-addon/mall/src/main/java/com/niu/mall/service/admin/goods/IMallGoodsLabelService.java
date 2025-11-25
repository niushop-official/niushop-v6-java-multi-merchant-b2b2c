package com.niu.mall.service.admin.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.admin.goods.param.EditGoodsLabelSortParam;
import com.niu.mall.service.admin.goods.param.EditGoodsLabelStatusParam;
import com.niu.mall.service.admin.goods.param.MallGoodsLabelParam;
import com.niu.mall.service.admin.goods.param.MallGoodsLabelSearchParam;
import com.niu.core.common.domain.PageParam;
import com.niu.mall.service.admin.goods.vo.MallGoodsLabelInfoVo;
import com.niu.mall.service.admin.goods.vo.MallGoodsLabelListVo;

import java.util.List;

/**
 * 商品标签服务接口
 */
public interface IMallGoodsLabelService {

    /**
     * 商品标签列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsLabelListVo>
     */
    PageResult<MallGoodsLabelListVo> page(PageParam pageParam, MallGoodsLabelSearchParam searchParam);

    /**
     * 商品标签列表
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsLabelListVo>
     */
    List<MallGoodsLabelListVo> list(MallGoodsLabelSearchParam searchParam);

    /**
     * 商品标签详情
     * @param id 主键ID
     * @return ShopGoodsLabelInfoVo
     */
    MallGoodsLabelInfoVo info(Integer id);

    /**
     * 商品标签添加
     * @param addParam 添加参数
     */
    void add(MallGoodsLabelParam addParam);

    /**
     * 商品标签编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MallGoodsLabelParam editParam);

    /**
     * 商品标签删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 修改商品标签排序
     *
     * @param editParam
     */
    void modifySort(EditGoodsLabelSortParam editParam);

    /**
     *  修改商品标签状态
     * @param editParam
     */
    void modifyStatus(EditGoodsLabelStatusParam editParam);

    /**
     * 商品标签复制
     * @param id 主键ID
     */
    void copy(Integer id);
}
