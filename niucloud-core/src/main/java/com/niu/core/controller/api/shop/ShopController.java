package com.niu.core.controller.api.shop;


import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.service.admin.site.ISiteGroupService;
import com.niu.core.service.api.goods.IGoodsServiceService;
import com.niu.core.service.api.shop.IShopConfigService;
import com.niu.core.service.api.shop.IShopMemberService;
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

    @Resource
    IShopMemberService shopMemberService;

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
     * @deprecated 请使用 {@code api/mall/shop_apply} 及站点行业类目接口
     */
    @Deprecated
    @GetMapping("/web/shop_apply/shop_category")
    public Result<List<SiteShopCategoryListVo>> shopCategory() {
        List<SiteShopCategoryListVo> list = goodsService.shopCategory();
        return Result.success(list);
    }


    /**
     * @return 获取店铺套餐列表
     * @deprecated 请使用 {@code api/mall/shop_apply} 相关接口
     */
    @Deprecated
    @GetMapping("/web/shop_apply/site_group")
    public Result<List<SiteGroup>> siteGroup() {
        return Result.success(siteGroupService.getApplyList());
    }


    /**
     * @return 获取店铺申请状态列表
     * @deprecated 请使用 {@code GET api/mall/shop_apply/status}
     */
    @Deprecated
    @GetMapping("/web/shop_apply/status")
    public Result<List<Map<String, Object>>> statusList() {
        List<Map<String, Object>> map = siteShopApplyService.statusList();
        return Result.success(map);
    }

    /**
     * @return 获取店铺申请配置
     * @deprecated 请使用 {@code GET api/mall/shop_apply/config}
     */
    @Deprecated
    @GetMapping("/web/shop_apply/config")
    public Result<ShopApplyConfigInfoVo> getShopApplyConfig() {
        return Result.success(shopConfigService.getShopApplyConfig());
    }


    /**
     * @param addParam 申请参数
     * @return 商户入驻店铺申请（旧版）
     * @deprecated 请使用 {@code POST api/mall/shop_apply}
     */
    @Deprecated
    @PostMapping("/web/shop_apply")
    public Result shopApply(@Validated @RequestBody SiteShopApplyParam addParam) {
        siteShopApplyService.shopApply(addParam);
        return Result.success();
    }

    /**
     * @param addParam 申请参数
     * @return 商户入驻店铺申请（新版，支持草稿保存）
     * @deprecated 请使用 {@code POST api/mall/shop_apply}
     */
    @Deprecated
    @PostMapping("/web/shop_apply/add")
    public Result<Map<String, Long>> add(@Validated @RequestBody SiteShopApplyParam addParam) {
        Long applyId = siteShopApplyService.add(addParam);
        return Result.success(Map.of("id", applyId));
    }


    /**
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return 获取店铺申请列表
     * @deprecated 请使用 {@code GET api/mall/shop_apply}
     */
    @Deprecated
    @GetMapping("/web/shop_apply")
    public Result<PageResult<SiteShopApplyListVo>> shopApply(@Validated PageParam pageParam, @Validated com.niu.core.service.api.shop.param.SiteShopApplySearchParam searchParam) {
        return Result.success(siteShopApplyService.page(pageParam, searchParam));
    }

    /**
     * 店铺收藏列表（对齐 PHP shop.ShopMember/lists）
     */
    @GetMapping("/web/follow")
    public Result<PageResult<SiteShopFollowVo>> followList(@Validated PageParam pageParam, @RequestParam(required = false) String addon) {
        return Result.success(shopMemberService.followList(pageParam, addon));
    }

    /**
     * 当前是否关注店铺（对齐 PHP shop.ShopMember/getFollowBySiteAndAddon）
     */
    @GetMapping("/web/follow/get_by_site/{siteId}/{addon}")
    public Result<Boolean> getFollowBySiteAndAddon(@PathVariable("siteId") Integer siteId,
                                                   @PathVariable("addon") String addon) {
        return Result.success(shopMemberService.getFollowBySiteAndAddon(siteId, addon));
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
     * 关注/取消关注店铺（对齐 PHP shop.ShopMember/follow）
     *
     * @param siteId   店铺id
     * @param isFollow 1 关注  0取消关注
     * @param addon    所属插件
     */
    @PutMapping("/web/shop/{siteId}/{isFollow}/{addon}")
    public Result editFollowNumber(@PathVariable("siteId") Integer siteId,
                                   @PathVariable("isFollow") Integer isFollow,
                                   @PathVariable("addon") String addon) {
        shopMemberService.follow(siteId, isFollow, addon);
        return Result.success();
    }


    /**
     * @return 甄选好店
     */
    @GetMapping("/web/select_shop_list")
    public Result selectShopList() {
        return Result.success(siteShopService.selectShopList());
    }


    /**
     * @deprecated 请使用 mall 入驻流程，PHP 无此接口
     */
    @Deprecated
    @GetMapping("/web/shop_apply/check")
    public Result<Boolean> applyCheck() {

        return Result.success(siteShopApplyService.applyCheck());
    }

    /**
     * 店铺账号检测
     *
     * @deprecated 请使用 {@code GET api/mall/shop_apply/account_check}
     */
    @Deprecated
    @GetMapping("/web/shop_apply/account_check")
    public Result<Map<String, Object>> accountCheck(@RequestParam("account") String account) {
        return Result.success(siteShopApplyService.checkAccount(account));
    }

    /**
     * @deprecated 请使用 {@code GET api/mall/shop_apply/{id}}
     */
    @Deprecated
    @GetMapping("/web/shop_apply/{id}")
    public Result<SiteShopApplyInfoVo> info(@PathVariable("id") Integer id) {
        return Result.success(siteShopApplyService.info(id));
    }

    /**
     * 编辑店铺申请
     *
     * @deprecated 请使用 {@code PUT api/mall/shop_apply/{id}}
     */
    @Deprecated
    @PutMapping("/web/shop_apply/{id}")
    public Result edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopApplyParam editParam) {
        siteShopApplyService.edit(id, editParam);
        return Result.success();
    }

}