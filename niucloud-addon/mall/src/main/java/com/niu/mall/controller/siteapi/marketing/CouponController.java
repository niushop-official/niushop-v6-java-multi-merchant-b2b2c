package com.niu.mall.controller.siteapi.marketing;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.enums.coupon.CouponStatusEnum;
import com.niu.mall.service.site.marketing.ICouponService;
import com.niu.mall.service.site.marketing.param.MallCouponParam;
import com.niu.mall.service.site.marketing.param.MallCouponSearchParam;
import com.niu.mall.service.site.marketing.param.MallCouponSelectParam;
import com.niu.mall.service.site.marketing.param.MallCouponSendParam;
import com.niu.mall.service.site.marketing.vo.MallCouponInfoVo;
import com.niu.mall.service.site.marketing.vo.MallCouponListVo;
import com.niu.mall.service.site.marketing.vo.MallCouponMemberListVo;
import com.niu.mall.service.site.marketing.vo.MallCouponStatVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 优惠券控制器
 */

@RestController
@RequestMapping("adminapi/mall/site/goods/coupon")
public class CouponController {

    @Resource
    ICouponService couponService;


    @GetMapping("/init")
    public Result<JSONObject> init() {
        return Result.success(couponService.getInit());
    }

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
     * @return Result<ShopCouponInfoVo>
     */
    @GetMapping("/detail/{id}")
    public Result<MallCouponInfoVo> info(@Validated @PathVariable("id") Integer id) {
        MallCouponInfoVo info = couponService.getInfo(id);
        return Result.success(info);
    }

    /**
     * 优惠券添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody MallCouponParam addParam) {
        couponService.add(addParam);
        return Result.success();
    }

    /**
     * 优惠券编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/edit/{id}")
    public Result<Object> edit(@Validated @PathVariable("id") Integer id, @Validated @RequestBody MallCouponParam editParam) {
        couponService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 优惠券删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@Validated @PathVariable("id") Integer id) {
        couponService.delete(id);
        return Result.success();
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
    public Result<PageResult<MallCouponMemberListVo>> getMemberCoupon(@Validated PageParam pageParam,
                                                                      @RequestParam(value = "id", required = false) String id,
                                                                      @RequestParam(value = "keywords", required = false, defaultValue = "") String keywords) {
        Integer couponId = null;
        if (id != null && !id.isBlank()) {
            couponId = Integer.valueOf(id);
        }
        return Result.success(couponService.getMemberCoupon(pageParam, couponId, keywords));
    }

    /**
     * 选中优惠券
     *
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

    /**
     * 店铺优惠券统计
     *
     * @param id 优惠券ID
     * @return Result<MallCouponStatVo>
     */
    @GetMapping("/stat")
    public Result<MallCouponStatVo> couponStat(@RequestParam(name = "id", required = false) Integer id) {
        return Result.success(couponService.getCouponStat(id));
    }

    /**
     * 设置优惠券领用状态
     *
     * @param status
     * @param id
     * @return
     */
    @PutMapping("setstatus/{status}")
    public Result<Object> setCouponStatus(@Validated @PathVariable("status") Integer status, @Validated @RequestBody Integer id) {
        couponService.setStatus(id, status);
        return Result.success();
    }

    /**
     * 优惠券失效
     *
     * @param id
     * @return
     */
    @PutMapping("invalid/{id}")
    public Result<Object> couponInvalid(@Validated @PathVariable("id") Integer id) {
        couponService.couponInvalid(id);
        return Result.success();
    }

    /**
     * 批量发放优惠券
     *
     * @param param coupon_data : [['coupon_id'=>1, 'num'=>2],['coupon_id'=>2, 'num'=>3]], member_ids: [] 批量发放数据格式
     * @return Result<Object>
     */
    @PostMapping("/send")
    public Result<Object> sendCoupon(@Validated @RequestBody MallCouponSendParam param) {
        couponService.sendCoupon(param);
        return Result.success();
    }
}
