package com.niu.mall.controller.api.web;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.api.web.IMallWebFloorService;
import com.niu.mall.service.api.web.vo.MallWebFloorListVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 首页楼层控制器
 */

@RestController
@RequestMapping("api/mall/web/floor")
public class MallWebFloorController {

    @Resource
    IMallWebFloorService shopWebFloorService;

    /**
     * 首页楼层分页列表
     *
     * @return Result<List<ShopWebFloorListVo>>
     */
    @GetMapping("")
    public Result<PageResult<MallWebFloorListVo>> list(PageParam pageParam) {
        return Result.success(shopWebFloorService.list(pageParam));
    }

}
