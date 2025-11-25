package com.niu.core.controller.siteapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.member.MemberCashOutStatusEnum;
import com.niu.core.enums.pay.TransferTypeEnum;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.service.site.shop.ISiteShopCashOutService;
import com.niu.core.service.admin.shop.param.SiteShopCashOutAuditParam;
import com.niu.core.service.admin.shop.param.SiteShopCashOutParam;
import com.niu.core.service.admin.shop.param.SiteShopCashOutSearchParam;
import com.niu.core.service.admin.shop.vo.SiteShopCashOutInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopCashOutListVo;
import com.niu.core.service.api.shop.param.WithdrawApplyParam;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家站点提现表控制器
 */

@RestController
@RequestMapping("adminapi/shop/site/shop")
public class SiteShopCashOutController {

    @Resource
    ISiteShopCashOutService siteShopCashOutService;

    /**
     * 商家站点提现表分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteShopCashOutListVo>>
     */
    @GetMapping("/cash_out")
    public Result<PageResult<SiteShopCashOutListVo>> page(@Validated PageParam pageParam, @Validated SiteShopCashOutSearchParam searchParam) {
        PageResult<SiteShopCashOutListVo> list = siteShopCashOutService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 商家站点提现表详情
     *
     * @param id 主键ID
     * @return Result<SiteShopCashOutInfoVo>
     */
    @GetMapping("/cash_out/{id}")
    public Result<SiteShopCashOutInfoVo> info(@PathVariable("id") String id) {
        SiteShopCashOutInfoVo info = siteShopCashOutService.info(id);
        return Result.success(info);
    }

    /**
     * 店铺转账
     *
     * @param id 参数
     * @return Result<Object>
     */
    @GetMapping("/cash_out/transfer/{id}")
    public Result<Object> transfer(@PathVariable("id") Integer id) {
        return Result.success(siteShopCashOutService.transfer(id));
    }

    /**
     * 转账方式
     */
    @GetMapping("/cash_out/transfer_type")
    public Result<Map> getTransferType() {
        return Result.success(TransferTypeEnum.getMap());
    }

    /**
     * 提现状态
     */
    @GetMapping("/cash_out/status")
    public Result<Map> getStatus() {
        return Result.success(ShopCashOutEnum.getStatus());
    }

    @PostMapping("/cash_apply")
    public Result<Map<String, Object>> applyWithdraw(@Valid @RequestBody WithdrawApplyParam withdrawParam) {
        Integer cashOutId = siteShopCashOutService.apply(withdrawParam);
        Map<String, Object> data = new HashMap<>();
        data.put("id", cashOutId);
        return Result.success("提现申请成功", data);
    }

    /**
     * 店铺取消提现
     *
     * @param id 参数
     * @return Result<Object>
     */
    @PutMapping("/cash_cancel/{id}")
    public Result<Object> cancel(@PathVariable("id") Integer id) {
        return Result.success(siteShopCashOutService.cancel(id));
    }

}
