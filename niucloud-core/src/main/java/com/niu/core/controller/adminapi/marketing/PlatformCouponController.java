package com.niu.core.controller.adminapi.marketing;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.trade.TradeTypeDict;
import com.niu.core.enums.coupon.CouponStatusEnum;
import com.niu.core.service.admin.marketing.IPlatformCouponService;
import com.niu.core.service.admin.marketing.param.MallCouponParam;
import com.niu.core.service.admin.marketing.param.MallCouponSearchParam;
import com.niu.core.service.admin.marketing.param.MallCouponSelectParam;
import com.niu.core.service.site.marketing.param.MallCouponSendParam;
import com.niu.core.service.admin.marketing.vo.MallCouponInfoVo;
import com.niu.core.service.admin.marketing.vo.MallCouponListVo;
import com.niu.core.service.admin.marketing.vo.MallCouponMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 平台优惠券控制器（对齐 PHP marketing/admin/goods/platform_coupon）
 */
@RestController
@RequestMapping("adminapi/marketing/admin/goods/platform_coupon")
public class PlatformCouponController {

    @Resource
    IPlatformCouponService couponService;

    @GetMapping("/init")
    public Result<JSONObject> init() {
        return Result.success(couponService.getInit());
    }

    @GetMapping("/site")
    public Result<JSONObject> initSite(@RequestParam(value = "addon", required = false, defaultValue = "") String addon) {
        return Result.success(couponService.getInitSiteList(addon));
    }

    /**
     * 优惠券列表
     */
    @GetMapping("")
    public Result<PageResult<MallCouponListVo>> list(@Validated PageParam pageParam,
                                                     @Validated MallCouponSearchParam searchParam) {
        PageResult<MallCouponListVo> list = couponService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 优惠券详情
     */
    @GetMapping("/detail/{id}")
    public Result<MallCouponInfoVo> info(@Validated @PathVariable("id") Integer id) {
        MallCouponInfoVo info = couponService.getInfo(id);
        return Result.success(info);
    }

    /**
     * 优惠券添加
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody MallCouponParam addParam) {
        Integer id = couponService.add(addParam);
        return Result.success(Map.of("id", id));
    }

    /**
     * 优惠券编辑
     */
    @PutMapping("/edit/{id}")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody MallCouponParam editParam) {
        couponService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 优惠券删除
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        couponService.delete(id);
        return Result.success();
    }

    /**
     * 获取会员优惠券列表
     */
    @GetMapping("/records")
    public Result<PageResult<MallCouponMemberListVo>> getMemberCoupon(@Validated PageParam pageParam,
                                                                      @RequestParam("id") Integer id,
                                                                      @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords) {
        return Result.success(couponService.getMemberCoupon(pageParam, id, keywords));
    }

    /**
     * 选中优惠券
     */
    @GetMapping("/select")
    public Result<?> getSelectPage(PageParam pageParam, MallCouponSelectParam searchParam) {
        return Result.success(couponService.getSelectPage(pageParam, searchParam));
    }

    /**
     * 获取选中优惠券
     */
    @GetMapping("/selected")
    public Result<?> getSelectedLists(@Validated @RequestParam("coupon_id") String couponId) {
        return Result.success(couponService.getSelectedList(couponId));
    }

    /**
     * 优惠券状态列表
     */
    @GetMapping("/status")
    public Result<Object> getCouponStatus() {
        return Result.success(CouponStatusEnum.getMap());
    }

    /**
     * 优惠券业务类型（对齐 PHP TradeDict::getTradeType('coupon')）
     */
    @GetMapping("/trade_type")
    public Result<Object> getCouponTradeType() {
        return Result.success(TradeTypeDict.getTradeType("coupon"));
    }

    /**
     * 设置优惠券领用状态
     */
    @PutMapping("/setstatus/{status}")
    public Result<Object> setCouponStatus(@Validated @PathVariable("status") Integer status,
                                          @RequestBody Map<String, Object> body) {
        Object idObj = body == null ? null : body.get("id");
        if (idObj == null) {
            return Result.fail("id参数缺失");
        }
        Integer id = Integer.valueOf(String.valueOf(idObj));
        couponService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 优惠券失效
     */
    @PutMapping("/invalid/{id}")
    public Result<Object> couponInvalid(@Validated @PathVariable("id") Integer id) {
        couponService.couponInvalid(id);
        return Result.success();
    }

    /**
     * 平台优惠券批量发放
     */
    @PostMapping("/send")
    public Result<Object> sendCoupon(@Validated @RequestBody MallCouponSendParam param) {
        couponService.sendCoupon(param.getCouponData(), param.getMemberIds());
        return Result.success();
    }
}
