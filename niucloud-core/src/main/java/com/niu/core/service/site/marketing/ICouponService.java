package com.niu.core.service.site.marketing;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.marketing.param.MallCouponParam;
import com.niu.core.service.site.marketing.param.MallCouponSearchParam;
import com.niu.core.service.site.marketing.param.MallCouponSelectParam;
import com.niu.core.service.site.marketing.param.MallCouponSendParam;
import com.niu.core.service.site.marketing.vo.MallCouponInfoVo;
import com.niu.core.service.site.marketing.vo.MallCouponListVo;
import com.niu.core.service.site.marketing.vo.MallCouponMemberListVo;
import com.niu.core.service.site.marketing.vo.MallCouponSelectVo;
import com.niu.core.service.site.marketing.vo.MallCouponStatVo;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface ICouponService {

    /**
     * 初始化
     *
     * @return
     */
    JSONObject getInit();

    /**
     * 优惠券列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopCouponListVo>
     */
    PageResult<MallCouponListVo> getPage(PageParam pageParam, MallCouponSearchParam searchParam);


    MallCouponSelectVo getSelectPage(PageParam pageParam, MallCouponSelectParam searchParam);

    /**
     * 查询指定优惠券
     *
     * @param couponIds
     * @return
     */
    List<MallCouponListVo> getSelectedList(String couponIds);

    /**
     * 获取优惠券详情
     *
     * @param couponId
     * @return
     */
    MallCouponInfoVo getInfo(Integer couponId);

    /**
     * 添加优惠券
     *
     * @param addParam
     */
    void add(MallCouponParam addParam);

    /**
     * 编辑优惠券
     *
     * @param couponId
     * @param mallCouponParam
     */
    void edit(Integer couponId, MallCouponParam mallCouponParam);

    /**
     * 删除优惠券
     *
     * @param couponId
     */
    void delete(Integer couponId);

    /**
     * 获取会员优惠券
     *
     * @param pageParam
     * @param id
     * @param keywords
     * @return
     */
    PageResult<MallCouponMemberListVo> getMemberCoupon(PageParam pageParam, Integer id, String keywords);

    /**
     * 设置优惠券领用状态
     *
     * @param couponId
     * @param status
     */
    void setStatus(Integer couponId, Integer status);

    /**
     * 设置优惠券失效
     *
     * @param couponId
     */
    void couponInvalid(Integer couponId);

    Boolean sendCoupon(MallCouponSendParam param);

    /**
     * 店铺优惠券统计
     *
     * @param id 优惠券ID，可选
     * @return MallCouponStatVo
     */
    MallCouponStatVo getCouponStat(Integer id);
}
