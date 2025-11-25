package com.niu.core.controller.siteapi.site;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.site.site.ISiteAddressService;
import com.niu.core.service.site.site.param.SiteAddressParam;
import com.niu.core.service.site.site.param.SiteAddressSearchParam;
import com.niu.core.service.site.site.vo.SiteAddressInfoVo;
import com.niu.core.service.site.site.vo.SiteAddressListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商家地址库控制器
 */

@RestController
@RequestMapping("adminapi/delivery/site/order/refund/")
public class SiteAddressResultController {

    @Resource
    ISiteAddressService iSiteAddressService;

    /**
     * 商家地址库列表
     * @param searchParam 搜索条件
     * @return Result<PageResult<ShopAddressListVo>>
     */
    @GetMapping("address")
    public Result<List<SiteAddressListVo>> list(@Validated SiteAddressSearchParam searchParam) {
        return Result.success(iSiteAddressService.list(searchParam));
    }

}
