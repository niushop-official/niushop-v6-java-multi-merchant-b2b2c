package com.niu.core.controller.siteapi.delivery;

import com.niu.core.common.domain.Result;
import com.niu.core.service.site.delivery.IDeliveryCompanyService;
import com.niu.core.service.site.delivery.param.DeliveryCompanySearchParam;
import com.niu.core.service.site.delivery.vo.DeliveryCompanyInfoVo;
import com.niu.core.service.site.delivery.vo.DeliveryCompanyListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物流公司控制器
 */

@RestController
@RequestMapping("adminapi/delivery/site/delivery/company")
public class DeliveryCompanyController {

    @Resource
    IDeliveryCompanyService deliveryCompanyService;


    /**
     * 物流公司列表
     * @param searchParam 搜索条件
     * @return Result<List<ShopDeliveryCompanyListVo>>
     */
    @GetMapping("")
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

}
