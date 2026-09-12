package com.niu.core.controller.siteapi.point_deduct;

import cn.hutool.core.bean.BeanUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.point_deduct.PointDeduct;
import com.niu.core.service.core.point_deduct.IPointDeductService;
import com.niu.core.service.core.point_deduct.param.SitePointDeductParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("adminapi/basic/site/point_deduct/config")
@Api(tags = "站点端-积分抵扣配置")
public class SitePointDeductController {

    @Autowired
    private IPointDeductService pointDeductService;

    @PostMapping
    @ApiOperation("设置站点积分抵扣配置")
    public Result setSitePointDeduct(@Valid @RequestBody SitePointDeductParam param) {
        PointDeduct pointDeduct = new PointDeduct();
        BeanUtil.copyProperties(param, pointDeduct);
        pointDeduct.setSiteId(RequestUtils.siteId());
        pointDeductService.setSitePointDeductConfig(pointDeduct);
        return Result.success();
    }

    @GetMapping
    @ApiOperation("获取站点积分抵扣配置")
    public Result<PointDeduct> getSitePointDeduct() {
        Integer siteId = RequestUtils.siteId();
        PointDeduct config = pointDeductService.getSitePointDeductConfig(siteId);
        return Result.success(config);
    }
}