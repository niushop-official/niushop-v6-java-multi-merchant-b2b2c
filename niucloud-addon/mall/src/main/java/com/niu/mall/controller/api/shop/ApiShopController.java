package com.niu.mall.controller.api.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.ParamHelper;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.service.api.goods.IGoodsServiceService;
import com.niu.core.service.api.shop.ISiteShopService;
import com.niu.core.service.api.shop.param.ShopSearchParam;
import com.niu.core.service.api.shop.param.ShopSearchPostParam;
import com.niu.core.service.api.shop.param.SiteShopApplyParam;
import com.niu.core.service.api.shop.vo.*;
import com.niu.core.service.core.shop.vo.ShopApplyConfigInfoVo;
import com.niu.mall.service.api.shop.IApiShopService;
import com.niu.mall.service.api.shop.vo.ShopBusinessStatusVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/mall")
@Api(tags = "前台-店铺")
public class ApiShopController {

    @Resource
    private IApiShopService apiShopService;
    @Resource
    private IGoodsServiceService goodsService;
    @Resource
    private ISiteShopService siteShopService;

    @GetMapping("/shop/status/{siteId}")
    @ApiOperation("获取店铺营业状态")
    public Result<ShopBusinessStatusVo> status(@PathVariable("siteId") Integer siteId) {
        return Result.success(apiShopService.getBusinessStatus(siteId));
    }

    /**
     * 获取商品列表
     *
     * @param searchParam 商品搜索参数
     * @return
     */
    @PostMapping("/shop_list")
    public Result<PageResult<SiteShopListVo>> shopList(@Validated @RequestBody ShopSearchPostParam searchParam) {
        PageResult<SiteShopListVo> list = goodsService.shopList(searchParam);
        return Result.success(list);
    }

    /**
     * @param siteId 店铺ID
     * @return 获取店铺扩展信息
     */
    @GetMapping("/shop/{siteId}")
    public Result<SiteShopInfoVo> getSiteBySiteId(@PathVariable("siteId") Object siteId, @RequestParam Map<String, String> allParams) {
        return Result.success(siteShopService.info(siteId, allParams));
    }

    /**
     * @param allParams 商品搜索参数
     * @return 获取商品列表
     */
    @GetMapping("/shop/component")
    public Result<List<SiteShopListVo>> component(@RequestParam Map<String, String> allParams) {
        ShopSearchParam param = ParamHelper.mapToBean(allParams, ShopSearchParam.class);

        List<SiteShopListVo> list = goodsService.component(param);
        return Result.success(list);
    }

}
