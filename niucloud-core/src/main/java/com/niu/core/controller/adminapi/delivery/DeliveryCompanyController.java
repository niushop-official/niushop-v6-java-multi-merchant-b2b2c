package com.niu.core.controller.adminapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.common.domain.PageResult;
import com.niu.core.service.admin.delivery.param.DeliveryCompanyParam;
import com.niu.core.service.admin.delivery.param.DeliveryCompanySearchParam;
import com.niu.core.service.admin.delivery.IDeliveryCompanyService;
import com.niu.core.service.admin.delivery.vo.DeliveryCompanyInfoVo;
import com.niu.core.service.admin.delivery.vo.DeliveryCompanyListVo;
import com.niu.core.common.domain.PageParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 物流公司控制器
 */

@RestController
@RequestMapping("adminapi/delivery/admin/delivery/company")
public class DeliveryCompanyController {

    @Resource
    IDeliveryCompanyService deliveryCompanyService;

    /**
     * 物流公司列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopDeliveryCompanyListVo>>
     */
    @GetMapping("")
    public Result<PageResult<DeliveryCompanyListVo>> page(@Validated PageParam pageParam, @Validated DeliveryCompanySearchParam searchParam) {
        PageResult<DeliveryCompanyListVo> list = deliveryCompanyService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 物流公司列表
     * @param searchParam 搜索条件
     * @return Result<List<ShopDeliveryCompanyListVo>>
     */
    @GetMapping("/list")
    public Result<List<DeliveryCompanyListVo>> list(@Validated DeliveryCompanySearchParam searchParam) {
        List<DeliveryCompanyListVo> list = deliveryCompanyService.list(searchParam);
        return Result.success(list);
    }

    /**
     * 物流公司详情
     * @param id 主键ID
     * @return Result<ShopDeliveryCompanyInfoVo>
     */
    @GetMapping("/{id}")
    public Result<DeliveryCompanyInfoVo> info(@PathVariable("id") Integer id) {
        DeliveryCompanyInfoVo info = deliveryCompanyService.info(id);
        return Result.success(info);
    }

    /**
     * 物流公司添加
     * @param addParam 添加参数
     * @return AjaxResult<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody DeliveryCompanyParam addParam) {
        deliveryCompanyService.add(addParam);
        return Result.success();
    }

    /**
     * 物流公司编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody DeliveryCompanyParam editParam) {
        deliveryCompanyService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 物流公司删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        deliveryCompanyService.del(id);
        return Result.success();
    }

}
