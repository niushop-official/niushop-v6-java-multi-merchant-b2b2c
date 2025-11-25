package com.niu.mall.service.site.goods;

import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.site.goods.param.MallGoodsEvaluateParam;
import com.niu.core.common.domain.PageParam;
import com.niu.mall.service.site.goods.vo.MallGoodsEvaluateInfoVo;
import com.niu.mall.service.site.goods.vo.MallGoodsEvaluateListVo;
import com.niu.mall.service.site.goods.param.MallGoodsEvaluateSearchParam;

/**
 * 商品评价服务接口
 */
public interface IMallGoodsEvaluateService {


    /**
     * 商品评价列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsEvaluateListVo>
     */
    PageResult<MallGoodsEvaluateListVo> list(PageParam pageParam, MallGoodsEvaluateSearchParam searchParam);

    /**
     * 商品评价详情
     * @param id 主键ID
     * @return ShopGoodsEvaluateInfoVo
     */
    MallGoodsEvaluateInfoVo info(Integer id);

    /**
     * 商品评价添加
     * @param addParam 添加参数
     */
    void add(MallGoodsEvaluateParam addParam);

    /**
     * 商品评价编辑
     * @param id   主键
     * @param editParam 编辑参数
     */
    void edit(Integer id, MallGoodsEvaluateParam editParam);

    /**
     * 商品评价删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 商品评价审核通过
     * @param id
     */
    void auditAdopt(Integer id);

    /**
     * 商品评价审核拒绝
     * @param id
     */
    void auditRefuse(Integer id);

    /**
     * 商品评价申请回复
     * @param explainFirst
     */
    void reply(Integer id, String explainFirst);

    /**
     * 商品评价置顶
     * @param id
     */
    void topping(Integer id);

    /**
     * 商品评价取消置顶
     * @param id
     */
    void cancelTopping(Integer id);

}