package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.SiteAppToolsParam;
import com.niu.core.service.admin.site.param.SiteCircleListParam;
import com.niu.core.service.admin.site.param.SiteSearchParam;
import com.niu.core.service.site.shop.param.SiteShopBusinessStatusParam;
import com.niu.core.service.admin.site.vo.ShowAppListVo;
import com.niu.core.service.admin.site.vo.SiteListVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 站点控制器
 */
@Tag(name = "管理端站点")
@RestController
@RequestMapping("adminapi/site")
@SaCheckLogin
public class SiteController {

    @Resource
    ISiteService siteService;

    @Resource
    IAuthService authService;

    /**
     * 站点列表（不分页），对齐 PHP adminapi/site/list
     */
    @Operation(summary = "获取站点列表")
    @GetMapping("/list")
    public Result<List<SiteListVo>> list(@Validated SiteSearchParam searchParam) {
        return Result.success(siteService.getList(searchParam));
    }

    /**
     * 获取所有菜单
     *
     * @return
     */
    @Operation(summary = "获取全部菜单")
    @GetMapping("/site/menu")
    public Result<JSONArray> menu() {
        return Result.success(authService.getAuthMenuTreeList("all"));
    }

    /**
     * 获取平台拥有的应用工具列表
     *
     * @param param 搜索参数
     * @return
     */
    @Operation(summary = "获取平台应用工具")
    @GetMapping("/adminapp")
    public Result<Map<String, ShowAppListVo>> showAdminApp(SiteAppToolsParam param) {
        return Result.success(siteService.getAdminAppTools(param));
    }

    /**
     * 获取站点拥有的应用工具列表
     *
     * @param param 搜索参数
     * @return
     */
    @Operation(summary = "获取站点应用工具")
    @GetMapping("/siteapp")
    public Result<Map<String, ShowAppListVo>> showSiteApp(SiteAppToolsParam param) {
        return Result.success(siteService.getSiteAppTools(param));
    }

    @Operation(summary = "获取平台菜单")
    @GetMapping("/admin/menu")
    public Result<JSONArray> adminMenu() {
        return Result.success(authService.getAdminAuthMenuList(true, 1, "all", 0));
    }

    /**
     * 获取站点拥有的营销工具
     *
     * @return Response
     */
    @Operation(summary = "获取站点营销工具")
    @GetMapping("/sitepromotion")
    public Result<?> sitePromotionTools() {
        SiteAppToolsParam param = new SiteAppToolsParam();
        return Result.success(siteService.getSitePromotionTools(param));
    }

    /**
     * 获取平台拥有的营销工具
     *
     * @return Response
     */
    @Operation(summary = "获取平台营销工具")
    @GetMapping("/adminpromotion")
    public Result<?> adminPromotionTools() {
        SiteAppToolsParam param = new SiteAppToolsParam();
        return Result.success(siteService.getAdminPromotionTools(param));
    }

    /**
     * 商圈列表（不分页），对齐 PHP adminapi/site/circle/list
     */
    @Operation(summary = "获取商圈列表")
    @GetMapping("/circle/list")
    public Result<List<?>> circleList(SiteCircleListParam param) {
        return Result.success(siteService.getCircleList(param));
    }

    /**
     * 编辑站点营业状态，对齐 PHP adminapi/site/business_status
     */
    @Operation(summary = "修改站点营业状态")
    @PutMapping("/business_status")
    public Result<Object> editBusinessStatus(@Validated @RequestBody SiteShopBusinessStatusParam param) {
        siteService.editBusinessStatus(param.getBusinessStatus());
        return Result.success();
    }
}
