package com.niu.core.controller.adminapi.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.marketing.IPlatformCouponMemberService;
import com.niu.core.service.admin.marketing.param.MallCouponMemberSearchParam;
import com.niu.core.service.admin.marketing.vo.MallCouponMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员详情 优惠券列表（对齐 PHP marketing/admin/member/platform_coupon）
 */
@RestController
@RequestMapping("adminapi/marketing/admin/member/platform_coupon")
public class PlatformCouponMemberController {

    @Resource
    IPlatformCouponMemberService couponMemberService;

    /**
     * 优惠券列表
     */
    @GetMapping("/list/{id}")
    public Result<PageResult<MallCouponMemberListVo>> list(@Validated PageParam pageParam,
                                                           @Validated @PathVariable("id") Integer id,
                                                           @Validated MallCouponMemberSearchParam searchParam) {
        if (searchParam.getMemberId() == null) {
            searchParam.setMemberId(id);
        }
        PageResult<MallCouponMemberListVo> list = couponMemberService.getPage(pageParam, searchParam);
        return Result.success(list);
    }
}
