package com.niu.mall.service.api.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.service.api.goods.param.MallGoodsEvaluateParam;
import com.niu.mall.service.api.goods.param.MallGoodsEvaluateSearchParam;
import com.niu.mall.service.api.goods.vo.*;

import java.util.List;
import java.util.Map;

/**
 * 商品评价接口
 */
public interface IGoodsEvaluateService {

    /**
     * 商品评价列表
     * @param pageParam
     * @param searchParam
     * @return
     */
    PageResult<MallGoodsEvaluateListVo> getPages(PageParam pageParam, MallGoodsEvaluateSearchParam searchParam);

    /**
     * 商品评价信息
     * @param id
     * @return
     */
    MallGoodsEvaluateInfoVo getInfo(Integer id);

    /**
     * 添加商品评价
     * @param mallGoodsEvaluateParam
     */
    void add(MallGoodsEvaluateParam mallGoodsEvaluateParam);

    /**
     * 获取商品评价数量
     * @param goodsId
     * @return
     */
    Map<String, Long> getCount(Integer goodsId);

    /**
     * 商品显示评价
     * @param goodsId
     * @return
     */
    MallGoodsEvaluateInGoodsVo getList(Integer goodsId);

    /**
     * 订单展示评价信息
     * @param orderId
     * @return
     */
    List<MallGoodsEvaluateDetailVo> getDetail(Integer orderId);

}
