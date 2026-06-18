package com.niu.core.controller.api.member;

import com.niu.core.common.domain.Result;
import com.niu.core.service.api.member.IMemberVisitService;
import com.niu.core.service.api.member.param.MemberVisitParam;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 访问访客量
 */
@RestController
@RequestMapping("/api/member/visit")
@Slf4j
public class MemberVisitController {

    @Resource
    private IMemberVisitService memberVisitService;

    /**
     * 增加访问访客量
     */
    @PostMapping("")
    public Result<?> addVisit() {
        MemberVisitParam param = new MemberVisitParam();
        param.setSiteId(0);
        memberVisitService.addVisit(param);
        return Result.success();
    }

}
