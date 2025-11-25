package com.niu.core.controller.siteapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.site.delivery.param.DeliveryShippingTemplateParam;
import com.niu.core.service.site.delivery.param.DeliveryShippingTemplateSearchParam;
import com.niu.core.service.site.delivery.IDeliveryShippingTemplateService;
import com.niu.core.service.site.delivery.vo.DeliveryShippingTemplateInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryShippingTemplateListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 运费模板控制器
 */

@RestController
@RequestMapping("adminapi/delivery/site/shipping/template")
public class ShippingTemplateController {

    @Resource
    IDeliveryShippingTemplateService iDeliveryShippingTemplateService;

    /**
     * 运费模板分页列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopDeliveryShippingTemplateListVo>>
     */
    @GetMapping("")
    public Result<PageResult<DeliveryShippingTemplateListVo>> page(@Validated PageParam pageParam, @Validated DeliveryShippingTemplateSearchParam searchParam) {
        PageResult<DeliveryShippingTemplateListVo> list = iDeliveryShippingTemplateService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 运费模板列表
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopDeliveryShippingTemplateListVo>>
     */
    @GetMapping("/list")
    public Result<List<DeliveryShippingTemplateListVo>> list(@Validated DeliveryShippingTemplateSearchParam searchParam) {
        List<DeliveryShippingTemplateListVo> list = iDeliveryShippingTemplateService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 运费模板详情
     * @param id 主键ID
     * @return Result<ShopDeliveryShippingTemplateInfoVo>
     */
    @GetMapping("/{template_id}")
    public Result<DeliveryShippingTemplateInfoVo> info(@PathVariable("template_id") Integer id) {
        DeliveryShippingTemplateInfoVo info = iDeliveryShippingTemplateService.info(id);
        return Result.success(info);
    }

    /**
     * 运费模板添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody DeliveryShippingTemplateParam addParam) {
        iDeliveryShippingTemplateService.add(addParam);
        return Result.success();
    }

    /**
     * 运费模板编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{template_id}")
    public Result<Object> edit(@PathVariable("template_id") Integer id, @Validated @RequestBody DeliveryShippingTemplateParam editParam) {
        iDeliveryShippingTemplateService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 运费模板删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{template_id}")
    public Result<Object> del(@PathVariable("template_id") Integer id) {
        iDeliveryShippingTemplateService.del(id);
        return Result.success();
    }

}
