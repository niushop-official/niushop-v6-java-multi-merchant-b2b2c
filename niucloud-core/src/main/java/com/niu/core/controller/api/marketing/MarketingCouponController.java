package com.niu.core.controller.api.marketing;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.language.LanguageUtils;
import com.niu.core.service.api.marketing.ICouponService;
import com.niu.core.service.api.marketing.param.MallCouponComponentsParam;
import com.niu.core.service.api.marketing.param.MallCouponMemberSearchParam;
import com.niu.core.service.api.marketing.param.MallCouponReceiveParam;
import com.niu.core.service.api.marketing.param.MallCouponSearchParam;
import com.niu.core.service.api.marketing.param.MallGoodsCouponSearchParam;
import com.niu.core.service.api.marketing.vo.MallCouponExpiringVo;
import com.niu.core.service.api.marketing.vo.MallCouponInfoVo;
import com.niu.core.service.api.marketing.vo.MallCouponListVo;
import com.niu.core.service.api.marketing.vo.MallCouponMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 营销优惠券控制器（对齐 PHP api/marketing）
 * 对应 PHP: app\api\controller\marketing\Coupon
 */
@RestController("apiMarketingCouponController")
@RequestMapping("api/marketing")
public class MarketingCouponController {

    @Resource
    private ICouponService couponService;

    /**
     * 优惠券列表
     */
    @GetMapping("/coupon")
    public Result<PageResult<MallCouponListVo>> list(@Validated PageParam pageParam,
                                                     @Validated MallCouponSearchParam searchParam) {
        return Result.success(couponService.getPage(pageParam, searchParam));
    }

    /**
     * 商品详情优惠券列表
     */
    @GetMapping("/goods_coupon")
    public Result<List<Map<String, Object>>> goodsCoupon(@Validated MallGoodsCouponSearchParam searchParam) {
        return Result.success(couponService.getGoodsDetailCouponLists(searchParam));
    }

    /**
     * 优惠券详情
     * 注意：静态路径需在 /{id} 之前注册
     */
    @GetMapping("/coupon/components")
    public Result<List<MallCouponListVo>> components(@Validated MallCouponComponentsParam param) {
        return Result.success(couponService.getCouponComponents(param));
    }

    /**
     * 平台优惠券组件
     */
    @GetMapping("/coupon/components_of_mall")
    public Result<List<MallCouponListVo>> componentsOfMall(@Validated MallCouponComponentsParam param) {
        return Result.success(couponService.getPlatformCouponComponents(param));
    }

    /**
     * 优惠券二维码
     */
    @GetMapping("/coupon/qrcode/{id}")
    public Result<?> qrcode(@PathVariable("id") Integer id) {
        return Result.success(couponService.getQrcode(id));
    }

    /**
     * 优惠券详情
     */
    @GetMapping("/coupon/{id}")
    public Result<MallCouponInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(couponService.getDetail(id));
    }

    /**
     * 领取优惠券
     */
    @SaCheckLogin
    @PostMapping("/coupon")
    public Result<?> receive(@Validated @RequestBody MallCouponReceiveParam receiveParam) {
        couponService.receive(receiveParam);
        return Result.success(LanguageUtils.get("MALL_COUPON_RECEIVE_SUCCESS"));
    }

    /**
     * 会员优惠券列表
     */
    @SaCheckLogin
    @GetMapping("/member/coupon")
    public Result<PageResult<MallCouponMemberListVo>> memberCouponList(@Validated PageParam pageParam,
                                                                       @Validated MallCouponMemberSearchParam searchParam) {
        return Result.success(couponService.getMemberPage(pageParam, searchParam));
    }

    /**
     * 会员优惠券数量
     */
    @SaCheckLogin
    @GetMapping("/member/coupon/count")
    public Result<Long> memberCouponCount(@Validated MallCouponMemberSearchParam searchParam) {
        return Result.success(couponService.getMemberCount(searchParam));
    }

    /**
     * 即将过期优惠券
     */
    @SaCheckLogin
    @GetMapping("/member/coupon/expiring")
    public Result<MallCouponExpiringVo> memberCouponExpiring(@Validated MallCouponMemberSearchParam searchParam) {
        return Result.success(couponService.memberCouponExpiringSoon(searchParam));
    }

    /**
     * 会员优惠券状态数量
     */
    @SaCheckLogin
    @GetMapping("/member/coupon/status_count")
    public Result<List<JSONObject>> memberCouponStatusCount() {
        return Result.success(couponService.getCouponCountByStatus());
    }

    /**
     * 优惠券类型
     */
    @GetMapping("/coupon_type")
    public Result<?> getCouponType() {
        return Result.success(couponService.getCouponType());
    }
}
