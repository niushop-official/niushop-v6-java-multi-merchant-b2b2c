package com.niu.core.controller.adminapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.shop.ShopCashOutEnum;
import com.niu.core.enums.shop.ShopTransferEnum;
import com.niu.core.service.admin.shop.ISiteShopCashOutService;
import com.niu.core.service.admin.shop.param.SiteShopCashOutAuditParam;
import com.niu.core.service.admin.shop.param.SiteShopCashOutParam;
import com.niu.core.service.admin.shop.param.SiteShopCashOutSearchParam;
import com.niu.core.service.admin.shop.param.SiteShopCashOutTransferParam;
import com.niu.core.service.admin.shop.vo.SiteShopCashOutInfoVo;
import com.niu.core.service.admin.shop.vo.SiteShopCashOutListVo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

/**
 * 商家站点提现表控制器
 */

@RestController
@RequestMapping("adminapi/shop/admin/shop/cashout")
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
    @GetMapping("")
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
    @GetMapping("/info/{id}")
    public Result<SiteShopCashOutInfoVo> info(@PathVariable("id") String id) {
        SiteShopCashOutInfoVo info = siteShopCashOutService.info(id);
        return Result.success(info);
    }

    /**
     * 商家站点提现表添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteShopCashOutParam addParam) {
        siteShopCashOutService.add(addParam);
        return Result.success();
    }

    /**
     * 商家站点提现表编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopCashOutParam editParam) {
        siteShopCashOutService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 商家站点提现表删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        siteShopCashOutService.del(id);
        return Result.success();
    }

    /**
     * 获取提现状态
     * @return
     */
    @GetMapping("/status")
    public Result<Object> getStatus() {
        return Result.success(ShopCashOutEnum.getStatus());
    }

    /**
     * 转账方式
     * @return
     */
    @GetMapping("/transfer_type")
    public Result<Object> getTransferType() {
        return Result.success(ShopTransferEnum.getMap());
    }

    @GetMapping("/withdrawinfo")
    public Result<Object> getWithdrawData() {

        return Result.success(siteShopCashOutService.getWithdrawData());

    }
    @PutMapping("audit/{id}")
    public Result<Object> audit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopCashOutAuditParam auditParam) {
        siteShopCashOutService.audit(id,auditParam);
        return Result.success();
    }

    @PutMapping("transfer/{id}")
    public Result<Object> transfer(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopCashOutTransferParam transferParam) {
        siteShopCashOutService.transfer(id,transferParam);
        return Result.success();
    }


}
