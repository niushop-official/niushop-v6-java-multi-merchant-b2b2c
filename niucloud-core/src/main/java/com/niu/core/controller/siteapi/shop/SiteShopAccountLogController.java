package com.niu.core.controller.siteapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.shop.ShopAccountEnum;
import com.niu.core.service.site.shop.ISiteShopAccountLogService;
import com.niu.core.service.site.shop.ISiteShopInfoService;
import com.niu.core.service.admin.shop.param.SiteShopAccountLogSearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopAccountLogInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopAccountLogListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家结算账单记录表控制器
 */

@RestController
@RequestMapping("adminapi/shop/site/shop/account")
public class SiteShopAccountLogController {

    @Resource
    ISiteShopAccountLogService siteShopAccountLogService;

    @Resource
    ISiteShopInfoService siteShopService;

    /**
     * 商家结算账单记录表分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteShopAccountLogListVo>>
     */
    @GetMapping("/log")
    public Result<PageResult<SiteShopAccountLogListVo>> page(@Validated PageParam pageParam, @Validated SiteShopAccountLogSearchParam searchParam) {
        PageResult<SiteShopAccountLogListVo> list = siteShopAccountLogService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商家结算账单记录表详情
     *
     * @param id 主键ID
     * @return Result<SiteShopAccountLogInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SiteShopAccountLogInfoVo> info(@PathVariable("id") Integer id) {
        SiteShopAccountLogInfoVo info = siteShopAccountLogService.info(id);
        return Result.success(info);
    }

    /**
     * 获取账户变动方式
     *
     * @return Result<Object>
     */
    @GetMapping("/typelist")
    public Result<Object> getAccountType() {
        return Result.success(ShopAccountEnum.getMap());
    }

    /**
     * 检查商家收款账户信息是否已经完成配置
     *
     * @return Result<Map<String, Integer>>
     */
    @GetMapping("/check")
    public Result<Map<String, Integer>> checkAccountPayInfo() {
        Map<String, Integer> data = new HashMap<>();

        if (siteShopService.check()) {
            data.put("is_fill", 1);
        } else {
            data.put("is_fill", 0);
        }

        return Result.success(data);
    }
}