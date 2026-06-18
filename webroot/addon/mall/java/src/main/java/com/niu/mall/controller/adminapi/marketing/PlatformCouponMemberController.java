package com.niu.mall.controller.adminapi.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.marketing.IPlatformCouponMemberService;
import com.niu.mall.service.admin.marketing.param.MallCouponMemberSearchParam;
import com.niu.mall.service.admin.marketing.vo.MallCouponMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员详情 优惠券列表
 */

@RestController
@RequestMapping("adminapi/mall/admin/member/platform_coupon")
public class PlatformCouponMemberController {

    @Resource
    IPlatformCouponMemberService couponMemberService;

    /**
     * 优惠券列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < MallCouponListVo>>
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
