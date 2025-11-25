package com.niu.core.controller.siteapi.site;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.site.param.SiteAddressParam;
import com.niu.core.service.site.site.param.SiteAddressSearchParam;
import com.niu.core.service.site.site.ISiteAddressService;
import com.niu.core.service.site.site.vo.SiteAddressInfoVo;
import com.niu.core.service.site.site.vo.SiteAddressListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

/**
 * 商家地址库控制器
 */

@RestController
@RequestMapping("adminapi/delivery/site/site_address")
public class SiteAddressController {

    @Resource
    ISiteAddressService iSiteAddressService;

    /**
     * 商家地址库列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopAddressListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SiteAddressListVo>> list(@Validated PageParam pageParam, @Validated SiteAddressSearchParam searchParam) {
        PageResult<SiteAddressListVo> list = iSiteAddressService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商家地址库详情
     * @param id 主键ID
     * @return Result<ShopAddressInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SiteAddressInfoVo> info(@PathVariable("id") Integer id) {
        SiteAddressInfoVo info = iSiteAddressService.info(id);
        return Result.success(info);
    }

    /**
     * 获取默认发货地址
     * @return Result<ShopAddressInfoVo>
     */
    @GetMapping("/default/delivery")
    public Result<SiteAddressInfoVo> defaultDelivery() {
        SiteAddressInfoVo info = iSiteAddressService.defaultDelivery();
        return Result.success(info);
    }

    /**
     * 商家地址库添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteAddressParam addParam) {
        iSiteAddressService.add(addParam);
        return Result.success();
    }

    /**
     * 商家地址库编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteAddressParam editParam) {
        iSiteAddressService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商家地址库删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        iSiteAddressService.del(id);
        return Result.success();
    }
}
