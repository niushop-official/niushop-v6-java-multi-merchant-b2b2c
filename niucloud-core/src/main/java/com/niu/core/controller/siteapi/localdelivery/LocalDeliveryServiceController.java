package com.niu.core.controller.siteapi.localdelivery;

import com.niu.core.common.domain.Result;
import com.niu.core.service.site.local_delivery.ILocalDeliveryServiceService;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceListVo;
import com.niu.core.service.site.local_delivery.vo.LocalDeliveryServiceVo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 同城配送服务控制器
 */
@RestController
@RequestMapping("adminapi/delivery/site/local_delivery")
public class LocalDeliveryServiceController {

    @Resource
    ILocalDeliveryServiceService localDeliveryServiceService;

    /**
     * 获取已启用配送服务商列表
     * @return Result<PageResult<ShopGoodsAttrListVo>>
     */
    @GetMapping("/list")
    public Result<List<LocalDeliveryServiceListVo>> list() {
        return Result.success(localDeliveryServiceService.list());
    }

    @GetMapping("/service")
    public Result<List<LocalDeliveryServiceVo>> lists() {
        List<LocalDeliveryServiceVo> res = localDeliveryServiceService.getList();
        return Result.success(res);
    }

}
