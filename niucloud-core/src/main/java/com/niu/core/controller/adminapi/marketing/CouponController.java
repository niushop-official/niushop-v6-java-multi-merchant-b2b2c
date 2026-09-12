package com.niu.core.controller.adminapi.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.coupon.CouponStatusEnum;
import com.niu.core.service.admin.marketing.ICouponService;
import com.niu.core.service.admin.marketing.param.MallCouponSearchParam;
import com.niu.core.service.admin.marketing.param.MallCouponSelectParam;
import com.niu.core.service.admin.marketing.vo.MallCouponInfoVo;
import com.niu.core.service.admin.marketing.vo.MallCouponListVo;
import com.niu.core.service.admin.marketing.vo.MallCouponMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 平台端-店铺优惠券（只读查看）
 * 对应 PHP: app\adminapi\controller\marketing\admin\Coupon
 * 路由: adminapi/marketing/admin/goods/coupon
 */
@RestController
@RequestMapping("adminapi/marketing/admin/goods/coupon")
public class CouponController {

    @Resource
    ICouponService couponService;

    /**
     * 优惠券列表（对齐 PHP lists）
     */
    @GetMapping("")
    public Result<PageResult<MallCouponListVo>> list(@Validated PageParam pageParam,
                                                     @Validated MallCouponSearchParam searchParam) {
        PageResult<MallCouponListVo> list = couponService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 优惠券详情（对齐 PHP info）
     */
    @GetMapping("/detail/{id}")
    public Result<MallCouponInfoVo> info(@Validated @PathVariable("id") Integer id) {
        MallCouponInfoVo info = couponService.getInfo(id);
        return Result.success(info);
    }

    /**
     * 会员领取记录（对齐 PHP getMemberCoupon）
     */
    @GetMapping("/records")
    public Result<PageResult<MallCouponMemberListVo>> getMemberCoupon(@Validated PageParam pageParam,
                                                                     @RequestParam("id") Integer id,
                                                                     @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords) {
        return Result.success(couponService.getMemberCoupon(pageParam, id, keywords));
    }

    /**
     * 选中优惠券列表（对齐 PHP getSelectedLists）
     */
    @GetMapping("/selected")
    public Result<?> getSelectedLists(@RequestParam(value = "coupon_id", required = false, defaultValue = "") String couponId) {
        return Result.success(couponService.getSelectedList(couponId));
    }

    /**
     * 优惠券选择分页（扩展）
     */
    @GetMapping("/select")
    public Result<?> getSelectPage(PageParam pageParam, MallCouponSelectParam searchParam) {
        return Result.success(couponService.getSelectPage(pageParam, searchParam));
    }

    /**
     * 优惠券状态列表（扩展）
     */
    @GetMapping("/status")
    public Result<Object> getCouponStatus() {
        return Result.success(CouponStatusEnum.getMap());
    }
}
