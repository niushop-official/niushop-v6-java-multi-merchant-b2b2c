package com.niu.core.service.admin.goods;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.niu.core.service.admin.goods.param.EditGoodsCategorySortParam;
import com.niu.core.service.admin.goods.param.GoodsCategoryParam;
import com.niu.core.service.admin.goods.param.GoodsCategorySearchParam;
import com.niu.core.service.admin.goods.vo.GoodsCategoryInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsCategoryListVo;

import java.util.List;

/**
 * 商品分类服务接口
 */
public interface IGoodsCategoryService {

    /**
     * 商品分类列表
     * @return List<ShopGoodsCategoryListVo>
     */
    List<GoodsCategoryListVo> list(GoodsCategorySearchParam searchParam);

    /**
     * 获取商品分类树
     *
     * @return
     */
    JSONArray tree();

    /**
     * 获取商品分类树
     *
     * @return
     */
    JSONArray tree(GoodsCategorySearchParam searchParam);

    /**
     * 获取商品分类树结构供弹框调用（仅显示）
     * 对齐 PHP CategoryService::getTreeComponents
     */
    JSONArray components(GoodsCategorySearchParam searchParam);

    /**
     * 商品分类详情
     *
     * @param id 主键ID
     * @return ShopGoodsCategoryInfoVo
     */
    GoodsCategoryInfoVo info(Integer id);

    /**
     * 商品分类添加
     *
     * @param addParam 添加参数
     */
    void add(GoodsCategoryParam addParam);

    /**
     * 商品分类编辑
     *
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, GoodsCategoryParam editParam);

    /**
     * 修改分类排序
     *
     * @param editParam
     */
    void editGoodsCategorySort(EditGoodsCategorySortParam editParam);

    /**
     * 商品分类删除
     *
     * @param id 主键ID
     */
    void del(Integer id);
}
