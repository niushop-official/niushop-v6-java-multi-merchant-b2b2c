package com.niu.core.controller.adminapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.site.SiteStatusEnum;
import com.niu.core.service.admin.shop.ISiteShopService;
import com.niu.core.service.admin.shop.param.*;
import com.niu.core.service.admin.shop.vo.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.Map;

/**
 * 店铺扩展表控制器
 */

@RestController
@RequestMapping("adminapi/shop/admin/shop")
public class SiteShopController {

    @Resource
    ISiteShopService siteShopService;

    /**
     * 店铺扩展表分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteShopListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SiteShopListVo>> page(@Validated PageParam pageParam, @Validated SiteShopSearchParam searchParam) {
        PageResult<SiteShopListVo> list = siteShopService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 店铺扩展列表
     *
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteShopListVo>>
     */
    @GetMapping("/list")
    public Result<List<SiteShopListVo>> list(@Validated SiteShopSearchParam searchParam) {
        List<SiteShopListVo> list = siteShopService.list(searchParam);
        return Result.success(list);
    }



    /**
     * 店铺扩展表详情
     *
     * @param id 主键ID
     * @return Result<SiteShopInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SiteShopInfoVo> info(@PathVariable("id") Integer id) {
        SiteShopInfoVo info = siteShopService.info(id);
        return Result.success(info);
    }

    /**
     * 店铺扩展表添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteShopParam addParam) {
        siteShopService.add(addParam);
        return Result.success();
    }

    /**
     * 店铺扩展表编辑
     *
     * @param id 店铺ID
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopInfoParam editParam) {
        if (id == null || id <= 0) {
            return Result.fail("店铺ID不能为空或无效");
        }
        
        // 执行编辑操作
        siteShopService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 店铺扩展表删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        siteShopService.del(id);
        return Result.success();
    }

    /**
     * 店铺扩展关闭店铺
     *
     * @return Result<Object>
     */
    @PutMapping("/close/{id}")
    public Result<Object> closeShop(@PathVariable("id") Integer id) {
        siteShopService.closeShop(id);
        return Result.success();
    }
    /**
     * 店铺扩展关闭店铺
     *
     * @return Result<Object>
     */
    @PutMapping("/open/{id}")
    public Result<Object> openShop(@PathVariable("id") Integer id) {
        siteShopService.openShop(id);
        return Result.success();
    }
    /**
     * 站点状态列表
     * @return
     */
    @GetMapping("/statuslist")
    public Result<Map<Integer, String>> statusList(){
        return Result.success(SiteStatusEnum.getMap());
    }

    /**
     * 店铺账户详情
     * @param pageParam
     * @param id
     * @return
     */
    @GetMapping("/accountLog/{id}")
    public Result<PageResult<SiteShopAccountLogListVo>> getAccountLogList(@Validated PageParam pageParam, @PathVariable("id") Integer id){
        return Result.success(siteShopService.getShopAccountLog(pageParam,id));
    }

    /**
     * 店铺提现详情
     * @param pageParam
     * @param id
     * @return
     */
    @GetMapping("/cashout/{id}")
    public Result<PageResult<SiteShopCashOutListVo>> getShopCashOutList(@Validated PageParam pageParam, @PathVariable("id") Integer id){
        return Result.success(siteShopService.getShopCashOutlog(pageParam,id));
    }

    /**
     * 店铺账户信息
     * @param id
     * @return
     */
    @GetMapping("/account/{id}")
    public Result<SiteShopInShopVo> getShopAccount(@PathVariable("id") Integer id){
        return Result.success(siteShopService.getShopAccountInfo(id));
    }

    /**
     * 店铺账户信息
     * @param id
     * @return
     */
    @PutMapping("/account/{id}")
    public Result<Object> editShopAccount(@PathVariable("id") Integer id, @RequestBody SiteShopAccountInfoParam siteShopAccountInfoParam){
        siteShopService.editShopAccount(id, siteShopAccountInfoParam);
        return Result.success();
    }


    /**
     * 店铺选择分页列表
     *
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<SiteShopSelectPage>
     */
    @GetMapping("/select")
    public Result<SiteShopSelectPage> select(@Validated PageParam pageParam, 
                                             @Validated SiteShopSelectParam searchParam,
                                             @RequestParam(value = "verify_site_ids", required = false) Integer[] verifySiteIds) {
        // 手动设置数组参数
        if (verifySiteIds != null && verifySiteIds.length > 0) {
            searchParam.setVerifySiteIds(verifySiteIds);
        }
        return Result.success(siteShopService.getSelectPage(pageParam, searchParam));
    }
}
