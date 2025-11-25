package com.niu.mall.controller.siteapi.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.api.marketing.ICouponMemberService;
import com.niu.mall.service.api.marketing.vo.MallCouponMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("adminapi/mall/site/member/coupon")
public class CouponMemberController {

    @Resource
    ICouponMemberService couponMemberService;

    @GetMapping("/list/{id}")
    public Result<PageResult<MallCouponMemberListVo>> list(@Validated PageParam pageParam, @Validated @PathVariable("id") Integer memberId) {
        PageResult<MallCouponMemberListVo> result = couponMemberService.getPage(pageParam,memberId);
        return Result.success(result);
    }
}
