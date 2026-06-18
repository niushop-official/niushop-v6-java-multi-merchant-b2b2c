package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.param.SiteAppToolsParam;
import com.niu.core.service.admin.site.vo.ShowAppListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 站点控制器
 */

@RestController
@RequestMapping("adminapi/site")
@SaCheckLogin
public class SiteController {

    @Resource
    ISiteService siteService;

    @Resource
    IAuthService authService;

    /**
     * 获取所有菜单
     *
     * @return
     */
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
    @GetMapping("/siteapp")
    public Result<Map<String, ShowAppListVo>> showSiteApp(SiteAppToolsParam param) {
        return Result.success(siteService.getSiteAppTools(param));
    }

    @GetMapping("/admin/menu")
    public Result<JSONArray> adminMenu() {
        return Result.success(authService.getAdminAuthMenuList(true, 1, "all", 0));
    }

    /**
     * 获取站点拥有的营销工具
     *
     * @return Response
     */
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
    @GetMapping("/adminpromotion")
    public Result<?> adminPromotionTools() {
        SiteAppToolsParam param = new SiteAppToolsParam();
        return Result.success(siteService.getAdminPromotionTools(param));
    }
}
