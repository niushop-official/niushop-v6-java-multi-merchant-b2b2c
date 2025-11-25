package com.niu.mall.controller.adminapi.marketing;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.enums.coupon.CouponStatusEnum;
import com.niu.mall.service.admin.marketing.ICouponService;
import com.niu.mall.service.admin.marketing.param.MallCouponParam;
import com.niu.mall.service.admin.marketing.param.MallCouponSearchParam;
import com.niu.mall.service.admin.marketing.param.MallCouponSelectParam;
import com.niu.mall.service.admin.marketing.vo.MallCouponInfoVo;
import com.niu.mall.service.admin.marketing.vo.MallCouponListVo;
import com.niu.mall.service.admin.marketing.vo.MallCouponMemberListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 优惠券控制器
 */

@RestController
@RequestMapping("adminapi/mall/admin/goods/coupon")
public class CouponController {

    @Resource
    ICouponService couponService;

    /**
     * 优惠券列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < MallCouponListVo>>
     */
    @GetMapping("")
    public Result<PageResult<MallCouponListVo>> list(@Validated PageParam pageParam,
                                                     @Validated MallCouponSearchParam searchParam) {
        PageResult<MallCouponListVo> list = couponService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 优惠券详情
     *
     * @param id 主键ID
     * @return Result<MallCouponInfoVo>
     */
    @GetMapping("/detail/{id}")
    public Result<MallCouponInfoVo> info(@Validated @PathVariable("id") Integer id) {
        MallCouponInfoVo info = couponService.getInfo(id);
        return Result.success(info);
    }

    /**
     * 获取会员优惠券列表
     *
     * @param pageParam
     * @param id
     * @param keywords
     * @return
     */
    @GetMapping("/records")
    public Result<PageResult<MallCouponMemberListVo>> getMemberCoupon(@Validated PageParam pageParam, @RequestParam("id") Integer id, @RequestParam("keywords") String keywords) {
        return Result.success(couponService.getMemberCoupon(pageParam, id, keywords));
    }

    /**
     * 选中优惠券
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/select")
    public Result<?> getSelectPage(PageParam pageParam, MallCouponSelectParam searchParam) {
        return Result.success(couponService.getSelectPage(pageParam, searchParam));
    }

    /**
     * 获取选中优惠券
     *
     * @param couponId
     * @return
     */
    @GetMapping("/selected")
    public Result<?> getSelectedLists(@Validated @RequestParam("coupon_id") String couponId) {
        return Result.success(couponService.getSelectedList(couponId));
    }

    @GetMapping("/status")
    public Result<Object> getCouponStatus() {
        return Result.success(CouponStatusEnum.getMap());
    }
}
