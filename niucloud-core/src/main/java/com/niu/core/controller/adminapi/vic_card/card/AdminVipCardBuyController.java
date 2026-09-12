package com.niu.core.controller.adminapi.vic_card.card;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.vip_card.card.IAdminVipCardBuyService;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardBuyPageParam;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardBuyPageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("adminapi/vip_card/buy")
@Api(tags = "购卡会员管理")
public class AdminVipCardBuyController {

    @Autowired
    private IAdminVipCardBuyService vipCardBuyService;

    @GetMapping("/pages")
    @ApiOperation("购卡会员分页列表")
    public Result<PageResult<AdminVipCardBuyPageVo>> pages(PageParam pageParam, AdminVipCardBuyPageParam param) {
        return Result.success(vipCardBuyService.getPage(pageParam, param));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("购卡会员详情")
    public Result<AdminVipCardBuyPageVo> info(@PathVariable Integer id) {
        return Result.success(vipCardBuyService.getInfo(id));
    }
}