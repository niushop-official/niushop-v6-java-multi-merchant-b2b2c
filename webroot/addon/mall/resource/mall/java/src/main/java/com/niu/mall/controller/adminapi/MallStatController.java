package com.niu.mall.controller.adminapi;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.admin.stat.IMallStatService;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 【请填写功能名称】控制器
 */

@RestController
@RequestMapping("adminapi/mall/admin/stat")
public class MallStatController {

    @Resource
    IMallStatService iMallStatService;

    /**
     *
     * @return
     */
    @GetMapping("")
    public Result stat() {
        return Result.success(iMallStatService.getStatData("",""));
    }

    /**
     *
     * @return
     */
    @GetMapping("/total")
    public Result total() {
        return Result.success(iMallStatService.getStat());
    }

    /**
     *
     * @return
     */
    @GetMapping("/today")
    public Result today() {
        DateTime date = new DateTime(DateUtil.today());
        return Result.success(iMallStatService.getStat(DateUtil.beginOfDay(date).getTime() / 1000, DateUtil.endOfDay(date).getTime() / 1000));
    }

    /**
     *
     * @return
     */
    @GetMapping("/yesterday")
    public Result yesterday() {
        DateTime date = new DateTime(DateUtil.yesterday());
        return Result.success(iMallStatService.getStat(DateUtil.beginOfDay(date).getTime() / 1000, DateUtil.endOfDay(date).getTime() / 1000));
    }

    /**
     *
     * @return
     */
    @GetMapping("/order")
    public Result order() {
        return Result.success(iMallStatService.orderStat());
    }

    /**
     *
     * @return
     */
    @GetMapping("/goods")
    public Result goods() {
        return Result.success(iMallStatService.goodsStat());
    }
}
