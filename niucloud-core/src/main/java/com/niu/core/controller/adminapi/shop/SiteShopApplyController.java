package com.niu.core.controller.adminapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.shop.ShopApplyEnum;
import com.niu.core.service.admin.shop.IShopConfigService;
import com.niu.core.service.admin.shop.ISiteShopApplyService;
import com.niu.core.service.admin.shop.param.SiteShopApplySearchParam;
import com.niu.core.service.admin.shop.param.SiteShopApplyVerifyParam;
import com.niu.core.service.admin.shop.vo.SiteShopApplyInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopApplyListVo;
import com.niu.core.service.core.shop.param.ShopApplyConfigParam;
import com.niu.core.service.core.shop.vo.ShopApplyConfigInfoVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 商户入驻申请表控制器
 */

@RestController
//@RequestMapping("adminapi/mall/admin/shop_apply")
public class SiteShopApplyController {

    @Resource
    ISiteShopApplyService siteShopApplyService;

    @Resource
    IShopConfigService shopConfigService;

    /**
     * 商户入驻申请表分页列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteShopApplyListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SiteShopApplyListVo>> page(@Validated PageParam pageParam, @Validated SiteShopApplySearchParam searchParam) {
        PageResult<SiteShopApplyListVo> list = siteShopApplyService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商户入驻申请表详情
     *
     * @param id 主键ID
     * @return Result<SiteShopApplyInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SiteShopApplyInfoVo> info(@PathVariable("id") Integer id) {
        SiteShopApplyInfoVo info = siteShopApplyService.info(id);
        return Result.success(info);
    }

    /**
     * 商户入驻申请审核
     *
     * @return Result<Object>
     */
    @PutMapping("/verify/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopApplyVerifyParam verifyParam) {
        siteShopApplyService.verify(id, verifyParam);
        return Result.success();
    }

    /**
     * 商户入驻申请表删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        siteShopApplyService.del(id);
        return Result.success();
    }

    /**
     * 站点状态列表
     *
     * @return
     */
    @GetMapping("/statuslist")
    public Result<Map<Integer, String>> statusList() {
        return Result.success(ShopApplyEnum.getStatus());
    }

    /**
     * 获取商户入驻配置
     */
    @GetMapping("/config")
    public Result<ShopApplyConfigInfoVo> getShopApplyConfig() {
        var info = shopConfigService.getShopApplyConfig();
        return Result.success(info);
    }

    /**
     * 设置商户入驻配置
     */
    @PostMapping("/config")
    public Result<Object> setShopApplyConfig(@Validated @RequestBody ShopApplyConfigParam addParam) {
        shopConfigService.setShopApplyConfig(addParam);
        return Result.success();
    }

}
