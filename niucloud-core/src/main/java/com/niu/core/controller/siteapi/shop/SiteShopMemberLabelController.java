package com.niu.core.controller.siteapi.shop;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.service.site.shop.ISiteShopMemberLabelService;
import com.niu.core.service.site.shop.param.SiteShopMemberLabelEditParam;
import com.niu.core.service.site.shop.param.SiteShopMemberLabelSearchParam;
import com.niu.core.service.site.shop.vo.SiteShopMemberLabelAllListVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberLabelInfoVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberLabelListVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员标签控制器
 */

@RestController
@RequestMapping("adminapi/shop/site/member/label")
@SaCheckLogin
public class SiteShopMemberLabelController {

    @Resource
    ISiteShopMemberLabelService siteShopMemberLabelService;

    /**
     * 店铺会员标签列表
     * @param pageParam 分页
     * @param searchParam 搜索条件
     * @return Result<PageResult<SiteShopMemberLabelListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SiteShopMemberLabelListVo>> list(@Validated PageParam pageParam, @Validated SiteShopMemberLabelSearchParam searchParam) {
        PageResult<SiteShopMemberLabelListVo> list = siteShopMemberLabelService.list(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 店铺会员标签详情
     * @param id 主键ID
     * @return Result<SiteShopMemberLabelInfoVo>
     */
    @GetMapping("/{id}")
    public Result<SiteShopMemberLabelInfoVo> info(@PathVariable("id") Integer id) {
        SiteShopMemberLabelInfoVo info = siteShopMemberLabelService.info(id);
        return Result.success(info);
    }

    /**
     * 店铺会员标签添加
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteShopMemberLabelEditParam addParam) {
        siteShopMemberLabelService.add(addParam);
        return Result.success();
    }

    /**
     * 店铺会员标签编辑
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopMemberLabelEditParam editParam) {
        siteShopMemberLabelService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 店铺会员标签删除
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        siteShopMemberLabelService.del(id);
        return Result.success();
    }

    /**
     * 全部店铺会员标签
     * @return
     */
    @GetMapping("/all")
    public Result<List<SiteShopMemberLabelAllListVo>> all() {
        List<SiteShopMemberLabelAllListVo> list = siteShopMemberLabelService.all();
        return Result.success(list);
    }
}
