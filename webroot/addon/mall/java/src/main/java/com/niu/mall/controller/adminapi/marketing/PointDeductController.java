package com.niu.mall.controller.adminapi.marketing;

import com.niu.core.common.domain.Result;
import com.niu.core.service.core.point_deduct.ICorePointDeductService;
import com.niu.core.entity.point_deduct.PointDeduct;
import com.niu.mall.service.admin.marketing.param.PointDeductParam;
import com.niu.mall.service.admin.marketing.vo.PointDeductVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 积分抵扣规则配置
 */
@RestController
@RequestMapping("adminapi/mall/admin/point_deduct/config")
public class PointDeductController {

    @Resource
    private ICorePointDeductService pointDeductService;

    /**
     * 获取积分抵扣规则
     */
    @GetMapping("")
    public Result<PointDeductVo> getPointDeduct() {
        var config = pointDeductService.getPointDeductConfig();
        var vo = new PointDeductVo();
        BeanUtils.copyProperties(config, vo);
        return Result.success(vo);
    }

    /**
     * 设置积分抵扣规则
     */
    @PostMapping("")
    public Result<?> setPointDeduct(@Validated @RequestBody PointDeductParam param) {
        var config = new PointDeduct();
        BeanUtils.copyProperties(param, config);
        config.setDesc("每1积分抵扣" + param.getDeductAmount() + "元");
        pointDeductService.setPointDeductConfig(config);
        return Result.success();
    }
}
