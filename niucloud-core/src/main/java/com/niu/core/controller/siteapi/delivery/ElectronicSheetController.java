package com.niu.core.controller.siteapi.delivery;

import cn.hutool.core.collection.CollectionUtil;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.service.site.delivery.IDeliveryElectronicSheetService;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetListsParam;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetPagesParam;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetParam;
import com.niu.core.service.site.delivery.vo.DeliveryElectronicSheetInfoVo;
import com.niu.core.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.core.service.core.delivery.vo.ElectronicSheetPrintVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 电子面单控制器
 */

@RestController
@RequestMapping("adminapi/delivery/site/electronic_sheet")
public class ElectronicSheetController {

    @Resource
    IDeliveryElectronicSheetService iDeliveryElectronicSheetService;

    /**
     * 电子面单列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < ShopDeliveryElectronicSheetListVo>>
     */
    @GetMapping("/list")
    public Result<?> list(@Validated PageParam pageParam, @Validated DeliveryElectronicSheetListsParam searchParam) {
        return Result.success(iDeliveryElectronicSheetService.lists(pageParam, searchParam));
    }

    @GetMapping
    public Result<?> pages(@Validated PageParam pageParam, DeliveryElectronicSheetPagesParam pagesParam) {
        return Result.success(iDeliveryElectronicSheetService.pages(pageParam, pagesParam));
    }

    /**
     * 电子面单详情
     *
     * @param id 主键ID
     * @return Result<ShopDeliveryElectronicSheetInfoVo>
     */
    @GetMapping("/info")
    public Result<DeliveryElectronicSheetInfoVo> info(@Validated @RequestParam("id") Integer id) {
        DeliveryElectronicSheetInfoVo info = iDeliveryElectronicSheetService.info(id);
        return Result.success(info);
    }

    @GetMapping("/{id}")
    public Result<?> detail(@Validated @PathVariable("id") Integer id) {
        return Result.success(iDeliveryElectronicSheetService.info(id));
    }


    /**
     * 电子面单添加
     *
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("/add")
    public Result<Object> add(@Validated @RequestBody DeliveryElectronicSheetParam addParam) {
        iDeliveryElectronicSheetService.add(addParam);
        return Result.success();
    }

    @PostMapping
    public Result<?> addElectronicSheet(@Validated @RequestBody DeliveryElectronicSheetParam addParam) {
        iDeliveryElectronicSheetService.add(addParam);
        return Result.success();
    }

    /**
     * 电子面单编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PostMapping("/edit")
    public Result<Object> edit(Integer id, @Validated @RequestBody DeliveryElectronicSheetParam editParam) {
        iDeliveryElectronicSheetService.edit(id, editParam);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> editElectronicSheet(@PathVariable("id") Integer id, @Validated @RequestBody DeliveryElectronicSheetParam editParam) {
        iDeliveryElectronicSheetService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 电子面单删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @PostMapping("/del")
    public Result<Object> del(@Validated @RequestBody Integer id) {
        iDeliveryElectronicSheetService.del(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delElectronicSheet(@PathVariable("id") Integer id) {
        iDeliveryElectronicSheetService.del(id);
        return Result.success();
    }

    @PostMapping("/config")
    public Result<?> setConfig(@RequestBody Object obj) {
        iDeliveryElectronicSheetService.setElectronicSheetConfig(obj);
        return Result.success();
    }

    /**
     * 电子面单 获取设置
     *
     * @return
     */
    @GetMapping("/config")
    public Result<?> getConfig() {
        return Result.success(iDeliveryElectronicSheetService.getElectronicSheetConfig());
    }

    @GetMapping("/paytype")
    public Result<?> paytype() {
        return Result.success(iDeliveryElectronicSheetService.getPayType());
    }

    @PutMapping("/setDefault/{id}")
    public Result<?> setDefault(@PathVariable("id") Integer id) {
        iDeliveryElectronicSheetService.setDefault(id);
        return Result.success();
    }
}
