package com.niu.core.controller.adminapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.sys.SeparTypeEnum;
import com.niu.core.service.admin.shop.ISitePaymentApplyService;
import com.niu.core.service.core.site_payment.param.SitePaymentApplyParam;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("adminapi/shop/admin/spear_user_apply")
public class SitePaymentApplyController {

    @Resource
    private ISitePaymentApplyService applyService;

    /**
     * 获取平台分账账户列表
     *
     * @return
     */
    @GetMapping("")
    public Result<?> getList(@Validated PageParam pageParam,
                             @RequestParam(value = "site_name", required = false) String siteName,
                             @RequestParam(value = "type", required = false) String type,
                             @RequestParam(value = "create_time[]", required = false)
                             @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime[] createTime) {
        SitePaymentApplyParam param = new SitePaymentApplyParam();
        if (siteName != null) param.setSiteName(siteName);
        if (type != null) param.setType(type);
        if (createTime != null && createTime.length == 2) {
            List<Long> list = new ArrayList<>();
            list.add(createTime[0].atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
            list.add(createTime[1].atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
            param.setCreateTime(list);
        }
        return Result.success(applyService.getPage(pageParam, param));
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable("id") Integer id) {
        return Result.success(applyService.getDetail(id));
    }

    @GetMapping("/type")
    public Result<?> type() {
        return Result.success(SeparTypeEnum.getMap());
    }

    @GetMapping("/status")
    public Result<?> status() {
        return Result.success();
    }
}
