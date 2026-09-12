package com.niu.core.controller.api.vip_card.card;

import com.niu.core.common.domain.Result;
import com.niu.core.service.api.vip_card.card.IApiVipCardService;
import com.niu.core.service.api.vip_card.card.param.ApiVipCardGoodsParam;
import com.niu.core.service.api.vip_card.card.vo.ApiVipCardBenefitsGroupVo;
import com.niu.core.service.api.vip_card.card.vo.ApiVipCardComponentsVo;
import com.niu.core.service.api.vip_card.card.vo.ApiVipCardDetailVo;
import com.niu.core.service.api.vip_card.card.vo.ApiVipCardGoodsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("api/vip_card")
@Api(tags = "付费会员卡前台API")
public class ApiVipCardController {

    @Autowired
    private IApiVipCardService vipCardService;

    @GetMapping("/info")
    @ApiOperation("获取会员卡详情")
    public Result<ApiVipCardDetailVo> info(@Valid @RequestParam(value = "card_id",required = false) Integer cardId) {
        return Result.success(vipCardService.getInfo(cardId));
    }

    @GetMapping("/benefits/group")
    @ApiOperation("获取会员卡权益分组")
    public Result<ApiVipCardBenefitsGroupVo> getVipCardBenefitsGroup() {
        return Result.success(vipCardService.getVipCardBenefitsGroup());
    }

    @GetMapping("/goods")
    @ApiOperation("获取付费会员卡专属商品列表")
    public Result<List<ApiVipCardGoodsVo>> getVipCardGoods(@Valid ApiVipCardGoodsParam param) {
        return Result.success(vipCardService.getVipCardGoods(param));
    }

    @GetMapping("/components")
    @ApiOperation("获取付费会员卡组件信息")
    public Result<ApiVipCardComponentsVo> getVipCardComponents() {
        return Result.success(vipCardService.getVipCardComponents());
    }
}