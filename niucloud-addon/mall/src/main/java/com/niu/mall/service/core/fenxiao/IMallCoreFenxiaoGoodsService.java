package com.niu.mall.service.core.fenxiao;

import com.niu.core.common.domain.PageResult;
import com.niu.core.event.fenxiao.param.FenxiaoGoodsListParam;
import com.niu.core.event.fenxiao.vo.FenxiaoGoodsDetailVo;
import com.niu.core.event.fenxiao.vo.FenxiaoGoodsSelectVo;

/**
 * 商城分销商品服务接口
 */
public interface IMallCoreFenxiaoGoodsService {

    /**
     * 获取商城分销可选商品列表
     * @param param 查询参数
     * @return PageResult<FenxiaoGoodsSelectVo>
     */
    PageResult<FenxiaoGoodsSelectVo> getGoodsList(FenxiaoGoodsListParam param);

    /**
     * 获取商城分销商品详情（含规格列表）
     * @param param 查询参数，goodsId 必填
     * @return FenxiaoGoodsDetailVo
     */
    FenxiaoGoodsDetailVo getGoodsInfo(FenxiaoGoodsListParam param);
}
