package com.niu.mall.controller.siteapi.marketing;

import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.core.point_deduct.ICorePointDeductService;
import com.niu.core.entity.point_deduct.PointDeduct;
import com.niu.mall.service.site.marketing.param.PointDeductParam;
import com.niu.mall.service.site.marketing.vo.PointDeductVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("adminapi/mall/site/point_deduct/config")
public class PointDeductController {

    @Resource
    private ICorePointDeductService pointDeductService;

    /**
     * 获取积分抵扣规则
     */
    @GetMapping("")
    public Result<PointDeductVo> getPointDeduct() {
        var config = pointDeductService.getSitePointDeductConfig(RequestUtils.siteId());
        var vo = new PointDeductVo();
        BeanUtils.copyProperties(config, vo);
        return Result.success(vo);
    }

    /**
     * 设置积分抵扣规则
     */
    @PostMapping("")
    public Result<?> setPointDeduct(@Validated @RequestBody PointDeductParam param) {
        param.setSiteId(RequestUtils.siteId());
        var config = new PointDeduct();
        BeanUtils.copyProperties(param, config);
        config.setDesc("");
        pointDeductService.setSitePointDeductConfig(config);
        return Result.success();
    }
}
