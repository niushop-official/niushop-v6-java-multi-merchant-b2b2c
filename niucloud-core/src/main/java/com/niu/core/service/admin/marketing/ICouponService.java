package com.niu.core.service.admin.marketing;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.marketing.param.MallCouponParam;
import com.niu.core.service.admin.marketing.param.MallCouponSearchParam;
import com.niu.core.service.admin.marketing.param.MallCouponSelectParam;
import com.niu.core.service.admin.marketing.vo.MallCouponInfoVo;
import com.niu.core.service.admin.marketing.vo.MallCouponListVo;
import com.niu.core.service.admin.marketing.vo.MallCouponMemberListVo;
import com.niu.core.service.admin.marketing.vo.MallCouponSelectVo;

import java.util.List;

/**
 * 优惠券服务接口
 */
public interface ICouponService {

    /**
     * 优惠券列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopCouponListVo>
     */
    PageResult<MallCouponListVo> getPage(PageParam pageParam, MallCouponSearchParam searchParam);


    MallCouponSelectVo getSelectPage(PageParam pageParam, MallCouponSelectParam searchParam);
    /**
     * 查询指定优惠券
     * @param couponIds
     * @return
     */
    List<MallCouponListVo> getSelectedList(String couponIds);

    /**
     * 获取优惠券详情
     * @param couponId
     * @return
     */
    MallCouponInfoVo getInfo(Integer couponId);

    /**
     * 获取会员优惠券
     * @param pageParam
     * @param id
     * @param keywords
     * @return
     */
    PageResult<MallCouponMemberListVo> getMemberCoupon(PageParam pageParam, Integer id, String keywords);
}
