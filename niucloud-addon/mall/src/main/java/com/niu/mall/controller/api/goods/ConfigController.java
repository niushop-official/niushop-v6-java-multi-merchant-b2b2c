package com.niu.mall.controller.api.goods;

import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.Result;
import com.niu.mall.service.api.goods.IGoodsConfigService;
import com.niu.mall.service.core.goods.vo.GoodsCategoryConfigVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 商品设置控制器、应该在商城
 */

@RestController
@RequestMapping("api/basic/goods")
public class ConfigController {

    @Resource
    IGoodsConfigService configService;


    /**
     * 获取商品分类配置
     *
     * @return
     */
    @GetMapping("category/config")
    public Result<GoodsCategoryConfigVo> getGoodsCategoryConfig() {
        return Result.success(configService.getGoodsCategoryConfig());
    }

}
