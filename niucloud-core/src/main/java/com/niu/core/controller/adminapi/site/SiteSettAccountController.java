package com.niu.core.controller.adminapi.site;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.annotation.NoRepeatSubmit;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.site.ISiteSettAccountService;
import com.niu.core.service.admin.site.param.SiteSettAccountParam;
import com.niu.core.service.admin.site.param.SiteSettAccountSearchParam;
import com.niu.core.service.admin.site.vo.SiteSettAccountVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 站点结算账户控制器
 * 提供结算账户的CRUD操作接口
 */
@RestController
@RequestMapping("/adminapi/site/sett_account")
@SaCheckLogin
public class SiteSettAccountController {

    /**
     * 结算账户服务接口
     */
    @Resource
    ISiteSettAccountService siteSettAccountService;

    /**
     * 获取结算账户列表
     *
     * @param searchParam 搜索参数
     * @return 结算账户列表
     */
    @GetMapping("")
    public Result<List<SiteSettAccountVo>> lists(SiteSettAccountSearchParam searchParam) {
        return Result.success(siteSettAccountService.getList(searchParam));
    }

    /**
     * 获取结算账户详情
     *
     * @param id 账户ID
     * @return 结算账户详情
     */
    @GetMapping("/{id}")
    public Result<SiteSettAccountVo> info(@PathVariable("id") Integer id) {
        return Result.success(siteSettAccountService.getInfo(id));
    }

    /**
     * 添加结算账户
     *
     * @param param 结算账户参数
     * @return 添加结果（新增记录ID）
     */
    @PostMapping("")
    @NoRepeatSubmit(interval = 5, message = "请勿重复提交")
    public Result<?> add(@RequestBody SiteSettAccountParam param) {
        Integer result = siteSettAccountService.add(param);
        return Result.success(Map.of("id", result));
    }

    /**
     * 编辑结算账户
     *
     * @param id    账户ID
     * @param param 结算账户参数
     * @return 编辑结果（影响行数）
     */
    @PutMapping("/{id}")
    public Result<?> edit(@PathVariable("id") Integer id, @RequestBody SiteSettAccountParam param) {
        Integer result = siteSettAccountService.edit(id, param);
        return Result.success(Map.of("id", result));
    }

    /**
     * 删除结算账户
     *
     * @param id 账户ID
     * @return 删除结果（影响行数）
     */
    @DeleteMapping("/{id}")
    public Result<?> del(@PathVariable("id") Integer id) {
        Integer result = siteSettAccountService.del(id);
        return Result.success(Map.of("id", result));
    }

}