package com.niu.core.controller.adminapp;

import com.niu.core.common.domain.Result;
import com.niu.core.enums.adminapp.BottomNavEnum;
import com.niu.core.enums.adminapp.StatAppEnum;
import com.niu.core.enums.adminapp.TodoAppEnum;
import com.niu.core.service.adminapp.IAppsService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;


/**
 * 控制器
 */

@RestController
@RequestMapping("adminapi/adminapp/site")
public class AppsController {

    @Resource
    private IAppsService appsService;

    /**
     * 首页应用
     */
    @GetMapping("/apps_of_index")
    public Result getAppsOfIndex() {
        return Result.success(appsService.getAppsOfIndex());
    }

    /**
     * 应用列表
     */
    @GetMapping("/apps")
    public Result apps() {
        return Result.success(appsService.getAppsOfCheck());
    }

    /**
     * 个人中心应用
     */
    @GetMapping("/apps_of_user_center")
    public Result getAppOfUserCenter() {
        return Result.success(appsService.getAppOfUserCenter());
    }

    /**
     * 底部导航
     */
    @GetMapping("/navs")
    public Result navs() {
        return Result.success(BottomNavEnum.getBottomNav());
    }

    /**
     * 待办
     */
    @GetMapping("/todo")
    public Result todo() {
        return Result.success(TodoAppEnum.getTodo());
    }

    /**
     * 统计
     */
    @GetMapping("/stat")
    public Result stat() {
        return Result.success(StatAppEnum.getStat());
    }
}
