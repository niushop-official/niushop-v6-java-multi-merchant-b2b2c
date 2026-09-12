package com.niu.core.controller.siteapi.marketing;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.api.marketing.ICouponMemberService;
import com.niu.core.service.api.marketing.vo.MallCouponMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Generic site-marketing route retained for compatibility with the PHP admin API.
 */
@RestController
@RequestMapping("adminapi/marketing/site/member/coupon")
public class MarketingCouponMemberController {

    @Resource
    private ICouponMemberService couponMemberService;

    @GetMapping("/list/{memberId}")
    public Result<PageResult<MallCouponMemberListVo>> list(
            @Validated PageParam pageParam,
            @PathVariable("memberId") Integer memberId) {
        return Result.success(couponMemberService.getPage(pageParam, memberId));
    }
}
