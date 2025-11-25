package com.niu.core.controller.adminapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.shop.ShopApplyEnum;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.service.admin.shop.ISiteShopApplyService;
import com.niu.core.service.admin.shop.param.SiteShopApplyParam;
import com.niu.core.service.admin.shop.param.SiteShopApplySearchParam;
import com.niu.core.service.admin.shop.param.SiteShopApplyVerifyParam;
import com.niu.core.service.admin.shop.vo.SiteShopApplyInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopApplyListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.Map;

/**
 * 商户入驻申请表控制器
 */

@RestController
@RequestMapping("adminapi/shop/admin/shop_apply")
public class SiteShopApplyController {

    @Resource
    ISiteShopApplyService siteShopApplyService;

    /**
     * 商户入驻申请表分页列表
     *
     * @param pageParam 分页
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
     * @return
     */
    @GetMapping("/statuslist")
    public Result<Map<Integer, String>> statusList(){
        return Result.success(ShopApplyEnum.getStatus());
    }

}
