package com.niu.core.controller.adminapi.member;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.enums.member.*;
import com.niu.core.service.admin.member.IMemberService;
import com.niu.core.service.admin.member.param.*;
import com.niu.core.service.admin.member.vo.MemberAllListVo;
import com.niu.core.service.admin.member.vo.MemberInfoVo;
import com.niu.core.service.admin.member.vo.MemberListVo;
import com.niu.core.service.admin.member.vo.MemberRecommenderSelectVo;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 会员控制器
 */

@RestController
@RequestMapping("/adminapi/member")
@SaCheckLogin
public class MemberController {

    @Resource
    IMemberService memberService;

    /**
     * 会员列表
     *
     * @param pageParam   分页
     * @param searchParam 搜索条件
     * @return Result<PageResult < MemberListVo>>
     */
    @GetMapping("/member")
    public Result<PageResult<MemberListVo>> list(@Validated PageParam pageParam,
                                                 @Validated MemberSearchParam searchParam) {
        PageResult<MemberListVo> list = memberService.list(pageParam, searchParam);
        return Result.success(list);
    }


    /**
     * 查询全部会员
     *
     * @param searchParam
     * @return
     */
    @GetMapping("/member/list")
    public Result<List<MemberAllListVo>> list(@Validated MemberSearchParam searchParam) {
        List<MemberAllListVo> list = memberService.all(searchParam);
        return Result.success(list);
    }

    /**
     * 会员详情
     *
     * @param id 主键ID
     * @return Result<MemberInfoVo>
     */
    @GetMapping("/member/{id}")
    public Result<MemberInfoVo> info(@PathVariable("id") Integer id) {
        MemberInfoVo info = memberService.info(id);
        return Result.success(info);
    }

    /**
     * 会员添加
     *
     * @param addParam 添加参数
     * @return Result<Object>
     */
    @PostMapping("/member")
    public Result<Object> add(@Validated @RequestBody MemberAddParam addParam) {
        memberService.add(addParam);
        return Result.success();
    }

    /**
     * 会员编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/member/{member_id}")
    public Result<Object> edit(@PathVariable("member_id") Integer memberId, @Validated @RequestBody MemberParam editParam) {
        memberService.edit(memberId, editParam);
        return Result.success();
    }

    /**
     * 会员编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/member/modify/{member_id}/{field}")
    public Result<Object> modify(@PathVariable("member_id") Integer memberId, @PathVariable("field") String field, @Validated @RequestBody MemberModifyParam editParam) {
        editParam.setMemberId(memberId);
        editParam.setField(field);
        memberService.modify(editParam);
        return Result.success();
    }

    /**
     * 会员编辑
     *
     * @param editParam 编辑参数
     * @return Result<Object>
     */
    @PutMapping("/member/batch_modify")
    public Result<Object> batchModify(@Validated @RequestBody MemberBatchModifyParam editParam) {
        memberService.batchModify(editParam);
        return Result.success();
    }

    /**
     * 会员删除
     *
     * @param memberId 参数
     * @return Result<Object>
     */
    @DeleteMapping("/member/{member_id}")
    public Result<Object> del(@PathVariable("member_id") Integer memberId) {
        memberService.del(memberId);
        return Result.success();
    }

    /**
     * 推荐人
     *
     * @param pageParam
     * @param param
     * @return
     */
    @GetMapping("/member/recommender/select")
    public Result<PageResult<MemberRecommenderSelectVo>> recommenderSelectlists(PageParam pageParam,
                                                                                MemberRecommenderSelectParam param) {
        return Result.success(memberService.getRecommenderSelectlists(pageParam, param));
    }

    @PutMapping("/member/recommender/{memberId}")
    public Result modifyRecommender(@PathVariable("memberId") Integer memberId, @RequestBody MemberRecommenderModifyParam param) {
        Integer pid = StringUtils.isNotEmpty(param.getPid()) ? Integer.parseInt(param.getPid()) : 0;
        memberService.modifyRecommender(memberId, pid);
        return Result.success();
    }

    /**
     * 重置密码
     *
     * @param memberId
     * @param param
     * @return
     */
    @PutMapping("/member/resetpwd/{memberId}")
    public Result resetPassword(@PathVariable("memberId") Integer memberId, @RequestBody MemberResetPasswordParam param) {
        memberService.resetPassword(memberId, param.getPassword());
        return Result.success();
    }

    /**
     * 获取会员码
     *
     * @return
     */
    @GetMapping("/memberno")
    public Result<String> getMemberNo() {
        return Result.success(memberService.getMemberNo());
    }

    /**
     * 获取所有注册方式
     *
     * @return
     */
    @GetMapping("/registertype")
    public Result<Map> getRegisterType() {
        return Result.success(MemberRegisterTypeEnum.getMap());
    }

    /**
     * 获取所有注册渠道
     *
     * @return
     */
    @GetMapping("/register/channel")
    public Result<Map> getRegisterChannel() {
        return Result.success(MemberRegisterChannelEnum.getMap());
    }

    /**
     * 获取所有会员状态
     *
     * @return
     */
    @GetMapping("/status/list")
    public Result<Map> getStatusList() {
        return Result.success(StatusEnum.getMap());
    }

    /**
     * 设置状态
     *
     * @param status
     */
    @PutMapping("/setstatus/{status}")
    public Result<Object> setStatus(@PathVariable("status") Integer status, @RequestBody MemberParam param) {
        memberService.setStatus(status, param);
        return Result.success();
    }

    /**
     * 获取会员权益字典
     *
     * @return
     */
    @GetMapping("/dict/benefits")
    public Result<JSONObject> getMemberBenefitsDict() {
        return Result.success(memberService.getMemberBenefitsDict());
    }

    /**
     * 获取会员礼包字典
     *
     * @return
     */
    @GetMapping("/dict/gift")
    public Result<JSONObject> getMemberGiftDict() {
        return Result.success(memberService.getMemberGiftDict());
    }

    /**
     * 获取成长值规则字典
     *
     * @return
     */
    @GetMapping("/dict/growth_rule")
    public Result<JSONObject> getGrowthRuleDict() {
        return Result.success(GrowthRuleEnum.getType());
    }

    /**
     * 获取积分规则字典
     *
     * @return
     */
    @GetMapping("/dict/point_rule")
    public Result<JSONObject> getPointRuleDict() {
        return Result.success(PointRuleEnum.getType());
    }

    /**
     * 获取会员礼包内容
     *
     * @return
     */
    @PostMapping("/gifts/content")
    public Result<JSONObject> getMemberGiftsContent(@RequestBody JSONObject param) {
        return Result.success(memberService.getMemberGiftsContent(param));
    }

    /**
     * 获取权益内容
     *
     * @return
     */
    @PostMapping("/benefits/content")
    public Result<JSONObject> getMemberBenefitsContent(@RequestBody JSONObject param) {
        return Result.success(memberService.getMemberBenefitsContent(param));
    }

    /**
     * getMemberContentType
     */
    @GetMapping("/member/content/type")
    public Result<?> contentType() {
        var contentType = memberService.getMemberContentType();
        return Result.success(contentType);
    }

    /**
     * getMemberSendComponentByType
     * 通过发放类型获取会员发放组件
     */
    @GetMapping("/member/send/component/{type}")
    public Result<?> getMemberSendComponentByType(@PathVariable("type") String type) {
        var contentType = memberService.getMemberSendComponentByType(type);
        return Result.success(contentType);
    }
}
