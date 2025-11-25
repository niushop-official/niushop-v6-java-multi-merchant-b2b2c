package com.niu.mall.controller.siteapi.marketing;

import cn.hutool.json.JSONArray;
import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.mall.enums.active.ActiveStatusEnum;
import com.niu.mall.service.site.marketing.IMallDiscountService;
import com.niu.mall.service.site.marketing.param.*;
import com.niu.mall.service.site.marketing.vo.*;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 限时折扣控制器
 */

@RestController
@RequestMapping("adminapi/mall/site/active")
public class DiscountController {

    @Resource
    IMallDiscountService shopDiscountService;

    /**
     * 限时折扣列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<MallDiscountListVo>>
     */
    @GetMapping("/discount")
    public Result<PageResult<MallDiscountListVo>> list(@Validated PageParam pageParam,
                                                       @Validated MallDiscountSearchParam searchParam) {
        PageResult<MallDiscountListVo> list = shopDiscountService.getPage(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 限时折扣详情
     * @param id 主键ID
     * @return Result<MallDiscountDetailVo>
     */
    @GetMapping("/discount/{id}")
    public Result<MallDiscountDetailVo> detail(@Validated @PathVariable("id") Integer id) {
        MallDiscountDetailVo detail = shopDiscountService.detail(id);
        return Result.success(detail);
    }



    /**
     * 限时折扣添加
     * @param addParam
     * @return Result<Object>
     */
    @PostMapping("/discount")
    public Result<Object> add(@Validated @RequestBody MallDiscountParam addParam) {
        shopDiscountService.add(addParam);
        return Result.success();
    }

    /**
     * 限时折扣编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/discount/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody MallDiscountParam editParam) {
        shopDiscountService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 限时折扣删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/discount/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        shopDiscountService.del(id);
        return Result.success();
    }
    /**
     * 获取限时折扣状态
     * @return
     */
    @GetMapping("/status")
    public Result<Object> getStatus(){
        return Result.success(ActiveStatusEnum.getMap());
    }

    /**
     * 限时折扣关闭
     * @param id
     * @return
     */
    @PutMapping("/discount/close/{id}")
    public Result<Object> close(@PathVariable("id") Integer id) {
        shopDiscountService.discountClose(id);
        return Result.success();
    }

    /**
     * 活动订单列表
     * @param activeId
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("discount/order/{id}")
    public Result<PageResult<MallDiscountOrderListVo>> order(@PathVariable("id") String activeId, PageParam pageParam, MallDiscountOrderSearchParam searchParam) {
        return Result.success(shopDiscountService.order(Integer.valueOf(activeId), pageParam, searchParam));
    }

    /**
     * 活动会员列表
     * @param activeId
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/discount/member/{id}")
    public Result<PageResult<MallDiscountMemberListVo>> member(@PathVariable("id") String activeId, PageParam pageParam, MallDiscountMemberSearchParam searchParam) {

        return Result.success(shopDiscountService.member(pageParam, Integer.valueOf(activeId), searchParam));
    }

    /**
     * 活动商品列表
     * @param activeId
     * @param pageParam
     * @param searchParam
     * @return
     */
    @GetMapping("/discount/goods/{id}")
    public Result<PageResult<MallDiscountGoodsListVo>> goods(@PathVariable("id") String activeId, PageParam pageParam, MallDiscountGoodsSearchParam searchParam) {

        return Result.success(shopDiscountService.goods(pageParam, Integer.valueOf(activeId), searchParam));
    }

    /**
     * 获取配置
     * @return
     */
    @GetMapping("/discount/config")
    public Result<JSONArray> banner(){
        return Result.success(shopDiscountService.getDiscountBannerConfig());
    }

    /**
     * 活动配置
     * @param config
     * @return
     */
    @PutMapping("/discount/config")
    public Result <Object> saveBanner(@RequestBody MallDiscountConfigParam config) {

        shopDiscountService.setConfig("SHOP_DISCOUNT_BANNER_CONFIG", config.getList());
        return Result.success();
    }





}