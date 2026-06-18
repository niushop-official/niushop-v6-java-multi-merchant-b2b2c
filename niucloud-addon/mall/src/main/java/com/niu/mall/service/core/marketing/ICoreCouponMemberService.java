package com.niu.mall.service.core.marketing;

import com.baomidou.mybatisplus.extension.service.IService;
import com.niu.mall.entity.coupon.MallCouponMember;
import com.niu.mall.service.core.marketing.vo.MallMemberCouponInfoVo;
import com.niu.mall.service.core.marketing.vo.MallMemberCouponListVo;

import java.util.List;

public interface ICoreCouponMemberService extends IService<MallCouponMember> {


    /**
     * 获取可使用优惠券列表
     *
     * @param memberId
     * @param siteId
     * @return
     */
    List<MallMemberCouponListVo> getUseCouponListByMemberId(Integer memberId, Integer siteId);

    /**
     * 获取可使用平台优惠券列表
     *
     * @param memberId
     * @return
     */
    List<MallMemberCouponListVo> getUsePlatformCouponListByMemberId(Integer memberId);

    MallMemberCouponInfoVo getUsePlatformCouponInfoById(Integer Id);

    /**
     * 使用优惠券
     *
     * @param id
     * @param tradeId
     */
    void use(Integer id, Integer tradeId);

    /**
     * 恢复已使用的优惠券
     *
     * @param id
     */
    void recover(Integer id);

    /**
     * 优惠券失效
     *
     * @param ids 优惠券ids
     */
    void invalid(List<Integer> ids);

    /**
     * 优惠券过期
     */
    void expire(List<Integer> ids);

    /**
     * 发放优惠券
     *
     * @param siteId   站点ID
     * @param memberId 会员ID
     * @param couponId 优惠券ID
     * @param num      发放数量
     */
    void sendCoupon(Integer siteId, Integer memberId, Integer couponId, Integer num);

    /**
     * 发放优惠券
     *
     * @param siteId   站点ID
     * @param memberId 会员ID
     * @param couponId 优惠券ID
     * @param num      发放数量
     * @param memo     备注
     */
    void sendCoupon(Integer siteId, Integer memberId, Integer couponId, Integer num, String memo);
}
