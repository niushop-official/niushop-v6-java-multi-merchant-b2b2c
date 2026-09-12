package com.niu.core.controller.siteapi.goods;

import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.goods.param.GoodsBadgeSearchParam;
import com.niu.core.service.admin.goods.vo.GoodsBadgeListVo;
import com.niu.core.service.site.goods.IGoodsBadgeService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品角标控制器（站点端）
 * 对应 PHP: app\adminapi\controller\goods\site\Badge
 */
@RestController
@RequestMapping("adminapi/basic/site/goods/badge")
public class BadgeController {

    @Resource
    private IGoodsBadgeService goodsBadgeService;

    @GetMapping("/list")
    public Result<List<GoodsBadgeListVo>> list(@Validated GoodsBadgeSearchParam searchParam) {
        return Result.success(goodsBadgeService.list(searchParam));
    }
}
