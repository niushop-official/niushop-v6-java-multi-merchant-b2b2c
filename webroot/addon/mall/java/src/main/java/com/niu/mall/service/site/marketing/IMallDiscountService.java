package com.niu.mall.service.site.marketing;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.service.site.marketing.param.*;
import com.niu.mall.service.site.marketing.vo.*;

/**
 * 限时折扣服务接口
 */
public interface IMallDiscountService {


    /**
     * 限时折扣列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopActiveListVo>
     */
    PageResult<MallDiscountListVo> getPage(PageParam pageParam, MallDiscountSearchParam searchParam);

    /**
     * 限时折扣信息
     * @param id 主键ID
     * @return MallDiscountInfoVo
     */
    MallDiscountInfoVo info(Integer id);

    /**
     * 限时折扣详情
     * @param id
     * @return
     */
    MallDiscountDetailVo detail(Integer id);

    void checkGoods(JSONArray goodsList);

    /**
     * 限时折扣删除
     * @param id 主键ID
     */
    void del(Integer id);

    /**
     * 限时折扣添加
     * @param mallDiscountParam
     * @return
     */
    void add(MallDiscountParam mallDiscountParam);

    /**
     * 限时折扣编辑
     * @param id
     * @param mallDiscountParam
     */
    void edit(Integer id, MallDiscountParam mallDiscountParam);

    /**
     * 设置商品折扣
     * @param activeId
     * @return
     */
    boolean setGoodsDiscount(Integer activeId);

    /**
     * 取消商品折扣
     * @param activeId
     * @return
     */
    boolean cancelGoodsDiscount(Integer activeId);

    /**
     * 限时折扣关闭
     * @param activeId
     */
    void discountClose(Integer activeId);

    /**
     * 限时折扣开始后执行
     * @param activeId
     */
    void discountStartAfter(Integer activeId);

    /**
     * 限时折扣结束后执行
     * @param activeId
     */
    void discountEndAfter(Integer activeId);

    /**
     * 限时折扣订单列表
     * @param activeId
     * @param
     * @return
     */
    PageResult<MallDiscountOrderListVo> order(Integer activeId, PageParam pageParam, MallDiscountOrderSearchParam mallDiscountOrderSearchParam);

    /**
     * 限时折扣参与会员列表
     * @param pageParam
     *  * @param activeId
     * @return
     */
    PageResult<MallDiscountMemberListVo>  member(PageParam pageParam, Integer activeId, MallDiscountMemberSearchParam mallDiscountMemberSearchParam);

    /**
     * 限时折扣商品列表
     * @param activeId
     * @param searchParam
     * @return
     */
    PageResult<MallDiscountGoodsListVo> goods(PageParam pageParam, Integer activeId, MallDiscountGoodsSearchParam searchParam);

    /**
     * 限时折扣设置
     * @param key
     * @param data
     */
    void setConfig(String key, JSONArray data);

    /**
     * 获取限时折扣设置
     * @return
     */
    JSONArray getDiscountBannerConfig();

    /**
     * 限时折扣订单支付后执行
     * @param order
     */
    void orderPayAfter(MallOrder order);

}
