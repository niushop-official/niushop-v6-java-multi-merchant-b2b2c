package com.niu.core.controller.api.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.ParamHelper;
import com.niu.core.service.api.shop.IShopMemberService;
import com.niu.core.service.api.shop.vo.SiteShopFollowVo;
import com.niu.core.service.api.site.ISiteService;
import com.niu.core.service.api.site.param.ApiSiteAreaCheckParam;
import com.niu.core.service.api.site.param.ApiSiteCircleListParam;
import com.niu.core.service.api.site.param.ApiSiteInfoParam;
import com.niu.core.service.api.site.param.ApiSiteNearestFrontWarehouseParam;
import com.niu.core.service.api.site.vo.ApiSiteAreaCheckVo;
import com.niu.core.service.api.site.vo.ApiSiteInfoVo;
import com.niu.core.service.api.site.vo.ApiSiteNearestFrontWarehouseVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * C端站点控制器
 */
@Tag(name = "前台站点")
@RestController
@RequestMapping("api/site")
public class SiteController {

    @Resource
    private ISiteService siteService;
    @Resource
    private IShopMemberService shopMemberService;

    /**
     * 商圈列表（不分页），对齐 PHP api/site/circle/list
     */
    @Operation(summary = "获取商圈列表")
    @GetMapping("/circle/list")
    public Result<List<?>> circleList(ApiSiteCircleListParam param) {
        return Result.success(siteService.getCircleList(param));
    }

    /**
     * 检查店铺是否在配送范围内，对齐 PHP api/site/area/check
     */
    @Operation(summary = "检查店铺是否在配送范围内")
    @GetMapping("/area/check")
    public Result<ApiSiteAreaCheckVo> checkSiteInArea(@RequestParam Map<String, String> allParams) {
        ApiSiteAreaCheckParam param = ParamHelper.mapToBean(allParams, ApiSiteAreaCheckParam.class);
        return Result.success(siteService.checkSiteInArea(param));
    }

    /**
     * 附近前置仓， api/site/front_warehouse/nearest
     */
    @Operation(summary = "附近前置仓")
    @GetMapping("/front_warehouse/nearest")
    public Result<ApiSiteNearestFrontWarehouseVo> nearestFrontWarehouse(@RequestParam Map<String, String> allParams) {
        ApiSiteNearestFrontWarehouseParam param = ParamHelper.mapToBean(allParams, ApiSiteNearestFrontWarehouseParam.class);
        return Result.success(siteService.getNearestFrontWarehouse(param));
    }

    /**
     * @param siteId   店铺id
     * @param isFollow 1 关注  0取消关注
     * @return 关注/取消关注店铺
     */
    @Operation(summary = "关注或取消关注店铺")
    @PutMapping("/shop/{siteId}/{isFollow}/{addon}")
    public Result editFollowNumber(@PathVariable("siteId") Integer siteId, @PathVariable("isFollow") Integer isFollow, @PathVariable("addon") String addon) {
        shopMemberService.follow(siteId, isFollow, addon);
        return Result.success();
    }

    /**
     * @param pageParam 分页参数
     * @return 获取店铺关注、收藏列表
     */
    @Operation(summary = "获取关注店铺列表")
    @GetMapping("/follow")
    public Result<PageResult<SiteShopFollowVo>> followList(@Validated PageParam pageParam, @RequestParam(required = false) String addon) {
        return Result.success(shopMemberService.followList(pageParam, addon));
    }

    /**
     * 店铺信息，对齐 PHP api/site/info/:site_id
     */
    @Operation(summary = "获取站点详情")
    @SaCheckLogin
    @GetMapping("/info/{site_id}")
    public Result<ApiSiteInfoVo> info(@PathVariable("site_id") Integer siteId, @RequestParam Map<String, String> allParams) {
        ApiSiteInfoParam param = ParamHelper.mapToBean(allParams, ApiSiteInfoParam.class);
        param.setSiteId(siteId);
        return Result.success(siteService.getInfo(param));
    }
}
