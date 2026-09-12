package com.niu.core.service.admin.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.goods.param.EditGoodsLabelSortParam;
import com.niu.core.service.admin.goods.param.EditGoodsLabelStatusParam;
import com.niu.core.service.admin.goods.param.GoodsLabelParam;
import com.niu.core.service.admin.goods.param.GoodsLabelSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsLabelInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsLabelListVo;

import java.util.List;

/**
 * 商品标签服务接口
 * 对应 PHP: app\service\admin\basic\admin\LabelService
 */
public interface IGoodsLabelService {

    /**
     * 商品标签分页列表
     */
    PageResult<GoodsLabelListVo> page(PageParam pageParam, GoodsLabelSearchParam searchParam);

    /**
     * 商品标签列表（启用状态）
     */
    List<GoodsLabelListVo> list(GoodsLabelSearchParam searchParam);

    /**
     * 商品标签详情
     */
    GoodsLabelInfoVo info(Integer id);

    /**
     * 添加商品标签
     */
    Integer add(GoodsLabelParam addParam);

    /**
     * 编辑商品标签
     */
    void edit(Integer id, GoodsLabelParam editParam);

    /**
     * 删除商品标签
     */
    void del(Integer id);

    /**
     * 修改排序
     */
    void modifySort(EditGoodsLabelSortParam editParam);

    /**
     * 修改状态
     */
    void modifyStatus(EditGoodsLabelStatusParam editParam);

    /**
     * 复制商品标签
     */
    void copy(Integer id);
}
