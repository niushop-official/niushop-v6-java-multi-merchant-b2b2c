package com.niu.core.controller.api.shop;


import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.api.goods.IGoodsServiceService;
import com.niu.core.service.api.shop.IShopConfigService;
import com.niu.core.service.api.shop.ISiteShopApplyService;
import com.niu.core.service.api.shop.ISiteShopService;
import com.niu.core.service.api.shop.param.ShopSearchParam;
import com.niu.core.service.api.shop.param.ShopSearchPostParam;
import com.niu.core.service.api.shop.param.SiteShopApplyParam;
import com.niu.core.service.api.shop.vo.*;
import com.niu.core.service.core.shop.vo.ShopApplyConfigInfoVo;
import com.niu.core.service.site.shop.ISiteShopMemberService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 商品控制器
 */
@RestController
@RequestMapping("api/shop")
public class ShopController {


    @Resource
    IGoodsServiceService goodsService;

    @Resource
    ISiteGroupService siteGroupService;

    @Resource
    ISiteShopApplyService siteShopApplyService;

    @Resource
    ISiteShopMemberService siteShopMemberService;

    @Resource
    ISiteShopService siteShopService;

    @Resource
    IShopConfigService shopConfigService;

    /**
     * 获取商品列表
     *
     * @param searchParam 商品搜索参数
     * @return
     */
    @PostMapping("/web/shop_list")
    public Result<PageResult<SiteShopListVo>> shopList(@Validated @RequestBody ShopSearchPostParam searchParam) {
        PageResult<SiteShopListVo> list = goodsService.shopList(searchParam);
        return Result.success(list);
    }


    /**
     * @param shopSearchParam 商品搜索参数
     * @return 获取商品列表
     */
    @PostMapping("/web/shop/component")
    public Result<List<SiteShopListVo>> component(@Validated @RequestBody ShopSearchParam shopSearchParam) {
        List<SiteShopListVo> list = goodsService.component(shopSearchParam);
        return Result.success(list);
    }

    /**
     * @return 获取店铺分类列表
     */
    @GetMapping("/web/shop_apply/shop_category")
    public Result<List<SiteShopCategoryListVo>> shopCategory() {
        List<SiteShopCategoryListVo> list = goodsService.shopCategory();
        return Result.success(list);
    }


    /**
     * @return 获取店铺套餐列表
     */
    @GetMapping("/web/shop_apply/site_group")
    public Result<List<SiteGroup>> siteGroup() {
        List<SiteGroup> all = siteGroupService.getAll();
        return Result.success(all);
    }


    /**
     * @return 获取店铺申请状态列表
     */
    @GetMapping("/web/shop_apply/status")
    public Result<List<Map<String, Object>>> statusList() {
        List<Map<String, Object>> map = siteShopApplyService.statusList();
        return Result.success(map);
    }

    /**
     * @return 获取店铺申请配置
     */
    @GetMapping("/web/shop_apply/config")
    public Result<ShopApplyConfigInfoVo> getShopApplyConfig() {
        return Result.success(shopConfigService.getShopApplyConfig());
    }


    /**
     * @param addParam 申请参数
     * @return 商户入驻店铺申请
     */
    @PostMapping("/web/shop_apply")
    public Result shopApply(@Validated @RequestBody SiteShopApplyParam addParam) {
        siteShopApplyService.shopApply(addParam);
        return Result.success();
    }


    /**
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return 获取店铺申请列表
     */
    @GetMapping("/web/shop_apply")
    public Result<PageResult<SiteShopApplyListVo>> shopApply(@Validated PageParam pageParam, @Validated com.niu.core.service.api.shop.param.SiteShopApplySearchParam searchParam) {
        return Result.success(siteShopApplyService.page(pageParam, searchParam));
    }

    /**
     * @param pageParam 分页参数
     * @return 获取店铺关注、收藏列表
     */
    @GetMapping("/web/follow")
    public Result<PageResult<SiteShopFollowVo>> followList(@Validated PageParam pageParam) {
        return Result.success(siteShopMemberService.followList(pageParam));
    }


    /**
     * @param siteId 店铺ID
     * @return 获取店铺扩展信息
     */
    @GetMapping("/web/shop/{siteId}")
    public Result<SiteShopInfoVo> getSiteBySiteId(@PathVariable("siteId") Object siteId, @RequestParam Map<String, String> allParams) {
        return Result.success(siteShopService.info(siteId, allParams));
    }

    /**
     * @param siteId 店铺ID
     * @return 获取店铺二维码
     */
    @GetMapping("/web/shop/qrcode/{siteId}")
    public Result<Object> shopQrcode(@PathVariable("siteId") Integer siteId) {
        Object result = siteShopService.shopQrcode(siteId);
        return Result.success(result);
    }


    /**
     * @param siteId   店铺id
     * @param isFollow 1 关注  0取消关注
     * @return 关注/取消关注店铺
     */
    @PutMapping("/web/shop/{siteId}/{isFollow}")
    public Result editFollowNumber(@PathVariable("siteId") Integer siteId, @PathVariable("isFollow") Integer isFollow) {
        siteShopService.editFollowNumber(siteId, isFollow);
        return Result.success();
    }


    /**
     * @return 甄选好店
     */
    @GetMapping("/web/select_shop_list")
    public Result selectShopList() {
        return Result.success(siteShopService.selectShopList());
    }


    @GetMapping("/web/shop_apply/check")
    public Result<Boolean> applyCheck() {

        return Result.success(siteShopApplyService.applyCheck());
    }

    /**
     * 店铺账号检测
     *
     * @return Response
     */
    @GetMapping("/web/shop_apply/account_check")
    public Result<Map<String, Object>> accountCheck(@RequestParam("account") String account) {
        return Result.success(siteShopApplyService.checkAccount(account));
    }

    @GetMapping("/web/shop_apply/{id}")
    public Result<SiteShopApplyInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(siteShopApplyService.info(id));
    }

    /**
     * 编辑店铺申请
     *
     * @param id        店铺申请id
     * @param editParam 编辑参数
     * @return Result
     */
    @PutMapping("/web/shop_apply/{id}")
    public Result edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopApplyParam editParam) {
        siteShopApplyService.edit(id, editParam);
        return Result.success();
    }

}
