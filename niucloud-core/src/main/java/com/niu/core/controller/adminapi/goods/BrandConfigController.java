package com.niu.core.controller.adminapi.goods;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.admin.goods.IGoodsBrandService;
import com.niu.core.service.admin.goods.param.*;
import com.niu.core.service.admin.goods.vo.GoodsBrandInfoVo;
import com.niu.core.service.admin.goods.vo.GoodsBrandListVo;
import com.niu.core.service.core.goods.ICoreGoodsConfigService;
import com.niu.core.service.core.goods.param.GoodsConfigParam;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品品牌控制器
 */

@RestController
@RequestMapping("adminapi/basic/admin/goods/config")
public class BrandConfigController {

    @Resource
    ICoreGoodsConfigService coreGoodsConfigService;

    /**
     * 获取商品设置
     */
    @GetMapping("")
    public Result getConfig() {
        return Result.success(coreGoodsConfigService.getConfig());
    }

    /**
     * 商品设置
     *
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> setConfig(@Validated @RequestBody GoodsConfigParam param) {
        coreGoodsConfigService.setConfig(param);
        return Result.success(true);
    }

}
