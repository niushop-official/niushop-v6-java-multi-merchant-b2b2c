package com.niu.mall.controller.api.marketing;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.api.marketing.ICouponService;
import com.niu.mall.service.api.marketing.param.MallCouponReceiveParam;
import com.niu.mall.service.api.marketing.param.MallCouponSearchParam;
import com.niu.mall.service.api.marketing.vo.MallCouponInfoVo;
import com.niu.mall.service.api.marketing.vo.MallCouponListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 优惠券控制器
 */

@RestController
@RequestMapping("api/mall")
public class CouponController {

    @Resource
    ICouponService couponService;

    /**
     * 优惠券列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < ShopCouponListVo>>
     */
    @GetMapping("/coupon")
    public Result<PageResult<MallCouponListVo>> list(@Validated PageParam pageParam,
                                                     @Validated MallCouponSearchParam searchParam) {
        PageResult<MallCouponListVo> list = couponService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 优惠券详情
     *
     * @param id 主键ID
     * @return Result<ShopCouponInfoVo>
     */
    @GetMapping("/coupon/{id}")
    public Result<MallCouponInfoVo> info(@Validated @PathVariable("id") Integer id) {
        MallCouponInfoVo info = couponService.getDetail(id);
        return Result.success(info);
    }

    /**
     * 优惠券领取
     *
     * @param receiveParam
     * @return
     */
    @SaCheckLogin
    @PostMapping("/coupon")
    public Result<?> receive(@Validated @RequestBody MallCouponReceiveParam receiveParam) {
        couponService.receive(receiveParam);
        return Result.success("领取成功");
    }

    /**
     * 会员优惠券列表
     *
     * @param pageParam
     * @param status
     * @return
     */
    @SaCheckLogin
    @GetMapping("/member/coupon")
    public Result<Object> memberCouponList(@Validated PageParam pageParam, @RequestParam(name = "status") Integer status, @RequestParam(name = "type",required = false) String type) {
        return Result.success(couponService.getMemberPage(pageParam, status ,type  ));
    }

    /**
     * 会员优惠券状态数量
     *
     * @return
     */
    @SaCheckLogin
    @GetMapping("/member/coupon/status_count")
    public Result<List<JSONObject>> memberCouponStatusCount() {
        return Result.success(couponService.getCouponCountByStatus());
    }

    /**
     * 会员优惠券数量
     *
     * @return
     */
    @SaCheckLogin
    @GetMapping("/member/coupon/count")
    public Result<Long> memberCouponCount(@RequestParam(name = "status") Integer status) {
        return Result.success(couponService.getMemberCount(status));
    }

    /**
     * 获取优惠券列表组件调用
     *
     * @param couponIds
     * @param num
     * @return
     */
    @GetMapping("/coupon/components")
    public Result<List<MallCouponListVo>> components(@RequestParam(name = "coupon_ids", defaultValue = "") String couponIds, @RequestParam(name = "num", defaultValue = "4") Integer num, @RequestParam(name = "site_id", defaultValue = "") String siteId) {
        return Result.success(couponService.getCouponComponents(siteId, couponIds, num));
    }

    /**
     * 获取优惠券列表组件调用
     *
     * @param couponIds
     * @param num
     * @return
     */
    @GetMapping("/coupon/components_of_mall")
    public Result<List<MallCouponListVo>> componentsOfMall(@RequestParam(name = "coupon_ids", defaultValue = "") String couponIds, @RequestParam(name = "num", defaultValue = "4") Integer num) {
        return Result.success(couponService.getPlatformCouponComponents(couponIds, num));
    }
    /**
     * 优惠券二维码
     *
     * @param id
     * @return
     */
    @GetMapping("/coupon/qrcode/{id}")
    public Result<Object> qrcode(@PathVariable("id") Integer id) {
        return null;
    }

    /**
     * 获取优惠券类型
     *
     * @return
     */
    @GetMapping("/coupon_type")
    public Result<Object> getCouponType() {
        return Result.success(couponService.getCouponType());
    }

}
