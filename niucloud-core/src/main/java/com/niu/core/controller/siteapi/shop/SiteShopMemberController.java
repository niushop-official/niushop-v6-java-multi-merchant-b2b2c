package com.niu.core.controller.siteapi.shop;

import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.enums.member.MemberRegisterChannelEnum;
import com.niu.core.enums.shop.ShopMemberIsFollowEnum;
import com.niu.core.service.site.shop.ISiteShopMemberService;
import com.niu.core.service.site.shop.param.*;
import com.niu.core.service.site.shop.vo.PlatMemberSelectVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberInfoVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberListVo;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 店铺会员表控制器
 */

@RestController
@RequestMapping("adminapi/shop/site/member")
public class SiteShopMemberController {

    @Resource
    ISiteShopMemberService siteShopMemberService;

    /**
     * 店铺会员表分页列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < SiteShopMemberListVo>>
     */
    @GetMapping("")
    public Result<PageResult<SiteShopMemberListVo>> page(@Validated PageParam pageParam, @Validated SiteShopMemberSearchParam searchParam) {
        PageResult<SiteShopMemberListVo> list = siteShopMemberService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 核销会员选择列表
     *
     * @param searchParam 搜索条件
     * @return Result<List<PlatMemberSelectVo>>
     */
    @GetMapping("/plat_select")
    public Result<List<PlatMemberSelectVo>> platMemberSelect(@Validated PlatMemberSelectParam searchParam) {
        List<PlatMemberSelectVo> list = siteShopMemberService.platMemberSelect(searchParam);
        return Result.success(list);
    }

    /**
     * 获取所有注册渠道
     *
     * @return
     */
    @GetMapping("/register_channel")
    public Result<Map> getRegisterChannel() {
        return Result.success(MemberRegisterChannelEnum.getMap());
    }

    /**
     * 获取关注状态
     *
     * @return
     */
    @GetMapping("/is_follow")
    public Result<Map> getIsFollow() {
        return Result.success(ShopMemberIsFollowEnum.getMap());
    }

    /**
     * 获取关注状态
     *
     * @return
     */
    @GetMapping("/follow_status")
    public Result<Map> getFollowStatus() {
        return Result.success(ShopMemberIsFollowEnum.getMap());
    }

    /**
     * 店铺会员详情
     *
     * @param memberId 会员ID
     * @param addon    所属插件
     * @return Result<SiteShopMemberInfoVo>
     */
    @GetMapping("/{memberId}/{addon}")
    public Result<SiteShopMemberInfoVo> infoByAddon(@PathVariable("memberId") Integer memberId, @PathVariable("addon") String addon) {
        SiteShopMemberInfoVo info = siteShopMemberService.info(memberId, addon);
        return Result.success(info);
    }

    /**
     * 按插件分页列表
     *
     * @param addon       所属插件
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < SiteShopMemberListVo>>
     */
    @GetMapping("/{addon}")
    public Result<PageResult<SiteShopMemberListVo>> pageByAddon(@PathVariable("addon") String addon, @Validated PageParam pageParam, @Validated SiteShopMemberSearchParam searchParam) {
        searchParam.setAddon(addon);
        PageResult<SiteShopMemberListVo> list = siteShopMemberService.page(pageParam, searchParam);
        return Result.success(list);
    }

    /**
     * 店铺会员表添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("")
    public Result<Object> add(@Validated @RequestBody SiteShopMemberParam addParam) {
        siteShopMemberService.add(addParam);
        return Result.success();
    }

    /**
     * 店铺会员表编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/{id}")
    public Result<Object> edit(@PathVariable("id") Integer id, @Validated @RequestBody SiteShopMemberParam editParam) {
        siteShopMemberService.edit(id, editParam);
        return Result.success();
    }

    /**
     * 会员编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/modify/{member_id}/{addon}/{field}")
    public Result<Object> modify(@PathVariable("member_id") Integer memberId, @PathVariable("addon") String addon, @PathVariable("field") String field, @Validated @RequestBody SiteShopMemberModifyParam editParam) {
        editParam.setMemberId(memberId);
        editParam.setAddon(addon);
        editParam.setField(field);
        siteShopMemberService.modify(editParam);
        return Result.success();
    }


    @PutMapping("/batch_modify/{addon}")
    @ApiOperation("批量修改会员字段")
    public Result<?> batchModify(@PathVariable("addon") String addon,
                                 @RequestBody SiteShopMemberBatchModifyParam editParam) {
        editParam.setAddon(addon);
        siteShopMemberService.batchModify(editParam);
        return Result.success();
    }

    /**
     * 店铺会员表删除
     *
     * @param id 参数
     * @return Result<Object>
     */
    @DeleteMapping("/{id}")
    public Result<Object> del(@PathVariable("id") Integer id) {
        siteShopMemberService.del(id);
        return Result.success();
    }

}
