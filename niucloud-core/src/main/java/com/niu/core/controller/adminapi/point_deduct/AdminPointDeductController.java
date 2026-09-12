package com.niu.core.controller.adminapi.point_deduct;

import cn.hutool.core.bean.BeanUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.entity.point_deduct.PointDeduct;
import com.niu.core.service.core.point_deduct.IPointDeductService;
import com.niu.core.service.core.point_deduct.param.PointDeductParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("adminapi/basic/admin/point_deduct/config")
@Api(tags = "平台端-积分抵扣配置")
public class AdminPointDeductController {

    @Autowired
    private IPointDeductService pointDeductService;

    @PostMapping
    @ApiOperation("设置平台积分抵扣配置")
    public Result setPointDeduct(@Valid @RequestBody PointDeductParam param) {
        PointDeduct pointDeduct = new PointDeduct();
        BeanUtil.copyProperties(param, pointDeduct);
        pointDeductService.setPointDeductConfig(pointDeduct);
        return Result.success();
    }

    @GetMapping
    @ApiOperation("获取平台积分抵扣配置")
    public Result<PointDeduct> getPointDeduct() {
        PointDeduct config = pointDeductService.getPointDeductConfig();
        return Result.success(config);
    }
}