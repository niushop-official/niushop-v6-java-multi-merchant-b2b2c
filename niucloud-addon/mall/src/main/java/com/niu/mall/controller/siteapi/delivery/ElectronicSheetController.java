package com.niu.mall.controller.siteapi.delivery;

import cn.hutool.core.collection.CollectionUtil;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.service.core.delivery.param.ShopDeliveryPrintElectronicSheetParam;
import com.niu.core.service.core.delivery.vo.ElectronicSheetPrintVo;
import com.niu.core.service.site.delivery.IDeliveryElectronicSheetService;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetListsParam;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetPagesParam;
import com.niu.core.service.site.delivery.param.DeliveryElectronicSheetParam;
import com.niu.core.service.site.delivery.vo.DeliveryElectronicSheetInfoVo;
import com.niu.mall.service.admin.delivery.IMallDeliveryElectronicSheetService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 电子面单控制器
 */

@RestController
@RequestMapping("adminapi/delivery/site/electronic_sheet")
public class ElectronicSheetController {

    @Resource
    IMallDeliveryElectronicSheetService iDeliveryElectronicSheetService;


    @PostMapping("/print")
    public Result<?> printElectronicSheet(@RequestBody ShopDeliveryPrintElectronicSheetParam param) {
        List<ElectronicSheetPrintVo> results = iDeliveryElectronicSheetService.printElectronicSheet(param);
        if (CollectionUtil.size(results) != 1) {
            return Result.success(results);
        }

        ElectronicSheetPrintVo result = results.get(0);
        if (result.isSuccess()) {
            return Result.success(results);
        }

        return Result.fail(result.getReason());
    }
}
