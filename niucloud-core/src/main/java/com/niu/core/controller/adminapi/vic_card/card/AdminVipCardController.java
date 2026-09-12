package com.niu.core.controller.adminapi.vic_card.card;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.vip_card.card.IAdminVipCardService;
import com.niu.core.service.admin.vip_card.card.param.AdminModifyStatusParam;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardAddParam;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardEditParam;
import com.niu.core.service.admin.vip_card.card.param.AdminVipCardPageParam;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardInfoVo;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardListVo;
import com.niu.core.service.admin.vip_card.card.vo.AdminVipCardPageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("adminapi/vip_card")
@Api(tags = "会员卡管理")
public class AdminVipCardController {

    @Autowired
    private IAdminVipCardService vipCardCardService;

    @GetMapping("/pages")
    @ApiOperation("会员卡分页列表")
    public Result<PageResult<AdminVipCardPageVo>> pages(PageParam pageParam, AdminVipCardPageParam param) {
        return Result.success(vipCardCardService.getPage(pageParam, param));
    }

    @GetMapping("/lists")
    @ApiOperation("会员卡列表（不分页）")
    public Result<List<AdminVipCardListVo>> lists(AdminVipCardPageParam param) {
        return Result.success(vipCardCardService.getList(param));
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("会员卡详情")
    public Result<AdminVipCardInfoVo> info(@PathVariable Integer id) {
        return Result.success(vipCardCardService.getInfo(id));
    }

    @PostMapping("/add")
    @ApiOperation("添加会员卡")
    public Result<Map<String, Integer>> add(@Valid @RequestBody AdminVipCardAddParam param) {
        Integer cardId = vipCardCardService.add(param);
        return Result.success(Map.of("card_id", cardId));
    }

    @PutMapping("/edit/{id}")
    @ApiOperation("编辑会员卡")
    public Result edit(@PathVariable Integer id, @Valid @RequestBody AdminVipCardEditParam param) {
        vipCardCardService.edit(id, param);
        return Result.success();
    }

    @DeleteMapping("/del/{id}")
    @ApiOperation("删除会员卡")
    public Result del(@PathVariable Integer id) {
        vipCardCardService.del(id);
        return Result.success();
    }

    @PutMapping("/status")
    @ApiOperation("修改会员卡状态")
    public Result modifyStatus(@Valid @RequestBody AdminModifyStatusParam param) {
        vipCardCardService.modifyStatus(param);
        return Result.success();
    }
}